package com.minecrafttas.tbc.mixin.rng;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.GlobalRandom;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

@Mixin(Entity.class)
public class MixinEntityRand {
//    @ModifyExpressionValue(method = "<init>", at = @At(value = "NEW", target = "Ljava/util/Random;"))
//    public Random modifyEntityRandom(Random original) {
//        return new GlobalRandom();
//    }
}
