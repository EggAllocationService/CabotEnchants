package dev.cabotmc.cabotenchants.spawner.quest;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import dev.cabotmc.cabotenchants.spawner.SpawnerSwordReward;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class SwordStartQuest extends QuestStep {
    @Override
    protected ItemStack internalCreateStepItem() {
      return null;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void blockBreak(BlockBreakEvent e) {
      if (e.getBlock().getType() == Material.SPAWNER) {
        CreatureSpawner meta = (CreatureSpawner) e.getBlock().getState();
        if (meta.getPersistentDataContainer().has(SpawnerSwordReward.SOULDRINKER_TAG)) {
          e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), SpawnerSwordReward.createSpawner(meta.getSpawnedType()));
          return;
        }
        if (Math.random() > 0.50) {
          e.getBlock()
                  .getWorld()
                  .dropItemNaturally(e.getBlock().getLocation(), getNextStep().createStepItem());
        }
      }
    }
}
