package com.minecrafttas.tbc.mixin;

import com.minecrafttas.tbc.TasingByCoding;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MixinMultiPlayerGameMode {
    @Inject(method = "handleInventoryMouseClick", at = @At(value = "HEAD"))
    private void injectHandleInventoryMouseClick(int i, int j, int k, ClickType clickType, Player player, CallbackInfoReturnable<ItemStack> cir) {
        TasingByCoding.LOGGER.info("{}, {}, {}", j, k, clickType);
    }
}
