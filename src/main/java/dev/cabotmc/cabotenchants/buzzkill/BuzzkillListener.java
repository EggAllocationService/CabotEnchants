package dev.cabotmc.cabotenchants.buzzkill;

import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.meta.Damageable;

public class BuzzkillListener implements Listener {

    Enchantment BUZZKILL = Enchantment.getByKey(new NamespacedKey("cabot", "buzzkill"));

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void attack(PrePlayerAttackEntityEvent e) {
        if (!e.willAttack()) return;
        var p = e.getPlayer();
        if (!(e.getAttacked() instanceof Player attacked)) return;

        var item = p.getInventory().getItemInMainHand();
        if (item.getEnchantments().containsKey(BUZZKILL)) {
            var level = item.getEnchantmentLevel(BUZZKILL);
            var chance = 0.1 * level;
            var rand = Math.random();
            if (rand < chance) {
                var chest = attacked.getInventory().getChestplate();
                if (chest.getType() != Material.ELYTRA) {
                    return;
                }
                chest.damage(100000, p);
                p.getWorld().playSound(attacked.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                p.getWorld().spawnParticle(Particle.ITEM_CRACK, attacked.getLocation(), 100, 0.5, 0.5, 0.5, 0.1, item);
            }

        }
    }
}
