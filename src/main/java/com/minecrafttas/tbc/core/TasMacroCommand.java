package com.minecrafttas.tbc.core;

import com.minecrafttas.tbc.TasCommand;
import com.minecrafttas.tbc.util.TBCMacros;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.RotationArgument;

public class TasMacroCommand {
    private static final LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("macro");

    public static void register() {
        registerPress();
        registerGui();
        registerRunCommand();
        registerStop();
        TasCommand.tas.then(builder);
    }

    private static void registerPress() {
        builder.then(Commands.literal("press")
            .then(Commands.argument("duration", TimeArgument.time())
            .executes(context -> pressKeys(context, ""))
            .then(Commands.argument("keys", StringArgumentType.string())
            .executes(context -> pressKeys(context,
                    StringArgumentType.getString(context, "keys")))
            .then(Commands.argument("serverRotation", RotationArgument.rotation())
            .executes(context -> pressKeys(context,
                    StringArgumentType.getString(context, "keys"),
                    RotationArgument.getRotation(context, "serverRotation"))
        )))));
    }

    private static void registerGui() {
        // gui hasn't done yet so i will just keep the original class
        builder.then(Commands.literal("gui"));
    }

    private static void registerRunCommand() {
        builder.then(Commands.literal("command")
            .then(Commands.argument("command", MessageArgument.message())
            .executes(TasMacroCommand::addCommand)));
    }

    private static void registerStop() {
        builder.then(Commands.literal("stop")
            .executes(TasMacroCommand::stopMacros));
    }

    private static int pressKeys(CommandContext<CommandSourceStack> context, String keys) {
        TBCMacros.macroQueue.add(new TBCMacros(IntegerArgumentType.getInteger(context, "duration"), keys, null, null));
        return 1;
    }

    private static int pressKeys(CommandContext<CommandSourceStack> context, String keys, Coordinates rotation) {
        TBCMacros.macroQueue.add(new TBCMacros(IntegerArgumentType.getInteger(context, "duration"), keys, rotation.getRotation(context.getSource()).x, rotation.getRotation(context.getSource()).y));
        return 1;
    }

    private static int addCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (!TBCMacros.macroQueue.isEmpty()) {
            TBCMacros.macroQueue.get(TBCMacros.macroQueue.size() - 1).addCommand(MessageArgument.getMessage(context, "command"));
        } else {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.chat("/" + MessageArgument.getMessage(context, "command").getString());
            }
        }
        return 1;
    }

    private static int stopMacros(CommandContext<CommandSourceStack> context) {
        TBCMacros.macroQueue.clear();
        return 1;
    }
}
