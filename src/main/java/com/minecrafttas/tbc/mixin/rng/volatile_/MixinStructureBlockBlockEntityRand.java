package com.minecrafttas.tbc.mixin.rng.volatile_;

import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Structure blocks with seed 0 (the "random" rotation/mirror mode) seed their Random
 * with {@code Util.getMeasuringTimeMs()}, i.e. with the wall clock. Only that seed is
 * replaced; a seed explicitly set in the block still wins.
 *
 * <p>The site is VOLATILE, so the stand-in for the wall clock comes from the one shared instance of the site
 * ({@link RandomManager#site(RandomTypes)}): a deterministic pseudo clock instead of the real one.
 */
@Mixin(StructureBlockBlockEntity.class)
public class MixinStructureBlockBlockEntityRand {
    @ModifyExpressionValue(method = "createRandom", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getMeasuringTimeMs()J"))
    private static long modifyTimeSeed(long original) {
        return RandomManager.site(RandomTypes.STRUCTURE_BLOCK_SEED).nextLong();
    }
}
