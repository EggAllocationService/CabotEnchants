package dev.cabotmc.cabotenchants.godpick;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PickStartStep extends QuestStep {
  @Override
  protected ItemStack internalCreateStepItem() {
    return null;
  }
  static List<Material> WHITELIST = List.of(
          Material.GOLD_ORE,
            Material.DEEPSLATE_GOLD_ORE,
            Material.IRON_ORE,
            Material.DEEPSLATE_IRON_ORE,
            Material.DEEPSLATE_GOLD_ORE,
            Material.DIAMOND_ORE,
            Material.DEEPSLATE_DIAMOND_ORE,
            Material.EMERALD_ORE,
            Material.DEEPSLATE_EMERALD_ORE,
          Material.ANCIENT_DEBRIS
  );
  Advancement KILLED_WITHER = Bukkit.getAdvancement(new NamespacedKey("minecraft", "nether/summon_wither"));
  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void breakBlock(BlockBreakEvent e) {

    if ((WHITELIST.contains(e.getBlock().getType()) && Math.random() <= 0.005)) {
        e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), getNextStep().createStepItem());
    }
  }
}
