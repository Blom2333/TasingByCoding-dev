package com.minecrafttas.tbc.mixin.rng;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import net.minecraft.world.gen.GeneratorOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

/**
 * GeneratorOptions has three unseeded Random instances, all of them picking a
 * random world seed:
 * <ul>
 *     <li>{@code getDefaultOptions()} (static)</li>
 *     <li>{@code getChunkGenerator()}</li>
 *     <li>{@code fromProperties(Properties)} (static)</li>
 * </ul>
 * Mixin requires handlers for static targets to be private static.
 */
@Mixin(GeneratorOptions.class)
public class MixinGeneratorOptionsRand {
    @ModifyExpressionValue(method = "getDefaultOptions", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    private static Random modifyDefaultOptionsRandom(Random original) {
        return new RandomManager();
    }

    @ModifyExpressionValue(method = "getChunkGenerator", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    public Random modifyChunkGeneratorRandom(Random original) {
        return new RandomManager();
    }

    @ModifyExpressionValue(method = "fromProperties", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    private static Random modifyFromPropertiesRandom(Random original) {
        return new RandomManager();
    }
}
