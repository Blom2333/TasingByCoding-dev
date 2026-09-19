package com.minecrafttas.tbc.rng;

import lombok.Getter;
import lombok.Setter;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 受控的 {@link java.util.Random}。
 *
 * <p>每个实例都有身份：{@code (站点, 实例键)}，站点来自 {@link RandomTypes}，实例键由调用点给出
 * （实体 uuid / entityId / 维度名 ...），拿不出来时由本站点的计数器分配 {@code #序号}。
 * 身份决定了：
 * <ul>
 *     <li>种子 —— {@code seedOf(site, index)}，与创建顺序无关（同一个站点的同一个序号永远是同一个种子）</li>
 *     <li>可寻址性 —— 指令可以按 {@code site} / {@code site + key} 定位到实例</li>
 * </ul>
 *
 * <p>操纵分三层：实例级注入序列 {@link #script(int...)} > 实例级锁定 {@link #lock(int)} >
 * 全局锁定 {@link #setLocked(boolean)}（保留给旧的 {@code tas rng global lock}）。
 */
public class RandomManagerDev extends Random {

    /** 站点实例的可定位程度，只影响展示/提示，不影响随机流 */
    public enum Stability {
        /** 每次运行产生的实例数量和顺序都确定，可以写死在脚本里 */
        STABLE,
        /** 数量随游戏进程变化，但实例带可读标识（uuid / entityId / raid id ...） */
        SESSION,
        /** 每次调用就新建，无法定位单个实例，只能对整个站点操作 */
        VOLATILE
    }

    // ------------------------------------------------------------------ 全局兜底

    @Setter private static boolean locked = false;
    @Setter private static int value;

    // ------------------------------------------------------------------ 注册表

    private static final Map<String, SitePool> POOLS = new ConcurrentHashMap<>();
    private static final Map<Long, WeakReference<RandomManager>> BY_ID = new ConcurrentHashMap<>();
    private static final ReferenceQueue<RandomManager> QUEUE = new ReferenceQueue<>();
    private static final AtomicLong RID = new AtomicLong();
    private static final AtomicLong OPS = new AtomicLong();

    /** 站点名下所有实例的容器 */
    public static final class SitePool {
        @Getter private final String name;
        @Getter private final Stability stability;
        private final AtomicLong counter = new AtomicLong();
        private final Map<String, WeakReference<RandomManager>> byKey = new ConcurrentHashMap<>();

        private SitePool(String name, Stability stability) {
            this.name = name;
            this.stability = stability;
        }

        /** 该站点当前活着（未被 GC）的实例，按序号排序 */
        public List<RandomManager> instances() {
            List<RandomManager> list = new ArrayList<>();
            for (WeakReference<RandomManager> ref : this.byKey.values()) {
                RandomManager random = ref.get();
                if (random != null) {
                    list.add(random);
                }
            }
            list.sort(Comparator.comparingLong(RandomManager::getIndex));
            return list;
        }

        public RandomManager byKey(String key) {
            WeakReference<RandomManager> ref = this.byKey.get(key);
            return ref == null ? null : ref.get();
        }

        public int size() {
            return instances().size();
        }

        @Override
        public String toString() {
            return this.name + "[" + this.stability + ", " + size() + " instance(s)]";
        }
    }

    /** 回收队列条目：同时记住 id / site / key，方便从两张表里摘掉 */
    private static final class Entry extends WeakReference<RandomManager> {
        final long id;
        final String site;
        final String key;

        Entry(RandomManager random, ReferenceQueue<RandomManager> queue) {
            super(random, queue);
            this.id = random.id;
            this.site = random.site;
            this.key = random.key;
        }
    }

    // ------------------------------------------------------------------ 实例身份

    @Getter private final long id;
    @Getter private final String site;
    @Getter private final String key;
    @Getter private final Stability stability;
    /** 站点内序号（决定默认种子） */
    @Getter private final long index;
    /** 创建时使用的种子，{@link #reset()} 会回到它 */
    @Getter private final long seed;
    /** 被 next() 消耗的次数，用来排查"这一步动了哪个随机源" */
    @Getter private long consumed;

    // 实例级操纵状态
    private Long forcedValue;
    /**
     * -- GETTER --
     * 剩余锁定次数，-1 表示永久
     */
    @Getter
    private int remaining = -1;
    private int[] script;
    private int scriptIndex;

    private RandomManager(long id, String site, String key, Stability stability, long index, long seed) {
        super(seed);
        this.id = id;
        this.site = site;
        this.key = key;
        this.stability = stability;
        this.index = index;
        this.seed = seed;
    }

    // ------------------------------------------------------------------ 创建

    /** 兼容旧调用：没有站点身份 */
    public static RandomManager create() {
        return create(RandomTypes.UNKNOWN);
    }

    public static RandomManager create(String site) {
        return create(site, null, null);
    }

    /**
     * @param key 实例键；{@code null} 时自动使用 {@code #序号}
     */
    public static RandomManager create(String site, String key) {
        return create(site, key, null);
    }

    /**
     * @param explicitSeed 显式种子；{@code null} 时由 {@link #seedOf(String, long)} 推导
     */
    public static RandomManager create(String site, String key, Long explicitSeed) {
        SitePool pool = POOLS.computeIfAbsent(site, name -> new SitePool(name, RandomTypes.stabilityOf(name)));
        long index = pool.counter.getAndIncrement();
        String realKey = key != null ? key : "#" + index;
        long seed = explicitSeed != null ? explicitSeed : seedOf(site, index);
        RandomManager random = new RandomManager(RID.incrementAndGet(), site, realKey, pool.stability, index, seed);
        Entry entry = new Entry(random, QUEUE);
        pool.byKey.putIfAbsent(realKey, entry);
        BY_ID.put(random.id, entry);
        if ((OPS.incrementAndGet() & 0x3FF) == 0) {
            drain();
        }
        return random;
    }

    /**
     * 48 位有效种子（{@code java.util.Random} 只用种子的低 48 位）：
     * 高 16 位放站内序号，低 32 位放站点名哈希。
     *
     * <p>序号放高位是有意的：{@code Random} 对相邻种子的首个输出是线性相关的
     * （{@code new Random(1)} 与 {@code new Random(2)} 的首输出只差一个常数），
     * 序号每 +1 让种子相差 2^32，乘上 LCG 常数后进入输出的高位，首输出不会呈现"相邻"特征。
     */
    private static long seedOf(String site, long index) {
        return ((index & 0xFFFFL) << 32) | (site.hashCode() & 0xFFFFFFFFL);
    }

    // ------------------------------------------------------------------ 查询

    public static RandomManager byId(long id) {
        WeakReference<RandomManager> ref = BY_ID.get(id);
        return ref == null ? null : ref.get();
    }

    /** 按站点 + 实例键查找，找不到返回 null */
    public static RandomManager byKey(String site, String key) {
        SitePool pool = POOLS.get(site);
        return pool == null ? null : pool.byKey(key);
    }

    public static SitePool pool(String site) {
        return POOLS.get(site);
    }

    /** 所有已知站点：声明过的 + 已经注册过实例的（排序，给指令补全用） */
    public static List<String> sites() {
        Set<String> names = new TreeSet<>(RandomTypes.all());
        names.addAll(POOLS.keySet());
        return new ArrayList<>(names);
    }

    public static List<RandomManager> all() {
        drain();
        return BY_ID.values().stream()
                .map(Reference::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingLong(RandomManager::getId))
                .collect(Collectors.toList());
    }

    /** 被消耗次数最多的前 n 个实例（排查用；只统计还活着的） */
    public static List<RandomManager> recent(int count) {
        return all().stream()
                .filter(random -> random.consumed > 0)
                .sorted(Comparator.comparingLong(RandomManager::getConsumed).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private static void drain() {
        Reference<? extends RandomManager> ref;
        if (pool != null) {
            pool.byKey.remove(entry.key, entry);
            while ((ref = QUEUE.poll()) != null) {
                Entry entry = (Entry) ref;
                BY_ID.remove(entry.id);
                SitePool pool = POOLS.get(entry.site);
            }
        }
    }

    // ------------------------------------------------------------------ 操纵

    /** 永久锁定这个实例：之后每次 next() 都返回 value（按 bits 截断） */
    public void lock(int value) {
        this.forcedValue = (long) value;
        this.remaining = -1;
    }

    /** 只锁定接下来的 times 次 next() */
    public void lock(int value, int times) {
        this.forcedValue = (long) value;
        this.remaining = times;
    }

    public void unlock() {
        this.forcedValue = null;
        this.remaining = -1;
    }

    public boolean isLocked() {
        return this.forcedValue != null;
    }

    /** 预置接下来几次 next() 的返回值（按 bits 截断），优先级高于锁定 */
    public void script(int... values) {
        this.script = values.clone();
        this.scriptIndex = 0;
    }

    public void clearScript() {
        this.script = null;
        this.scriptIndex = 0;
    }

    public int remainingScript() {
        return this.script == null ? 0 : this.script.length - this.scriptIndex;
    }

    /** 回到创建时的种子，并清空注入序列与统计 */
    public void reset() {
        super.setSeed(this.seed);
        this.script = null;
        this.scriptIndex = 0;
        this.consumed = 0;
    }

    /** 只在 bits < 32 时才有意义的掩码；bits == 32 表示"全都要" */
    private static int mask(int bits) {
        return bits >= 32 ? -1 : (1 << bits) - 1;
    }

    /**
     * 取值顺序：{@link #script(int...)} 注入 > {@link #lock(int)} 实例锁定 >
     * 全局 {@link #setLocked(boolean)} 兜底 > 真实随机。
     *
     * <p>全局兜底那一行刻意保持历史写法（{@code value & ((1 << bits) - 1)}），
     * 因此它在 {@code nextInt()}/{@code nextLong()} 用到的 bits == 32 时会返回 0；
     * 实例级的两条新路径用 {@link #mask(int)}，bits == 32 时返回完整的 value。
     */
    @Override
    protected int next(int bits) {
        ++this.consumed;
        int[] script = this.script;
        if (script != null && this.scriptIndex < script.length) {
            return script[this.scriptIndex++] & mask(bits);
        }
        Long forced = this.forcedValue;
        if (forced != null) {
            int result = (int) (forced.longValue() & (long) mask(bits));
            if (this.remaining > 0 && --this.remaining == 0) {
                this.forcedValue = null;
            }
            return result;
        }
        if (locked) {
            return value & ((1 << bits) - 1);
        }
        return super.next(bits);
    }

    @Override
    public String toString() {
        return this.site + "[" + this.key + ", #" + this.id + ", " + this.stability + ", seed=" + this.seed
                + ", consumed=" + this.consumed + (isLocked() ? ", locked=" + this.forcedValue : "") + "]";
    }
}
