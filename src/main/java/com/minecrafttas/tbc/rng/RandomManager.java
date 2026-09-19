package com.minecrafttas.tbc.rng;

import lombok.Getter;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class RandomManager extends Random {
//    @Setter private static boolean locked = false;
//    @Setter private static int value;

    public enum Stability {
        STABLE,
        SESSION,
        VOLATILE
    }

    private static final Map<Long, Entry> REGISTRY = new ConcurrentHashMap<>();
    private static final ReferenceQueue<RandomManager> QUEUE = new ReferenceQueue<>();
    private static final AtomicLong RID = new AtomicLong();
    private static final AtomicLong OPS = new AtomicLong();

    private static final class Entry extends WeakReference<RandomManager> {
        final long id;
        Entry(RandomManager r, ReferenceQueue<RandomManager> q) { super(r, q); this.id = r.id; }
    }

    @Getter private final long id;

    private RandomManager(RandomTypes type, long id) {
        super(seedOf(type, id));
        this.id = id;
    }

    private static long seedOf(RandomTypes type, long id) {
        return ((id & 0xFFFFL) << 32) | (type.getName().hashCode() & 0xFFFFFFFFL);
    }

    public static RandomManager create(RandomTypes type) {
        long id = RID.incrementAndGet();
        RandomManager r = new RandomManager(type, id);
        if ((OPS.incrementAndGet() & 0x1F) == 0) drain();
        REGISTRY.put(r.id, new Entry(r, QUEUE));
        return r;
    }

    public static RandomManager byId(long id) {
        Entry e = REGISTRY.get(id);
        return e == null ? null : e.get();  // handling null
    }

    public static List<RandomManager> all() {
        drain();
        return REGISTRY.values().stream()
                .map(Entry::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(RandomManager::getId))
                .collect(Collectors.toList());
    }

    private static void drain() {
        Reference<? extends RandomManager> ref;
        while ((ref = QUEUE.poll()) != null) {
            REGISTRY.remove(((Entry) ref).id);
        }
    }

//    @Override
//    protected int next(int bits) {
//        if (locked) return value & ((1 << bits) - 1);
//        return super.next(bits);
//    }
}