package com.minecrafttas.tbc.rng;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * Every random source type that has been taken over by the mod.
 *
 * <p>Each constant describes one "random source site":
 * <ul>
 *     <li>{@code name} - the name used in commands and logs, taken from the vanilla class name. Sites inside
 *     the same class use {@code Class.method} to tell them apart.</li>
 *     <li>{@code stability} - whether a single instance can be located across runs. This is only a hint for
 *     the command layer, it does not influence the random stream.</li>
 *     <li>{@code salt} - the seed salt, used by {@code seedOf(type, index)}.</li>
 * </ul>
 *
 * <p><b>Salt rule: append only.</b> A new site must use a salt value that has never been used before. Do not
 * reorder, do not reuse and do not derive it from {@code ordinal()}: changing a salt changes the random
 * stream of that site and invalidates every existing TAS recording. Duplicate names and salts are detected
 * when the class is initialised.
 */
public enum RandomTypes {

    // ------------------------------------------------------------------ unclassified

    /** Sources without a known site (kept for the legacy argument-less {@code create()}) */
    UNKNOWN("Unknown", Stability.VOLATILE, 0x00),

    // ------------------------------------------------------------------ common

    DISPENSER_BLOCK_ENTITY("DispenserBlockEntity", Stability.STABLE, 0x01),
    ENCHANTING_TABLE_BLOCK_ENTITY("EnchantingTableBlockEntity", Stability.STABLE, 0x02),
    ENDER_DRAGON_SPAWN_PILLARS("EnderDragonSpawnState", Stability.VOLATILE, 0x03),
    /** One instance per entity, the instance key is the entity id */
    ENTITY("Entity", Stability.SESSION, 0x04),
    ENTITY_SELECTOR("EntitySelectorReader", Stability.VOLATILE, 0x05),
    EXPLOSION("Explosion", Stability.SESSION, 0x06),
    GENERATOR_OPTIONS_DEFAULT("GeneratorOptions.getDefaultOptions", Stability.VOLATILE, 0x07),
    GENERATOR_OPTIONS_CHUNK_GENERATOR("GeneratorOptions.getChunkGenerator", Stability.VOLATILE, 0x08),
    GENERATOR_OPTIONS_FROM_PROPERTIES("GeneratorOptions.fromProperties", Stability.VOLATILE, 0x09),
    ITEM("Item", Stability.STABLE, 0x0A),
    ITEM_SCATTERER("ItemScatterer", Stability.STABLE, 0x0B),
    LOOT_CONTEXT("LootContext.Builder", Stability.VOLATILE, 0x0C),
    MATH_HELPER("MathHelper", Stability.STABLE, 0x0D),
    /** One instance for the whole server */
    MINECRAFT_SERVER("MinecraftServer", Stability.STABLE, 0x0E),
    /** Picks a world seed when the dimension map has no overworld entry */
    MINECRAFT_SERVER_SEED("MinecraftServer.createWorlds", Stability.VOLATILE, 0x0F),
    /** Shuffles the player list sample */
    MINECRAFT_SERVER_PLAYER_SAMPLE("MinecraftServer.tick", Stability.VOLATILE, 0x10),
    RAID("Raid", Stability.SESSION, 0x11),
    SENSOR("Sensor", Stability.STABLE, 0x12),
    SERVER_CHUNK_MANAGER_TICK_SHUFFLE("ServerChunkManager.tickChunks", Stability.VOLATILE, 0x13),
    SERVER_PLAYER_ENTITY_SPAWN_OFFSET("ServerPlayerEntity.moveToSpawn", Stability.VOLATILE, 0x14),
    SPREAD_PLAYERS_COMMAND("SpreadPlayersCommand", Stability.VOLATILE, 0x15),
    STRUCTURE_BLOCK_SEED("StructureBlockBlockEntity.createRandom", Stability.VOLATILE, 0x16),
    STRUCTURE_PLACEMENT_SEED("StructurePlacementData.getRandom", Stability.VOLATILE, 0x17),
    WANDER_INDOORS_TASK("WanderIndoorsTask", Stability.VOLATILE, 0x18),
    WANDERING_TRADER_MANAGER("WanderingTraderManager", Stability.STABLE, 0x19),
    WEIGHTED_LIST("WeightedList", Stability.SESSION, 0x1A),
    /** Two sources inside the same constructor: index 0 = lcgBlockSeed, index 1 = random */
    WORLD("World", Stability.STABLE, 0x1B),
    ZOMBIE_VILLAGER_TYPE("EntityZombieVillagerTypeFix", Stability.STABLE, 0x1C),

    // ------------------------------------------------------------------ client

    CLIENT_PLAY_NETWORK_HANDLER("ClientPlayNetworkHandler", Stability.STABLE, 0x1D),
    /** Shuffles the dimension keys in {@code onGameJoin} */
    CLIENT_PLAY_NETWORK_HANDLER_GAME_JOIN_SHUFFLE("ClientPlayNetworkHandler.onGameJoin", Stability.VOLATILE, 0x1E),
    CLIENT_WORLD_BLOCK_DISPLAY("ClientWorld.doRandomBlockDisplayTicks", Stability.VOLATILE, 0x1F),
    ENCHANTMENT_SCREEN("EnchantmentScreen", Stability.VOLATILE, 0x20),
    ENDERMAN_RENDERER("EndermanEntityRenderer", Stability.STABLE, 0x21),
    FONT_STORAGE("FontStorage", Stability.STABLE, 0x22),
    GAME_RENDERER("GameRenderer", Stability.STABLE, 0x23),
    ITEM_ENTITY_RENDERER("ItemEntityRenderer", Stability.STABLE, 0x24),
    MUSIC_TRACKER("MusicTracker", Stability.STABLE, 0x25),
    PARTICLE("Particle", Stability.SESSION, 0x26),
    PARTICLE_MANAGER("ParticleManager", Stability.STABLE, 0x27),
    SPELL_PARTICLE("SpellParticle", Stability.STABLE, 0x28),
    SPLASH_TEXT("SplashTextResourceSupplier", Stability.STABLE, 0x29),
    TEAM_TELEPORT_SPECTATOR_MENU("TeamTeleportSpectatorMenu", Stability.VOLATILE, 0x2A),
    TEXT_RENDERER("TextRenderer", Stability.STABLE, 0x2B),
    TITLE_SCREEN("TitleScreen", Stability.STABLE, 0x2C),
    WEIGHTED_SOUND_SET("WeightedSoundSet", Stability.STABLE, 0x2D);

    /** Display name of the site (vanilla class name, {@code Class.method} for sub-sites) */
    @Getter private final String name;

    @Getter private final Stability stability;

    /** Seed salt, see the "append only" rule in the class javadoc */
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
        /** The amount depends on the session, but instances carry a readable key (uuid / entity id / raid id ...) */
        SESSION,
        /** A new instance is created per call, single instances cannot be located, only the whole site */
        VOLATILE
    }

    @Override
    public String toString() {
        return this.name;
    }

    static {
        Set<String> names = new HashSet<>();
        Set<Integer> salts = new HashSet<>();
        for (RandomTypes type : values()) {
            if (!names.add(type.name)) {
                throw new IllegalStateException("Duplicate RandomTypes name: " + type.name);
            }
            if (!salts.add(type.salt)) {
                throw new IllegalStateException("Duplicate RandomTypes salt: " + type.name);
            }
        }
    }
}
