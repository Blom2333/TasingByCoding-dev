package com.minecrafttas.tbc.mixin.rng.stable;

import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import net.minecraft.datafixer.fix.EntityZombieVillagerTypeFix;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

/**
 * {@code EntityZombieVillagerTypeFix.RANDOM} picks the profession when a zombie villager of an old save
 * is migrated by the data fixer.
 */
@Mixin(EntityZombieVillagerTypeFix.class)
public class MixinZombieVillagerTypeRand {
    @Shadow @Mutable @Final private static Random RANDOM;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void replaceRandom(CallbackInfo ci) {
        RANDOM = RandomManager.create(RandomTypes.ZOMBIE_VILLAGER_TYPE);
    }
}
