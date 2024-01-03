package dev.cabotmc.cabotenchants;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import dev.cabotmc.cabotenchants.bettertable.BetterTableListener;
import dev.cabotmc.cabotenchants.buzzkill.BuzzkillEnchant;
import dev.cabotmc.cabotenchants.buzzkill.BuzzkillListener;
import dev.cabotmc.cabotenchants.commands.GiveQuestItemCommand;
import dev.cabotmc.cabotenchants.eternalrocket.ERChargeGunpowderStep;
import dev.cabotmc.cabotenchants.eternalrocket.ERExplosionStep;
import dev.cabotmc.cabotenchants.eternalrocket.ERMilkMooshroomStep;
import dev.cabotmc.cabotenchants.eternalrocket.ERReward;
import dev.cabotmc.cabotenchants.flight.*;
import dev.cabotmc.cabotenchants.frost.FrostAspectEnchant;
import dev.cabotmc.cabotenchants.god.*;
import dev.cabotmc.cabotenchants.godpick.*;
import dev.cabotmc.cabotenchants.protocol.TitleHandler;
import dev.cabotmc.cabotenchants.quest.Quest;
import dev.cabotmc.cabotenchants.quest.QuestListener;
import dev.cabotmc.cabotenchants.quest.QuestManager;
import dev.cabotmc.cabotenchants.railgun.RailgunEnchant;
import dev.cabotmc.cabotenchants.railgun.RailgunListener;
import dev.cabotmc.cabotenchants.sentient.SentienceEnchant;
import dev.cabotmc.cabotenchants.sentient.SentienceListener;
import dev.cabotmc.cabotenchants.sentient.quest.*;
import dev.cabotmc.cabotenchants.spawner.SpawnerSwordReward;
import dev.cabotmc.cabotenchants.spawner.quest.SwordKillSpawnableMobs;
import dev.cabotmc.cabotenchants.spawner.quest.SwordStartQuest;
import dev.cabotmc.cabotenchants.table.TableListenener;
import dev.cabotmc.cabotenchants.unbreakingx.UBXRewardStep;
import dev.cabotmc.cabotenchants.unbreakingx.UBXStartQuest;
import dev.cabotmc.cabotenchants.unbreakingx.UBXThrowIntoPortalStep;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.IdentityHashMap;

public final class CabotEnchants extends JavaPlugin {
    @Override
    public void onLoad() {
        unfreeze_registries();
        Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation("cabot", "railgun"), new RailgunEnchant());
        Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation("cabot", "buzzkill"), new BuzzkillEnchant());
        Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation("cabot", "flight"), new OldFlightEnchant());
        Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation("cabot", "god"), new GodEnchant());
        Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation("cabot", "freeze"), new FrostAspectEnchant());
        Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation("cabot", "new_flight"), new OldFlightEnchant());
        Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation("cabot", "sentience"), new SentienceEnchant());
        Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation("cabot", "veinminer"), new VeinminerEnchant());
        BuiltInRegistries.ENCHANTMENT.freeze();

    }
    public static QuestManager q;
    static Quest GOD_BOOK_QUEST;
    static Quest EVERLASTING_ROCKET_QUEST;
    static ProtocolManager protocolManager;
    public static TitleHandler titleHandler;

    static Quest UNBREAKING_X_QUEST;

    static Quest FLIGHT_QUEST;

    static Quest TRIDENT_QUEST;

    static Quest SOULDRINKER_QUEST;

    static Quest COSMIC_PICK_QUEST;

    @Override
    public void onEnable() {
        q = new QuestManager(this);
        protocolManager = ProtocolLibrary.getProtocolManager();

        titleHandler = new TitleHandler(this, protocolManager);
        titleHandler.registerPacketListeners();

        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new RailgunListener(), this);
        getServer().getPluginManager().registerEvents(new BuzzkillListener(), this);
        getServer().getPluginManager().registerEvents(new TableListenener(), this);

        //getServer().getPluginManager().registerEvents(new GodQuestListener(), this);
        getServer().getPluginManager().registerEvents(new GodListener(), this);
        GOD_BOOK_QUEST = new Quest(new GodWardenStep(), new GodWitherStep(), new GodDragonStep(), new GodRewardStep());
        q.registerQuest(GOD_BOOK_QUEST);
        EVERLASTING_ROCKET_QUEST = new Quest(new ERMilkMooshroomStep(), new ERChargeGunpowderStep(), new ERExplosionStep(),
        new ERReward());
        q.registerQuest(EVERLASTING_ROCKET_QUEST);

        UNBREAKING_X_QUEST = new Quest(new UBXStartQuest(), new UBXThrowIntoPortalStep(), new UBXRewardStep());
        q.registerQuest(UNBREAKING_X_QUEST);

        FLIGHT_QUEST = new Quest(new FlightQuestStart(), new FlightKillBlazeStep(), new FlightKillFlyingMobsStep(), new FlightThrowIntoVoidStep(), new FlightRewardStep());
        q.registerQuest(FLIGHT_QUEST);

        TRIDENT_QUEST = new Quest(new TridentQuestStart(), new TridentKillAquaticEnemiesStep(), new TridentDropUnderwaterStep(), new TridentKillLibrariansStep(), new TridentRewardItem());
        q.registerQuest(TRIDENT_QUEST);

        SOULDRINKER_QUEST = new Quest(new SwordStartQuest(), new SwordKillSpawnableMobs(), new SpawnerSwordReward());
        q.registerQuest(SOULDRINKER_QUEST);

        COSMIC_PICK_QUEST = new Quest(new PickStartStep(), new BreakAllOresStep(), new BreakAncientDebrisStep(), new GodPickReward());
        q.registerQuest(COSMIC_PICK_QUEST);

        Bukkit.getPluginManager().registerEvents(new SentienceListener(), this);

        Bukkit.getPluginManager().registerEvents(new BetterTableListener(), this);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new FlightEnchantTask(), 0, 1);
        Bukkit.getPluginManager().registerEvents(new QuestListener(), this);
        getCommand("givequestitem").setExecutor(new GiveQuestItemCommand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void unfreeze_registries() {
        // NOTE: MAGIC VALUES! Introduced for 1.18.2 when registries were frozen. Sad, no workaround at the time.
        try {
            // Make relevant fields accessible
            final var frozen = MappedRegistry.class.getDeclaredField("l" /* frozen */);
            frozen.setAccessible(true);
            final var intrusive_holder_cache = MappedRegistry.class.getDeclaredField("m" /* unregisteredIntrusiveHolders (1.19.3+), intrusiveHolderCache (until 1.19.2) */);
            intrusive_holder_cache.setAccessible(true);

            // Unfreeze required registries
            frozen.set(BuiltInRegistries.ENTITY_TYPE, false);
            frozen.set(BuiltInRegistries.ENCHANTMENT, false);
            // Since 1.20.2 this is also needed for enchantments:
            intrusive_holder_cache.set(BuiltInRegistries.ENCHANTMENT, new IdentityHashMap<Enchantment, Holder.Reference<Enchantment>>());
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
