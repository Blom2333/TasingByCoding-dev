package com.minecrafttas.tbc.mixin;

import com.minecrafttas.tbc.util.KeyBindingMapper;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Map;

/**
 * Mixin the KeyboardHandler
 * Do with the custom keybindings while key pressing
 */
@Mixin(KeyboardHandler.class)
public class MixinKeyboardHandler {
    @Inject(method = "keyPress", at = @At(value = "HEAD"))
    private void handleCommandKeybind(long l, int i, int j, int k, int m, CallbackInfo ci) {
        if (Minecraft.getInstance().player == null) return;
        if (Minecraft.getInstance().screen instanceof  ChatScreen) return;  // optimization needed, for now it's just banned chat screen
        for (Map.Entry<Integer, ArrayList<String>> entry : KeyBindingMapper.BOUND_KEYS.entrySet()) {
            if (i != entry.getKey() || k == 1) continue;
            for (String command : entry.getValue()) {
                Minecraft.getInstance().player.chat("/" + command);
            }
        }
    }
}
