package dev.nevah5.nevexis.regionmap.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class RegionCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("region")
                .then(CommandManager.argument("name", StringArgumentType.string())
                        .executes(RegionCommand::execute)));
    }

    private static int execute(CommandContext<ServerCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        context.getSource().sendFeedback(() -> Text.literal("Hey " + name), false);
        return 1;
    }
}