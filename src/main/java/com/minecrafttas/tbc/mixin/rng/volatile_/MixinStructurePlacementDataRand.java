package com.minecrafttas.tbc.mixin.rng.volatile_;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import net.minecraft.structure.StructurePlacementData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * When no Random and no position are supplied, structure placement falls back to a
 * wall clock seeded Random ({@code Util.getMeasuringTimeMs()}). Only that seed is
 * replaced, the position based seed is left untouched.
 *
 * <p>The site is VOLATILE, so the stand-in for the wall clock comes from the one shared instance of the site
 * ({@link RandomManager#site(RandomTypes)}): a deterministic pseudo clock instead of the real one.
 */
@Mixin(StructurePlacementData.class)
public class MixinStructurePlacementDataRand {
    @ModifyExpressionValue(method = "getRandom", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getMeasuringTimeMs()J"))
    private long modifyTimeSeed(long original) {
        return RandomManager.site(RandomTypes.STRUCTURE_PLACEMENT_SEED).nextLong();
    }
}
