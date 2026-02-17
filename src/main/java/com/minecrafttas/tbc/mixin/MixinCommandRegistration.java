package com.minecrafttas.tbc.mixin;

import com.minecrafttas.tbc.TasCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class)
public abstract class MixinCommandRegistration {
    @Shadow @Final private CommandDispatcher<CommandSourceStack> dispatcher;
    @Inject( at = @At("TAIL"), method = "<init>")
    private void injectCommandRegistration(Commands.CommandSelection commandSelection, CallbackInfo ci){
        TasCommand.register(dispatcher);
    }
}