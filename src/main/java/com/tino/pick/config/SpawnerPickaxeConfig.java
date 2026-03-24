package com.tino.pick.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tino.pick.SpawnerPickaxe;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SpawnerPickaxeConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(),
            SpawnerPickaxe.MOD_ID + ".json");

    // Default configuration values
    public String recipeIngredient = "minecraft:dragon_egg";
    public int pickaxeDamage = 16;

    private static SpawnerPickaxeConfig instance;

    public static SpawnerPickaxeConfig get() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    public static SpawnerPickaxeConfig load() {
        SpawnerPickaxeConfig config = new SpawnerPickaxeConfig();
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                config = GSON.fromJson(reader, SpawnerPickaxeConfig.class);
            } catch (IOException e) {
                SpawnerPickaxe.LOGGER.error("Failed to load config file", e);
            }
        } else {
            save(config);
        }
        return config;
    }

    private static void save(SpawnerPickaxeConfig config) {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            SpawnerPickaxe.LOGGER.error("Failed to save config file", e);
        }
    }
}
