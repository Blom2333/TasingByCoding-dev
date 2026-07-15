package com.minecrafttas.tbc.mixin.rng;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public class MixinEntityRand {
//    @ModifyExpressionValue(method = "<init>", at = @At(value = "NEW", target = "Ljava/util/Random;"))
//    public Random modifyEntityRandom(Random original) {
//        return new GlobalRandom();
//    }
}
