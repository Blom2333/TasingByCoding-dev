package com.minecrafttas.tbc.mixin.oper;

import com.minecrafttas.tbc.macro.OperMacros;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class MixinCamera {
    @Shadow protected abstract void setRotation(float f, float g);

    @Shadow
    private float xRot;

    @Shadow
    private float yRot;

    @Inject(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V"))
    private void doLerpRotate(BlockView blockGetter, Entity entity, boolean bl, boolean bl2, float f, CallbackInfo ci) {
        if (!OperMacros.macroQueue.isEmpty()) {
            Vec2f rot = OperMacros.getAngleLerp();
            if (rot == null) return;
            setRotation(rot.y, rot.x);
        }
    }
}
