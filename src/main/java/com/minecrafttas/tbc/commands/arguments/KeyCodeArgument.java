package com.minecrafttas.tbc.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;

import java.util.Arrays;
import java.util.Collection;

public class KeyCodeArgument implements ArgumentType<String> {
    private static final Collection<String> EXAMPLES = Arrays.asList("Ssd", "%%I", "3PP", "B");

    private KeyCodeArgument() {
    }

    public static KeyCodeArgument keyCode() {
        return new KeyCodeArgument();
    }

    public static String getKeyCode(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse(final StringReader reader) {
        final int start = reader.getCursor();
        while (reader.canRead() && reader.peek() != ' ') {
            reader.skip();
        }

        if (reader.getCursor() == start) {
            return "";
        }

        return reader.getString().substring(start, reader.getCursor());
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public String toString() {
        return "keyCode()";
    }
}