package com.minecrafttas.tbc.mixin.rng.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Three unseeded Random sources:
 * <ul>
 *     <li>the {@code random} field (player list sample)</li>
 *     <li>{@code createWorlds}, which picks a random world seed if the dimension
 *     map has no overworld entry</li>
 *     <li>the argumentless {@code Collections.shuffle} of the player list sample in
 *     {@code tick}, which uses the static Random inside {@code java.util.Collections}</li>
 * </ul>
 */
@Mixin(MinecraftServer.class)
public class MixinMinecraftServerRand {
    @ModifyExpressionValue(method = "<init>", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    public Random modifyRandom(Random original) {
        return new RandomManager();
    }

    @ModifyExpressionValue(method = "createWorlds", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    public Random modifyCreateWorldsRandom(Random original) {
        return new RandomManager();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/Collections;shuffle(Ljava/util/List;)V"))
    private void redirectPlayerSampleShuffle(List<GameProfile> list) {
        Collections.shuffle(list, new RandomManager());
    }
}
