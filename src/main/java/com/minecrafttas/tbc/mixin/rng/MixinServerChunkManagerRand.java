package com.minecrafttas.tbc.mixin.rng;

import com.minecrafttas.tbc.rng.RandomManager;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collections;
import java.util.List;

/**
 * Every tick the chunk manager shuffles the list of loaded chunk holders before
 * ticking them, using the static Random inside {@code java.util.Collections}.
 * That shuffle directly influences the order in which chunks (and therefore mob
 * spawning, random ticks, block events) are processed, so it is redirected to an
 * explicit Random.
 */
@Mixin(ServerChunkManager.class)
public class MixinServerChunkManagerRand {
    @Redirect(method = "tickChunks", at = @At(value = "INVOKE", target = "Ljava/util/Collections;shuffle(Ljava/util/List;)V"))
    private void redirectShuffle(List<ChunkHolder> list) {
        Collections.shuffle(list, new RandomManager());
    }
}
