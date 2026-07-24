package com.minecrafttas.tbc.macro;

import com.minecrafttas.tbc.mixin.oper.AccessKeyMapping;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import java.util.ArrayList;
import java.util.HashMap;

public class OperMacros {
    private static final HashMap<Character, KeyBinding> KEY_CODES = new HashMap<>();
    static {
        // optimization needed here
        GameOptions opt = MinecraftClient.getInstance().options;
        KEY_CODES.put('w', opt.keyForward);
        KEY_CODES.put('s', opt.keyBack);
        KEY_CODES.put('a', opt.keyLeft);
        KEY_CODES.put('d', opt.keyRight);
        KEY_CODES.put('J', opt.keyJump);
        KEY_CODES.put('S', opt.keySneak);
        KEY_CODES.put('R', opt.keySprint);
        KEY_CODES.put('e', opt.keyInventory);
        KEY_CODES.put('f', opt.keySwapHands);
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
            KEY_CODES.put(i, opt.keysHotbar[i-'1']);
        }
    }

    public static ArrayList<OperMacros> macroQueue = new ArrayList<>();
    public static int defaultDelay = 5;
    public static int lerpDelay = 0;
    public static float lastPitchRot;
    public static float lastYawRot;

    private int duration;
    private final char[] keys;
    private final Vec2f serverRot;
    @Getter private final Vec2f clientRot;
    private final ArrayList<String> runningCommands = new ArrayList<>();

    public OperMacros(int duration, String keys, Vec2f serverRot, Vec2f clientRot) {
        this.duration = duration;
        this.keys = keys.toCharArray();
        this.serverRot = serverRot;
        this.clientRot = clientRot;
    }

    public void addCommand(Text command) {
        runningCommands.add(command.getString());
    }

    public void runScript() {
       for (char keyCode : keys) {
           KeyBinding key = KEY_CODES.get(keyCode);
           if (key != null) {
               KeyBinding.onKeyPressed(((AccessKeyMapping) key).boundKey());
               key.setPressed(true);
           }
       }
       if (lerpDelay < defaultDelay) lerpDelay++;
       duration--;
    }

    public void startRotate() {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        if (serverRot != null) player.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), serverRot.y, serverRot.x);
        if (clientRot != null) {
            Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
            lastPitchRot = camera.getPitch();
            lastYawRot = (camera.getYaw() + 180) % 360 - 180;
            lerpDelay = 0;
        }
    }

    public void onMacroEnd() {
        if (duration == 0) {
            for (String command : runningCommands) {
                MinecraftClient.getInstance().player.sendChatMessage("/" + command);
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
        return (float) MathHelper.lerp(MathHelper.perlinFade(t), a, b);
    }

    public static Vec2f getAngleLerp() {
        Vec2f clientRot = macroQueue.get(0).getClientRot();
        if (clientRot == null) return null;
        if (lerpDelay == defaultDelay) return new Vec2f(clientRot.x, clientRot.y);
        float t = (lerpDelay + MinecraftClient.getInstance().getTickDelta()) / defaultDelay;
        return new Vec2f(
                smoothLerp(lastPitchRot, clientRot.x, t, false),
                smoothLerp(lastYawRot, clientRot.y, t, true)
        );
    }
}