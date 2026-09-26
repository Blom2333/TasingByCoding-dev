package com.minecrafttas.tbc.mixin.rng.session;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import net.minecraft.util.collection.WeightedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

/**
 * {@code WeightedList.random} assigns the shuffled order of the weighted entries, which is what
 * weighted picking (mainly villager task lists) uses.
 */
@Mixin(WeightedList.class)
public class MixinWeightedListRand {
    @ModifyExpressionValue(method = "<init>(Ljava/util/List;)V", at = @At(value = "NEW", target = "()Ljava/util/Random;"))
    public Random modifyRandom(Random original) {
        return RandomManager.create(RandomTypes.WEIGHTED_LIST);
    }
}