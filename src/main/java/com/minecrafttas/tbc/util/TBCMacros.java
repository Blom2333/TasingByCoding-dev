package com.minecrafttas.tbc.util;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;

public class TBCMacros {
    public static ArrayList<TBCMacros> macroQueue = new ArrayList<>();

    @Getter private int duration;
    private final String[] keys;
    private final Float serverRotX;
    private final Float serverRotY;
    @Getter private ArrayList<String> runningCommands = new ArrayList<>();

    public TBCMacros(int duration, String keys, Float serverRotX, Float serverRotY) {
        this.duration = duration;
        this.keys = keys.split("");
        this.serverRotX = serverRotX;
        this.serverRotY = serverRotY;
    }

    public void addCommand(Component command) {
        runningCommands.add(command.getString());
    }

    public void runScript(Options options) {
        Player player = Minecraft.getInstance().player;
        if (player != null && keys.length > 0) {
            for (String key : keys) {
                switch (key) {
                    case "w": options.keyUp.setDown(true); break;
                    case "d": options.keyRight.setDown(true); break;
                    case "s": options.keyDown.setDown(true); break;
                    case "a": options.keyLeft.setDown(true); break;
                    case "S": options.keyShift.setDown(true); break;
                    case "R": options.keySprint.setDown(true); break;
                    case "J": options.keyJump.setDown(true); break;
                    case "P": options.keyUse.setDown(true); break;
                    case "O": options.keyPickItem.setDown(true); break;
                    case "I": options.keyAttack.setDown(true); break;
                    case "f": options.keySwapOffhand.setDown(true); break;
                    case "q": options.keyDrop.setDown(true); break;
                    case "e": options.keyInventory.setDown(true); break;
                }

                // special handling for left click
                //if (key.equals("I")) {

                //}
            }
            if (serverRotX != null) player.moveTo(player.getX(), player.getY(), player.getZ(), serverRotY, serverRotX);
            duration--;
        }
    }
}
