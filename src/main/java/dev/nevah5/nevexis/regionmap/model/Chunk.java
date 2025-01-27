package dev.nevah5.nevexis.regionmap.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;


@RequiredArgsConstructor
@Getter
public class Chunk {
    private final ChunkPos pos;
    private final int chunkX = pos.x;
    private final int chunkZ = pos.z;

    private final double minX = chunkX << 4;
    private final double minZ = chunkZ << 4;
    private final double maxX = minX + 16;
    private final double maxZ = minZ + 16;

    public static Chunk fromPlayerPos(final Entity player) {
        return fromPlayerPos(player.getPos());
    }

    public static Chunk fromPlayerPos(final Vec3d position) {
        final ChunkPos pos = new ChunkPos(new BlockPos((int) position.x, (int) position.y, (int) position.z));
        return new Chunk(pos);
    }
}
