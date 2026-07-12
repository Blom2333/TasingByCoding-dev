package com.minecrafttas.tbc.mixin.oper;

import com.minecrafttas.tbc.macro.GuiMacros;
import com.minecrafttas.tbc.macro.OperMacros;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraftClient {
    @Shadow private int rightClickDelay;
    @Shadow private boolean pause;
    @Shadow @Final public Options options;
    @Shadow private static Minecraft instance;

    @Inject(method = "handleKeybinds", at = @At(value = "HEAD"))
    private void injectKeybinds(CallbackInfo ci) {
        if (!OperMacros.macroQueue.isEmpty() && !pause && instance.player != null) {
            rightClickDelay = 0;
            OperMacros.macroQueue.get(0).runScript();
        }
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void cancelKeyPressing(CallbackInfo ci) {
        if (OperMacros.macroQueue.isEmpty() || instance.player == null) return;

        KeyMapping.releaseAll();
        OperMacros.macroQueue.get(0).onMacroEnd();
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void closeGui(CallbackInfo ci) {
        if (OperMacros.macroQueue.isEmpty()) return;
        if (instance.player == null || instance.screen == null || instance.screen.isPauseScreen()) return;

        GuiMacros.closeDelay++;
        if (GuiMacros.closeDelay > GuiMacros.defaultDelay) {
            GuiMacros.runAllMacros(instance); // Force to clear all macros
            instance.player.closeContainer();
            GuiMacros.closeDelay = 0;
        }
    }

    @Inject(method = "runTick", at = @At(value = "HEAD"))
    private void renderInterpolation(boolean bl, CallbackInfo ci) {
        if (instance.screen != null) {
            if (!OperMacros.macroQueue.isEmpty() && !GuiMacros.macroQueue.isEmpty()) {
                GuiMacros.whenGuiOpen(instance);
            }
        }
    }
}