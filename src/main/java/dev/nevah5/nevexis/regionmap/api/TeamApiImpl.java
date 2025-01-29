package dev.nevah5.nevexis.regionmap.api;

import dev.nevah5.nevexis.regionmap.config.RegionMapConfig;
import dev.nevah5.nevexis.regionmap.model.Team;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static dev.nevah5.nevexis.regionmap.config.RegionMapConfig.LOGGER;
import static dev.nevah5.nevexis.regionmap.config.RegionMapConfig.setupConfigFile;

public class TeamApiImpl implements TeamApi {
    public static final String TEAM_DIRECTORY = "teams/";

    @Override
    public int createTeam(Team team, ServerCommandSource source) {
        if (RegionMapConfig.teams.stream().anyMatch(t -> t.getName().equals(team.getName()))) {
            source.sendFeedback(() -> Text.literal("Team with name " + team.getName() + " already exists"), false);
            return 0;
        }
        RegionMapConfig.teams.add(team);
        setupConfigFile(TEAM_DIRECTORY + team.getName() + ".json", team);
        source.sendFeedback(() -> Text.literal(team.getDisplayName() + " created"), false);
        return 1;
    }

    @Override
    public int leaveTeam(String teamName, ServerCommandSource source) {
        if (!(source.getEntity() instanceof ServerPlayerEntity)) {
            LOGGER.error("Only players can leave teams!");
            return 0;
        }
        if (source.getEntity().getUuid().equals(RegionMapConfig.teams.stream().filter(t -> t.getName().equals(teamName)).findFirst().orElse(null).getOwner())) {
            source.sendFeedback(() -> Text.literal("You cannot leave a team you own!"), false);
            return 0;
        }
        source.sendFeedback(() -> Text.literal("Leaving teams is not implemented yet."), false);
        return 0;
    }

    @Override
    public int deleteTeam(String teamName, ServerCommandSource source) {
        if (!(source.getEntity() instanceof ServerPlayerEntity)) {
            LOGGER.error("Only players can delete teams!");
            return 0;
        }
        source.sendFeedback(() -> Text.literal("Deleting teams is not implemented yet."), false);
        return 0;
    }

    @Override
    public int listTeam(String teamName, ServerCommandSource source) {
        Team team = RegionMapConfig.teams.stream().filter(t -> t.getName().equals(teamName)).findFirst().orElse(null);
        if (team == null) {
            source.sendFeedback(() -> Text.literal("Team with name " + teamName + " does not exist"), false);
            return 0;
        }
        source.sendFeedback(() -> Text.literal("Listing team " + teamName + "..."), false);

        source.sendFeedback(() -> Text.literal("Name: " + team.getDisplayName()), false);
        source.sendFeedback(() -> Text.literal("Owner: " + team.getOwner()), false);
        if(!team.getMembers().isEmpty()) {
            source.sendFeedback(() -> Text.literal("Members:"), false);
            team.getMembers().forEach(m -> source.sendFeedback(() -> Text.literal(m.toString()), false));
        } else {
            source.sendFeedback(() -> Text.literal("No members"), false);
        }
        return 1;
    }

    @Override
    public int listTeams(ServerCommandSource source) {
        source.sendFeedback(() -> Text.literal("Listing teams..."), false);
        RegionMapConfig.teams.forEach(team -> source.sendFeedback(() -> Text.literal(team.getName()), false));
        return 1;
    }
}
