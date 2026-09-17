package com.minecrafttas.tbc.mixin.rng;

import com.minecrafttas.tbc.rng.RandomManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collections;
import java.util.List;

/**
 * On joining a world the dimension keys are shuffled with the argumentless
 * {@code Collections.shuffle} before they end up in {@code worldKeys}.
 * The other unseeded Random of this class (the particle jitter) is handled by
 * {@link MixinClientParticlesRand}.
 */
@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandlerRand {
    @Redirect(method = "onGameJoin", at = @At(value = "INVOKE", target = "Ljava/util/Collections;shuffle(Ljava/util/List;)V"))
    private void redirectDimensionShuffle(List<RegistryKey<World>> list) {
        Collections.shuffle(list, new RandomManager());
    }
}
