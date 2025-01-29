package dev.nevah5.nevexis.regionmap.model;

import lombok.Data;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

@Data
public class ClaimedRegion {
    private UUID id = UUID.randomUUID();
    private Vec3d pos;
    private UUID team;
    private int claimedAt;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ClaimedRegion claimedRegion = new ClaimedRegion();
        public Builder() {
               claimedRegion.id = UUID.randomUUID();
        }

        public Builder pos(Vec3d pos) {
            this.claimedRegion.pos = pos;
            return this;
        }

        public Builder team(UUID team) {
            this.claimedRegion.team = team;
            return this;
        }

        public Builder setClaimedAt() {
            this.claimedRegion.claimedAt = (int) (System.currentTimeMillis() / 1000);
            return this;
        }

        public ClaimedRegion build() {
            return this.claimedRegion;
        }
    }

    public Chunk toChunk() {
        return Chunk.fromPlayerPos(pos);
    }
}
