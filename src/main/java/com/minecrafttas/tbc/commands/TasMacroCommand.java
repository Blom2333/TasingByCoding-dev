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
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.command.arguments.PosArgument;
import net.minecraft.command.arguments.RotationArgumentType;
import net.minecraft.command.arguments.TimeArgumentType;
import net.minecraft.item.Item;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class TasMacroCommand {
    private static final LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.literal("macro");

    public static void register() {
        registerPress();
        registerGui();
        registerRunCommand();
        registerStop();
        TasCommand.tas.then(builder);
    }

    private static void registerPress() {
        builder.then(CommandManager.literal("press")
            .then(CommandManager.argument("duration", TimeArgumentType.time())
            .executes(context -> pressKeys(context, ""))
            .then(CommandManager.argument("keys", KeyCodeArgument.keyCode())
            .executes(context -> pressKeys(context,
                    KeyCodeArgument.getKeyCode(context, "keys")))
            .then(CommandManager.argument("serverRotation", RotationArgumentType.rotation())
            .executes(context -> pressKeys(context,
                    KeyCodeArgument.getKeyCode(context, "keys"),
                    RotationArgumentType.getRotation(context, "serverRotation")))
            .then(CommandManager.argument("clientRotation", RotationArgumentType.rotation())
            .executes(context -> pressKeys(context,
                    KeyCodeArgument.getKeyCode(context, "keys"),
                    RotationArgumentType.getRotation(context, "serverRotation"),
                    RotationArgumentType.getRotation(context, "clientRotation"))))
            ))));
    }

    private static void registerGui() {
        builder.then(CommandManager.literal("gui")
            .then(CommandManager.argument("slot", IntegerArgumentType.integer())

            // PICKUP
            .then(CommandManager.literal("pick")
            .executes(context -> GuiMacros.addGuiMacro(context, SlotActionType.PICKUP, 0))
            .then(CommandManager.literal("leftclick")
            .executes(context -> GuiMacros.addGuiMacro(context, SlotActionType.PICKUP, 0)))
            .then(CommandManager.literal("rightclick")
            .executes(context -> GuiMacros.addGuiMacro(context, SlotActionType.PICKUP, 1))))

            // QUICK_MOVE
            .then(CommandManager.literal("quickmove")
            .executes(context -> GuiMacros.addGuiMacro(context, SlotActionType.QUICK_MOVE, 0)))

            // SWAP
            .then(CommandManager.literal("swap")
                    .executes(context -> GuiMacros.addGuiMacro(context, SlotActionType.SWAP, 40))
            .then(CommandManager.argument("slot", IntegerArgumentType.integer())
            .executes(context -> GuiMacros.addGuiMacro(context, SlotActionType.SWAP, IntegerArgumentType.getInteger(context, "slot")))))

            // CLONE
            .then(CommandManager.literal("clone")
            .executes(context -> GuiMacros.addGuiMacro(context, SlotActionType.CLONE, 0)))

            // THROW
            .then(CommandManager.literal("throw")
            .executes(context -> GuiMacros.addGuiMacro(context, SlotActionType.THROW, 1))
            .then(CommandManager.argument("times", IntegerArgumentType.integer(0))
            .executes(context -> GuiMacros.addGuiMacro(context, SlotActionType.THROW, IntegerArgumentType.getInteger(context, "times")))))

            // QUICK_CRAFT
            .then(CommandManager.literal("drag")
            .executes(context -> GuiMacros.addGuiMacro(context, SlotActionType.QUICK_CRAFT, 1))
            .then(CommandManager.literal("start")
            .executes(context -> GuiMacros.addGuiMacro(context, SlotActionType.QUICK_CRAFT, 0)))
            .then(CommandManager.literal("end")
            .executes(context -> GuiMacros.addGuiMacro(context, SlotActionType.QUICK_CRAFT, 2))))

            // PICKUP_ALL
            .then(CommandManager.literal("pickupall")
            .executes(context -> pickupAllOperation(context, false))
            .then(CommandManager.argument("doQuickPick", BoolArgumentType.bool())
            .executes(context -> pickupAllOperation(context, BoolArgumentType.getBool(context, "doQuickPick"))
        )))));
    }

    private static void registerRunCommand() {
        builder.then(CommandManager.literal("command")
            .then(CommandManager.argument("command", MessageArgumentType.message())
            .executes(TasMacroCommand::addCommand)));
    }

    private static void registerStop() {
        builder.then(CommandManager.literal("stop")
            .executes(TasMacroCommand::stopMacros));
    }

    private static int pressKeys(CommandContext<ServerCommandSource> context, String keys) {
        OperMacros macro = new OperMacros(IntegerArgumentType.getInteger(context, "duration"), keys, null, null);
        OperMacros.macroQueue.add(macro);
        if (OperMacros.macroQueue.size() == 1) {
            macro.startRotate();
        }
        return 1;
    }

    private static int pressKeys(CommandContext<ServerCommandSource> context, String keys, PosArgument serverRotation) {
        pressKeys(context, keys, serverRotation, serverRotation);
        return 1;
    }

    private static int pressKeys(CommandContext<ServerCommandSource> context, String keys, PosArgument serverRotation, PosArgument clientRotation) {
        OperMacros macro = new OperMacros(
                IntegerArgumentType.getInteger(context, "duration"),
                keys,
                serverRotation.toAbsoluteRotation(context.getSource()),
                clientRotation.toAbsoluteRotation(context.getSource())
        );
        OperMacros.macroQueue.add(macro);
        if (OperMacros.macroQueue.size() == 1) {
            macro.startRotate();
        }
        return 1;
    }

    public static int pickupAllOperation(CommandContext<ServerCommandSource> context, boolean doQuickPick) {
        if (!doQuickPick) {
            GuiMacros.addGuiMacro(context, SlotActionType.PICKUP, 0);
            GuiMacros.addGuiMacro(context, SlotActionType.PICKUP_ALL, 0);
        } else {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null && mc.interactionManager != null && mc.currentScreen != null) {
                Slot targetSlot = mc.player.currentScreenHandler.slots.get(IntegerArgumentType.getInteger(context, "slot"));
                Item targetItem = targetSlot.getStack().getItem();
                for (Slot slot : mc.player.currentScreenHandler.slots) {
                    if (slot.getStack().getItem() == targetItem && slot.inventory == targetSlot.inventory) {
                        GuiMacros.addGuiMacro(slot.id, SlotActionType.QUICK_MOVE, 0);
                    }
                }
            }
        }
        return 1;
    }

    private static int addCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (!OperMacros.macroQueue.isEmpty()) {
            OperMacros.macroQueue.get(OperMacros.macroQueue.size() - 1).addCommand(MessageArgumentType.getMessage(context, "command"));
        } else {
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.sendChatMessage("/" + MessageArgumentType.getMessage(context, "command").getString());
            }
        }
        return 1;
    }

    private static int stopMacros(CommandContext<ServerCommandSource> context) {
        OperMacros.macroQueue.clear();
        return 1;
    }
}
