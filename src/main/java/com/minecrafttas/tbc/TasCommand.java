package com.minecrafttas.tbc;

import com.minecrafttas.tbc.commands.TasMacroCommand;
import com.minecrafttas.tbc.commands.TasRngCommand;
import com.minecrafttas.tbc.util.TasRules;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.ObjectiveArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class TasCommand {
    public static final LiteralArgumentBuilder<ServerCommandSource> tas = CommandManager.literal("tas")
            .requires(commandSourceStack -> commandSourceStack.hasPermissionLevel(2));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {

        TasMacroCommand.register();
        TasRngCommand.register();

        TasRules[] rules = TasRules.values();
        for (TasRules rule : rules) {
            registerTasRule(commandDispatcher, rule);
        }

        commandDispatcher.register(tas);
    }

    private static void registerTasRule(CommandDispatcher<ServerCommandSource> commandDispatcher, TasRules rule) {
        commandDispatcher.register(CommandManager.literal("tas")
            .requires(commandSourceStack -> commandSourceStack.hasPermissionLevel(2))
            .then(CommandManager.literal("rule")
            .then(CommandManager.literal(rule.name)
            .executes(context -> queryRule(context, rule))
            .then(CommandManager.argument("value", ObjectiveArgumentType.objective())
            .executes(context -> setRule(context, rule))))));
    }

    private static int queryRule(CommandContext<ServerCommandSource> context, TasRules rule) {
        context.getSource().sendFeedback(new TranslatableText("commands.tbc.tas.rule.query"), false);
        return 1;
    }

    private static int setRule(CommandContext<ServerCommandSource> context, TasRules rule) throws CommandSyntaxException {
        rule.setValue(ObjectiveArgumentType.getObjective(context, "value"));
        context.getSource().sendFeedback(new TranslatableText("commands.tbc.tas.rule.set", rule.toString(), rule.value.toString()), true);
        return 1;
    }
}
