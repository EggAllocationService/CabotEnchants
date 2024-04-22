package dev.cabotmc.cabotenchants;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import dev.cabotmc.cabotenchants.bettertable.quest.AncientTombReward;
import dev.cabotmc.cabotenchants.bettertable.quest.BookKillVariousMobsStep;
import dev.cabotmc.cabotenchants.bettertable.quest.EnchantRandomStep;
import dev.cabotmc.cabotenchants.buzzkill.BuzzkillEnchant;
import dev.cabotmc.cabotenchants.buzzkill.BuzzkillListener;
import dev.cabotmc.cabotenchants.career.CareerListener;
import dev.cabotmc.cabotenchants.career.CosmeticsCommand;
import dev.cabotmc.cabotenchants.career.UnlockRewardCommand;
import dev.cabotmc.cabotenchants.commands.CEReloadCommand;
import dev.cabotmc.cabotenchants.commands.GiveQuestItemCommand;
import dev.cabotmc.cabotenchants.config.CEConfig;
import dev.cabotmc.cabotenchants.eternalrocket.*;
import dev.cabotmc.cabotenchants.flight.*;
import dev.cabotmc.cabotenchants.frost.FrostAspectEnchant;
import dev.cabotmc.cabotenchants.god.*;
import dev.cabotmc.cabotenchants.godpick.*;
import dev.cabotmc.cabotenchants.mace.MCGetBedrockStep;
import dev.cabotmc.cabotenchants.mace.MCQuestStart;
import dev.cabotmc.cabotenchants.mace.MCRewardStep;
import dev.cabotmc.cabotenchants.mace.MCWindChargeStep;
import dev.cabotmc.cabotenchants.protocol.TitleHandler;
import dev.cabotmc.cabotenchants.quest.Quest;
import dev.cabotmc.cabotenchants.quest.QuestListener;
import dev.cabotmc.cabotenchants.quest.QuestManager;
import dev.cabotmc.cabotenchants.railgun.RailgunEnchant;
import dev.cabotmc.cabotenchants.railgun.RailgunListener;
import dev.cabotmc.cabotenchants.sentient.CETridentConfig;
import dev.cabotmc.cabotenchants.sentient.SentienceEnchant;
import dev.cabotmc.cabotenchants.sentient.SentienceListener;
import dev.cabotmc.cabotenchants.sentient.quest.*;
import dev.cabotmc.cabotenchants.spawner.CESpawnerConfig;
import dev.cabotmc.cabotenchants.spawner.SpawnerSwordReward;
import dev.cabotmc.cabotenchants.spawner.quest.SwordKillSpawnableMobs;
import dev.cabotmc.cabotenchants.spawner.quest.SwordStartQuest;
import dev.cabotmc.cabotenchants.table.TableListenener;
import dev.cabotmc.cabotenchants.unbreakingx.UBXRewardStep;
import dev.cabotmc.cabotenchants.unbreakingx.UBXStartQuest;
import dev.cabotmc.cabotenchants.unbreakingx.UBXThrowIntoPortalStep;
import dev.cabotmc.cabotenchants.util.ResourcepackSender;
import dev.cabotmc.cabotenchants.util.YAxisFalldamageGate;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.IdentityHashMap;

public final class CabotEnchants extends JavaPlugin {

    public static CEConfig config;
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
    public static Path configFile;

    static Quest UNBREAKING_X_QUEST;

    static Quest FLIGHT_QUEST;

    static Quest TRIDENT_QUEST;

    static Quest SOULDRINKER_QUEST;

    static Quest COSMIC_PICK_QUEST;

    static Quest ANCIENT_TOME_QUEST;

    static Quest MACE_QUEST;

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
        GOD_BOOK_QUEST = new Quest("god_enchant", CEConfig.class, new GodWardenStep(), new GodWitherStep(), new GodDragonStep(), new GodRewardStep());
        q.registerQuest(GOD_BOOK_QUEST);

        EVERLASTING_ROCKET_QUEST = new Quest("rocket", CERocketConfig.class, new ERLaunchFireworkStep(), new ERChargeGunpowderStep(), new ERExplosionStep(),
        new ERReward());
        q.registerQuest(EVERLASTING_ROCKET_QUEST);

        UNBREAKING_X_QUEST = new Quest("unbreakingx", CEConfig.class, new UBXStartQuest(), new UBXThrowIntoPortalStep(), new UBXRewardStep());
        q.registerQuest(UNBREAKING_X_QUEST);

        FLIGHT_QUEST = new Quest("flight_enchant", CEFlightConfig.class, new FlightQuestStart(), new FlightKillBlazeStep(), new FlightKillFlyingMobsStep(), new FlightThrowIntoVoidStep(), new FlightRewardStep());
        q.registerQuest(FLIGHT_QUEST);

        TRIDENT_QUEST = new Quest("trident", CETridentConfig.class, new TridentQuestStart(), new TridentKillAquaticEnemiesStep(), new TridentDropUnderwaterStep(), new TridentKillLibrariansStep(), new TridentRewardItem());
        q.registerQuest(TRIDENT_QUEST);

        SOULDRINKER_QUEST = new Quest("souldrinker", CESpawnerConfig.class, new SwordStartQuest(), new SwordKillSpawnableMobs(), new SpawnerSwordReward());
        q.registerQuest(SOULDRINKER_QUEST);

        COSMIC_PICK_QUEST = new Quest("cosmic_pick", CEConfig.class,  new PickStartStep(), new BreakAllOresStep(), new BreakAncientDebrisStep(), new GodPickReward());
        q.registerQuest(COSMIC_PICK_QUEST);

        ANCIENT_TOME_QUEST = new Quest("ancient_tome", CEConfig.class, new EnchantRandomStep(), new BookKillVariousMobsStep(), new AncientTombReward());
        q.registerQuest(ANCIENT_TOME_QUEST);

        MACE_QUEST = new Quest("mace", CEConfig.class, new MCQuestStart(), new MCGetBedrockStep(), new MCRewardStep(), new MCWindChargeStep());
        q.registerQuest(MACE_QUEST);

        var folder = getDataFolder();
        folder.mkdirs();
        var file = new java.io.File(folder, "config.json");
        if (!file.exists()) {
            try {
                file.createNewFile();
                Files.writeString(file.toPath(), q.saveConfigs());
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        } else {
            try {
                q.loadConfigs(Files.readString(file.toPath()));
                Files.writeString(file.toPath(), q.saveConfigs());
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        configFile = file.toPath();

        Bukkit.getPluginManager().registerEvents(new SentienceListener(), this);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new FlightEnchantTask(), 0, 1);
        Bukkit.getPluginManager().registerEvents(new QuestListener(), this);
        getCommand("givequestitem").setExecutor(new GiveQuestItemCommand());
        getCommand("cereload").setExecutor(new CEReloadCommand());
        getCommand("cosmetics").setExecutor(new CosmeticsCommand());
        getCommand("cosmeticunlock").setExecutor(new UnlockRewardCommand());

        Bukkit.getPluginManager().registerEvents(new CareerListener(), this);

        Bukkit.getPluginManager().registerEvents(new YAxisFalldamageGate(), this);

        Bukkit.getPluginManager().registerEvents(new ResourcepackSender(), this);

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
