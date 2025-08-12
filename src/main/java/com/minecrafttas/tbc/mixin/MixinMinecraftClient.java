package com.minecrafttas.tbc.mixin;

import com.minecrafttas.tbc.commands.PressCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Minecraft.class)
public class MixinMinecraftClient {
    @Shadow private int rightClickDelay;
    @Shadow private boolean pause;
    @Shadow @Final public Options options;

    @Inject(method = "handleKeybinds", at = @At(value = "HEAD"))
    private void injectKeybinds(CallbackInfo ci) {

        //Minecraft.getInstance().player must not be null, just in case...
        if (PressCommand.index > 0 && !pause && Minecraft.getInstance().player != null) {
            rightClickDelay = 0;
            List<String> currentKeys = PressCommand.pressingQueue.get(0);
            for (String key : currentKeys) {
                switch (key) {
                    case "w": options.keyUp.setDown(true); break;
                    case "d": options.keyRight.setDown(true); break;
                    case "s": options.keyDown.setDown(true); break;
                    case "a": options.keyLeft.setDown(true); break;
                    case "S": options.keyShift.setDown(true); break;
                    case "R": options.keySprint.setDown(true); break;
                    case "J": options.keyJump.setDown(true); break;
                    case "P": options.keyUse.setDown(true); break;
                    //case "O": Minecraft.getInstance().options.keyPickItem.setDown(true); break;
                    //case "I": Minecraft.getInstance().options.keyAttack.setDown(true); break;
                    //case "f": Minecraft.getInstance().options.keySwapOffhand.setDown(true); break;
                }
            }
            PressCommand.pressingQueue.remove(0);

            Player player = Minecraft.getInstance().player;
            List<Float> currentRot = PressCommand.rotQueue.get(0);
            if (!currentRot.isEmpty()) player.moveTo(player.getX(), player.getY(), player.getZ(), currentRot.get(1), currentRot.get(0));
            PressCommand.rotQueue.remove(0);
        }
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void cancelKeyPressing(CallbackInfo ci) {
        if (PressCommand.index > 0) {
            Minecraft.getInstance().options.keyUp.setDown(false);
            Minecraft.getInstance().options.keyRight.setDown(false);
            Minecraft.getInstance().options.keyDown.setDown(false);
            Minecraft.getInstance().options.keyLeft.setDown(false);
            Minecraft.getInstance().options.keyShift.setDown(false);
            Minecraft.getInstance().options.keySprint.setDown(false);
            Minecraft.getInstance().options.keyJump.setDown(false);
            Minecraft.getInstance().options.keyUse.setDown(false);
            PressCommand.index--;
        }
    }
}
