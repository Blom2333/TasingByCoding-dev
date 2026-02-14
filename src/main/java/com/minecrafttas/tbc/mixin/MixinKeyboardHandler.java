package com.minecrafttas.tbc.mixin;

import com.minecrafttas.tbc.core.KeyBindingMapper;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * Mixin the KeyboardHandler
 * Do with the custom keybindings while key pressing
 */
@Mixin(KeyboardHandler.class)
public class MixinKeyboardHandler {
    @Inject(method = "keyPress", at = @At(value = "HEAD"))
    private void handleCommandKeybind(long l, int i, int j, int k, int m, CallbackInfo ci) {
        if (Minecraft.getInstance().player != null) {
            for (Map.Entry<Integer, String> entry : KeyBindingMapper.getBOUND_KEYS().entrySet()) {
                if (i == entry.getKey() && k != 1) {
                    Minecraft.getInstance().player.chat("/" + entry.getValue());
                }
            }
        }
    }
}
