package com.minecrafttas.tbc.rng;

import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A {@link Random} that stays addressable, so a TAS tool can find, seed, lock or manipulate it later.
 * Instances are handed out by {@link #create(RandomTypes)} for sites that own one Random per object or per
 * call, and by {@link #site(RandomTypes)} for sites that only ever have a single one.
 */
public class RandomManager extends Random {

    /** The one instance of every VOLATILE site, see {@link #site(RandomTypes)} */
    private static final Map<RandomTypes, RandomManager> SITES = new ConcurrentHashMap<>();

    /**
     * The instance behind every replaced argument-less {@code Collections.shuffle(list)}. It is the shared
     * instance of {@link RandomTypes#SHUFFLE}, vanilla also uses one static Random for all of those sites.
     */
    public static final RandomManager COLLECTIONS_RANDOM = site(RandomTypes.SHUFFLE);

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

    /**
     * The seed of an instance, derived from the name of the site and the instance id. The salt of the site is
     * not folded into it yet, see {@link RandomTypes#getSalt()}.
     */
    private static long seedOf(RandomTypes type, long id) {
        return ((id & 0xFFFFL) << 32) | (type.getName().hashCode() & 0xFFFFFFFFL);
    }

    /**
     * Creates the next instance of a site that owns one Random per object or per call, so every call returns
     * a new instance with its own stream. Sites that have a single instance for their whole life must use
     * {@link #site(RandomTypes)} instead, otherwise they become one instance per call again.
     *
     * @throws IllegalArgumentException if the type is VOLATILE, those sites must stay at one instance
     */
    public static RandomManager create(RandomTypes type) {
        if (type.getStability() == RandomTypes.Stability.VOLATILE) {
            throw new IllegalArgumentException("RandomManager.create() must not be used for the VOLATILE site "
                    + type + " - it has exactly one instance, use site() instead");
        }
        return newInstance(type);
    }

    /** Creates one instance of a site and registers it in the pool of that site */
    private static RandomManager newInstance(RandomTypes type) {
        RandomPool pool = poolOf(type);

        long id = pool.randomId.getAndIncrement();
        RandomManager r = new RandomManager(type, id);
        pool.registry.put(r.getId(), new RandomPool.Entry(r, pool.queue));

        if ((pool.ops.incrementAndGet() & 0x1F) == 0) pool.drain();
        return r;
    }

    /**
     * The one and only instance of a {@link RandomTypes.Stability#VOLATILE} site. Vanilla creates a new
     * Random on every call there, which is neither reproducible nor addressable, so every call of that site
     * shares this instance instead: the stream is continuous, deterministic in call order, and a command can
     * seed or lock the whole site at once.
     *
     * @throws IllegalArgumentException if the type is not VOLATILE, those sites need {@link #create(RandomTypes)}
     */
    public static RandomManager site(RandomTypes type) {
        if (type.getStability() != RandomTypes.Stability.VOLATILE) {
            throw new IllegalArgumentException("RandomManager.site() only serves VOLATILE sites, " + type
                    + " is " + type.getStability() + " - use create() instead");
        }
        RandomManager r = SITES.get(type);
        return r != null ? r : createSite(type);
    }

    /** Creates the shared instance of a site exactly once, even if several threads ask for it at the same time */
    private static synchronized RandomManager createSite(RandomTypes type) {
        RandomManager r = SITES.get(type);
        if (r == null) {
            r = newInstance(type);
            SITES.put(type, r);
        }
        return r;
    }

    private static RandomPool poolOf(RandomTypes type) {
        return RandomPool.POOLS.computeIfAbsent(type, t -> new RandomPool());
    }

//    @Override
//    protected int next(int bits) {
//        if (locked) return value & ((1 << bits) - 1);
//        return super.next(bits);
//    }
}
