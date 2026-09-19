package com.minecrafttas.tbc.mixin.rng.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import net.minecraft.village.raid.Raid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

/**
 * {@code Raid.random} decides the bonus raider counts of every wave ({@code Raid.getBonusCount})
 * and therefore the composition of the raid.
 */
@Mixin(Raid.class)
public class MixinRaidRand {
    @ModifyExpressionValue(method = "<init>*", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    public Random modifyEntityRandom(Random original) {
        return RandomManager.create(RandomTypes.RAID);
    }
}