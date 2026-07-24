package com.minecrafttas.tbc.mixin.oper;

import com.minecrafttas.tbc.util.KeyBindingMapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Map;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;

/**
 * Mixin the KeyboardHandler
 * Do with the custom keybindings while key pressing
 */
@Mixin(Keyboard.class)
public class MixinKeyboardHandler {
    @Inject(method = "onKey", at = @At(value = "HEAD"))
    private void handleCommandKeybind(long window, int key, int scancode, int i, int j, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player == null) return;
        if (MinecraftClient.getInstance().currentScreen instanceof ChatScreen) return;  // optimization needed, for now it's just banned chat screen
        for (Map.Entry<Integer, ArrayList<String>> entry : KeyBindingMapper.BOUND_KEYS.entrySet()) {
            if (key != entry.getKey() || i == 1) continue;
            for (String command : entry.getValue()) {
                MinecraftClient.getInstance().player.sendChatMessage("/" + command);
            }
        }
    }
}
