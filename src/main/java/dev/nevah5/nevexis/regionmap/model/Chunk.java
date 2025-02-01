package dev.nevah5.nevexis.regionmap.model;

import com.flowpowered.math.vector.Vector2d;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
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

    public static Chunk fromChunkCoords(int x, int z) {
        return new Chunk(new ChunkPos(x, z));
    }

    /**
     * determines the next directions to check for the next points
     * @param point the current point
     * @return the next directions to check
     */
    public List<Direction> getNextDirectionsFromPoint(final Vector2d point) {
        if (point.getX() == minX && point.getY() == minZ) {
            return Arrays.asList(
                    Direction.WEST,
                    Direction.SOUTH,
                    Direction.EAST,
                    Direction.NORTH);
        } else if (point.getX() == minX && point.getY() == maxZ) {
            return Arrays.asList(
                    Direction.SOUTH,
                    Direction.EAST,
                    Direction.NORTH,
                    Direction.WEST);
        } else if (point.getX() == maxX && point.getY() == maxZ) {
            return Arrays.asList(
                    Direction.EAST,
                    Direction.NORTH,
                    Direction.WEST,
                    Direction.SOUTH);
        } else if (point.getX() == maxX && point.getY() == minZ) {
            return Arrays.asList(
                    Direction.NORTH,
                    Direction.WEST,
                    Direction.SOUTH,
                    Direction.EAST);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * gets the next point from the direction there is NO CHUNK AT
     * @param direction where no chunk is located
     * @return the next point
     */
    public Vector2d getNextPointFromDirection(final Direction direction) {
        if (direction == Direction.NORTH) {
            return new Vector2d(minX, minZ);
        } else if (direction == Direction.EAST) {
            return new Vector2d(maxX, minZ);
        } else if (direction == Direction.SOUTH) {
            return new Vector2d(maxX, maxZ);
        } else if (direction == Direction.WEST) {
            return new Vector2d(minX, maxZ);
        } else {
            return null;
        }
    }

    /**
     * Get the next points for the region group in order, according to the provided adjacent chunks
     * @return list of next points in order
     */
    public List<Vector2d> getNextPointsForBlueMap(Vector2d currentPoint, final Map<Direction, Chunk> adjacentChunks) {
        List<Vector2d> nextPoints = new ArrayList<>();

        if (adjacentChunks.size() == 4) {
            return nextPoints;
        }

        List<Direction> directionsToCheck = getNextDirectionsFromPoint(currentPoint);

        for (Direction direction : directionsToCheck) {
            if(!adjacentChunks.containsKey(direction)) {
                nextPoints.add(getNextPointFromDirection(direction));
            } else {
                return nextPoints;
            }
        }

        return nextPoints;
    }

    public enum Direction {
        NORTH,
        EAST,
        SOUTH,
        WEST
    }
}
