package com.minecrafttas.tbc.mixin.settings;

import com.minecrafttas.tbc.commands.TasBindCommand;
import com.mojang.blaze3d.platform.InputConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InputConstants.Type.class)
public class MixinInputConstants {

    @Inject(method = "addKey", at = @At("TAIL"))
    private static void addKeyToCommand(InputConstants.Type type, String string, int i, CallbackInfo ci) {
        TasBindCommand.registerBind(string);
    }
}
