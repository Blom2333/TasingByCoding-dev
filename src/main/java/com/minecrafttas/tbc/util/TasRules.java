package com.minecrafttas.tbc.util;

import lombok.Getter;
import lombok.Setter;

public enum TasRules {
    RULE_VISUAL_ROTATING_SPEED("visualRotatingSpeed", 1),
    RULE_GUI_OPENING_DURATION("guiOpeningDuration", 1);

    public final String name;
    @Getter @Setter public Object value;

    TasRules(String name, Object value) {
        this.name = name;
        this.value = value;
    }
}