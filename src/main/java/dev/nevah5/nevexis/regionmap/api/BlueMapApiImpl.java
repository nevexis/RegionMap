package dev.nevah5.nevexis.regionmap.api;

import com.flowpowered.math.vector.Vector2d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.ExtrudeMarker;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.math.Color;
import de.bluecolored.bluemap.api.math.Shape;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class BlueMapApiImpl implements BlueMapApi {
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
        int chunkX = pos.x;
        int chunkZ = pos.z;

        double minX = chunkX << 4;
        double minZ = chunkZ << 4;
        double maxX = minX + 15;
        double maxZ = minZ + 15;

        // TODO: add dimension support
        final ExtrudeMarker marker = new ExtrudeMarker.Builder()
                .label(name)
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

        final MarkerSet markerSet = MarkerSet.builder()
                .label(name)
                .build();

        markerSet.getMarkers()
                .put("my-marker-id", marker);

        api.getWorld(world).ifPresent(bmWorld -> {
            for (BlueMapMap map : bmWorld.getMaps()) {
                map.getMarkerSets().put("my-marker-set-id", markerSet);
            }
        });
    }

    private Vector2d toPoint(double x, double z) {
        return new Vector2d(x, z);
    }
}
