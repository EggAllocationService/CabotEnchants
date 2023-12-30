package dev.cabotmc.cabotenchants.god;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.MendingEnchantment;

public class GodEnchant extends Enchantment{

    public GodEnchant() {
        super(Enchantment.Rarity.RARE, EnchantmentCategory.WEARABLE, new EquipmentSlot[]{});
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

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return other instanceof MendingEnchantment || other instanceof DigDurabilityEnchantment;
    }
}

