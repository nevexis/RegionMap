package dev.nevah5.nevexis.regionmap.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.nevah5.nevexis.regionmap.RegionMap;
import dev.nevah5.nevexis.regionmap.api.RegionMapApi;
import dev.nevah5.nevexis.regionmap.api.RegionMapApiImpl;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegionCommand {
    public static final Logger LOGGER = LoggerFactory.getLogger(RegionMap.MOD_ID);
    private static final RegionMapApi regionMapApi = new RegionMapApiImpl();

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
        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity)) {
            LOGGER.error("Only players can claim regions!");
            return 0;
        }
        regionMapApi.claim(context.getSource().getEntity(), context.getSource());
        return 1;
    }

    private static int list(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(() -> Text.literal("Listing regions..."), false);
        return 1;
    }

    private static int remove(CommandContext<ServerCommandSource> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity)) {
            LOGGER.error("Only players can remove regions!");
            return 0;
        }
        regionMapApi.remove(context.getSource().getEntity(), context.getSource());
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
