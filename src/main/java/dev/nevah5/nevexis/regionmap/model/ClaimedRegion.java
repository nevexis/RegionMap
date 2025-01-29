package dev.nevah5.nevexis.regionmap.model;

import lombok.Builder;
import lombok.Data;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

@Builder
@Data
public class ClaimedRegion {
    private Vec3d pos;
    private UUID team;
    private int claimedAt;

    public String getRegionId() {
        Chunk chunk = toChunk();
        return "region_" + chunk.getChunkX() + "_" + chunk.getChunkZ();
    }

    public String getRegionName() {
        Chunk chunk = toChunk();
        return "Chunk " + chunk.getChunkX() + ", " + chunk.getChunkZ();
    }

    public Chunk toChunk() {
        return Chunk.fromPlayerPos(pos);
    }
}
