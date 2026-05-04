package com.minecrafttas.tbc.mixin;

import com.minecrafttas.tbc.util.macro.OperMacros;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class MixinCamera {
    @Shadow protected abstract void setRotation(float f, float g);

    @Inject(method = "setup", at = @At(value = "TAIL"))
    private void doLerpRotate(BlockGetter blockGetter, Entity entity, boolean bl, boolean bl2, float f, CallbackInfo ci) {
        if (!OperMacros.macroQueue.isEmpty()) {
            Vec2 rot = OperMacros.getAngleLerp();
            setRotation(rot.y, rot.x);
        }
    }
}
