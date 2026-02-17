package com.minecrafttas.tbc.core;

import com.minecrafttas.tbc.TasCommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class TasRngCommand {
    private static final LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("rng");

    public static void register() {
        TasCommand.tas.then(builder);
    }
}
