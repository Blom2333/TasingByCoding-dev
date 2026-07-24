package com.minecrafttas.tbc.macro;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import java.util.ArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.command.ServerCommandSource;

public class GuiMacros {
    public static ArrayList<GuiMacros> macroQueue = new ArrayList<>();
    public static int defaultDelay = 5;
    public static int closeDelay = 0;
    public static int macroLength = 0;
    private static float macroInterval;
    public static int macroIndex = 1;

    private final int slot;
    private final int specialType;
    private final SlotActionType clickType;

    public GuiMacros(int slot, SlotActionType clickType, int specialType) {
        this.slot = slot;
        this.clickType = clickType;
        this.specialType = specialType;
    }

    public void sendGuiMacro(MinecraftClient instance) {
        instance.interactionManager.clickSlot(instance.player.currentScreenHandler.syncId, slot, specialType, clickType, instance.player);
    }

    public static int addGuiMacro(CommandContext<ServerCommandSource> context, SlotActionType clickType, int specialType) {
        addGuiMacro(IntegerArgumentType.getInteger(context, "slot"), clickType, specialType);
        return 1;
    }

    public static void addGuiMacro(int slot, SlotActionType clickType, int specialType) {
        macroQueue.add(new GuiMacros(slot, clickType, specialType));
        macroLength++;
        macroInterval = (float) (GuiMacros.defaultDelay + 1) / (macroLength + 1);
    }

    public static void whenGuiOpen(MinecraftClient instance) {
        while (GuiMacros.closeDelay + MinecraftClient.getInstance().getTickDelta() >= macroIndex * macroInterval) {
            macroQueue.get(0).sendGuiMacro(instance);
            macroQueue.remove(0);
            macroIndex++;
        }
    }

    public static void runAllMacros(MinecraftClient instance) {
        for (GuiMacros macro : macroQueue) {
            macro.sendGuiMacro(instance);
        }
        macroLength = 0;
        macroIndex = 1;
        macroQueue.clear();
    }
}