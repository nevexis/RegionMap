package dev.nevah5.nevexis.regionmap.api;

import dev.nevah5.nevexis.regionmap.RegionMap;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegionMapApiImpl implements RegionMapApi {
    public static final Logger LOGGER = LoggerFactory.getLogger(RegionMap.MOD_ID);
    private final WorldGuardApi worldGuardApi = new WorldGuardApiImpl();
    private final BlueMapApi blueMapApi = new BlueMapApiImpl();

    @Override
    public void claim(final Entity player, final ServerCommandSource source) {
        ChunkPos chunkPos = getChunkPos(player);

        blueMapApi.addRegion(player.getWorld(), chunkPos, player.getNameForScoreboard());

        source.sendFeedback(() -> Text.literal("Region claimed!"), false);
    }

    private final ChunkPos getChunkPos(final Entity player) {
        Vec3d position = player.getPos();
        return new ChunkPos(new BlockPos((int) position.x, (int) position.y, (int) position.z));
    }
}
