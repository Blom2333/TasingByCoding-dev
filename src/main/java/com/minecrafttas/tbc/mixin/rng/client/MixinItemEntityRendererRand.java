package com.minecrafttas.tbc.mixin.rng.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

/**
 * {@code ItemEntityRenderer.random} offsets stacked item entities so they do not overlap exactly.
 * Note that the renderer re-seeds it per stack ({@code setSeed(itemStack hash)}), so the seed is
 * overwritten on every frame; the instance is still routed through RandomManager so it stays controllable.
 */
@Mixin(ItemEntityRenderer.class)
public class MixinItemEntityRendererRand {
    @ModifyExpressionValue(method = "<init>", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    public Random modifyRandom(Random original) {
        return RandomManager.create(RandomTypes.ITEM_ENTITY_RENDERER);
    }
}
