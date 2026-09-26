package com.minecrafttas.tbc.mixin.rng.volatile_;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

/**
 * ClientWorld#doRandomBlockDisplayTicks picks the invisible blocks (barrier,
 * structure void, ...) that are rendered while holding one.
 *
 * <p>The method runs on every client tick and the site is VOLATILE, so all calls share the one instance of
 * the site ({@link RandomManager#site(RandomTypes)}), which keeps the sequence continuous.
 */
@Mixin(ClientWorld.class)
public class MixinClientWorldRand {
    @ModifyExpressionValue(method = "doRandomBlockDisplayTicks", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    public Random modifyRandom(Random original) {
        return RandomManager.site(RandomTypes.CLIENT_WORLD_BLOCK_DISPLAY);
    }
}
