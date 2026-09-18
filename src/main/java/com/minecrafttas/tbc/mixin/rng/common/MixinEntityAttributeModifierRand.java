package com.minecrafttas.tbc.mixin.rng.common;

import com.minecrafttas.tbc.rng.RandomManager;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;
import java.util.UUID;

/**
 * The deprecated {@code EntityAttributeModifier(String, double, Operation)} constructor
 * generates its UUID from netty's {@code ThreadLocalRandom}, which is a second Random
 * implementation invisible to the java.util.Random mixins. Vanilla itself never calls
 * this constructor (every vanilla modifier passes an explicit UUID), it is only here so
 * that the whole class is covered.
 */
@Mixin(EntityAttributeModifier.class)
public class MixinEntityAttributeModifierRand {
    @Redirect(method = "<init>(Ljava/lang/String;DLnet/minecraft/entity/attribute/EntityAttributeModifier$Operation;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;randomUuid(Ljava/util/Random;)Ljava/util/UUID;"))
    private static UUID redirectRandomUuid(Random original) {
        return MathHelper.randomUuid(new RandomManager());
    }
}
