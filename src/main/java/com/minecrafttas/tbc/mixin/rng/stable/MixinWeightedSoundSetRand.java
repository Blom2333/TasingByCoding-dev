package com.minecrafttas.tbc.mixin.rng.stable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import net.minecraft.client.sound.WeightedSoundSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

/**
 * {@code WeightedSoundSet.random} picks which variant of a sound event is played.
 */
@Mixin(WeightedSoundSet.class)
public class MixinWeightedSoundSetRand {
    @ModifyExpressionValue(method = "<init>", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    public Random modifyRandom(Random original) {
        return RandomManager.create(RandomTypes.WEIGHTED_SOUND_SET);
    }
}
