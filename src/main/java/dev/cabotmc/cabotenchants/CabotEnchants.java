package dev.cabotmc.cabotenchants;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLib;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import dev.cabotmc.cabotenchants.buzzkill.BuzzkillEnchant;
import dev.cabotmc.cabotenchants.buzzkill.BuzzkillListener;
import dev.cabotmc.cabotenchants.flight.FlightEnchant;
import dev.cabotmc.cabotenchants.flight.FlightEnchantTask;
import dev.cabotmc.cabotenchants.frost.FrostAspectEnchant;
import dev.cabotmc.cabotenchants.god.GodEnchant;
import dev.cabotmc.cabotenchants.god.GodListener;
import dev.cabotmc.cabotenchants.god.GodQuestListener;
import dev.cabotmc.cabotenchants.packet.EnchantmentLoreAdapter;
import dev.cabotmc.cabotenchants.railgun.RailgunEnchant;
import dev.cabotmc.cabotenchants.railgun.RailgunListener;
import dev.cabotmc.cabotenchants.table.TableListenener;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.EnchantCommand;
import net.minecraft.world.item.enchantment.Enchantment;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.IdentityHashMap;

public final class CabotEnchants extends JavaPlugin {
    private ProtocolManager protocolManager;
    @Override
    public void onLoad() {
        unfreeze_registries();
        Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation("cabot", "railgun"), new RailgunEnchant());
        Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation("cabot", "buzzkill"), new BuzzkillEnchant());
        Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation("cabot", "flight"), new FlightEnchant());
        Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation("cabot", "god"), new GodEnchant());
        Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation("cabot", "freeze"), new FrostAspectEnchant());

        BuiltInRegistries.ENCHANTMENT.freeze();

    }
    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new RailgunListener(), this);
        getServer().getPluginManager().registerEvents(new BuzzkillListener(), this);
        getServer().getPluginManager().registerEvents(new TableListenener(), this);

        getServer().getPluginManager().registerEvents(new GodQuestListener(), this);
        getServer().getPluginManager().registerEvents(new GodListener(), this);
        /*protocolManager = ProtocolLibrary.getProtocolManager();

        PacketType.Play.Server.getInstance().values().forEach((packetType) -> {
            protocolManager.addPacketListener(new EnchantmentLoreAdapter(this, packetType));
            getLogger().info("Registered packet listener for " + packetType.name());
        });*/
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new FlightEnchantTask(), 0, 1);

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
