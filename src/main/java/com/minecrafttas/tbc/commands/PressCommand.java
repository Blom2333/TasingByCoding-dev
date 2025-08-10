package com.minecrafttas.tbc.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PressCommand {
    public static int index = 0;
    public static List<String> pressingList = new ArrayList<>();
    public static List<List<String>> pressingQueue = new ArrayList<>();

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher){
        commandDispatcher.register(Commands.literal("press")
                .then(Commands.argument("duration", TimeArgument.time())
                .executes(context -> setDuration(context, IntegerArgumentType.getInteger(context, "duration")))
                .then(Commands.argument("keys", StringArgumentType.string())
                .executes(context -> pressKeys(context, StringArgumentType.getString(context, "keys"), IntegerArgumentType.getInteger(context, "duration")))
                .then(Commands.argument("serverRotation", RotationArgument.rotation())
                .executes(context -> serverRotate(context, RotationArgument.getRotation(context, "serverRotation"))))
                )));
    }

    private static int setDuration(CommandContext<CommandSourceStack> context, Integer time) {
        index += time;
        return 1;
    }

    private static int pressKeys(CommandContext<CommandSourceStack> context, String keys, Integer time) {
        setDuration(context, time);
        pressingList.clear();
        pressingList.addAll(Arrays.asList(keys.split("")));
        for (int i = 0; i < time; i++) pressingQueue.add(new ArrayList<>(pressingList));
        return 1;
    }

    private static int serverRotate(CommandContext<CommandSourceStack> context, Coordinates rotation) {

        pressKeys(context, StringArgumentType.getString(context, "keys"), IntegerArgumentType.getInteger(context, "duration"));
        sendChatMessage(context, "Rotated to " + rotation.getRotation(context.getSource()).x + " "  + rotation.getRotation(context.getSource()).y);
        return 1;
    }

    private static void sendChatMessage(CommandContext<CommandSourceStack> context, String string){
        context.getSource().sendSuccess(new TextComponent(string), false);
    }
}
