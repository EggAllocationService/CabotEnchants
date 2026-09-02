package dev.cabotmc.cabotenchants.flight;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.checkerframework.checker.units.qual.C;

import java.util.Arrays;

public class FlightEnchantTask implements Runnable {

    Enchantment FLIGHT = Enchantment.getByKey(new NamespacedKey("cabot", "flight"));
    NamespacedKey COSMIC = new NamespacedKey("cabot", "cosmic");

    long damageTicks = 0;

    @Override
    public void run() {
        damageTicks++;
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            if (player.getGameMode() != GameMode.SURVIVAL) return;
            if (canFly(player)) {
                player.setAllowFlight(true);
                if (player.isFlying()) {
                    player.getWorld().spawnParticle(
                            Particle.DOLPHIN,
                            player.getLocation(),
                            20,
                            0.3,
                            0.0,
                            0.3
                    );
                    if (damageTicks % 40 == 0) {
                        var chest = player.getInventory().getChestplate();
                        if (!chest.getItemMeta().isUnbreakable()) {
                            chest.damage(1, player);
                        }
                    }
                }
            } else if (player.getAllowFlight() && !isVanished(player)) {
                if (player.isFlying()) {
                    player.setFlying(false);
                }
                player.setAllowFlight(false);
            }
        });
    }

    private boolean canFly(Player player) {
        return player.getInventory().getChestplate().getEnchantments().containsKey(FLIGHT)
                ||
                Arrays.stream(player.getInventory().getArmorContents()).allMatch(i -> i != null && i.getPersistentDataContainer().has(COSMIC));
    }

    private boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }
}
