package dev.nevah5.nevexis.regionmap.api;

import dev.nevah5.nevexis.regionmap.model.Chunk;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public interface BlueMapApi {
    void addRegion(World world, Chunk chunk, String name);

    void removeRegion(World world, Chunk chunk, String name);
}
