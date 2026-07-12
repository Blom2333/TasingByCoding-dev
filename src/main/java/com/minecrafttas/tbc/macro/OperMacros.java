package com.minecrafttas.tbc.macro;

import com.minecrafttas.tbc.mixin.oper.AccessKeyMapping;
import lombok.Getter;
import net.minecraft.client.Camera;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.HashMap;

public class OperMacros {
    private static final HashMap<Character, KeyMapping> KEY_CODES = new HashMap<>();
    static {
        // optimization needed here
        Options opt = Minecraft.getInstance().options;
        KEY_CODES.put('w', opt.keyUp);
        KEY_CODES.put('s', opt.keyDown);
        KEY_CODES.put('a', opt.keyLeft);
        KEY_CODES.put('d', opt.keyRight);
        KEY_CODES.put('J', opt.keyJump);
        KEY_CODES.put('S', opt.keyShift);
        KEY_CODES.put('R', opt.keySprint);
        KEY_CODES.put('e', opt.keyInventory);
        KEY_CODES.put('f', opt.keySwapOffhand);
        KEY_CODES.put('q', opt.keyDrop);
        KEY_CODES.put('P', opt.keyUse);
        KEY_CODES.put('I', opt.keyAttack);
        KEY_CODES.put('O', opt.keyPickItem);
        KEY_CODES.put('t', opt.keyChat);
        KEY_CODES.put('T', opt.keyPlayerList);
        KEY_CODES.put('/', opt.keyCommand);
        KEY_CODES.put('@', opt.keyScreenshot);
        KEY_CODES.put('%', opt.keyTogglePerspective);
        KEY_CODES.put('C', opt.keySmoothCamera);
        KEY_CODES.put('F', opt.keyFullscreen);
        KEY_CODES.put('H', opt.keySpectatorOutlines);
        KEY_CODES.put('l', opt.keyAdvancements);
        // creative tab activator needed

        // slots
        for (char i = '1'; i <= '9'; i++) {
            KEY_CODES.put(i, opt.keyHotbarSlots[i-'1']);
        }
    }

    public static ArrayList<OperMacros> macroQueue = new ArrayList<>();
    public static int defaultDelay = 5;
    public static int lerpDelay = 0;
    public static float lastPitchRot;
    public static float lastYawRot;

    private int duration;
    private final char[] keys;
    private final Vec2 serverRot;
    @Getter private final Vec2 clientRot;
    private final ArrayList<String> runningCommands = new ArrayList<>();

    public OperMacros(int duration, String keys, Vec2 serverRot, Vec2 clientRot) {
        this.duration = duration;
        this.keys = keys.toCharArray();
        this.serverRot = serverRot;
        this.clientRot = clientRot;
    }

    public void addCommand(Component command) {
        runningCommands.add(command.getString());
    }

    public void runScript() {
       for (char keyCode : keys) {
           KeyMapping key = KEY_CODES.get(keyCode);
           if (key != null) KeyMapping.click(((AccessKeyMapping) key).getKey());
       }
       if (lerpDelay < defaultDelay) lerpDelay++;
       duration--;
    }

    public void startRotate() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        if (serverRot != null) player.moveTo(player.getX(), player.getY(), player.getZ(), serverRot.y, serverRot.x);
        if (clientRot != null) {
            Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            lastPitchRot = camera.getXRot();
            lastYawRot = (camera.getYRot() + 180) % 360 - 180;
            lerpDelay = 0;
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

    public static float smoothLerp(float a, float b, float t, boolean adjustRotDirection) {
        if (adjustRotDirection) {
            float diff = a - b;
            if (diff <= -180) {
                a += 360;
            } else if (diff > 180) {
                b += 360;
            }
        }
        return (float) Mth.lerp(Mth.smoothstep(t), a, b);
    }

    public static Vec2 getAngleLerp() {
        Vec2 clientRot = macroQueue.get(0).getClientRot();
        if (clientRot == null) return null;
        if (lerpDelay == defaultDelay) return new Vec2(clientRot.x, clientRot.y);
        float t = (lerpDelay + Minecraft.getInstance().getFrameTime()) / defaultDelay;
        return new Vec2(
                smoothLerp(lastPitchRot, clientRot.x, t, false),
                smoothLerp(lastYawRot, clientRot.y, t, true)
        );
    }
}