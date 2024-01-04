package dev.cabotmc.cabotenchants.quest.impl;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

public abstract class KillEntityStep extends QuestStep {
  static final NamespacedKey ENTITY_PROGRESS_KEY = new NamespacedKey("cabot", "entity_progress");
  int amount;
  public KillEntityStep(int amount) {
    this.amount = amount;
  }

  protected abstract boolean isValidKill(LivingEntity e);
  @EventHandler
  public void onKill(EntityDeathEvent e) {
    if (!isValidKill(e.getEntity())) return;
    if (e.getEntity().getKiller() == null) return;
    if (e.getEntity().getKiller().getType() != EntityType.PLAYER) return;
    var p = (Player) e.getEntity().getKiller();
    if (amount == 1 && getStepNum() == 0) {
      e.getEntity().getWorld()
              .dropItem(e.getEntity().getLocation(), getNextStep().createStepItem(), i -> {
                i.setVelocity(new Vector(0, 0.2, 0));
              });
      return;
    }
    var itemResult = getStepItem(p);
    if (itemResult == null) return;
    var item = itemResult.item();
    var m = item.getItemMeta();
    var progress = m.getPersistentDataContainer().get(ENTITY_PROGRESS_KEY, PersistentDataType.INTEGER);
    if (progress == null) progress = 0;
    progress++;
    if (progress >= amount) {
      p.getInventory().setItem(itemResult.slot(), getNextStep().createStepItem());
    } else {
        m.getPersistentDataContainer().set(ENTITY_PROGRESS_KEY, PersistentDataType.INTEGER, progress);
        item.setItemMeta(m);
        modifyItemOnProgress(item, p, progress);
        p.spawnParticle(
          Particle.END_ROD,
            e.getEntity().getLocation()
                    .add(0, e.getEntity().getHeight() / 2, 0),
                10,
                0,
                e.getEntity().getHeight() / 2,
                0,
                0.003
        );
    }
  }
  protected void modifyItemOnProgress(ItemStack item, Player p, int progress) {

  }

  protected void updateAmount(int amount) {
    this.amount = amount;
  }

  @Override
  public ItemStack createStepItem() {
    var i = super.createStepItem();
    var m = i.getItemMeta();
    m.getPersistentDataContainer().set(ENTITY_PROGRESS_KEY, PersistentDataType.INTEGER, 0);
    i.setItemMeta(m);
    return i;
  }
}
