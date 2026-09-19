package com.minecrafttas.tbc.mixin.rng.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.minecrafttas.tbc.rng.RandomManager;
import com.minecrafttas.tbc.rng.RandomTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

/**
 * The skin shown for a team in the spectator teleport menu is picked with an
 * unseeded Random. The code sits in the package private inner class
 * {@code TeamTeleportSpectatorMenu.TeleportToSpecificTeamCommand}, hence the target name.
 * A constructor name never needs remapping.
 */
@Mixin(targets = "net.minecraft.client.gui.hud.spectator.TeamTeleportSpectatorMenu$TeleportToSpecificTeamCommand")
public class MixinTeamTeleportSpectatorMenuRand {
    @ModifyExpressionValue(method = "<init>", at = @At(value = "NEW", target = "Ljava/util/Random;"))
    public Random modifyRandom(Random original) {
        return RandomManager.create(RandomTypes.TEAM_TELEPORT_SPECTATOR_MENU);
    }
}
