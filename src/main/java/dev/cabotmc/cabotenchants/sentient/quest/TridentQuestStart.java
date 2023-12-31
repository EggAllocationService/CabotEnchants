package dev.cabotmc.cabotenchants.sentient.quest;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class TridentQuestStart extends QuestStep {

  @Override
  protected ItemStack internalCreateStepItem() {
    return null;
  }
  @EventHandler
  public void onKill(EntityDeathEvent e) {
    if (e.getEntityType() == EntityType.ELDER_GUARDIAN && Math.random() < 0.1) {
      e.getDrops().add(getNextStep().createStepItem());
    }
  }
}
