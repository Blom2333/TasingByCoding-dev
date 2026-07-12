package com.minecrafttas.tbc.rng;

import lombok.Setter;

import java.util.Random;

public class GlobalRandom extends Random {
    @Setter private static boolean locked = false;
    @Setter private static int value;
    private static int from;
    private static int to;
    private int time;

    public static void stepTick() {
        if (to == 0) locked = false;
        from--;to--;
        if (from == 0) locked = true;
    }

    @Override
    protected int next(int bits) {
        if (locked) return value & ((1 << bits) - 1);
        return super.next(bits);
    }
}
