package dev.nevah5.nevexis.regionmap.model;

import lombok.Builder;
import lombok.Data;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

@Builder
@Data
public class ClaimedRegion {
    private UUID id = UUID.randomUUID();
    private Vec3d pos;
    private UUID team;
    private int claimedAt;

    public Chunk toChunk() {
        return Chunk.fromPlayerPos(pos);
    }
}
