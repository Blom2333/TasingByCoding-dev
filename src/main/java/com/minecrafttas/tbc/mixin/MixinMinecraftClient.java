package com.minecrafttas.tbc.mixin;

import com.minecrafttas.tbc.commands.PressCommand;
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
        //Minecraft.getInstance().player must not be null, just in case...
        if (!PressCommand.macroQueue.isEmpty() && !pause && Minecraft.getInstance().player != null) {
            rightClickDelay = 0;
            PressCommand.macroQueue.get(0).runScript(options);
        }
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void cancelKeyPressing(CallbackInfo ci) {
        if (PressCommand.macroQueue.isEmpty()) return;

        options.keyUp.setDown(false);
        options.keyRight.setDown(false);
        options.keyDown.setDown(false);
        options.keyLeft.setDown(false);
        options.keyShift.setDown(false);
        options.keySprint.setDown(false);
        options.keyJump.setDown(false);
        options.keyUse.setDown(false);
        options.keyPickItem.setDown(false);
        options.keyAttack.setDown(false);
        options.keySwapOffhand.setDown(false);
        options.keyDrop.setDown(false);
        options.keyInventory.setDown(false);

        if (PressCommand.macroQueue.get(0).getDuration() == 0) {
            for (String command : PressCommand.macroQueue.get(0).getRunningCommands()) {
                if (Minecraft.getInstance().player != null) {
                    Minecraft.getInstance().player.chat("/" + command);
                }
            }
            PressCommand.macroQueue.remove(0);
        }
    }
}
