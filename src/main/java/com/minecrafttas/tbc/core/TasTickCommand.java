package com.minecrafttas.tbc.core;

import com.minecrafttas.tbc.TasCommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class TasTickCommand {
    private static final LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("tick");

    public static void register() {
        TasCommand.tas.then(builder);
    }
}
