package dev.nevah5.nevexis.regionmap;

import dev.nevah5.nevexis.regionmap.command.RegionCommand;
import dev.nevah5.nevexis.regionmap.config.RegionMapConfig;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegionMap implements ModInitializer {
	public static final String MOD_ID = "regionmap";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("RegionMap by Nevah5 initialized!");

		RegionMapConfig.init();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			RegionCommand.register(dispatcher);
		});
	}
}
