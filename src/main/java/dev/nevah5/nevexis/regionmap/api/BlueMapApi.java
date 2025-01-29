package dev.nevah5.nevexis.regionmap.api;

import dev.nevah5.nevexis.regionmap.model.Chunk;
import dev.nevah5.nevexis.regionmap.model.Team;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public interface BlueMapApi {
    void addRegion(Entity player, Team team, ServerCommandSource source);

    void removeRegion(Entity player, Team team, ServerCommandSource source);
}
