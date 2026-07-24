package com.minecrafttas.tbc.mixin.oper;

import com.minecrafttas.tbc.TasCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public abstract class MixinCommandRegistration {
    @Shadow @Final private CommandDispatcher<ServerCommandSource> dispatcher;
    @Inject( at = @At("TAIL"), method = "<init>")
    private void injectCommandRegistration(CommandManager.RegistrationEnvironment environment, CallbackInfo ci){
        TasCommand.register(dispatcher);
    }
}