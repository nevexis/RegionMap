package dev.nevah5.nevexis.regionmap.model;

import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public record ClaimedRegion(Vec3d pos, UUID team, int claimedAt) {
    public Chunk toChunk() {
        return Chunk.fromPlayerPos(pos);
    }
}
