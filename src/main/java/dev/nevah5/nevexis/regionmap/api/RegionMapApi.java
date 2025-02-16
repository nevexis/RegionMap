package dev.nevah5.nevexis.regionmap.api;

import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;

public interface RegionMapApi {
    int createMarker(Entity player, String name, ServerCommandSource source);

    int removeMarker(Entity player, String name, ServerCommandSource source);

    int claim(Entity player, String teamName, ServerCommandSource source);

    int merge(Entity player, String name, ServerCommandSource source);

    int unmerge(Entity player, ServerCommandSource source);

    int remove(Entity player, ServerCommandSource source);
}
