package com.minecrafttas.tbc.mixin.rng.session;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

/**
 * {@code Particle.random} is the per-particle Random (lifetime, velocity, colour). It is initialized in
 * every constructor, hence the {@code <init>*} selector.
 */
@Mixin(Particle.class)
public class MixinParticleRand {
    @ModifyExpressionValue(method = "<init>*", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    public Random modifyRandom(Random original) {
        return RandomManager.create(RandomTypes.PARTICLE);
    }
}
