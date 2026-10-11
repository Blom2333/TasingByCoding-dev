package com.minecrafttas.tbc.rng;

import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A {@link Random} that stays addressable, so a TAS tool can find, seed, lock or manipulate it.
 * Instances are handed out by {@link #create(RandomTypes)} for sites that own one Random per object or per
 * call, and by {@link #createSite(RandomTypes)} for sites that only ever have a single one.
 */
public class RandomManager extends Random {

    /** The one instance of every VOLATILE site, see {@link #createSite(RandomTypes)} */
    private static final Map<RandomTypes, RandomManager> SITES = new ConcurrentHashMap<>();

    /**
     * The instance behind every replaced argument-less {@code Collections.shuffle(list)}. It is the shared
     * instance of {@link RandomTypes#SHUFFLE}, vanilla also uses one static Random for all of those sites.
     */
    public static final RandomManager COLLECTIONS_RANDOM = createSite(RandomTypes.SHUFFLE);

    @Getter private final long id;

    private RandomManager(RandomTypes type, long id) {
        super(seedOf(type, id));
        this.id = id;
    }

    /**
     * The seed of an instance, derived from the name of the site and the instance id. The salt of the site is
     * not folded into it yet, see {@link RandomTypes#getSalt()}.
     */
    private static long seedOf(RandomTypes type, long id) {
        long seed = id * 0x9E3779B97F4A7C15L + type.getSalt();
        seed ^= (seed >>> 33);
        seed *= 0xff51afd7ed558ccdL;
        seed ^= (seed >>> 33);
        return seed;
    }

    /**
     * Creates the next instance of a site that owns one Random per object or per call, so every call returns
     * a new instance with its own stream. Sites that have a single instance for their whole life must use
     * {@link #createSite(RandomTypes)} instead, otherwise they become one instance per call again.
     */
    public static RandomManager create(RandomTypes type) {
        if (type.getStability() == RandomTypes.Stability.VOLATILE) {
            throw new IllegalArgumentException("RandomManager.create() must not be used for the VOLATILE site");
        }
        return newInstance(type);
    }

    /**
     * The one and only instance of a {@link RandomTypes.Stability#VOLATILE} site. Vanilla creates a new
     * Random on every call there, which is neither reproducible nor addressable, so every call of that site
     * shares this instance instead: the stream is continuous, deterministic in call order, and a command can
     * seed or lock the whole site at once.
     */
    public static RandomManager createSite(RandomTypes type) {
        if (type.getStability() != RandomTypes.Stability.VOLATILE) {
            throw new IllegalArgumentException("RandomManager.site() only serves VOLATILE sites");
        }
        RandomManager r = SITES.get(type);
        return r != null ? r : site(type);
    }

    /** Creates one instance of a site and registers it in the pool of that site */
    private static RandomManager newInstance(RandomTypes type) {
        RandomPool pool = RandomPool.POOLS.computeIfAbsent(type, t -> new RandomPool());

        long id = pool.randomId.getAndIncrement();
        RandomManager r = new RandomManager(type, id);
        pool.registry.put(r.getId(), new RandomPool.Entry(r, pool.queue));

        if ((pool.ops.incrementAndGet() & 0x1F) == 0) pool.drain();
        return r;
    }

    /** Creates the shared instance of a site exactly once, even if several threads ask for it at the same time */
    private static synchronized RandomManager site(RandomTypes type) {
        RandomManager r = SITES.get(type);
        if (r == null) {
            r = newInstance(type);
            SITES.put(type, r);
        }
        return r;
    }

//    @Override
//    protected int next(int bits) {
//        if (locked) return value & ((1 << bits) - 1);
//        return super.next(bits);
//    }
}
