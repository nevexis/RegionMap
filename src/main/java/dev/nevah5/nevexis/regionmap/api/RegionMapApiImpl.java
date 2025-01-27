package dev.nevah5.nevexis.regionmap.api;

import dev.nevah5.nevexis.regionmap.RegionMap;
import dev.nevah5.nevexis.regionmap.model.Chunk;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegionMapApiImpl implements RegionMapApi {
    public static final Logger LOGGER = LoggerFactory.getLogger(RegionMap.MOD_ID);
    private final WorldGuardApi worldGuardApi = new WorldGuardApiImpl();
    private final BlueMapApi blueMapApi = new BlueMapApiImpl();

    @Override
    public void claim(final Entity player, final ServerCommandSource source) {
        final Chunk chunk = Chunk.fromPlayerPos(player);
        blueMapApi.addRegion(player.getWorld(), chunk, player.getNameForScoreboard());
        source.sendFeedback(() -> Text.literal("Region claimed!"), false);
    }

    @Override
    public void remove(Entity player, ServerCommandSource source) {
        final Chunk chunk = Chunk.fromPlayerPos(player);
        blueMapApi.removeRegion(player.getWorld(), chunk, player.getNameForScoreboard());
        source.sendFeedback(() -> Text.literal("Region removed!"), false);
    }
}
