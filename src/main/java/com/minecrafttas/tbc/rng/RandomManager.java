package com.minecrafttas.tbc.rng;

import lombok.Getter;

import java.util.*;

public class RandomManager extends Random {
//    @Setter private static boolean locked = false;
//    @Setter private static int value;

//    public enum Stability {
//        STABLE,
//        SESSION,
//        VOLATILE
//    }

    @Getter private final long id;

    private RandomManager(RandomTypes type, long id) {
        super(seedOf(type, id));
        this.id = id;
    }

    private static long seedOf(RandomTypes type, long id) {
        return ((id & 0xFFFFL) << 32) | (type.getName().hashCode() & 0xFFFFFFFFL);
    }

    public static RandomManager create(RandomTypes type) {
        RandomPool pool = RandomPool.POOLS.computeIfAbsent(type, t -> new RandomPool());

        long id = ++pool.randomId;
        RandomManager r = new RandomManager(type, id);
        pool.registry.put(r.getId(), new RandomPool.Entry(r, pool.queue));

        if ((++pool.ops & 0x1F) == 0) pool.drain();
        return r;
    }

//    @Override
//    protected int next(int bits) {
//        if (locked) return value & ((1 << bits) - 1);
//        return super.next(bits);
//    }
}