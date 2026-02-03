package com.minecrafttas.tbc.mixin;

import com.minecrafttas.tbc.commands.PressCommand;
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

    @Inject(method = "handleKeybinds", at = @At(value = "HEAD"))
    private void injectKeybinds(CallbackInfo ci) {
        if (!PressCommand.macroQueue.isEmpty() && !pause && Minecraft.getInstance().player != null) {
            rightClickDelay = 0;
            PressCommand.macroQueue.get(0).runScript(options);
        }
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void cancelKeyPressing(CallbackInfo ci) {
        if (PressCommand.macroQueue.isEmpty() || Minecraft.getInstance().player == null) return;

        for (KeyMapping key : options.keyMappings) key.setDown(false);
        Minecraft.getInstance().player.closeContainer();

        if (PressCommand.macroQueue.get(0).getDuration() == 0) {
            for (String command : PressCommand.macroQueue.get(0).getRunningCommands()) {
                Minecraft.getInstance().player.chat("/" + command);
            }
            PressCommand.macroQueue.remove(0);
        }
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void closeGui(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen == null || mc.screen.isPauseScreen() || PressCommand.macroQueue.isEmpty()) return;
        mc.player.closeContainer();
    }
}
