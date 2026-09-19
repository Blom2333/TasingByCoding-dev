package com.minecrafttas.tbc.mixin.rng.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

/**
 * ServerPlayerEntity#moveToSpawn picks the spawn offset with an unseeded Random,
 * which decides where a player is placed inside the spawn radius.
 */
@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntityRand {
    @ModifyExpressionValue(method = "moveToSpawn", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    public Random modifyRandom(Random original) {
        return RandomManager.create(RandomTypes.SERVER_PLAYER_ENTITY_SPAWN_OFFSET);
    }
}
