package dev.nevah5.nevexis.regionmap.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;


@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class Chunk {
    private final ChunkPos pos;
    private final int chunkX;
    private final int chunkZ;

    private final double minX;
    private final double minZ;
    private final double maxX;
    private final double maxZ;

    public Chunk(ChunkPos pos) {
        this.pos = pos;
        this.chunkX = pos.x;
        this.chunkZ = pos.z;
        this.minX = chunkX << 4;
        this.minZ = chunkZ << 4;
        this.maxX = minX + 16;
        this.maxZ = minZ + 16;
    }

    public static Chunk fromPlayerPos(final Entity player) {
        return fromPlayerPos(player.getPos());
    }

    public static Chunk fromPlayerPos(final Vec3d position) {
        final ChunkPos pos = new ChunkPos(new BlockPos((int) position.x, (int) position.y, (int) position.z));
        return new Chunk(pos);
    }
}
