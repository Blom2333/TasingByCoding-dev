package com.minecrafttas.tbc.commands;

import com.minecrafttas.tbc.TasCommand;
import com.minecrafttas.tbc.commands.arguments.KeyCodeArgument;
import com.minecrafttas.tbc.macro.GuiMacros;
import com.minecrafttas.tbc.macro.OperMacros;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;

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
            .then(Commands.argument("keys", KeyCodeArgument.keyCode())
            .executes(context -> pressKeys(context,
                    KeyCodeArgument.getKeyCode(context, "keys")))
            .then(Commands.argument("serverRotation", RotationArgument.rotation())
            .executes(context -> pressKeys(context,
                    KeyCodeArgument.getKeyCode(context, "keys"),
                    RotationArgument.getRotation(context, "serverRotation")))
            .then(Commands.argument("clientRotation", RotationArgument.rotation())
            .executes(context -> pressKeys(context,
                    KeyCodeArgument.getKeyCode(context, "keys"),
                    RotationArgument.getRotation(context, "serverRotation"),
                    RotationArgument.getRotation(context, "clientRotation"))))
            ))));
    }

    private static void registerGui() {
        builder.then(Commands.literal("gui")
            .then(Commands.argument("slot", IntegerArgumentType.integer())

            // PICKUP
            .then(Commands.literal("pick")
            .executes(context -> GuiMacros.addGuiMacro(context, ClickType.PICKUP, 0))
            .then(Commands.literal("leftclick")
            .executes(context -> GuiMacros.addGuiMacro(context, ClickType.PICKUP, 0)))
            .then(Commands.literal("rightclick")
            .executes(context -> GuiMacros.addGuiMacro(context, ClickType.PICKUP, 1))))

            // QUICK_MOVE
            .then(Commands.literal("quickmove")
            .executes(context -> GuiMacros.addGuiMacro(context, ClickType.QUICK_MOVE, 0)))

            // SWAP
            .then(Commands.literal("swap")
                    .executes(context -> GuiMacros.addGuiMacro(context, ClickType.SWAP, 40))
            .then(Commands.argument("slot", IntegerArgumentType.integer())
            .executes(context -> GuiMacros.addGuiMacro(context, ClickType.SWAP, IntegerArgumentType.getInteger(context, "slot")))))

            // CLONE
            .then(Commands.literal("clone")
            .executes(context -> GuiMacros.addGuiMacro(context, ClickType.CLONE, 0)))

            // THROW
            .then(Commands.literal("throw")
            .executes(context -> GuiMacros.addGuiMacro(context, ClickType.THROW, 1))
            .then(Commands.argument("times", IntegerArgumentType.integer(0))
            .executes(context -> GuiMacros.addGuiMacro(context, ClickType.THROW, IntegerArgumentType.getInteger(context, "times")))))

            // QUICK_CRAFT
            .then(Commands.literal("drag")
            .executes(context -> GuiMacros.addGuiMacro(context, ClickType.QUICK_CRAFT, 1))
            .then(Commands.literal("start")
            .executes(context -> GuiMacros.addGuiMacro(context, ClickType.QUICK_CRAFT, 0)))
            .then(Commands.literal("end")
            .executes(context -> GuiMacros.addGuiMacro(context, ClickType.QUICK_CRAFT, 2))))

            // PICKUP_ALL
            .then(Commands.literal("pickupall")
            .executes(context -> pickupAllOperation(context, false))
            .then(Commands.argument("doQuickPick", BoolArgumentType.bool())
            .executes(context -> pickupAllOperation(context, BoolArgumentType.getBool(context, "doQuickPick"))
        )))));
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
        OperMacros macro = new OperMacros(IntegerArgumentType.getInteger(context, "duration"), keys, null, null);
        OperMacros.macroQueue.add(macro);
        if (OperMacros.macroQueue.size() == 1) {
            macro.startRotate();
        }
        return 1;
    }

    private static int pressKeys(CommandContext<CommandSourceStack> context, String keys, Coordinates serverRotation) {
        pressKeys(context, keys, serverRotation, serverRotation);
        return 1;
    }

    private static int pressKeys(CommandContext<CommandSourceStack> context, String keys, Coordinates serverRotation, Coordinates clientRotation) {
        OperMacros macro = new OperMacros(
                IntegerArgumentType.getInteger(context, "duration"),
                keys,
                serverRotation.getRotation(context.getSource()),
                clientRotation.getRotation(context.getSource())
        );
        OperMacros.macroQueue.add(macro);
        if (OperMacros.macroQueue.size() == 1) {
            macro.startRotate();
        }
        return 1;
    }

    public static int pickupAllOperation(CommandContext<CommandSourceStack> context, boolean doQuickPick) {
        if (!doQuickPick) {
            GuiMacros.addGuiMacro(context, ClickType.PICKUP, 0);
            GuiMacros.addGuiMacro(context, ClickType.PICKUP_ALL, 0);
        } else {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.gameMode != null && mc.screen != null) {
                Slot targetSlot = mc.player.containerMenu.slots.get(IntegerArgumentType.getInteger(context, "slot"));
                Item targetItem = targetSlot.getItem().getItem();
                for (Slot slot : mc.player.containerMenu.slots) {
                    if (slot.getItem().getItem() == targetItem && slot.container == targetSlot.container) {
                        GuiMacros.addGuiMacro(slot.index, ClickType.QUICK_MOVE, 0);
                    }
                }
            }
        }
        return 1;
    }

    private static int addCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (!OperMacros.macroQueue.isEmpty()) {
            OperMacros.macroQueue.get(OperMacros.macroQueue.size() - 1).addCommand(MessageArgument.getMessage(context, "command"));
        } else {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.chat("/" + MessageArgument.getMessage(context, "command").getString());
            }
        }
        return 1;
    }

    private static int stopMacros(CommandContext<CommandSourceStack> context) {
        OperMacros.macroQueue.clear();
        return 1;
    }
}
