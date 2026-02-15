package com.minecrafttas.tbc.commands;

import com.minecrafttas.tbc.util.KeyBindingMapper;
import com.minecrafttas.tbc.util.TasRules;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.network.chat.TranslatableComponent;

import java.io.IOException;

public class TasCommand {
    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register(Commands.literal("tas")
            .requires(commandSourceStack -> commandSourceStack.hasPermission(2))

            .then(Commands.literal("run")
            .then(Commands.argument("command", MessageArgument.message())
            .executes(TasCommand::addCommand)))

            .then(Commands.literal("stop")
            .executes(TasCommand::stopMacros)));

        TasRules[] rules = TasRules.values();
        for (TasRules rule : rules) {
            registerTasRule(commandDispatcher, rule);
        }

        iterateKeysViaReflection(commandDispatcher);
    }

    private static void registerTasRule(CommandDispatcher<CommandSourceStack> commandDispatcher, TasRules rule) {
        commandDispatcher.register(Commands.literal("tas")
            .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
            .then(Commands.literal("rule")
            .then(Commands.literal(rule.name)
            .executes(context -> queryRule(context, rule))
            .then(Commands.argument("value", ObjectiveArgument.objective())
            .executes(context -> setRule(context, rule))))));
    }

    private static void iterateKeysViaReflection(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        try {
            java.lang.reflect.Field nameMapField =
                    InputConstants.Key.class.getDeclaredField("NAME_MAP");
            nameMapField.setAccessible(true);

            @SuppressWarnings("unchecked")
            java.util.Map<String, InputConstants.Key> nameMap =
                    (java.util.Map<String, InputConstants.Key>) nameMapField.get(null);

            for (String key : nameMap.keySet()) {
                registerTasBind(commandDispatcher, key);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void registerTasBind(CommandDispatcher<CommandSourceStack> commandDispatcher, String key) {
        commandDispatcher.register(Commands.literal("tas")
            .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
            .then(Commands.literal("bind")
            .then(Commands.literal(key)
            .then(Commands.argument("command", MessageArgument.message())
            .executes(context -> {
                try {
                    KeyBindingMapper.addKeybinding(InputConstants.getKey(key).getValue(), MessageArgument.getMessage(context, "command").getString());
                } catch (IOException e) {throw new RuntimeException(e);}
                return 1;
            }))))
            .then(Commands.literal("unbind")
            .then(Commands.literal(key)
            .executes(context -> {
                try {
                    KeyBindingMapper.removeKeybinding(InputConstants.getKey(key).getValue());
                } catch (IOException e) {throw new RuntimeException(e);}
                return 1;
            }))));
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

    private static int stopMacros(CommandContext<CommandSourceStack> context) {
        PressCommand.macroQueue.clear();
        return 1;
    }

    private static int queryRule(CommandContext<CommandSourceStack> context, TasRules rule) {
        context.getSource().sendSuccess(new TranslatableComponent("commands.tbc.tas.rule.query"), false);
        return 1;
    }

    private static int setRule(CommandContext<CommandSourceStack> context, TasRules rule) throws CommandSyntaxException {
        rule.setValue(ObjectiveArgument.getObjective(context, "value"));
        context.getSource().sendSuccess(new TranslatableComponent(I18n.get("commands.tbc.tas.rule.set", rule.toString(), rule.value.toString())), true);
        return 1;
    }
}
