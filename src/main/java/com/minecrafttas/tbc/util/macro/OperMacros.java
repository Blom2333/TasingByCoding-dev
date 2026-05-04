package com.minecrafttas.tbc.util.macro;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;

public class OperMacros {
    public static ArrayList<OperMacros> macroQueue = new ArrayList<>();
    public static int defaultDelay = 10;
    public static int lerpDelay = 0;
    public static float lastXRot = 0.0f;
    public static float lastYRot = 0.0f;

    private int duration;
    private final String[] keys;
    private final Vec2 serverRot;
    @Getter private final Vec2 clientRot;
    private final ArrayList<String> runningCommands = new ArrayList<>();

    public OperMacros(int duration, String keys, Vec2 serverRot, Vec2 clientRot) {
        this.duration = duration;
        this.keys = keys.split("");
        this.serverRot = serverRot;
        this.clientRot = clientRot;
    }

    public void addCommand(Component command) {
        runningCommands.add(command.getString());
    }

    public void runScript(Options options) {
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

       if (lerpDelay < defaultDelay) lerpDelay++;
       duration--;
    }

    public void startRotate() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        if (serverRot != null) player.moveTo(player.getX(), player.getY(), player.getZ(), serverRot.y, serverRot.x);
        if (clientRot != null) {
            lastXRot = Minecraft.getInstance().gameRenderer.getMainCamera().getXRot();
            lastYRot = Minecraft.getInstance().gameRenderer.getMainCamera().getYRot();
            OperMacros.lerpDelay = 0;
        }
    }

    public void onMacroEnd() {
        if (duration == 0) {
            for (String command : runningCommands) {
                Minecraft.getInstance().player.chat("/" + command);
            }
            macroQueue.remove(0);

            if (!macroQueue.isEmpty()) {
                macroQueue.get(0).startRotate();
            }
        }
    }

    public static float smoothLerp(float a, float b, float t) {
        float diff = a - b;
        if (diff <= -180) {
            a += 360;
        } else if (diff > 180) {
            b += 360;
        }
        return a + (b - a) * t * t * (3 - 2 * t);
    }

    public static Vec2 getAngleLerp() {
        float t = (lerpDelay + Minecraft.getInstance().getFrameTime()) / defaultDelay;
        Vec2 clientRot = macroQueue.get(0).getClientRot();
        return new Vec2(
                smoothLerp(lastXRot, clientRot.x, t),
                smoothLerp(lastYRot, clientRot.y, t)
        );
    }
}