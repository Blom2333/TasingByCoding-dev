package com.minecrafttas.tbc.core;

import com.minecrafttas.tbc.TasCommand;
import com.minecrafttas.tbc.util.KeyBindingMapper;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

public class TasSettingsCommand {
    private static final LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("settings");

    public static void register() {
        for (String key : Objects.requireNonNull(getKeys()).keySet()) {
            registerBind(key);
        }
        TasCommand.tas.then(builder);
    }

    private static void registerBind(String key) {
        builder.then(Commands.literal("bind")
            .then(Commands.literal(key)
            .then(Commands.argument("command", MessageArgument.message())
            .executes(context -> {
                try {
                    KeyBindingMapper.addKeybinding(InputConstants.getKey(key).getValue(), MessageArgument.getMessage(context, "command").getString());
                } catch (IOException e) {throw new RuntimeException("Cannot register bind command " + key, e);}
                return 1;
            }))))
            .then(Commands.literal("unbind")
            .then(Commands.literal(key)
            .executes(context -> {
                try {
                    KeyBindingMapper.removeKeybinding(InputConstants.getKey(key).getValue());
                } catch (IOException e) {throw new RuntimeException("Cannot register unbind command " + key, e);}
                return 1;
            })));
    }

    private static Map<String, InputConstants.Key> getKeys() {
        try {
            Field nameMapField = InputConstants.Key.class.getDeclaredField("NAME_MAP");
            nameMapField.setAccessible(true);

            @SuppressWarnings("unchecked")
            Map<String, InputConstants.Key> nameMap = (Map<String, InputConstants.Key>) nameMapField.get(null);

            return nameMap;
        } catch (Exception e) {e.printStackTrace();}
        return null;
    }
}
