package com.minecrafttas.tbc.mixin.rng.stable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;
import net.minecraft.world.World;

/**
 * {@code World.random} is the per-world Random behind block drops, block sounds, entity spawning and most
 * other world randomness. The same constructor also initializes {@code lcgBlockSeed} with an unseeded
 * Random for random ticks, so this handler runs twice:
 * index 0 = {@code lcgBlockSeed}, index 1 = {@code random}.
 */
@Mixin(World.class)
public class MixinWorldRand {
    @ModifyExpressionValue(method = "<init>", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    public Random modifyEntityRandom(Random original) {
        return RandomManager.create(RandomTypes.WORLD);
    }
}