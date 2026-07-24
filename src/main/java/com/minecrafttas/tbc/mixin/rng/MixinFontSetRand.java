package com.minecrafttas.tbc.mixin.rng;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.GlyphRenderer;

@Mixin(FontStorage.class)
public abstract class MixinFontSetRand {
    @Unique private static final Random customRandom = new RandomManager();

    @ModifyExpressionValue(method = "getObfuscatedGlyphRenderer", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
    private int replaceRandom(int original) {
        return customRandom.nextInt();
    }
}
