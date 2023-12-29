package dev.cabotmc.cabotenchants.flight;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.MetadataValue;

public class FlightEnchantTask implements Runnable {

    Enchantment FLIGHT = Enchantment.getByKey(new NamespacedKey("cabot", "flight"));
    @Override
    public void run() {
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
