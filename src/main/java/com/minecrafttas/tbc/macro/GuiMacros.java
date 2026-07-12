package com.minecrafttas.tbc.macro;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.inventory.ClickType;

import java.util.ArrayList;

public class GuiMacros {
    public static ArrayList<GuiMacros> macroQueue = new ArrayList<>();
    public static int defaultDelay = 5;
    public static int closeDelay = 0;
    public static int macroLength = 0;
    private static float macroInterval;
    public static int macroIndex = 1;

    private final int slot;
    private final int specialType;
    private final ClickType clickType;

    public GuiMacros(int slot, ClickType clickType, int specialType) {
        this.slot = slot;
        this.clickType = clickType;
        this.specialType = specialType;
    }

    public void sendGuiMacro(Minecraft instance) {
        instance.gameMode.handleInventoryMouseClick(instance.player.containerMenu.containerId, slot, specialType, clickType, instance.player);
    }

    public static int addGuiMacro(CommandContext<CommandSourceStack> context, ClickType clickType, int specialType) {
        addGuiMacro(IntegerArgumentType.getInteger(context, "slot"), clickType, specialType);
        return 1;
    }

    public static void addGuiMacro(int slot, ClickType clickType, int specialType) {
        macroQueue.add(new GuiMacros(slot, clickType, specialType));
        macroLength++;
        macroInterval = (float) (GuiMacros.defaultDelay + 1) / (macroLength + 1);
    }

    public static void whenGuiOpen(Minecraft instance) {
        while (GuiMacros.closeDelay + Minecraft.getInstance().getFrameTime() >= macroIndex * macroInterval) {
            macroQueue.get(0).sendGuiMacro(instance);
            macroQueue.remove(0);
            macroIndex++;
        }
    }

    public static void runAllMacros(Minecraft instance) {
        for (GuiMacros macro : macroQueue) {
            macro.sendGuiMacro(instance);
        }
        macroLength = 0;
        macroIndex = 1;
        macroQueue.clear();
    }
}