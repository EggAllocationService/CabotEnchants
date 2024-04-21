package dev.cabotmc.cabotenchants.eternalrocket;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.inventory.ItemStack;

public class ERLaunchFireworkStep extends QuestStep {
  @Override
  protected ItemStack internalCreateStepItem() {
    return null;
  }

  @EventHandler
  public void explode(FireworkExplodeEvent e) {
    if (e.getEntity().getAttachedTo() != null) return;
    if (!e.getEntity().getFireworkMeta().hasEffects()) return;
    var config = getConfig(CERocketConfig.class);

    if (Math.random() < config.MOOSHROOM_MILK_CHANCE) {
      e.getEntity()
              .getWorld()
              .dropItemNaturally(e.getEntity().getLocation(),
                      getNextStep().createStepItem());
    }
  }
}
