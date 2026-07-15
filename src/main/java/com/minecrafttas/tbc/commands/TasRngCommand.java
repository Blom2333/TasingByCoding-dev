package com.minecrafttas.tbc.commands;

import com.minecrafttas.tbc.TasCommand;
import com.minecrafttas.tbc.rng.RandomManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;

public class TasRngCommand {
    private static final LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("rng");
    public static int loadCount = 0;

    public static void register() {
        registerGlobal();
        registerRule();
        TasCommand.tas.then(builder);
    }

    private static void registerGlobal() {
        builder.then(Commands.literal("global")
                   .then(Commands.literal("set")
                       .then(Commands.argument("from", IntegerArgumentType.integer(1))
                           .then(Commands.argument("to", IntegerArgumentType.integer(1))
                               .then(Commands.argument("value", IntegerArgumentType.integer())
                                   .executes(TasRngCommand::setRngInRange)))))
                   .then(Commands.literal("lock")
                       .executes(context -> unlockRng())
                       .then(Commands.argument("value", IntegerArgumentType.integer())
                           .executes(TasRngCommand::lockRng))));
    }

    private static void registerRule() {
        
    }

    private static int setRngInRange(CommandContext<CommandSourceStack> context) {
        int from = IntegerArgumentType.getInteger(context, "from");
        int to = IntegerArgumentType.getInteger(context, "to");
        if (from > to) {
            context.getSource().sendFailure(new TextComponent("The range is invalid"));
            return 0;
        }
        int value = IntegerArgumentType.getInteger(context, "value");
        context.getSource().sendSuccess(new TextComponent("Set " + value + " to " + from + " ~ " + to), false);

        return 1;
    }

    private static int lockRng(CommandContext<CommandSourceStack> context) {
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
