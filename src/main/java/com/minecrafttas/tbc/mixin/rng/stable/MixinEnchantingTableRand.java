package com.minecrafttas.tbc.mixin.rng.stable;

import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

/**
 * {@code EnchantingTableBlockEntity.RANDOM} turns the book floating above the table to a random page.
 * Static final field, replaced at the end of the class initializer.
 */
@Mixin(EnchantingTableBlockEntity.class)
public class MixinEnchantingTableRand {
    @Shadow @Mutable @Final private static Random RANDOM;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void replaceRandom(CallbackInfo ci) {
        RANDOM = RandomManager.create(RandomTypes.ENCHANTING_TABLE_BLOCK_ENTITY);
    }
}
