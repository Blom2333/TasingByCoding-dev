package com.minecrafttas.tbc.mixin.rng.stable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

/**
 * {@code ClientPlayNetworkHandler.random} applies the random offset the client adds to entity positions
 * sent by the server (entity/particle jitter).
 */
@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientParticlesRand {
    @ModifyExpressionValue(method = "<init>", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    public Random modifyRandom(Random original) {
        return RandomManager.create(RandomTypes.CLIENT_PLAY_NETWORK_HANDLER);
    }
}
