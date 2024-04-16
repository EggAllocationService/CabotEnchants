package dev.cabotmc.cabotenchants.vanillalite;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;

public class ElytraPreventer implements Listener {
    @EventHandler
    public void drop(ItemSpawnEvent e) {
        if (e.getEntity().getItemStack().getType() == Material.ELYTRA) {
            e.getEntity().getItemStack().setType(Material.DIAMOND_BLOCK);
        }
    }
}
