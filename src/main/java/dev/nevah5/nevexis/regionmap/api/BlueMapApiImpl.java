package dev.nevah5.nevexis.regionmap.api;

import com.flowpowered.math.vector.Vector2d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.ExtrudeMarker;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.math.Shape;
import dev.nevah5.nevexis.regionmap.RegionMap;
import dev.nevah5.nevexis.regionmap.config.RegionMapConfig;
import dev.nevah5.nevexis.regionmap.model.Chunk;
import dev.nevah5.nevexis.regionmap.model.ClaimedRegion;
import dev.nevah5.nevexis.regionmap.model.RegionGroup;
import dev.nevah5.nevexis.regionmap.model.Team;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.groupingBy;

public class BlueMapApiImpl implements BlueMapApi {
    public static final Logger LOGGER = LoggerFactory.getLogger(RegionMap.MOD_ID);
    public static final String REGION_DIRECTORY = "regions/";
    public static final String MARKER_DIRECTORY = "markers/";

    private static final float EXTRUDE_FROM = -64;
    private static final float EXTRUDE_TO = 319;

    private static BlueMapAPI api;

    public BlueMapApiImpl() {
        BlueMapAPI.onEnable(api -> {
            BlueMapApiImpl.api = api;
            loadMarkers();
        });
    }

    public static void reloadMarkers() {
        loadMarkers();
    }

    public static void reloadTeamMarkers(Team team) {
        loadMarkerSetByTeam(team);
    }

    private static void loadMarkers() {
        Map<UUID, List<ClaimedRegion>> regionsByTeam = RegionMapConfig.regions.stream()
                .collect(groupingBy(ClaimedRegion::getTeam));

        for (Map.Entry<UUID, List<ClaimedRegion>> entry : regionsByTeam.entrySet()) {
            Team team = RegionMapConfig.teams.stream().filter(t -> t.getTeamId().equals(entry.getKey())).findFirst().orElse(null);
            if (team == null) {
                LOGGER.error("Failed to load team for region: " + entry.getKey());
                continue;
            }
            loadMarkerSetByTeam(team, entry.getValue());
        }
    }

    private static void loadMarkerSetByTeam(Team team) {
        loadMarkerSetByTeam(team, RegionMapConfig.regions.stream()
                .filter(region -> region.getTeam().equals(team.getTeamId()))
                .toList());
    }

    private static void loadMarkerSetByTeam(Team team, List<ClaimedRegion> regions) {
        MarkerSet markerSet = MarkerSet.builder()
                .label(team.getDisplayName())
                .build();
        // get all unique region groups from claimed regions
        Map<RegionGroup, List<ClaimedRegion>> regionsByGroupTmp = regions.stream()
                .filter(region -> region.getRegionGroup() != null)
                .collect(groupingBy(ClaimedRegion::getRegionGroup));
        Map<RegionGroup, List<ClaimedRegion>> regionsByGroup = new HashMap<>();
        for (Map.Entry<RegionGroup, List<ClaimedRegion>> entry : regionsByGroupTmp.entrySet()) {
            RegionGroup key = regionsByGroup.keySet().stream()
                    .filter(regionGroup -> regionGroup.getId().equals(entry.getKey().getId()))
                    .findFirst()
                    .orElse(entry.getKey());
            List<ClaimedRegion> regionsOfGroup = regionsByGroup.getOrDefault(key, new ArrayList<>());
            regionsOfGroup.addAll(entry.getValue());
            regionsByGroup.put(key, regionsOfGroup);
        }

        // load single chunks
        for (ClaimedRegion region : regions) {
            if (region.getRegionGroup() != null) {
                continue;
            }
            Chunk chunk = region.toChunk();
            final ExtrudeMarker marker = new ExtrudeMarker.Builder()
                    .label(region.getRegionName())
                    .shape(Shape.builder()
                                    .addPoint(toPoint(chunk.getMinX(), chunk.getMinZ()))
                                    .addPoint(toPoint(chunk.getMaxX(), chunk.getMinZ()))
                                    .addPoint(toPoint(chunk.getMaxX(), chunk.getMaxZ()))
                                    .addPoint(toPoint(chunk.getMinX(), chunk.getMaxZ()))
                                    .build(),
                            EXTRUDE_FROM,
                            EXTRUDE_TO)
                    .fillColor(team.getColor().getColor())
                    .lineColor(team.getColor().getLineColor())
                    .build();

            markerSet.getMarkers()
                    .put(region.getRegionId(), marker);
        }

        // load region groups
        for (Map.Entry<RegionGroup, List<ClaimedRegion>> entry : regionsByGroup.entrySet()) {
            Shape.Builder groupShapeBuilder = Shape.builder();
            Chunk currentChunk = entry.getValue().stream()
                    .map(ClaimedRegion::toChunk)
                    .min(Comparator.comparingInt(Chunk::getChunkX)
                            .thenComparing(Chunk::getChunkZ, Comparator.reverseOrder()))
                    .orElseThrow(() -> new IllegalStateException("No chunks found"));

            List<Vector2d> points = new ArrayList<>();
            Vector2d startingPoint = new Vector2d(currentChunk.getMinX(), currentChunk.getMinZ());
            points.add(startingPoint);
            Vector2d lastPoint = startingPoint;
            Map<Vector2d, List<Chunk.Direction>> chunkFromDirections = new HashMap<>();
            do {
                Map<Chunk.Direction, Chunk> adjacentChunks = new HashMap<>();
                Chunk finalCurrentChunk = currentChunk; // tmp for lambda functions
                entry.getValue()
                        .stream()
                        .map(ClaimedRegion::toChunk)
                        .filter(c -> c.getChunkX() == finalCurrentChunk.getChunkX() && c.getChunkZ() == finalCurrentChunk.getChunkZ() - 1)
                        .findFirst()
                        .ifPresent(c -> adjacentChunks.put(Chunk.Direction.NORTH, c));
                entry.getValue()
                        .stream()
                        .map(ClaimedRegion::toChunk)
                        .filter(c -> c.getChunkX() == finalCurrentChunk.getChunkX() + 1 && c.getChunkZ() == finalCurrentChunk.getChunkZ())
                        .findFirst()
                        .ifPresent(c -> adjacentChunks.put(Chunk.Direction.EAST, c));
                entry.getValue()
                        .stream()
                        .map(ClaimedRegion::toChunk)
                        .filter(c -> c.getChunkX() == finalCurrentChunk.getChunkX() && c.getChunkZ() == finalCurrentChunk.getChunkZ() + 1)
                        .findFirst()
                        .ifPresent(c -> adjacentChunks.put(Chunk.Direction.SOUTH, c));
                entry.getValue()
                        .stream()
                        .map(ClaimedRegion::toChunk)
                        .filter(c -> c.getChunkX() == finalCurrentChunk.getChunkX() - 1 && c.getChunkZ() == finalCurrentChunk.getChunkZ())
                        .findFirst()
                        .ifPresent(c -> adjacentChunks.put(Chunk.Direction.WEST, c));

                List<Vector2d> nextPoints = currentChunk.getNextPointsForBlueMap(lastPoint, adjacentChunks);
                if (nextPoints.contains(startingPoint)) {
                    int index = nextPoints.indexOf(startingPoint);
                    if (index != -1) {
                        nextPoints.subList(index, nextPoints.size()).clear();
                    }
                    break;
                } else if (nextPoints.size() != 0) {
                    lastPoint = nextPoints.get(nextPoints.size() - 1);
                }
                points.addAll(nextPoints);

                List<Chunk.Direction> currentChunkDirections = chunkFromDirections.getOrDefault(new Vector2d(currentChunk.getChunkX(), currentChunk.getChunkZ()), new ArrayList<>());

                Chunk.Direction lastDirection = currentChunkDirections.size() > 0 ? currentChunkDirections.get(currentChunkDirections.size() - 1) : null;
                List<Chunk> backoutOptions = new ArrayList<>(adjacentChunks.values());
                backoutOptions.remove(adjacentChunks.get(lastDirection));

                List<Chunk.Direction> nextDirections = currentChunk.getNextDirectionsFromChunkDirection(lastDirection);
                boolean hasFoundNextChunk = false;
                for (Chunk.Direction direction : nextDirections) {
                    // only allow directions that are possible from the current point
                    if (currentChunkDirections.stream().filter(d -> d != lastDirection).toList().size() == 1 && currentChunkDirections.contains(direction)) {
                        currentChunk = adjacentChunks.get(direction);
                        addChunkFromDirection(chunkFromDirections, currentChunk, Chunk.Direction.getOpposite(direction));
                        hasFoundNextChunk = true;
                        break;
                    }
                    if (adjacentChunks.containsKey(direction) && adjacentChunks.size() == currentChunkDirections.stream().distinct().toList().size()) { // other special case
                        currentChunk = adjacentChunks.get(direction);
                        addChunkFromDirection(chunkFromDirections, currentChunk, Chunk.Direction.getOpposite(direction));
                        hasFoundNextChunk = true;
                        break;
                    } else if (adjacentChunks.containsKey(direction) && (!currentChunkDirections.contains(direction) || lastDirection == Chunk.Direction.getOpposite(direction))) {
                        currentChunk = adjacentChunks.get(direction);
                        addChunkFromDirection(chunkFromDirections, currentChunk, Chunk.Direction.getOpposite(direction));
                        hasFoundNextChunk = true;
                        break;
                    }
                }
                if (hasFoundNextChunk) {
                    continue;
                }

                if (backoutOptions.size() == 1) {
                    try {
                        Chunk.Direction direction = null;
                        for (Map.Entry<Chunk.Direction, Chunk> e : adjacentChunks.entrySet()) {
                            if (e.getValue().equals(backoutOptions.get(0))) {
                                direction = e.getKey();
                                break;
                            }
                        }
                        currentChunk = adjacentChunks.get(direction);
                        Chunk.Direction oppositeDirection = Chunk.Direction.getOpposite(direction);
                        addChunkFromDirection(chunkFromDirections, currentChunk, oppositeDirection);
                    } catch (Exception e) {
                        LOGGER.error("Failed to find next chunk for region group: " + entry.getKey().getName(), e);
                    }
                } else if (adjacentChunks.size() == 1 && entry.getValue().size() != 2) {
                    Map.Entry<Chunk.Direction, Chunk> lastChunk = adjacentChunks.entrySet().iterator().next();
                    currentChunk = lastChunk.getValue();
                    if (lastChunk.getKey() == Chunk.Direction.NORTH) {
                        addChunkFromDirection(chunkFromDirections, currentChunk, Chunk.Direction.SOUTH);
                    } else if (lastChunk.getKey() == Chunk.Direction.EAST) {
                        addChunkFromDirection(chunkFromDirections, currentChunk, Chunk.Direction.WEST);
                    } else if (lastChunk.getKey() == Chunk.Direction.SOUTH) {
                        addChunkFromDirection(chunkFromDirections, currentChunk, Chunk.Direction.NORTH);
                    } else if (lastChunk.getKey() == Chunk.Direction.WEST) {
                        addChunkFromDirection(chunkFromDirections, currentChunk, Chunk.Direction.EAST);
                    }
                } else {
                    LOGGER.error("Failed to find next chunk for region group: " + entry.getKey().getName(), new IllegalStateException("Failed to find next chunk to check"));
                }
            } while (lastPoint != startingPoint);

            points.forEach(groupShapeBuilder::addPoint);

            final ExtrudeMarker marker = new ExtrudeMarker.Builder()
                    .label(entry.getKey().getName())
                    .shape(groupShapeBuilder.build(),
                            EXTRUDE_FROM,
                            EXTRUDE_TO)
                    .fillColor(team.getColor().getColor())
                    .lineColor(team.getColor().getLineColor())
                    .build();
            markerSet.getMarkers()
                    .put(entry.getKey().getId().toString(), marker);
        }

        // TODO: multi-world support
        api.getWorld(World.OVERWORLD).ifPresent(bmWorld -> {
            for (BlueMapMap map : bmWorld.getMaps()) {
                map.getMarkerSets().put(team.getTeamId().toString(), markerSet);
            }
        });
    }

    private static void addChunkFromDirection(Map<Vector2d, List<Chunk.Direction>> chunks, Chunk chunk, Chunk.Direction direction) {
        final Vector2d key = new Vector2d(chunk.getChunkX(), chunk.getChunkZ());
        final List<Chunk.Direction> directions = chunks.getOrDefault(key, new ArrayList<>());
        directions.add(direction);
        chunks.put(key, directions);
    }

    @Override
    public void addRegion(final Entity player, final Team team, ServerCommandSource source) throws IllegalStateException, IllegalArgumentException {
        ClaimedRegion region = ClaimedRegion.builder()
                .team(team.getTeamId())
                .pos(player.getPos())
                .claimedAt((int) (System.currentTimeMillis() / 1000))
                .build();
        RegionMapConfig.regions.add(region);
        RegionMapConfig.setupConfigFile(REGION_DIRECTORY + region.getRegionId() + ".json", region);
        loadMarkerSetByTeam(team);
    }

    private static Vector2d toPoint(double x, double z) {
        return new Vector2d(x, z);
    }
}
