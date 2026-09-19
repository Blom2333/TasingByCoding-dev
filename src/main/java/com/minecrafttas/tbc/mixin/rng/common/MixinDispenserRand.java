package com.minecrafttas.tbc.mixin.rng.common;

import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import net.minecraft.block.entity.DispenserBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

/**
 * {@code DispenserBlockEntity.RANDOM} picks the slot a dispenser ejects from.
 * It is a {@code private static final} field, so the instance is swapped at the end of the class initializer.
 */
@Mixin(DispenserBlockEntity.class)
public class MixinDispenserRand {
    @Shadow @Mutable @Final private static Random RANDOM;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void replaceRandom(CallbackInfo ci) {
        RANDOM = RandomManager.create(RandomTypes.DISPENSER_BLOCK_ENTITY);
    }
}
