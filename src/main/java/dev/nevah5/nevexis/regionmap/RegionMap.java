package dev.nevah5.nevexis.regionmap;

import de.bluecolored.bluemap.api.BlueMapAPI;
import dev.nevah5.nevexis.regionmap.api.BlueMapApiImpl;
import dev.nevah5.nevexis.regionmap.command.RegionCommand;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RegionMap implements ModInitializer {
	public static final String MOD_ID = "regionmap";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final String REGION_MAP_CONFIG_DIRECTORY = "config/regionmap/";

	@Override
	public void onInitialize() {
		LOGGER.info("RegionMap by Nevah5 initialized!");

		Path configDirectory = Paths.get(REGION_MAP_CONFIG_DIRECTORY);
		try {
			if (Files.notExists(configDirectory)) {
				Files.createDirectories(configDirectory);
				LOGGER.info("Config directory created at: " + configDirectory.toAbsolutePath());
			}
		} catch (IOException ex) {
			LOGGER.error("Failed to create config directory", ex);
		}
		BlueMapApiImpl.setup();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			RegionCommand.register(dispatcher);
		});
	}
}
