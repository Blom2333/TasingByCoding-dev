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

        // PICKUP
        .then(Commands.literal("pick")
        .executes(context -> pickupOperation(context, 0))
        .then(Commands.literal("leftclick")
        .executes(context -> pickupOperation(context, 0)))
        .then(Commands.literal("rightclick")
        .executes(context -> pickupOperation(context, 1))))

        // QUICK_MOVE
        .then(Commands.literal("quickmove")
        .executes(GuiCommand::quickMoveOperation))

        // SWAP
        .then(Commands.literal("swap")
        .then(Commands.argument("hotbar", IntegerArgumentType.integer(0, 8))
        .executes(GuiCommand::swapOperation)))

        // CLONE
        .then(Commands.literal("clone")
        .executes(GuiCommand::cloneOperation))

        // THROW
        .then(Commands.literal("throw")
        .executes(context -> throwOperation(context, 1))
        .then(Commands.argument("times", IntegerArgumentType.integer(0))
        .executes(context -> throwOperation(context, IntegerArgumentType.getInteger(context, "times")))))

        // QUICK_CRAFT
        .then(Commands.literal("drag")
        .executes(context -> quickCraftOperation(context, 1))
        .then(Commands.literal("start")
        .executes(context -> quickCraftOperation(context, 0)))
        .then(Commands.literal("end")
        .executes(context -> quickCraftOperation(context, 2))))

        // PICKUP_ALL
        .then(Commands.literal("pickupall")
        .executes(GuiCommand::pickupAllOperation))));
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

    public static int swapOperation(CommandContext<CommandSourceStack> context) {
        int hotbarSlot = IntegerArgumentType.getInteger(context, "hotbar");
        sendGuiOperation(context, hotbarSlot, ClickType.SWAP);
        return 1;
    }

    public static int cloneOperation(CommandContext<CommandSourceStack> context) {
        sendGuiOperation(context, 0, ClickType.CLONE);
        return 1;
    }

    public static int quickCraftOperation(CommandContext<CommandSourceStack> context, int dragType) {
        sendGuiOperation(context, dragType, ClickType.QUICK_CRAFT);
        return 1;
    }

    public static int pickupAllOperation(CommandContext<CommandSourceStack> context) {
        sendGuiOperation(context, 0, ClickType.PICKUP);
        sendGuiOperation(context, 0, ClickType.PICKUP_ALL);
        return 1;
    }

    public static void sendGuiOperation(CommandContext<CommandSourceStack> context, int key, ClickType clickType) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode != null && mc.player != null) {
            mc.gameMode.handleInventoryMouseClick(mc.player.containerMenu.containerId, IntegerArgumentType.getInteger(context, "slot"), key, clickType, mc.player);
        }
    }
}