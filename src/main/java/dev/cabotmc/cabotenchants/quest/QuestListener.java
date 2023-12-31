package dev.cabotmc.cabotenchants.quest;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.persistence.PersistentDataType;

import static dev.cabotmc.cabotenchants.quest.QuestStep.QUEST_ID_KEY;

public class QuestListener implements Listener {
  long lastTick = 0;
  @EventHandler
  public void tick(ServerTickStartEvent e) {
    lastTick ++;
    if (lastTick % 2 != 0) return;
    Bukkit.getWorlds()
            .stream()
            .flatMap(w -> w.getEntities().stream())
            .filter(i -> i.getType() == EntityType.DROPPED_ITEM)
            .map(i -> (Item) i)
            .filter(i->i.getItemStack().getItemMeta().getPersistentDataContainer().has(QUEST_ID_KEY))
            .forEach(i -> {
              i.getWorld().spawnParticle(
                      Particle.TRIAL_SPAWNER_DETECTION,
                      i.getLocation(),
                      2,
                      0.2,
                      0.2,
                      0.2,
                      0.00003
              );;
            });
  }
  @EventHandler(priority = EventPriority.HIGHEST)
  public void damage(EntityDamageEvent e) {
    if (e.getEntityType() == EntityType.DROPPED_ITEM) {
      var i = (Item) e.getEntity();
      var item = i.getItemStack();
      if (item.getItemMeta().getPersistentDataContainer().has(QUEST_ID_KEY)) {
        e.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void craft(CraftItemEvent e) {
    for (var i :  e.getInventory()
            .getMatrix()) {
        if (i != null && i.getItemMeta().getPersistentDataContainer().has(QUEST_ID_KEY)) {
          e.setCancelled(true);
          return;
        }
    }
  }
}
