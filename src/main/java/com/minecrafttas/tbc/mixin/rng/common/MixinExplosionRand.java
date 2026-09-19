package com.minecrafttas.tbc.mixin.rng.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

/**
 * {@code Explosion.random} rolls the "intentional game design" random fire
 * ({@code random.nextInt(3) != 0}) and the small random offsets an explosion uses.
 */
@Mixin(Explosion.class)
public class MixinExplosionRand {
    @ModifyExpressionValue(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionBehavior;DDDFZLnet/minecraft/world/explosion/Explosion$DestructionType;)V", at = @At(value = "NEW", target = "()Ljava/util/Random;"))
    public Random modifyEntityRandom(Random original) {
        return RandomManager.create(RandomTypes.EXPLOSION);
    }
}