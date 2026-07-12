package com.minecrafttas.tbc.mixin.rng;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.GlobalRandom;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

@Mixin(FontSet.class)
public abstract class MixinFontSetRand {
    @Shadow public abstract BakedGlyph getGlyph(int i2);

    @Unique private static final Random customRandom = new GlobalRandom();

    @ModifyExpressionValue(method = "getRandomGlyph", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
    private int replaceRandom(int original) {
        return customRandom.nextInt();
    }
}
