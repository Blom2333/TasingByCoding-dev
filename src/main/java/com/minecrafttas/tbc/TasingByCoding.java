package com.minecrafttas.tbc;

import com.minecrafttas.tbc.util.KeyBindingMapper;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class TasingByCoding implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("TasingByCoding");
    public static final String MOD_ID = "tbc";

    @Override
    public void onInitialize() {
        LOGGER.info("[TasingByCoding] Initializing TasingByCoding...");
        try {
            KeyBindingMapper.loadConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
