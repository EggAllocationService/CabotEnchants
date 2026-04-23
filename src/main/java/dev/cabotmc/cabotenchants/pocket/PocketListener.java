package dev.cabotmc.cabotenchants.pocket;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.PortalCreateEvent;

public class PocketListener implements Listener {
    public static final NamespacedKey POCKET_WORLD = new NamespacedKey("cabot", "pocket");

    @EventHandler
    public void dragon(PortalCreateEvent e) {
        if (e.getWorld().getKey().equals(POCKET_WORLD)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void entity(CreatureSpawnEvent e) {
        if (e.getLocation().getWorld().getKey().equals(POCKET_WORLD)) {
            e.setCancelled(true);
        }
    }
}
