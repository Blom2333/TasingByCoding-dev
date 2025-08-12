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
    public static List<Float> rotList = new ArrayList<>();
    public static List<List<Float>> rotQueue = new ArrayList<>();

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher){
        commandDispatcher.register(Commands.literal("press")
                .then(Commands.argument("duration", TimeArgument.time())
                .executes(context -> pressKeys(context, "", IntegerArgumentType.getInteger(context, "duration"), false))
                .then(Commands.argument("keys", StringArgumentType.string())
                .executes(context -> pressKeys(context, StringArgumentType.getString(context, "keys"), IntegerArgumentType.getInteger(context, "duration"), false))
                .then(Commands.argument("serverRotation", RotationArgument.rotation())
                .executes(context -> serverRotate(context, RotationArgument.getRotation(context, "serverRotation"))))
                )));
    }

    private static int pressKeys(CommandContext<CommandSourceStack> context, String keys, Integer time, boolean doRotate) {
        index += time;
        pressingList.clear();
        pressingList.addAll(Arrays.asList(keys.split("")));
        for (int i = 0; i < time; i++) {
            pressingQueue.add(new ArrayList<>(pressingList));
            if (!doRotate) rotList.clear();
            rotQueue.add(new ArrayList<>(rotList));
        }
        return 1;
    }

    private static int serverRotate(CommandContext<CommandSourceStack> context, Coordinates rotation) {
        rotList.clear();
        rotList.add(rotation.getRotation(context.getSource()).x);
        rotList.add(rotation.getRotation(context.getSource()).y);
        pressKeys(context, StringArgumentType.getString(context, "keys"), IntegerArgumentType.getInteger(context, "duration"), true);
        return 1;
    }

    private static void sendChatMessage(CommandContext<CommandSourceStack> context, String string){
        context.getSource().sendSuccess(new TextComponent(string), false);
    }
}
