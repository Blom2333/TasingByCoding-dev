package com.minecrafttas.tbc.mixin.rng.stable;

import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;
import net.minecraft.client.font.FontStorage;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * {@code FontStorage.RANDOM} picks the random glyph used by the obfuscated text effect.
 */
@Mixin(FontStorage.class)
public abstract class MixinFontStorageRand {
    @Shadow @Mutable @Final private static Random RANDOM;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void replaceRandom(CallbackInfo ci) {
        RANDOM = RandomManager.create(RandomTypes.FONT_STORAGE);
    }
}
