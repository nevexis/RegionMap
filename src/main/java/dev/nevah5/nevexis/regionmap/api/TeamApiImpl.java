package dev.nevah5.nevexis.regionmap.api;

import dev.nevah5.nevexis.regionmap.model.Team;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class TeamApiImpl implements TeamApi {
    @Override
    public void createTeam(Team team, ServerCommandSource source) {
        source.sendFeedback(() -> Text.literal(team.getDisplayName() + " created"), false);
    }
}
