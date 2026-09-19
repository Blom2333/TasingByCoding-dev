package com.minecrafttas.tbc.mixin.rng.common;

import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import net.minecraft.util.ItemScatterer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

/**
 * {@code ItemScatterer.RANDOM} spreads the stacks that are spat out when a container is broken.
 */
@Mixin(ItemScatterer.class)
public class MixinItemScattererRand {
    @Shadow @Mutable @Final private static Random RANDOM;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void replaceRandom(CallbackInfo ci) {
        RANDOM = RandomManager.create(RandomTypes.ITEM_SCATTERER);
    }
}
