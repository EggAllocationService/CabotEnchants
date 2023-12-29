package dev.cabotmc.cabotenchants.frost;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;

public class FrostAspectEnchant extends Enchantment {
    public FrostAspectEnchant() {
        super(Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public void doPostAttack(LivingEntity user, Entity target, int level) {
        var e = Bukkit.getEntity(target.getUUID());
        e.setFreezeTicks(e.getFreezeTicks() + 20 * level);
    }


    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return this != other;
    }
}
