package com.minecrafttas.tbc.mixin.rng.volatile_;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

/**
 * {@code EnderDragonSpawnState.SUMMONING_PILLARS} regenerates the obsidian pillars
 * with an unseeded Random. Since every enum constant has its own class body, that
 * code lives in the anonymous class {@code EnderDragonSpawnState$3}, which is why
 * this mixin has to target it by name. {@code run} is the same method as
 * {@code EnderDragonSpawnState.run} (intermediary {@code method_12507}).
 *
 * <p>The site is VOLATILE, so every pillar regeneration shares the one instance of the site
 * ({@link RandomManager#site(RandomTypes)}) instead of getting a fresh unseeded Random.
 */
@Mixin(targets = "net.minecraft.entity.boss.dragon.EnderDragonSpawnState$3")
public class MixinEnderDragonSpawnStateRand {
    @ModifyExpressionValue(method = "run", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    public Random modifyRandom(Random original) {
        return RandomManager.site(RandomTypes.ENDER_DRAGON_SPAWN_PILLARS);
    }
}
