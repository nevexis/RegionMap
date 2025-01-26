package dev.nevah5.nevexis.regionmap.api;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public interface BlueMapApi {
    void addRegion(World world, ChunkPos pos, String name);

    void removeRegion(World world, ChunkPos pos, String name);
}
