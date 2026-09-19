package com.minecrafttas.tbc.mixin.rng.common;

import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import net.minecraft.entity.ai.brain.task.WanderIndoorsTask;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collections;
import java.util.List;

/**
 * Villager style mobs pick their next indoor walk target by shuffling the 3x3x3
 * block positions around them. The argumentless {@code Collections.shuffle} uses the
 * static Random inside {@code java.util.Collections}, so the call is redirected to the
 * overload taking an explicit Random instead. {@code run} has a bridge overload for the
 * generic supertype, so the target is pinned down with its descriptor.
 */
@Mixin(WanderIndoorsTask.class)
public class MixinWanderIndoorsTaskRand {
    @Redirect(method = "run(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/mob/PathAwareEntity;J)V",
            at = @At(value = "INVOKE", target = "Ljava/util/Collections;shuffle(Ljava/util/List;)V"))
    private void redirectShuffle(List<BlockPos> list) {
        Collections.shuffle(list, RandomManager.create(RandomTypes.WANDER_INDOORS_TASK));
    }
}
