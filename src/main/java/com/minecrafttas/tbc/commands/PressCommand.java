package com.minecrafttas.tbc.commands;

import com.minecrafttas.tbc.util.TBCMacros;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.RotationArgument;

import java.util.ArrayList;

public class PressCommand {
    public static ArrayList<TBCMacros> macroQueue = new ArrayList<>();

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher){
        commandDispatcher.register(Commands.literal("press")
            .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
            .then(Commands.argument("duration", TimeArgument.time())
            .executes(context -> pressKeys(context, ""))
            .then(Commands.argument("keys", StringArgumentType.string())
            .executes(context -> pressKeys(context, StringArgumentType.getString(context, "keys")))
            .then(Commands.argument("serverRotation", RotationArgument.rotation())
            .executes(context -> pressKeys(context, StringArgumentType.getString(context, "keys"), RotationArgument.getRotation(context, "serverRotation")))))));
    }

    private static int pressKeys(CommandContext<CommandSourceStack> context, String keys) {
        macroQueue.add(new TBCMacros(IntegerArgumentType.getInteger(context, "duration"), keys, null, null));
        return 1;
    }

    private static int pressKeys(CommandContext<CommandSourceStack> context, String keys, Coordinates rotation) {
        macroQueue.add(new TBCMacros(IntegerArgumentType.getInteger(context, "duration"), keys, rotation.getRotation(context.getSource()).x, rotation.getRotation(context.getSource()).y));
        return 1;
    }
}
