package com.minecrafttas.tbc.mixin.rng.client;

import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import net.minecraft.client.particle.SpellParticle;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

/**
 * {@code SpellParticle.RANDOM} spreads the initial velocity of effect particles.
 */
@Mixin(SpellParticle.class)
public class MixinSpellParticleRand {
    @Shadow @Mutable @Final private static Random RANDOM;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void replaceRandom(CallbackInfo ci) {
        RANDOM = RandomManager.create(RandomTypes.SPELL_PARTICLE);
    }
}
