package dev.cabotmc.cabotenchants.godpick;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class VeinminerEnchant extends Enchantment {
  public VeinminerEnchant() {
    super(Rarity.VERY_RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
  }

  @Override
  public boolean isDiscoverable() {
    return false;
  }

  @Override
  public boolean isTradeable() {
    return false;
  }

  @Override
  public int getMaxLevel() {
    return 1;
  }

  @Override
  public int getMinCost(int level) {
    return 9999;
  }

  @Override
  public int getMaxCost(int level) {
    return 9999;
  }
}
