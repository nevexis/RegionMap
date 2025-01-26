package dev.nevah5.nevexis.regionmap.api;

import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;

public interface RegionMapApi {
    void claim(Entity player, ServerCommandSource source);
}
