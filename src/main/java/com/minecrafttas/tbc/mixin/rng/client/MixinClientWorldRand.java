package com.minecrafttas.tbc.mixin.rng.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

/**
 * ClientWorld#doRandomBlockDisplayTicks picks the invisible blocks (barrier,
 * structure void, ...) that are rendered while holding one.
 */
@Mixin(ClientWorld.class)
public class MixinClientWorldRand {
    @ModifyExpressionValue(method = "doRandomBlockDisplayTicks", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    public Random modifyRandom(Random original) {
        return new RandomManager();
    }
}
