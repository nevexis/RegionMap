package dev.nevah5.nevexis.regionmap.api;

import dev.nevah5.nevexis.regionmap.model.Team;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;

public interface BlueMapApi {
    void addRegion(Entity player, Team team, ServerCommandSource source);
}
