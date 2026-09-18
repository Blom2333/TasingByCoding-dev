package com.minecrafttas.tbc.mixin.rng.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import net.minecraft.structure.StructurePlacementData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * When no Random and no position are supplied, structure placement falls back to a
 * wall clock seeded Random ({@code Util.getMeasuringTimeMs()}). Only that seed is
 * replaced, the position based seed is left untouched.
 */
@Mixin(StructurePlacementData.class)
public class MixinStructurePlacementDataRand {
    @ModifyExpressionValue(method = "getRandom", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getMeasuringTimeMs()J"))
    private long modifyTimeSeed(long original) {
        return new RandomManager().nextLong();
    }
}
