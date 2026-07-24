package com.minecrafttas.tbc.commands;

public class TasDeclareCommand {

    public enum DeclarationType {
        GLITCHLESS("glitchless"),
        HUMAN_LIMIT("humanLimit"),
        NO_STRUCTURE("noStructure"),
        NO_F3("noF3");

        public final String name;

        DeclarationType(String name) {
            this.name = name;
        }
    }
}
