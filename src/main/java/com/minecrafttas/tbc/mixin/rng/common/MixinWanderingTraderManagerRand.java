package com.minecrafttas.tbc.mixin.rng.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import net.minecraft.world.WanderingTraderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

/**
 * {@code WanderingTraderManager.random} decides whether the wandering trader spawns, where it spawns
 * and how long it stays.
 */
@Mixin(WanderingTraderManager.class)
public class MixinWanderingTraderManagerRand {
    @ModifyExpressionValue(method = "<init>*", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    public Random modifyEntityRandom(Random original) {
        return RandomManager.create(RandomTypes.WANDERING_TRADER_MANAGER);
    }
}