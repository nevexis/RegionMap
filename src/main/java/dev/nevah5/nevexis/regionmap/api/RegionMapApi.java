package dev.nevah5.nevexis.regionmap.api;

import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;

public interface RegionMapApi {
    int claim(Entity player, String teamName, ServerCommandSource source);

    int merge(Entity player, String name, ServerCommandSource source);

    int remove(Entity player, ServerCommandSource source);
}
