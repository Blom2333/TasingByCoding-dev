package com.minecrafttas.tbc.rng;

import java.util.Random;

/**
 * {@link net.minecraft.entity.Entity} has a very special randomness:
 * the initializing is processed when chunks loaded, which is not thread-safe;
 * AI of some mobs are complicated.
 * So it needs an independent algorithm for fixing and manipulating it.
 */
public class EntityRandomManager extends Random {
    public static long latestId = 0;

    public static final ThreadLocal<Long> CURRENT_ID = new ThreadLocal<>();

    public EntityRandomManager() {
        super(seedOf(latestId++));
    }

    public EntityRandomManager(long id) {
        super(seedOf(id));
    }

    private static long seedOf(long id) {
        return id * 0x9E3779B97F4A7C15L;
    }
}
