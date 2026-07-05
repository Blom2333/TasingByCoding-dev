package com.minecrafttas.tbc.commands;

import com.minecrafttas.tbc.TasCommand;
import com.minecrafttas.tbc.util.KeyBindingMapper;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;

import java.io.IOException;

public class TasBindCommand {
    private static final LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("bind");

    public static void register() {
        TasCommand.tas.then(builder);
    }

    public static void registerBind(String key) {
        builder.then(Commands.literal(key)
            .executes(context -> {
                try {
                    KeyBindingMapper.removeKeybinding(InputConstants.getKey(key).getValue());
                } catch (IOException e) {throw new RuntimeException("Cannot register unbind command " + key, e);}
                return 1;
            })
            .then(Commands.argument("command", MessageArgument.message())
            .executes(context -> {
                try {
                    KeyBindingMapper.addKeybinding(InputConstants.getKey(key).getValue(), MessageArgument.getMessage(context, "command").getString());
                } catch (IOException e) {throw new RuntimeException("Cannot register bind command " + key, e);}
                return 1;
        })));
    }
}
