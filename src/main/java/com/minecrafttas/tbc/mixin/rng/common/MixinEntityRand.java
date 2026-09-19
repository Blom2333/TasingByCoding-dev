package com.minecrafttas.tbc.mixin.rng.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;
import net.minecraft.entity.Entity;

/**
 * {@code Entity.random} is the per-entity Random: sound pitch, particles and most other entity side
 * randomness. It also decides the entity UUID, because {@code Entity.uuid} is derived from this Random,
 * so every entity needs its own instance - therefore the entity id is passed as the instance key.
 */
@Mixin(Entity.class)
public class MixinEntityRand {
    @ModifyExpressionValue(method = "<init>", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    public Random modifyRandom(Random original) {
        return RandomManager.create(RandomTypes.ENTITY, Integer.toString(((Entity) (Object) this).getEntityId()));
    }
}
