package dev.cabotmc.cabotenchants.railgun;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.MultiShotEnchantment;
import org.bukkit.craftbukkit.v1_20_R3.enchantments.CraftEnchantment;

public class RailgunEnchant extends Enchantment {
    public RailgunEnchant() {
        super(Rarity.RARE, EnchantmentCategory.CROSSBOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 25;
    }
    @Override
    public int getMaxCost(int levl) {
        return 35;
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return this != other && other != Enchantments.MULTISHOT;
    }


}
