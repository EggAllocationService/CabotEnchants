package dev.cabotmc.cabotenchants.mace;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class MCWindChargeStep extends QuestStep {
    @Override
    protected ItemStack internalCreateStepItem() {
        return null;
    }

    private ItemStack createWindCharge() {
        var i = new ItemStack(Material.SNOWBALL);
        var m = i.getItemMeta();
        m.displayName(Component.text("Wind Charge").decoration(TextDecoration.ITALIC, false));
        m.setCustomModelData(1);
        m.getPersistentDataContainer()
                        .set(QUEST_ID_KEY, PersistentDataType.INTEGER, 0);
        i.setItemMeta(m);
        return i;
    }

    @EventHandler
    public void onKill(EntityDeathEvent e) {
        if (e.getEntity().getKiller() == null) return;
        if (e.getEntity().getKiller().getInventory().getItemInMainHand().getType() != Material.IRON_AXE) return;
        if (e.getEntity().getType() != EntityType.SNOWMAN) return;
        var item = e.getEntity().getKiller().getInventory().getItemInMainHand();
        if (!isQuestItem(item)) return;
        var drop = createWindCharge();
        drop.setAmount((int) (Math.random() * 8) + 8);
        e.getDrops().clear();
        e.getDrops().add(drop);
    }

    @EventHandler
    public void onCollide(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Snowball) {
            var snowball = (Snowball) e.getEntity();
            if (snowball.getItem().getItemMeta() != null && snowball.getItem().getItemMeta().getCustomModelData() == 1) {
                WindChargeExplosion.spawn(snowball.getLocation());
            }
        }
    }
}
