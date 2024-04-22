package dev.cabotmc.cabotenchants.util;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;

public class YAxisFalldamageGate implements Listener {
    public static final NamespacedKey Y_AXIS_FALLDAMAGE = new NamespacedKey("cabot", "y_axis_fall");
    @EventHandler
    public void damage(EntityDamageEvent e) {
        if (e.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        if (e.getEntity().getPersistentDataContainer().has(Y_AXIS_FALLDAMAGE, PersistentDataType.INTEGER)) {
            var prev_y_axis = e.getEntity().getPersistentDataContainer().get(Y_AXIS_FALLDAMAGE, PersistentDataType.INTEGER);
            if (e.getEntity().getY() >= prev_y_axis) {
                e.setCancelled(true);
            }
            e.getEntity().getPersistentDataContainer().remove(Y_AXIS_FALLDAMAGE);
        }
    }

}
