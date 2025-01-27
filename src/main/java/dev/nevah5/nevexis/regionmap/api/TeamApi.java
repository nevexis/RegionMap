package dev.nevah5.nevexis.regionmap.api;

import dev.nevah5.nevexis.regionmap.model.Team;
import net.minecraft.server.command.ServerCommandSource;

public interface TeamApi {
    void createTeam(Team team, ServerCommandSource source);
}
