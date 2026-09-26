package com.minecrafttas.tbc.mixin.rng.volatile_;

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
 *
 * <p>The site is VOLATILE: consecutive commands continue the one shared instance of the site
 * ({@link RandomManager#site(RandomTypes)}).
 */
@Mixin(SpreadPlayersCommand.class)
public class MixinSpreadPlayersCommandRand {
    @ModifyExpressionValue(method = "execute", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    private static Random modifyRandom(Random original) {
        return RandomManager.site(RandomTypes.SPREAD_PLAYERS_COMMAND);
    }
}
