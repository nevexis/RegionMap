package dev.nevah5.nevexis.regionmap.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.nevah5.nevexis.regionmap.RegionMap;
import dev.nevah5.nevexis.regionmap.api.BlueMapApiImpl;
import dev.nevah5.nevexis.regionmap.api.RegionMapApi;
import dev.nevah5.nevexis.regionmap.api.RegionMapApiImpl;
import dev.nevah5.nevexis.regionmap.api.TeamApi;
import dev.nevah5.nevexis.regionmap.api.TeamApiImpl;
import dev.nevah5.nevexis.regionmap.config.RegionMapConfig;
import dev.nevah5.nevexis.regionmap.model.Color;
import dev.nevah5.nevexis.regionmap.model.Team;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegionCommand {
    public static final Logger LOGGER = LoggerFactory.getLogger(RegionMap.MOD_ID);
    private static final RegionMapApi regionMapApi = new RegionMapApiImpl();
    private static final TeamApi teamApi = new TeamApiImpl();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        SuggestionProvider<ServerCommandSource> colorSuggestionProvider = (context, builder) -> {
            String[] colors = RegionMapConfig.colors.stream().map(Color::getName).toArray(String[]::new);
            return CommandSource.suggestMatching(colors, builder);
        };
        SuggestionProvider<ServerCommandSource> teamSuggestionProvider = (context, builder) -> {
            String[] teams = RegionMapConfig.teams.stream().map(Team::getName).toArray(String[]::new);
            return CommandSource.suggestMatching(teams, builder);
        };
        SuggestionProvider<ServerCommandSource> onlinePlayersProvider = (context, builder) -> {
            String[] players = context.getSource().getServer().getPlayerNames();
            return CommandSource.suggestMatching(players, builder);
        };
        dispatcher.register(CommandManager.literal("region")
                .then(CommandManager.literal("claim")
                        .then(CommandManager.argument("team", StringArgumentType.string())
                                .suggests(teamSuggestionProvider)
                                .executes(RegionCommand::claim)))
                .then(CommandManager.literal("merge")
                        .then(CommandManager.argument("name", StringArgumentType.greedyString())
                                .executes(RegionCommand::merge)))
                .then(CommandManager.literal("list")
                        .executes(RegionCommand::list))
                .then(CommandManager.literal("remove")
                        .executes(RegionCommand::remove))
                .then(CommandManager.literal("reload")
                        .executes(RegionCommand::reload)
                        .requires(source -> source.hasPermissionLevel(2)))
                .then(CommandManager.literal("team")
                        .then(CommandManager.literal("invite")
                                .then(CommandManager.argument("team", StringArgumentType.string())
                                        .suggests(teamSuggestionProvider)
                                        .then(CommandManager.argument("player", StringArgumentType.string())
                                                .suggests(onlinePlayersProvider)
                                                .executes(RegionCommand::teamInvite))))
                        .then(CommandManager.literal("kick")
                                .then(CommandManager.argument("team", StringArgumentType.string())
                                        .suggests(teamSuggestionProvider)
                                        .then(CommandManager.argument("player", StringArgumentType.string())
                                                .suggests(onlinePlayersProvider)
                                                .executes(RegionCommand::teamKick))))
                        .then(CommandManager.literal("create")
                                .then(CommandManager.argument("name", StringArgumentType.string())
                                        .then(CommandManager.argument("color", StringArgumentType.word())
                                                .suggests(colorSuggestionProvider)
                                                .then(CommandManager.argument("display", StringArgumentType.greedyString())
                                                        .executes(RegionCommand::teamAdd)))))
                        .then(CommandManager.literal("list")
                                .executes(RegionCommand::teamListAll)
                                .then(CommandManager.argument("team", StringArgumentType.string())
                                        .suggests(teamSuggestionProvider)
                                        .executes(RegionCommand::teamList)))
                        .then(CommandManager.literal("leave")
                                .then(CommandManager.argument("team", StringArgumentType.string())
                                        .suggests(teamSuggestionProvider)
                                        .executes(RegionCommand::teamLeave)))
                        .then(CommandManager.literal("delete")
                                .then(CommandManager.argument("team", StringArgumentType.string())
                                        .suggests(teamSuggestionProvider)
                                        .executes(RegionCommand::teamDelete)))
                ));
    }

    private static int claim(CommandContext<ServerCommandSource> context) {
        String teamName = StringArgumentType.getString(context, "team");
        return regionMapApi.claim(context.getSource().getEntity(), teamName, context.getSource());
    }

    private static int list(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(() -> Text.literal("Listing regions..."), false);
        return 1;
    }

    private static int merge(CommandContext<ServerCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        return regionMapApi.merge(context.getSource().getEntity(), name, context.getSource());
    }

    private static int remove(CommandContext<ServerCommandSource> context) {
        return regionMapApi.remove(context.getSource().getEntity(), context.getSource());
    }

    private static int reload(CommandContext<ServerCommandSource> context) {
        try {
            RegionMapConfig.init();
            BlueMapApiImpl.reloadMarkers();
            context.getSource().sendFeedback(() -> Text.literal("Region reloaded!"), false);
        } catch (Exception ex) {
            LOGGER.error("Failed to reload mod: " + ex.getMessage());
            context.getSource().sendFeedback(() -> Text.literal("Failed to reload!"), false);
        }
        return 1;
    }

    public static int teamAdd(CommandContext<ServerCommandSource> context) {
        String team = StringArgumentType.getString(context, "name");
        String color = StringArgumentType.getString(context, "color");
        String display = StringArgumentType.getString(context, "display");
        return teamApi.createTeam(team, color, display, context.getSource());
    }

    public static int teamList(CommandContext<ServerCommandSource> context) {
        String teamName = StringArgumentType.getString(context, "team");
        return teamApi.listTeam(teamName, context.getSource());
    }

    public static int teamListAll(CommandContext<ServerCommandSource> context) {
        return teamApi.listTeams(context.getSource());
    }

    public static int teamLeave(CommandContext<ServerCommandSource> context) {
        String teamName = StringArgumentType.getString(context, "team");
        return teamApi.leaveTeam(teamName, context.getSource());
    }

    public static int teamDelete(CommandContext<ServerCommandSource> context) {
        String teamName = StringArgumentType.getString(context, "team");
        return teamApi.deleteTeam(teamName, context.getSource());
    }

    public static int teamInvite(CommandContext<ServerCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        String teamName = StringArgumentType.getString(context, "team");
        return teamApi.invitePlayer(playerName, teamName, context.getSource());
    }

    public static int teamKick(CommandContext<ServerCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        String teamName = StringArgumentType.getString(context, "team");
        return teamApi.kickPlayer(playerName, teamName, context.getSource());
    }
}
