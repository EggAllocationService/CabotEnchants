package dev.cabotmc.cabotenchants.sentient.quest;

import dev.cabotmc.cabotenchants.CabotEnchants;
import dev.cabotmc.cabotenchants.quest.QuestStep;
import dev.cabotmc.cabotenchants.sentient.CETridentConfig;
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
    if (e.getEntityType() == EntityType.ELDER_GUARDIAN && Math.random() < getConfig(CETridentConfig.class).BROKEN_TRIDENT_DROP_CHANCE) {
      e.getDrops().add(getNextStep().createStepItem());
    }
  }
}
