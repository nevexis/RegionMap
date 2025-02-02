package dev.nevah5.nevexis.regionmap.api;

import net.minecraft.server.command.ServerCommandSource;

public interface TeamApi {
    int createTeam(String teamName, String color, String display, ServerCommandSource source);

    int leaveTeam(String teamName, ServerCommandSource source);

    int deleteTeam(String teamName, ServerCommandSource source);

    int listTeam(String teamName, ServerCommandSource source);

    int listTeams(ServerCommandSource source);

    int invitePlayer(String playerName, String teamName, ServerCommandSource source);

    int kickPlayer(String playerName, String teamName, ServerCommandSource source);
}
