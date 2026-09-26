package com.minecrafttas.tbc.mixin.rng.stable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

/**
 * The {@code MinecraftServer.random} field, which picks the player list sample shown in the server list.
 * There is one instance per server, so the site is STABLE.
 *
 * <p>The two other unseeded sources of {@code MinecraftServer} are VOLATILE and therefore live in their own
 * mixins: {@link com.minecrafttas.tbc.mixin.rng.volatile_.MixinMinecraftServerSeedRand} and
 * {@link com.minecrafttas.tbc.mixin.rng.volatile_.MixinMinecraftServerShuffleRand}.
 */
@Mixin(MinecraftServer.class)
public class MixinMinecraftServerRand {
    @ModifyExpressionValue(method = "<init>", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    public Random modifyRandom(Random original) {
        return RandomManager.create(RandomTypes.MINECRAFT_SERVER);
    }
}
