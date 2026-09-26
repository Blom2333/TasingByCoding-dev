package com.minecrafttas.tbc.mixin.rng.volatile_;

import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collections;
import java.util.List;

/**
 * {@code MinecraftServer.tick} shuffles the player list sample with the argument-less
 * {@code Collections.shuffle(list)}, which uses one static Random for every call site, hidden inside
 * {@code java.util.Collections}. It is redirected to {@link RandomManager#COLLECTIONS_RANDOM}, the one
 * instance of the {@link RandomTypes#SHUFFLE} site.
 *
 * <p>Split off from the {@code MINECRAFT_SERVER} site so that every mixin file belongs to exactly one
 * {@link RandomTypes.Stability}.
 */
@Mixin(MinecraftServer.class)
public class MixinMinecraftServerShuffleRand {
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/Collections;shuffle(Ljava/util/List;)V"))
    private void redirectPlayerSampleShuffle(List<GameProfile> list) {
        Collections.shuffle(list, RandomManager.COLLECTIONS_RANDOM);
    }
}
