package com.minecrafttas.tbc.mixin.rng.stable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import net.minecraft.client.font.TextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

/**
 * {@code TextRenderer.random} is the text renderer's own Random. Its only vanilla user is the credits
 * screen, which re-seeds it every frame ({@code random.setSeed(...)}), so replacing the instance only
 * matters for other callers.
 */
@Mixin(TextRenderer.class)
public class MixinTextRendererRand {
    @ModifyExpressionValue(method = "<init>", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    public Random modifyRandom(Random original) {
        return RandomManager.create(RandomTypes.TEXT_RENDERER);
    }
}
