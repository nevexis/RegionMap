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
import java.util.Optional;

public class BlueMapApiImpl implements BlueMapApi {
    public static final Logger LOGGER = LoggerFactory.getLogger(RegionMap.MOD_ID);
    public static final String REGION_DIRECTORY = "regions/";

    private static final float EXTRUDE_FROM = -64;
    private static final float EXTRUDE_TO = 319;

    private BlueMapAPI api;

    public BlueMapApiImpl() {
        BlueMapAPI.onEnable(api ->
                this.api = api
        );
    }

    @Override
    public void addRegion(final World world, final ChunkPos pos, final String name) {
        MarkerSet markerSet = loadMarkerSet(name).orElseGet(() -> MarkerSet.builder()
                .label("Team " + name)
                .build());

        int chunkX = pos.x;
        int chunkZ = pos.z;

        double minX = chunkX << 4;
        double minZ = chunkZ << 4;
        double maxX = minX + 16;
        double maxZ = minZ + 16;

        final ExtrudeMarker marker = new ExtrudeMarker.Builder()
                .label("Chunk " + chunkX + ", " + chunkZ)
                .shape(Shape.builder()
                        .addPoint(toPoint(minX, minZ))
                        .addPoint(toPoint(maxX, minZ))
                        .addPoint(toPoint(maxX, maxZ))
                        .addPoint(toPoint(minX, maxZ))
                        .build(),
                        EXTRUDE_FROM,
                        EXTRUDE_TO)
                .fillColor(new Color(255, 0, 0, 0.1f))
                .build();

        markerSet.getMarkers()
                .put(name.toLowerCase() + "-chunk-" + chunkX + "-" + chunkZ, marker);

        MarkerSet finalMarkerSet = markerSet;
        api.getWorld(world).ifPresent(bmWorld -> {
            for (BlueMapMap map : bmWorld.getMaps()) {
                map.getMarkerSets().put(name.toLowerCase() + "-regions", finalMarkerSet);
            }
        });

        saveMarkerSet(markerSet, name);
    }

    @Override
    public void removeRegion(World world, ChunkPos pos, String name) {
        final Path markerSetPath = Paths.get(REGION_DIRECTORY + name.toLowerCase() + ".json");
        MarkerSet markerSet = MarkerSet.builder()
                .label("Team " + name)
                .build();
        if (Files.exists(markerSetPath)) {
            try (FileReader reader = new FileReader(markerSetPath.toString())) {
                markerSet = MarkerGson.INSTANCE.fromJson(reader, MarkerSet.class);
            } catch (IOException ex) {
                LOGGER.error("Failed to load region marker set", ex);
            }
        }

        int chunkX = pos.x;
        int chunkZ = pos.z;

        markerSet.getMarkers()
                .remove(name.toLowerCase() + "-chunk-" + chunkX + "-" + chunkZ);

        MarkerSet finalMarkerSet = markerSet;
        api.getWorld(world).ifPresent(bmWorld -> {
            for (BlueMapMap map : bmWorld.getMaps()) {
                map.getMarkerSets().put(name.toLowerCase() + "-regions", finalMarkerSet);
            }
        });

        saveMarkerSet(markerSet, name);
    }

    private Vector2d toPoint(double x, double z) {
        return new Vector2d(x, z);
    }

    private Optional<MarkerSet> loadMarkerSet(String name) {
        final Path markerSetPath = Paths.get(REGION_DIRECTORY + name.toLowerCase() + ".json");
        if (Files.exists(markerSetPath)) {
            try (FileReader reader = new FileReader(markerSetPath.toString())) {
                return Optional.of(MarkerGson.INSTANCE.fromJson(reader, MarkerSet.class));
            } catch (IOException ex) {
                LOGGER.error("Failed to load region marker set", ex);
            }
        }
        return Optional.empty();
    }

    private void saveMarkerSet(MarkerSet markerSet, String name) {
        try (FileWriter writer = new FileWriter(REGION_DIRECTORY + name.toLowerCase() + ".json")) {
            MarkerGson.INSTANCE.toJson(markerSet, writer);
        } catch (IOException ex) {
            LOGGER.error("Failed to save region marker set", ex);
        }
    }
}
