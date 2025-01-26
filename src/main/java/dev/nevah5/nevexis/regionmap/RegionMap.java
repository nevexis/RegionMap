package dev.nevah5.nevexis.regionmap;

import de.bluecolored.bluemap.api.BlueMapAPI;
import dev.nevah5.nevexis.regionmap.command.RegionCommand;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegionMap implements ModInitializer {
	public static final String MOD_ID = "regionmap";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("RegionMap by Nevah5 initialized!");


		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			RegionCommand.register(dispatcher);
		});
	}
}