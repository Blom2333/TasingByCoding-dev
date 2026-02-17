package com.minecrafttas.tbc;

import com.minecrafttas.tbc.core.TasMacroCommand;
import com.minecrafttas.tbc.core.TasRngCommand;
import com.minecrafttas.tbc.core.TasSettingsCommand;
import com.minecrafttas.tbc.core.TasTickCommand;
import com.minecrafttas.tbc.util.TasRules;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.network.chat.TranslatableComponent;

public class TasCommand {
    public static final LiteralArgumentBuilder<CommandSourceStack> tas = Commands.literal("tas")
            .requires(commandSourceStack -> commandSourceStack.hasPermission(2));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        TasMacroCommand.register();
        TasRngCommand.register();
        TasSettingsCommand.register();
        TasTickCommand.register();

        TasRules[] rules = TasRules.values();
        for (TasRules rule : rules) {
            registerTasRule(commandDispatcher, rule);
        }

        commandDispatcher.register(tas);
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
