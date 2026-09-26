package com.minecrafttas.tbc.rng;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class RandomPool {
    public static final Map<RandomTypes, RandomPool> POOLS = new ConcurrentHashMap<>();

    protected final Map<Long, Entry> registry = new ConcurrentHashMap<>();
    protected final ReferenceQueue<RandomManager> queue = new ReferenceQueue<>();
    public final AtomicInteger randomId = new AtomicInteger();
    public final AtomicInteger ops = new AtomicInteger();

    public RandomPool() {}

    protected static final class Entry extends WeakReference<RandomManager> {
        final long id;
        Entry(RandomManager r, ReferenceQueue<RandomManager> q) { super(r, q); this.id = r.getId(); }
    }

    public RandomManager byId(long id) {
        Entry e = registry.get(id);
        return e == null ? null : e.get();  // handling null
    }

    public List<RandomManager> all() {
        drain();
        return registry.values().stream()
                .map(Entry::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(RandomManager::getId))
                .collect(Collectors.toList());
    }

    protected void drain() {
        Reference<? extends RandomManager> ref;
        while ((ref = queue.poll()) != null) {
            registry.remove(((Entry) ref).id);
        }
    }
}
