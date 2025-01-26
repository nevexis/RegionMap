package dev.nevah5.nevexis.regionmap.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.nevah5.nevexis.regionmap.api.BlueMapApi;
import dev.nevah5.nevexis.regionmap.api.BlueMapApiImpl;
import dev.nevah5.nevexis.regionmap.api.WorldGuardApi;
import dev.nevah5.nevexis.regionmap.api.WorldGuardApiImpl;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class RegionCommand {
    private static final WorldGuardApi worldGuardApi = new WorldGuardApiImpl();
    private static final BlueMapApi blueMapApi = new BlueMapApiImpl();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("region")
                .then(CommandManager.literal("claim")
                        .executes(RegionCommand::claim))
                .then(CommandManager.literal("list")
                        .executes(RegionCommand::list))
                .then(CommandManager.literal("remove")
                        .executes(RegionCommand::remove))
                .then(CommandManager.literal("reload")
                        .executes(RegionCommand::reload)
                        .requires(source -> source.hasPermissionLevel(2)))
                .then(CommandManager.literal("name")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .executes(RegionCommand::name))));
    }

    private static int claim(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(() -> Text.literal("Region claimed!"), false);
        return 1;
    }

    private static int list(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(() -> Text.literal("Listing regions..."), false);
        return 1;
    }

    private static int remove(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(() -> Text.literal("Region removed!"), false);
        return 1;
    }

    private static int name(CommandContext<ServerCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        context.getSource().sendFeedback(() -> Text.literal("Region named: " + name), false);
        return 1;
    }

    private static int reload(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(() -> Text.literal("Region reloaded!"), false);
        return 1;
    }
}
