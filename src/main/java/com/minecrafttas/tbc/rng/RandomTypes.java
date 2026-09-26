package com.minecrafttas.tbc.rng;

import lombok.Getter;

/**
 * Every random source type that has been taken over by the mod.
 *
 * <p>The constants are grouped by {@link Stability}, which describes how well a single instance can be
 * located:
 * <ul>
 *     <li>{@link Stability#STABLE} - the amount and order of instances is the same on every run</li>
 *     <li>{@link Stability#SESSION} - the amount depends on the session, so a single instance is addressed
 *     through the instance counter of its pool</li>
 *     <li>{@link Stability#VOLATILE} - vanilla creates a new instance per call, so the whole site is always
 *     served by one shared instance, see {@link RandomManager#site(RandomTypes)}</li>
 * </ul>
 *
 * <p>Each constant also carries:
 * <ul>
 *     <li>{@code name} - the name used in commands and logs, taken from the vanilla class name. Sites inside
 *     the same class use {@code Class.method} to tell them apart.</li>
 *     <li>{@code salt} - the seed salt of the site, see below.</li>
 * </ul>
 *
 * <p><b>Salts are assigned in declaration order</b>, starting at {@code 0x00}: a new site is appended to its
 * group and takes the next free value, it is never inserted in the middle. The static initialiser verifies
 * that every constant carries exactly its position as salt and rejects duplicate names, so a copied constant
 * cannot silently share the stream of another site.
 */
public enum RandomTypes {

    // ------------------------------------------------------------------ STABLE

    DISPENSER_BLOCK_ENTITY("DispenserBlockEntity", Stability.STABLE, 0x00),
    ENCHANTING_TABLE_BLOCK_ENTITY("EnchantingTableBlockEntity", Stability.STABLE, 0x01),
    /** One instance per screen, like {@link #TITLE_SCREEN} */
    ENCHANTMENT_SCREEN("EnchantmentScreen", Stability.STABLE, 0x02),
    ITEM("Item", Stability.STABLE, 0x03),
    ITEM_SCATTERER("ItemScatterer", Stability.STABLE, 0x04),
    MATH_HELPER("MathHelper", Stability.STABLE, 0x05),
    /** One instance for the whole server */
    MINECRAFT_SERVER("MinecraftServer", Stability.STABLE, 0x06),
    SENSOR("Sensor", Stability.STABLE, 0x07),
    WANDERING_TRADER_MANAGER("WanderingTraderManager", Stability.STABLE, 0x08),
    /** Two sources inside the same constructor: index 0 = lcgBlockSeed, index 1 = random */
    WORLD("World", Stability.STABLE, 0x09),
    ZOMBIE_VILLAGER_TYPE("EntityZombieVillagerTypeFix", Stability.STABLE, 0x0A),
    CLIENT_PLAY_NETWORK_HANDLER("ClientPlayNetworkHandler", Stability.STABLE, 0x0B),
    ENDERMAN_RENDERER("EndermanEntityRenderer", Stability.STABLE, 0x0C),
    FONT_STORAGE("FontStorage", Stability.STABLE, 0x0D),
    GAME_RENDERER("GameRenderer", Stability.STABLE, 0x0E),
    ITEM_ENTITY_RENDERER("ItemEntityRenderer", Stability.STABLE, 0x0F),
    MUSIC_TRACKER("MusicTracker", Stability.STABLE, 0x10),
    PARTICLE_MANAGER("ParticleManager", Stability.STABLE, 0x11),
    SPELL_PARTICLE("SpellParticle", Stability.STABLE, 0x12),
    SPLASH_TEXT("SplashTextResourceSupplier", Stability.STABLE, 0x13),
    TEXT_RENDERER("TextRenderer", Stability.STABLE, 0x14),
    TITLE_SCREEN("TitleScreen", Stability.STABLE, 0x15),
    WEIGHTED_SOUND_SET("WeightedSoundSet", Stability.STABLE, 0x16),

    // ------------------------------------------------------------------ SESSION

    /** One instance per entity, handed out in entity construction order */
    ENTITY("Entity", Stability.SESSION, 0x17),
    EXPLOSION("Explosion", Stability.SESSION, 0x18),
    RAID("Raid", Stability.SESSION, 0x19),
    WEIGHTED_LIST("WeightedList", Stability.SESSION, 0x1A),
    PARTICLE("Particle", Stability.SESSION, 0x1B),

    // ------------------------------------------------------------------ VOLATILE

    ENDER_DRAGON_SPAWN_PILLARS("EnderDragonSpawnState", Stability.VOLATILE, 0x1C),
    GENERATOR_OPTIONS_DEFAULT("GeneratorOptions.getDefaultOptions", Stability.VOLATILE, 0x1D),
    GENERATOR_OPTIONS_CHUNK_GENERATOR("GeneratorOptions.getChunkGenerator", Stability.VOLATILE, 0x1E),
    GENERATOR_OPTIONS_FROM_PROPERTIES("GeneratorOptions.fromProperties", Stability.VOLATILE, 0x1F),
    LOOT_CONTEXT("LootContext.Builder", Stability.VOLATILE, 0x20),
    /** Picks a world seed when the dimension map has no overworld entry */
    MINECRAFT_SERVER_SEED("MinecraftServer.createWorlds", Stability.VOLATILE, 0x21),
    SERVER_PLAYER_ENTITY_SPAWN_OFFSET("ServerPlayerEntity.moveToSpawn", Stability.VOLATILE, 0x22),
    SPREAD_PLAYERS_COMMAND("SpreadPlayersCommand", Stability.VOLATILE, 0x23),
    STRUCTURE_BLOCK_SEED("StructureBlockBlockEntity.createRandom", Stability.VOLATILE, 0x24),
    STRUCTURE_PLACEMENT_SEED("StructurePlacementData.getRandom", Stability.VOLATILE, 0x25),
    CLIENT_WORLD_BLOCK_DISPLAY("ClientWorld.doRandomBlockDisplayTicks", Stability.VOLATILE, 0x26),
    TEAM_TELEPORT_SPECTATOR_MENU("TeamTeleportSpectatorMenu", Stability.VOLATILE, 0x27),
    /**
     * The single shared random behind every replaced argument-less {@code Collections.shuffle(list)}
     * (vanilla also uses one static Random for all of them, hidden inside {@code java.util.Collections}).
     * The one instance of this site is {@link RandomManager#COLLECTIONS_RANDOM}.
     */
    SHUFFLE("Shuffle", Stability.VOLATILE, 0x28);

    /** Display name of the site (vanilla class name, {@code Class.method} for sub-sites) */
    @Getter private final String name;

    @Getter private final Stability stability;

    /** Seed salt, assigned in declaration order, see the class javadoc */
    @Getter private final int salt;

    RandomTypes(String name, Stability stability, int salt) {
        this.name = name;
        this.stability = stability;
        this.salt = salt;
    }

    /** How well a single instance of this site can be located */
    public enum Stability {
        /** The amount and order of instances is the same on every run, safe to hard-code in scripts */
        STABLE,
        /** The amount depends on the session, single instances are addressed by their instance counter */
        SESSION,
        /** Vanilla creates a new instance per call, the site is always served by one shared instance */
        VOLATILE
    }
}
