package com.minecrafttas.tbc.mixin.rng.stable;

import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

/**
 * {@code MathHelper.RANDOM} backs the argument-less {@code MathHelper.randomUuid()}, which mints UUIDs
 * for everything that does not pass a Random of its own.
 */
@Mixin(MathHelper.class)
public class MixinMathHelperRand {
    @Shadow @Mutable @Final private static Random RANDOM;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void replaceRandom(CallbackInfo ci) {
        RANDOM = RandomManager.create(RandomTypes.MATH_HELPER);
    }
}
