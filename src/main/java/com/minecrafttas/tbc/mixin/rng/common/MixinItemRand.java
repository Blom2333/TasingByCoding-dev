package com.minecrafttas.tbc.mixin.rng.common;

import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

/**
 * {@code Item.RANDOM} is the shared Random of all interactable items: item use sound pitch,
 * bone meal scatter and the crossbow charge time.
 */
@Mixin(Item.class)
public class MixinItemRand {
    @Shadow @Mutable @Final protected static Random RANDOM;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void replaceRandom(CallbackInfo ci) {
        RANDOM = RandomManager.create(RandomTypes.ITEM);
    }
}
