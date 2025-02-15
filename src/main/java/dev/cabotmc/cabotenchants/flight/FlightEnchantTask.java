package dev.cabotmc.cabotenchants.flight;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

public class FlightEnchantTask implements Runnable {

    Enchantment FLIGHT = Enchantment.getByKey(new NamespacedKey("cabot", "flight"));
    long damageTicks = 0;

    @Override
    public void run() {
        damageTicks++;
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            if (player.getGameMode() != GameMode.SURVIVAL) return;
            if (player.getInventory().getChestplate() != null) {
                if (player.getInventory().getChestplate().getEnchantments().containsKey(FLIGHT)) {
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
                    return;
                }
            }
            if (player.getAllowFlight() && !isVanished(player)) {
                if (player.isFlying()) {
                    player.setFlying(false);
                }
                player.setAllowFlight(false);
            }
        });
    }

    private boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }
}
