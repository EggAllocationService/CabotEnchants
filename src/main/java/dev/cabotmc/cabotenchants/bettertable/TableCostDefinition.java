package dev.cabotmc.cabotenchants.bettertable;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TableCostDefinition {
    Enchantment enchant;
    Material displayMaterial;
    /**
     * The cost per level of the enchantment.
     * add one to the index to get the level
     */
    int[] costPerLevel;
    static final int[] defaultCosts = new int[]{5, 10, 15, 25, 30};

    public TableCostDefinition(Enchantment enchant, Material m, int... costPerLevel) {
        this.enchant = enchant;
        this.displayMaterial = m;
        this.costPerLevel = costPerLevel;
    }

    public TableCostDefinition(Enchantment enchant, Material m) {
        // use default cost matrix
        var maxLevel = enchant.getMaxLevel();
        costPerLevel = new int[maxLevel];
        for (int i = 0; i < maxLevel; i++) {
            costPerLevel[i] = defaultCosts[i];
        }
        this.enchant = enchant;
        this.displayMaterial = m;
    }

    public int getCost(int level) {
        if (level == 0) return 0;
        return costPerLevel[level - 1];
    }

    public Enchantment getEnchant() {
        return enchant;
    }

    public static final List<Material> ARMOR_MATERIALS = List.of(
            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS,
            Material.CHAINMAIL_HELMET,
            Material.CHAINMAIL_CHESTPLATE,
            Material.CHAINMAIL_LEGGINGS,
            Material.CHAINMAIL_BOOTS,
            Material.IRON_HELMET,
            Material.IRON_CHESTPLATE,
            Material.IRON_LEGGINGS,
            Material.IRON_BOOTS,
            Material.GOLDEN_HELMET,
            Material.GOLDEN_CHESTPLATE,
            Material.GOLDEN_LEGGINGS,
            Material.GOLDEN_BOOTS,
            Material.DIAMOND_HELMET,
            Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_BOOTS,
            Material.NETHERITE_HELMET,
            Material.NETHERITE_CHESTPLATE,
            Material.NETHERITE_LEGGINGS,
            Material.NETHERITE_BOOTS
    );

    public boolean shouldDisplayLine(ItemStack i) {

        if (i.getEnchantments().containsKey(enchant)) {
            if (i.getEnchantmentLevel(enchant) > enchant.getMaxLevel()) {
                return false; // dont allow editing over level enchantments
            }
        }
        for (Enchantment other : i.getEnchantments().keySet()) {
            if (other.conflictsWith(enchant) && other != enchant) {
                return false;
            }
        }
        if (enchant == Enchantment.THORNS) {
            return ARMOR_MATERIALS.contains(i.getType());
        } else {
            return enchant.canEnchantItem(i);
        }
    }

    public Material getDisplayMaterial() {
        return displayMaterial;
    }
}
