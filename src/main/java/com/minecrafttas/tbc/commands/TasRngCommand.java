package com.minecrafttas.tbc.commands;

import com.minecrafttas.tbc.TasCommand;
import com.minecrafttas.tbc.rng.RandomManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public class TasRngCommand {
    private static final LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.literal("rng");
    public static int loadCount = 0;

    public static void register() {
        registerGlobal();
        registerRule();
        TasCommand.tas.then(builder);
    }

    private static void registerGlobal() {
        builder.then(CommandManager.literal("global")
                   .then(CommandManager.literal("set")
                       .then(CommandManager.argument("from", IntegerArgumentType.integer(1))
                           .then(CommandManager.argument("to", IntegerArgumentType.integer(1))
                               .then(CommandManager.argument("value", IntegerArgumentType.integer())
                                   .executes(TasRngCommand::setRngInRange)))))
                   .then(CommandManager.literal("lock")
                       .executes(context -> unlockRng())
                       .then(CommandManager.argument("value", IntegerArgumentType.integer())
                           .executes(TasRngCommand::lockRng))));
    }

    private static void registerRule() {
        
    }

    private static int setRngInRange(CommandContext<ServerCommandSource> context) {
        int from = IntegerArgumentType.getInteger(context, "from");
        int to = IntegerArgumentType.getInteger(context, "to");
        if (from > to) {
            context.getSource().sendError(new LiteralText("The range is invalid"));
            return 0;
        }
        int value = IntegerArgumentType.getInteger(context, "value");
        context.getSource().sendFeedback(new LiteralText("Set " + value + " to " + from + " ~ " + to), false);

        return 1;
    }

    private static int lockRng(CommandContext<ServerCommandSource> context) {
        RandomManager.setValue(IntegerArgumentType.getInteger(context, "value"));
        RandomManager.setLocked(true);
        return 1;
    }

    private static int unlockRng() {
        RandomManager.setLocked(false);
        return 1;
    }
}
// tas rng block/entity/world/rule/next pos/selector/(drop/summon)/(num/lock) ...
