package com.minecrafttas.tbc.rng;

import lombok.Setter;

import java.util.Random;

public class RandomManager extends Random {

    @Setter private static boolean locked = false;
    @Setter private static int value;

    @Override
    protected int next(int bits) {
        if (locked) return value & ((1 << bits) - 1);
        return super.next(bits);
    }
}