package com.minecrafttas.tbc.core;

import com.mojang.blaze3d.platform.InputConstants;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class KeyBindingMapper {
    @Getter private static final Map<Integer, String> BOUND_KEYS = new HashMap<>();

    public static void addKeybinding(String key, String command) {
        BOUND_KEYS.put(InputConstants.getKey(key).getValue(), command);
    }
}
