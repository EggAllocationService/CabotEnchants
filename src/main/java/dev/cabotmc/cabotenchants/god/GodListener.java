package dev.cabotmc.cabotenchants.god;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class GodListener implements @NotNull Listener {
    Enchantment GOD = Enchantment.getByKey(new NamespacedKey("cabot", "god"));
    boolean hasFullGodArmor(Player p) {
        var armor = p.getInventory().getArmorContents();
        for (var i : armor) {
            if (i == null) return false;
            if (!i.getEnchantments().containsKey(GOD)) return false;
        }
        return true;
    }
    static EntityDamageEvent.DamageCause[] BLACKLIST = new EntityDamageEvent.DamageCause[]
            {EntityDamageEvent.DamageCause.VOID, EntityDamageEvent.DamageCause.SUICIDE, EntityDamageEvent.DamageCause.KILL,
            EntityDamageEvent.DamageCause.SUICIDE};
    static EntityType[] ENTITY_BLACKLIST = new EntityType[]
            {EntityType.PLAYER, EntityType.WARDEN, EntityType.WITHER, EntityType.ENDER_DRAGON};
    @EventHandler
    public void damage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        var p = (Player) e.getEntity();
        if (!hasFullGodArmor(p)) return;
        for (var c : BLACKLIST) {
            if (e.getCause() == c) return;
        }
        e.setCancelled(true);
    }
    @EventHandler
    public void damage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player ) {
            var p = (Player) e.getDamager();
            if (hasFullGodArmor(p)) {
                for (var et : ENTITY_BLACKLIST) {
                    if (e.getEntity().getType() == et) {
                        e.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }
}
