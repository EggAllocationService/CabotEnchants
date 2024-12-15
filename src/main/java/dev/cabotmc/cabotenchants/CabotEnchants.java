package dev.cabotmc.cabotenchants;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import dev.cabotmc.cabotenchants.bettertable.quest.AncientTombReward;
import dev.cabotmc.cabotenchants.bettertable.quest.BookKillVariousMobsStep;
import dev.cabotmc.cabotenchants.bettertable.quest.EnchantRandomStep;
import dev.cabotmc.cabotenchants.boss.RiftWorldListener;
import dev.cabotmc.cabotenchants.boss.quest.RiftCatalystStep;
import dev.cabotmc.cabotenchants.career.CareerListener;
import dev.cabotmc.cabotenchants.commands.CEReloadCommand;
import dev.cabotmc.cabotenchants.commands.GiveQuestItemCommand;
import dev.cabotmc.cabotenchants.config.CEConfig;
import dev.cabotmc.cabotenchants.eternalrocket.*;
import dev.cabotmc.cabotenchants.flight.*;
import dev.cabotmc.cabotenchants.god.*;
import dev.cabotmc.cabotenchants.godarmor.*;
import dev.cabotmc.cabotenchants.godpick.*;
import dev.cabotmc.cabotenchants.quest.Quest;
import dev.cabotmc.cabotenchants.quest.QuestListener;
import dev.cabotmc.cabotenchants.quest.QuestManager;
import dev.cabotmc.cabotenchants.railgun.RailgunListener;
import dev.cabotmc.cabotenchants.sentient.CETridentConfig;
import dev.cabotmc.cabotenchants.sentient.SentienceListener;
import dev.cabotmc.cabotenchants.sentient.quest.*;
import dev.cabotmc.cabotenchants.shrinkray.KillCreakingStep;
import dev.cabotmc.cabotenchants.shrinkray.KillRavengerStep;
import dev.cabotmc.cabotenchants.shrinkray.KillSmallMobsStep;
import dev.cabotmc.cabotenchants.shrinkray.ShrinkrayReward;
import dev.cabotmc.cabotenchants.spawner.AwakenedSouldrinkerReward;
import dev.cabotmc.cabotenchants.spawner.CESpawnerConfig;
import dev.cabotmc.cabotenchants.spawner.SpawnerSwordReward;
import dev.cabotmc.cabotenchants.spawner.quest.DepletedSwordReward;
import dev.cabotmc.cabotenchants.spawner.quest.SwordKillSpawnableMobs;
import dev.cabotmc.cabotenchants.spawner.quest.SwordStartQuest;
import dev.cabotmc.cabotenchants.unbreakingx.UBXRewardStep;
import dev.cabotmc.cabotenchants.unbreakingx.UBXStartQuest;
import dev.cabotmc.cabotenchants.unbreakingx.UBXThrowIntoPortalStep;
import dev.cabotmc.cabotenchants.util.ResourcepackSender;
import dev.cabotmc.cabotenchants.util.YAxisFalldamageGate;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Files;
import java.nio.file.Path;

public final class CabotEnchants extends JavaPlugin {

    public static CEConfig config;

    public static NPCRegistry npcRegistry;

    public static QuestManager q;
    static Quest GOD_BOOK_QUEST;
    static Quest EVERLASTING_ROCKET_QUEST;
    static ProtocolManager protocolManager;
    public static Path configFile;

    static Quest UNBREAKING_X_QUEST;

    static Quest FLIGHT_QUEST;

    static Quest TRIDENT_QUEST;

    static Quest SOULDRINKER_QUEST;

    static Quest COSMIC_PICK_QUEST;

    static Quest ANCIENT_TOME_QUEST;

    static Quest GOD_ARMOR_QUEST;

    static Quest SHRINKRAY_QUEST;

    public static GodHelmet GOD_HELMET = new GodHelmet();
    public static GodChestplate GOD_CHESTPLATE = new GodChestplate();
    public static GodLeggings GOD_LEGGINGS = new GodLeggings();
    public static GodBoots GOD_BOOTS = new GodBoots();
    public static GodShield GOD_SHIELD = new GodShield();

    @Override
    public void onEnable() {
        q = new QuestManager(this);
        protocolManager = ProtocolLibrary.getProtocolManager();

        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new RailgunListener(), this);

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

        SOULDRINKER_QUEST = new Quest("souldrinker", CESpawnerConfig.class, new SwordStartQuest(), new SwordKillSpawnableMobs(), new SpawnerSwordReward(), new DepletedSwordReward(), new AwakenedSouldrinkerReward());
        q.registerQuest(SOULDRINKER_QUEST);

        COSMIC_PICK_QUEST = new Quest("cosmic_pick", CEConfig.class,  new PickStartStep(), new BreakAllOresStep(), new BreakAncientDebrisStep(), new GodPickReward());
        q.registerQuest(COSMIC_PICK_QUEST);

        ANCIENT_TOME_QUEST = new Quest("ancient_tome", CEConfig.class, new EnchantRandomStep(), new BookKillVariousMobsStep(), new AncientTombReward());
        q.registerQuest(ANCIENT_TOME_QUEST);

        GOD_ARMOR_QUEST = new Quest("god_armor", CEConfig.class, new RiftCatalystStep(), GOD_HELMET, GOD_CHESTPLATE, GOD_LEGGINGS, GOD_BOOTS, GOD_SHIELD);
        q.registerQuest(GOD_ARMOR_QUEST);

        SHRINKRAY_QUEST = new Quest("shrinkray", CEConfig.class, new KillCreakingStep(), new KillSmallMobsStep(), new KillRavengerStep(), new ShrinkrayReward());
        q.registerQuest(SHRINKRAY_QUEST);

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

        var manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            var commands = event.registrar();
            commands.register("givequestitem", "thing", new GiveQuestItemCommand());
            commands.register("cereload", "thing", new CEReloadCommand());
        });

       /* getCommand("givequestitem").setExecutor(new GiveQuestItemCommand());
        getCommand("cereload").setExecutor(new CEReloadCommand());
        getCommand("cosmetics").setExecutor(new CosmeticsCommand());
        getCommand("cosmeticunlock").setExecutor(new UnlockRewardCommand());

        getCommand("testboss").setExecutor((sender, command, label, args) -> {
            if (!sender.hasPermission("cabot.testboss")) return false;
            var p = (Player) sender;
            KyleFight.prepareFight(List.of(p));
            return false;
        });
*/
        Bukkit.getPluginManager().registerEvents(new CareerListener(), this);

        Bukkit.getPluginManager().registerEvents(new YAxisFalldamageGate(), this);

        Bukkit.getPluginManager().registerEvents(new ResourcepackSender(), this);


        // rift world
        Bukkit.getPluginManager().registerEvents(new RiftWorldListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
