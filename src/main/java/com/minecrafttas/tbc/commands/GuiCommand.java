package com.minecrafttas.tbc.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;

public class GuiCommand {
    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher){
        //commandDispatcher.register(Commands.literal("gui")
        //.then(Commands.argument("actions", ComponentArgument.textComponent()))
        //        .executes()
    }

}
