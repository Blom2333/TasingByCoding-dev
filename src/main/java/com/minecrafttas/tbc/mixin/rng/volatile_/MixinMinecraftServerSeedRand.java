package com.minecrafttas.tbc.mixin.rng.volatile_;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

/**
 * {@code MinecraftServer.createWorlds} picks a random world seed when the dimension map has no overworld
 * entry.
 *
 * <p>Split off from the {@code MINECRAFT_SERVER} site because this one is VOLATILE: every call shares the one
 * instance of the site ({@link RandomManager#site(RandomTypes)}).
 */
@Mixin(MinecraftServer.class)
public class MixinMinecraftServerSeedRand {
    @ModifyExpressionValue(method = "createWorlds", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    public Random modifyCreateWorldsRandom(Random original) {
        return RandomManager.site(RandomTypes.MINECRAFT_SERVER_SEED);
    }
}
