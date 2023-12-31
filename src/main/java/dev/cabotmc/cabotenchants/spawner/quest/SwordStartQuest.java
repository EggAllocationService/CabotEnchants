package dev.cabotmc.cabotenchants.spawner.quest;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import org.bukkit.Material;
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
      if (e.getBlock().getType() == Material.SPAWNER && Math.random() < 0.25) {
        e.getBlock()
                .getWorld()
                .dropItemNaturally(e.getBlock().getLocation(), getNextStep().createStepItem());
      }
    }
}
