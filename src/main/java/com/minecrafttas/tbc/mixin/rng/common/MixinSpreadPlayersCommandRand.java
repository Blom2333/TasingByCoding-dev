package com.minecrafttas.tbc.mixin.rng.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import net.minecraft.server.command.SpreadPlayersCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

/**
 * /spreadplayers creates its Random unseeded, which decides where every player lands.
 * {@code execute} is static, so the handler has to be private static as well.
 */
@Mixin(SpreadPlayersCommand.class)
public class MixinSpreadPlayersCommandRand {
    @ModifyExpressionValue(method = "execute", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    private static Random modifyRandom(Random original) {
        return RandomManager.create(RandomTypes.SPREAD_PLAYERS_COMMAND);
    }
}
