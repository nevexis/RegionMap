package dev.nevah5.nevexis.regionmap.api;

import dev.nevah5.nevexis.regionmap.model.Team;
import net.minecraft.server.command.ServerCommandSource;

public interface TeamApi {
    int createTeam(Team team, ServerCommandSource source);

    int leaveTeam(String teamName, ServerCommandSource source);

    int deleteTeam(String teamName, ServerCommandSource source);

    int listTeam(String teamName, ServerCommandSource source);

    int listTeams(ServerCommandSource source);
}
