package dev.nevah5.nevexis.regionmap.api;

import com.flowpowered.math.vector.Vector2d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.gson.MarkerGson;
import de.bluecolored.bluemap.api.markers.ExtrudeMarker;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.math.Color;
import de.bluecolored.bluemap.api.math.Shape;
import dev.nevah5.nevexis.regionmap.RegionMap;
import dev.nevah5.nevexis.regionmap.config.RegionMapConfig;
import dev.nevah5.nevexis.regionmap.model.Chunk;
import dev.nevah5.nevexis.regionmap.model.ClaimedRegion;
import dev.nevah5.nevexis.regionmap.model.Team;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static java.util.stream.Collectors.groupingBy;

public class BlueMapApiImpl implements BlueMapApi {
    public static final Logger LOGGER = LoggerFactory.getLogger(RegionMap.MOD_ID);
    public static final String REGION_DIRECTORY = "regions/";

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

        for (ClaimedRegion region : regions) {
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

        // TODO: multi-world support
        api.getWorld(World.OVERWORLD).ifPresent(bmWorld -> {
            for (BlueMapMap map : bmWorld.getMaps()) {
                map.getMarkerSets().put(team.getTeamId().toString(), markerSet);
            }
        });
    }

    @Override
    public void addRegion(final Entity player, final Team team, ServerCommandSource source) throws IllegalStateException, IllegalArgumentException {
        // TODO: check if chunk already claimed

        ClaimedRegion region = ClaimedRegion.builder()
                .team(team.getTeamId())
                .pos(player.getPos())
                .claimedAt((int) (System.currentTimeMillis() / 1000))
                .build();
        RegionMapConfig.regions.add(region);
        RegionMapConfig.setupConfigFile(REGION_DIRECTORY + region.getRegionId() + ".json", region);
        loadMarkerSetByTeam(team);
    }

    @Override
    public void removeRegion(Entity player, Team team, ServerCommandSource source) throws IllegalStateException {
        if (!(source.getEntity() instanceof ServerPlayerEntity)) {
            throw new IllegalStateException("Only players can remove regions!");
        }
        // TODO: implement

        source.sendFeedback(() -> Text.literal("Not implemented yet"), false);
    }

    private static Vector2d toPoint(double x, double z) {
        return new Vector2d(x, z);
    }
}
