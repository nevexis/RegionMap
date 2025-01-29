package dev.nevah5.nevexis.regionmap.api;

import dev.nevah5.nevexis.regionmap.config.RegionMapConfig;
import dev.nevah5.nevexis.regionmap.model.Team;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static dev.nevah5.nevexis.regionmap.config.RegionMapConfig.setupConfigFile;

public class TeamApiImpl implements TeamApi {
    public static final String TEAM_DIRECTORY = "teams/";

    @Override
    public void createTeam(Team team, ServerCommandSource source) {
        if(RegionMapConfig.teams.stream().anyMatch(t -> t.getName().equals(team.getName()))) {
            source.sendFeedback(() -> Text.literal("Team with name " + team.getName() + " already exists"), false);
            return;
        }
        RegionMapConfig.teams.add(team);
        setupConfigFile(TEAM_DIRECTORY + team.getName() + ".json", team);
        source.sendFeedback(() -> Text.literal(team.getDisplayName() + " created"), false);
    }

    // TODO
}
