package com.minecrafttas.tbc.util.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Path BASE_DIR = FabricLoader.getInstance().getConfigDir();
    private final Gson gson;

    public ConfigManager(String resolvePath) {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        // Make sure the dir exists
        try {
            Files.createDirectories(BASE_DIR);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create configuration dir: " + BASE_DIR, e);
        }
    }
//
//    /**
//     * Create the directory if doesn't exist.
//     * @throws IOException
//     */
//    public static void createConfigIfNonExistent(File configFile) throws IOException {
//        if(configFile.exists()){
//            Files.createDirectories(configFile.toPath());
//        }
//    }
//
//    public void saveConfig(File keybindingFile, Map map) throws IOException {
//        // Print
//        try (FileWriter writer = new FileWriter(keybindingFile)) {
//            ConfigManager.gson.toJson(map, writer);
//        }
//    }
//
//    public abstract void saveConfig() throws IOException;
//
//    /**
//     * Load config that has been in config already.
//     */
//    public abstract void loadConfig() throws FileNotFoundException;
//
//    /**
//     * Things you should do on game's initialize like loading information.
//     * Mainly called in the main class.
//     * @see TasingByCoding#onInitialize()
//     */
//    public abstract void init() throws IOException;
}
