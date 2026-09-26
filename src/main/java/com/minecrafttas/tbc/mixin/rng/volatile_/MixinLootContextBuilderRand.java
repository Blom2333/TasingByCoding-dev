package com.minecrafttas.tbc.mixin.rng.volatile_;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import net.minecraft.loot.context.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

/**
 * When a loot table is rolled without a seed ({@code LootContext.Builder#random(long)}
 * only sets a Random for non zero seeds), {@code build} falls back to
 * {@code new Random()} - which is the only unseeded Random of the loot system.
 *
 * <p>The fallback is VOLATILE, so every context without a seed draws from the one shared instance of the
 * site ({@link RandomManager#site(RandomTypes)}).
 */
@Mixin(LootContext.Builder.class)
public class MixinLootContextBuilderRand {
    @ModifyExpressionValue(method = "build", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    public Random modifyRandom(Random original) {
        return RandomManager.site(RandomTypes.LOOT_CONTEXT);
    }
}
