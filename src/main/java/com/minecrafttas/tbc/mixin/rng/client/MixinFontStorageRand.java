package com.minecrafttas.tbc.mixin.rng.client;

import com.minecrafttas.tbc.rng.RandomManager;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;
import net.minecraft.client.font.FontStorage;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FontStorage.class)
public abstract class MixinFontStorageRand {
    @Shadow @Mutable @Final private static Random RANDOM;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void replaceRandom(CallbackInfo ci) {
        RANDOM = new RandomManager();
    }
}
