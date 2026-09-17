package com.minecrafttas.tbc.mixin.rng;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

/**
 * The "Minceraft" easter egg rolls a fresh Random every time the title screen is opened.
 * TitleScreen has two constructors, so the target is pinned down with its descriptor.
 */
@Mixin(TitleScreen.class)
public class MixinTitleScreenRand {
    @ModifyExpressionValue(method = "<init>(Z)V", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    public Random modifyRandom(Random original) {
        return new RandomManager();
    }
}
