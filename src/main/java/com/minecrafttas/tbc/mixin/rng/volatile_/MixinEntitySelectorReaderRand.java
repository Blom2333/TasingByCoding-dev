package com.minecrafttas.tbc.mixin.rng.volatile_;

import com.minecrafttas.tbc.rng.RandomManager;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * {@code EntitySelectorReader.RANDOM} is the sorter behind {@code @e[sort=random]}.
 * It calls the argumentless {@code Collections.shuffle(list)}, which uses the static
 * Random hidden inside {@code java.util.Collections} - out of reach for any mixin
 * (bootstrap class loader). The lambda is created in the static initializer, so the
 * field is simply swapped for an equivalent lambda that shuffles with the same shared
 * {@link RandomManager#COLLECTIONS_RANDOM} every other argumentless shuffle uses.
 */
@Mixin(EntitySelectorReader.class)
public class MixinEntitySelectorReaderRand {
    @Shadow @Mutable @Final public static BiConsumer<Vec3d, List<? extends Entity>> RANDOM;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void replaceRandomShuffle(CallbackInfo ci) {
        RANDOM = (vec3d, list) -> Collections.shuffle(list, RandomManager.COLLECTIONS_RANDOM);
    }
}
