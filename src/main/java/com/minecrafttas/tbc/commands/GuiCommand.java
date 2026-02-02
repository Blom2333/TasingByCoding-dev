package com.minecrafttas.tbc.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.inventory.ClickType;

public class GuiCommand {

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher){
        commandDispatcher.register(Commands.literal("gui")
        .then(Commands.argument("slot", IntegerArgumentType.integer())
        .then(Commands.literal("throw")
        .executes(context -> throwOperation(context, 1))
        .then(Commands.argument("times", IntegerArgumentType.integer(0))
        .executes(context -> throwOperation(context, IntegerArgumentType.getInteger(context, "times")))))
        .then(Commands.literal("pick")
        .executes(context -> pickupOperation(context, 0))
        .then(Commands.literal("leftclick")
        .executes(context -> pickupOperation(context, 0)))
        .then(Commands.literal("rightclick")
        .executes(context -> pickupOperation(context, 1))))
        .then(Commands.literal("quickmove")
        .executes(GuiCommand::quickMoveOperation))));
    }

    public static int pickupOperation(CommandContext<CommandSourceStack> context, int button) {
        sendGuiOperation(context, button, ClickType.PICKUP);
        return 1;
    }

    public static int quickMoveOperation(CommandContext<CommandSourceStack> context) {
        sendGuiOperation(context, 0, ClickType.QUICK_MOVE);
        return 1;
    }

    public static int throwOperation(CommandContext<CommandSourceStack> context, int times) {
        if (times == 0) sendGuiOperation(context, 1, ClickType.THROW);
        else {
            for (int time = 0; time < times; time++) {
                sendGuiOperation(context, 0, ClickType.THROW);
            }
        }
        return 1;
    }

    public static void sendGuiOperation(CommandContext<CommandSourceStack> context, int operationType, ClickType clickType) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode != null && mc.player != null) {
            mc.gameMode.handleInventoryMouseClick(mc.player.containerMenu.containerId, IntegerArgumentType.getInteger(context, "slot"), operationType, clickType, mc.player);
        }
    }
}