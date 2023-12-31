package dev.cabotmc.cabotenchants.sentient;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SentienceEnchant extends Enchantment {
  public SentienceEnchant() {
    super(Rarity.VERY_RARE, EnchantmentCategory.TRIDENT, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
  }

  @Override
  public int getMaxLevel() {
    return 1;
  }

  @Override
  public int getMinCost(int level) {
    return 999;
  }

  @Override
  public int getMaxCost(int level) {
    return 999;
  }

  @Override
  public boolean isDiscoverable() {
    return false;
  }
}
