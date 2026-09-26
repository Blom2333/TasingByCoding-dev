package com.minecrafttas.tbc.mixin.rng.volatile_;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
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
 *
 * <p>All three sites are VOLATILE, so each of them has exactly one instance for the whole run
 * ({@link RandomManager#site(RandomTypes)}).
 */
@Mixin(GeneratorOptions.class)
public class MixinGeneratorOptionsRand {
    @ModifyExpressionValue(method = "getDefaultOptions", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    private static Random modifyDefaultOptionsRandom(Random original) {
        return RandomManager.site(RandomTypes.GENERATOR_OPTIONS_DEFAULT);
    }

    @ModifyExpressionValue(method = "getChunkGenerator", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    public Random modifyChunkGeneratorRandom(Random original) {
        return RandomManager.site(RandomTypes.GENERATOR_OPTIONS_CHUNK_GENERATOR);
    }

    @ModifyExpressionValue(method = "fromProperties", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    private static Random modifyFromPropertiesRandom(Random original) {
        return RandomManager.site(RandomTypes.GENERATOR_OPTIONS_FROM_PROPERTIES);
    }
}
