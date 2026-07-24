package com.minecrafttas.tbc.mixin.oper;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBinding.class)
public interface AccessKeyMapping {
    @Accessor InputUtil.Key getBoundKey();
}
