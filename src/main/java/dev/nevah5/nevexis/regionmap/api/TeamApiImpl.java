package dev.nevah5.nevexis.regionmap.api;

import dev.nevah5.nevexis.regionmap.config.RegionMapConfig;
import dev.nevah5.nevexis.regionmap.model.Team;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import javax.swing.text.html.parser.Entity;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static dev.nevah5.nevexis.regionmap.config.RegionMapConfig.LOGGER;
import static dev.nevah5.nevexis.regionmap.config.RegionMapConfig.setupConfigFile;

public class TeamApiImpl implements TeamApi {
    public static final String TEAM_DIRECTORY = "teams/";

    @Override
    public int createTeam(String teamName, String color, String display, ServerCommandSource source) {

        if (!(source.getEntity() instanceof ServerPlayerEntity)) {
            LOGGER.error("Only players can create teams!");
            return 0;
        }

        try {
            Team team = Team.builder()
                    .color(RegionMapConfig.colors.stream().filter(c -> c.getName().equals(color)).findFirst().orElse(null))
                    .displayName(display)
                    .name(teamName)
                    .owner(Objects.requireNonNull(source.getPlayer()).getUuid())
                    .build();
            if (RegionMapConfig.teams.stream().anyMatch(t -> t.getName().equals(team.getName()))) {
                source.sendFeedback(() -> Text.literal("Team with name " + team.getName() + " already exists"), false);
                return 0;
            }
            RegionMapConfig.teams.add(team);
            setupConfigFile(TEAM_DIRECTORY + team.getName() + ".json", team);
            source.sendFeedback(() -> Text.literal(team.getName() + " created"), false);
            return 1;
        } catch (IllegalArgumentException e) {
            source.sendFeedback(() -> Text.literal("Failed to create team: " + e.getMessage()), false);
            return 0;
        }
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
        Team team = RegionMapConfig.teams.stream().filter(t -> t.getName().equals(teamName)).findFirst().orElse(null);
        if (team == null) {
            source.sendFeedback(() -> Text.literal("Team with name " + teamName + " does not exist"), false);
            return 0;
        }
        if (!team.getOwner().equals(source.getEntity().getUuid())) {
            source.sendFeedback(() -> Text.literal("You are not the owner of this team!"), false);
            return 0;
        }
        RegionMapConfig.teams.remove(team);
        RegionMapConfig.deleteConfigFile(TEAM_DIRECTORY + teamName + ".json");
        source.sendFeedback(() -> Text.literal("Deleted team " + teamName + "!"), false);
        return 1;

        // TODO: Implement deleting all regions claimed by the team
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
        if (!team.getMembers().isEmpty()) {
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

    @Override
    public int invitePlayer(String playerName, String teamName, ServerCommandSource source) {
        if (!(source.getEntity() instanceof ServerPlayerEntity)) {
            LOGGER.error("Only players can invite players to teams!");
            return 0;
        }
        Team team = RegionMapConfig.teams.stream().filter(t -> t.getName().equals(teamName)).findFirst().orElse(null);
        if (team == null) {
            source.sendFeedback(() -> Text.literal("Team with name " + teamName + " does not exist"), false);
            return 0;
        }
        if (!team.getOwner().equals(source.getEntity().getUuid())) {
            source.sendFeedback(() -> Text.literal("You are not the owner of this team!"), false);
            return 0;
        }
        Optional<ServerPlayerEntity> invitedPlayer = Optional.ofNullable(source.getServer().getPlayerManager().getPlayer(playerName));
        if (invitedPlayer.isEmpty()) {
            source.sendFeedback(() -> Text.literal("Player with name " + playerName + " does not exist or is not online"), false);
            return 0;
        }
        if (team.getOwner().equals(invitedPlayer.get().getUuid())) {
            source.sendFeedback(() -> Text.literal("You cannot invite yourself to your own team!"), false);
            return 0;
        }
        if (team.getMembers().contains(invitedPlayer.get().getUuid())) {
            source.sendFeedback(() -> Text.literal("The player is already a member of this team!"), false);
            return 0;
        }
        team.addMember(invitedPlayer.get().getUuid());
        source.sendFeedback(() -> Text.literal("Player " + playerName + " added to team " + teamName), false);
        RegionMapConfig.writeConfigFile(TEAM_DIRECTORY + teamName + ".json", team);
        return 1;
    }

    @Override
    public int kickPlayer(String playerName, String teamName, ServerCommandSource source) {
        if (!(source.getEntity() instanceof ServerPlayerEntity)) {
            LOGGER.error("Only players can kick players from teams!");
            return 0;
        }
        Team team = RegionMapConfig.teams.stream().filter(t -> t.getName().equals(teamName)).findFirst().orElse(null);
        if (team == null) {
            source.sendFeedback(() -> Text.literal("Team with name " + teamName + " does not exist"), false);
            return 0;
        }
        if (!team.getOwner().equals(source.getEntity().getUuid())) {
            source.sendFeedback(() -> Text.literal("You are not the owner of this team!"), false);
            return 0;
        }
        Optional<ServerPlayerEntity> kickedPlayer = Optional.ofNullable(source.getServer().getPlayerManager().getPlayer(playerName));
        if (kickedPlayer.isEmpty()) {
            source.sendFeedback(() -> Text.literal("Player with name " + playerName + " does not exist or is not online"), false);
            return 0;
        }
        if (!team.getMembers().contains(kickedPlayer.get().getUuid())) {
            source.sendFeedback(() -> Text.literal("The player is not a member of this team!"), false);
            return 0;
        }
        team.removeMember(kickedPlayer.get().getUuid());
        source.sendFeedback(() -> Text.literal("Player " + playerName + " removed from team " + teamName), false);
        RegionMapConfig.writeConfigFile(TEAM_DIRECTORY + teamName + ".json", team);
        return 1;
    }
}
