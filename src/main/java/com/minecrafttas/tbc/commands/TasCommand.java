package com.minecrafttas.tbc.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;

public class TasCommand {
    //public static boolean doInstantExecute = true;

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register(Commands.literal("tas")
            .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
            .then(Commands.literal("run")
            .then(Commands.argument("command", MessageArgument.message())
            .executes(TasCommand::addCommand))));
    }

    private static int addCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (!PressCommand.macroQueue.isEmpty()) {
            PressCommand.macroQueue.get(PressCommand.macroQueue.size() - 1).addCommand(MessageArgument.getMessage(context, "command"));
        } else {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.chat("/" + MessageArgument.getMessage(context, "command").getString());
            }
        }
        return 1;
    }
}
