package com.minecrafttas.tbc.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.minecrafttas.tbc.TasingByCoding;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.*;

public class KeyBindingMapper {
    public static final Map<Integer, ArrayList<String>> BOUND_KEYS = new HashMap<>();

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final File keybindingFile = FabricLoader.getInstance().getConfigDir().resolve(TasingByCoding.MOD_ID + "/keybindings.json").toFile();

    public static void addKeybinding(Integer key, String command) throws IOException {
        if (BOUND_KEYS.containsKey(key)) {
            BOUND_KEYS.get(key).add(command);
        } else {
            BOUND_KEYS.put(key, new ArrayList<>(Collections.singletonList(command)));
        }
        saveConfig();
    }

    public static void removeKeybinding(Integer key) throws IOException {
        KeyBindingMapper.BOUND_KEYS.remove(key);
        saveConfig();
    }

    private static void saveConfig() throws IOException {
        try (FileWriter writer = new FileWriter(keybindingFile)) {
            gson.toJson(BOUND_KEYS, writer);
        }
    }

    public static void loadConfig() throws IOException {
        if(!keybindingFile.exists()){
            Files.createDirectories(keybindingFile.toPath().getParent());
            return;
        }
        Type type = new TypeToken<Map<Integer, ArrayList<String>>>(){}.getType();
        BOUND_KEYS.putAll(gson.fromJson(new FileReader(keybindingFile), type));
    }
}