package dev.nevah5.nevexis.regionmap.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.nevah5.nevexis.regionmap.RegionMap;
import dev.nevah5.nevexis.regionmap.api.BlueMapApiImpl;
import dev.nevah5.nevexis.regionmap.api.TeamApiImpl;
import dev.nevah5.nevexis.regionmap.model.ClaimedRegion;
import dev.nevah5.nevexis.regionmap.model.Color;
import dev.nevah5.nevexis.regionmap.model.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RegionMapConfig {
    public static final Logger LOGGER = LoggerFactory.getLogger(RegionMap.MOD_ID);
    public static final String REGION_MAP_CONFIG_DIRECTORY = "config/regionmap/";

    public static List<Color> colors = new ArrayList<>();
    public static List<Team> teams = new ArrayList<>();
    public static List<ClaimedRegion> regions = new ArrayList<>();

    public static void init() {
        setupConfigDirectory("");
        setupConfigDirectory(BlueMapApiImpl.REGION_DIRECTORY);
        setupConfigDirectory(TeamApiImpl.TEAM_DIRECTORY);
        setupConfigFile("colors.json", Color.getDefaultConfig());

        loadData();

        LOGGER.info("Loaded config files.");
    }

    private static void loadData() {
        Type colorsType = new TypeToken<List<Color>>() {}.getType();
        colors = readConfigFile("colors.json", colorsType);

        Type teamsType = new TypeToken<List<Team>>() {}.getType();
        teams = readConfigFiles(TeamApiImpl.TEAM_DIRECTORY, teamsType);
    }

    public static <T> void setupConfigFile(String name, T data) {
        Path configFile = Paths.get(REGION_MAP_CONFIG_DIRECTORY, name);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(data);

        try {
            if (Files.notExists(configFile)) {
                Files.createFile(configFile);
                LOGGER.info("Config file created at: " + configFile.toAbsolutePath());

                try (BufferedWriter writer = Files.newBufferedWriter(configFile, StandardCharsets.UTF_8)) {
                    writer.write(json);
                    LOGGER.info("Data written to config file: " + configFile.toAbsolutePath());
                }
            }
        } catch (IOException ex) {
            LOGGER.error("Failed to create or write to config file: " + configFile.toAbsolutePath(), ex);
        }
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


    public static <T> T readConfigFile(String filename, Type type) {
        Path configFile = Paths.get(REGION_MAP_CONFIG_DIRECTORY, filename);
        Gson gson = new Gson();

        try {
            if (Files.exists(configFile)) {
                String json = new String(Files.readAllBytes(configFile), StandardCharsets.UTF_8);
                T data = gson.fromJson(json, type);
                LOGGER.info("Data read from config file: " + configFile.toAbsolutePath());
                return data;
            } else {
                LOGGER.error("Config file does not exist: " + configFile.toAbsolutePath());
            }
        } catch (IOException ex) {
            LOGGER.error("Failed to read config file: " + configFile.toAbsolutePath(), ex);
        }
        return null;
    }


    public static <T> List<T> readConfigFiles(String directory, Type type) {
        Path configDirectory = Paths.get(REGION_MAP_CONFIG_DIRECTORY + directory);
        Gson gson = new Gson();
        List<T> data = new ArrayList<>();

        try {
            if (Files.exists(configDirectory)) {
                Files.list(configDirectory).forEach(file -> {
                    try {
                        String json = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
                        T fileData = gson.fromJson(json, type);
                        data.add(fileData);
                        LOGGER.info("Data read from config file: " + file.toAbsolutePath());
                    } catch (IOException ex) {
                        LOGGER.error("Failed to read config file: " + file.toAbsolutePath(), ex);
                    }
                });
            } else {
                LOGGER.error("Config directory does not exist: " + configDirectory.toAbsolutePath());
            }
        } catch (IOException ex) {
            LOGGER.error("Failed to read config directory: " + configDirectory.toAbsolutePath(), ex);
        }
        return data;
    }
}
