package com.minecrafttas.tbc.mixin.rng.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

/**
 * {@code EnderDragonSpawnState.SUMMONING_PILLARS} regenerates the obsidian pillars
 * with an unseeded Random. Since every enum constant has its own class body, that
 * code lives in the anonymous class {@code EnderDragonSpawnState$3}, which is why
 * this mixin has to target it by name. {@code run} is the same method as
 * {@code EnderDragonSpawnState.run} (intermediary {@code method_12507}).
 */
@Mixin(targets = "net.minecraft.entity.boss.dragon.EnderDragonSpawnState$3")
public class MixinEnderDragonSpawnStateRand {
    @ModifyExpressionValue(method = "run", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    public Random modifyRandom(Random original) {
        return new RandomManager();
    }
}
