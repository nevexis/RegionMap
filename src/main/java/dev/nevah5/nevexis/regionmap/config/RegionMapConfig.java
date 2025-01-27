package dev.nevah5.nevexis.regionmap.config;

import dev.nevah5.nevexis.regionmap.RegionMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RegionMapConfig {
    public static final Logger LOGGER = LoggerFactory.getLogger(RegionMap.MOD_ID);
    public static final String REGION_MAP_CONFIG_DIRECTORY = "config/regionmap/";

    public static void init() {
        setupConfigDirectory("");
    }

    public static void setupConfigDirectory(final String directoryName) {
        Path configDirectory = Paths.get(REGION_MAP_CONFIG_DIRECTORY + directoryName);
        try {
            if (Files.notExists(configDirectory)) {
                Files.createDirectories(configDirectory);
                LOGGER.info("Config directory created at: " + configDirectory.toAbsolutePath());
            }
        } catch (IOException ex) {
            LOGGER.error("Failed to create config directory", ex);
        }
    }
}
