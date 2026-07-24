package com.minecrafttas.tbc.mixin.oper;

import com.minecrafttas.tbc.macro.OperMacros;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Camera.class)
public abstract class MixinCamera {
    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Shadow private float pitch;
    @Shadow private float yaw;

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"))
    private void doLerpRotate(Camera instance, float yaw, float pitch) {
        if (!OperMacros.macroQueue.isEmpty()) {
            Vec2f rot = OperMacros.getAngleLerp();
            if (rot == null) return;
            setRotation(rot.y, rot.x);
        }
    }
}
