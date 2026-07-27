package com.minecrafttas.tbc.mixin.rng;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import net.minecraft.util.collection.WeightedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

@Mixin(WeightedList.class)
public class MixinWeightedListRand {
    @ModifyExpressionValue(method = "<init>(Ljava/util/List;)V", at = @At(value = "NEW", target = "()Ljava/util/Random;"))
    public Random modifyRandom(Random original) {
        return new RandomManager();
    }
}