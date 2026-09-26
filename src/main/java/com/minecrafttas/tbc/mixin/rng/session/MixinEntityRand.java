package com.minecrafttas.tbc.mixin.rng.session;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;
import net.minecraft.entity.Entity;

/**
 * {@code Entity.random} is the per-entity Random: sound pitch, particles and most other entity side
 * randomness. It also decides the entity UUID, because {@code Entity.uuid} is initialised with
 * {@code MathHelper.randomUuid(this.random)}, so every entity needs its own instance - each construction
 * therefore asks the {@link RandomTypes#ENTITY} pool for a fresh {@link RandomManager}, which keeps the
 * UUID sequence reproducible in entity construction order.
 */
@Mixin(Entity.class)
public class MixinEntityRand {
    @ModifyExpressionValue(method = "<init>", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    public Random modifyRandom(Random original) {
        return RandomManager.create(RandomTypes.ENTITY);
    }
}
