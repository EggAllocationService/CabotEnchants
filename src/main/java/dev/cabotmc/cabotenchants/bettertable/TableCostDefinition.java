package dev.cabotmc.cabotenchants.bettertable;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class TableCostDefinition {
  Enchantment enchant;
  /**
   * The cost per level of the enchantment.
   * add one to the index to get the level
   */
  int[] costPerLevel;
  static final int[] defaultCosts = new int[] {5, 10, 15, 25, 30};
    public TableCostDefinition(Enchantment enchant, int... costPerLevel) {
        this.enchant = enchant;
        this.costPerLevel = costPerLevel;
    }
    public TableCostDefinition(Enchantment enchant) {
      // use default cost matrix
        var maxLevel = enchant.getMaxLevel();
        costPerLevel = new int[maxLevel];
        for (int i = 0; i < maxLevel; i++) {
          costPerLevel[i] = defaultCosts[i];
        }
        this.enchant = enchant;
    }

    public int getCost(int level) {
      if (level == 0) return 0;
      return costPerLevel[level - 1];
    }
    public Enchantment getEnchant() {
        return enchant;
    }
    public boolean shouldDisplayLine(ItemStack i) {

      if (i.getEnchantments().containsKey(enchant)) {
        if (i.getEnchantmentLevel(enchant) > enchant.getMaxLevel()) {
          return false; // dont allow editing over level enchantments
        }
      }
      return enchant.getItemTarget().includes(i.getType());
    }
}
