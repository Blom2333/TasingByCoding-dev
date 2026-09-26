package com.minecrafttas.tbc.mixin.rng.stable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

/**
 * {@code EnchantmentScreen.random} shakes the book on the enchanting screen
 * (the {@code approximatePageAngle} jitter). Vanilla creates it as a field of the screen, so the site owns
 * one instance per screen and is STABLE, like {@link RandomTypes#TITLE_SCREEN}.
 */
@Mixin(EnchantmentScreen.class)
public class MixinEnchantmentScreenRand {
    @ModifyExpressionValue(method = "<init>", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    public Random modifyRandom(Random original) {
        return RandomManager.create(RandomTypes.ENCHANTMENT_SCREEN);
    }
}
