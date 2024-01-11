package dev.cabotmc.cabotenchants.god;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import net.minecraft.server.commands.EffectCommands;
import net.minecraft.world.effect.MobEffect;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GodListener implements @NotNull Listener {
    Enchantment GOD = Enchantment.getByKey(new NamespacedKey("cabot", "god"));
    boolean hasFullGodArmor(Player p) {
        var armor = p.getInventory().getArmorContents();

        return isAllGod(armor);
    }
    boolean isAllGod(ItemStack[] items) {
        for (var i : items) {
            if (i == null) return false;
            if (!i.getEnchantments().containsKey(GOD)) return false;
        }
        return true;
    }

    static EntityDamageEvent.DamageCause[] BLACKLIST = new EntityDamageEvent.DamageCause[]
            {EntityDamageEvent.DamageCause.VOID, EntityDamageEvent.DamageCause.SUICIDE, EntityDamageEvent.DamageCause.KILL,
            EntityDamageEvent.DamageCause.SUICIDE};
    static EntityType[] ENTITY_BLACKLIST = new EntityType[]
            {EntityType.WARDEN, EntityType.WITHER, EntityType.ENDER_DRAGON};


    static List<PotionEffectType> EFFECTS = List.of(PotionEffectType.DAMAGE_RESISTANCE,
                    PotionEffectType.WATER_BREATHING, PotionEffectType.FIRE_RESISTANCE, PotionEffectType.NIGHT_VISION,
             PotionEffectType.SPEED, PotionEffectType.JUMP);
    static int[] AMPLIFIERS = new int[] {2, 0, 0, 0, 0, 0};
    static List<EquipmentSlot> ARMOR_SLOTS = List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

    @EventHandler
    public void equip(PlayerInventorySlotChangeEvent e) {
        var p = e.getPlayer();
        if (e.getRawSlot() >= 5 && e.getRawSlot() <= 8) {
            var newStack = e.getNewItemStack();
            var oldStack = e.getOldItemStack();
            if (oldStack.containsEnchantment(GOD) && !newStack.containsEnchantment(GOD)) {
                p.getActivePotionEffects()
                        .stream()
                        .filter(PotionEffect::isInfinite)
                        .filter(effect -> EFFECTS.contains(effect.getType()))
                        .forEach(effect -> p.removePotionEffect(effect.getType()));
                p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
            } else if (!oldStack.containsEnchantment(GOD) && newStack.containsEnchantment(GOD)) {
                var armorIndex = e.getRawSlot() - 5;
                var armor = getOrderedArmor(p);
                armor[armorIndex] = newStack;
                if (isAllGod(armor)) {
                    for (int i = 0; i < EFFECTS.size(); i++) {
                        p.addPotionEffect(new PotionEffect(EFFECTS.get(i), -1, AMPLIFIERS[i], true, false, false));
                    }
                    p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30);
                }
            }
        }
    }

    ItemStack[] getOrderedArmor(Player p) {
        var inv = p.getInventory();
        var ordered = new ItemStack[4];
        for (var i = 0; i < 4; i++) {
            ordered[i] = inv.getItem(ARMOR_SLOTS.get(i));
        }
        return ordered;
    }

    @EventHandler
    public void target(EntityTargetEvent e) {
        if (e.getTarget() == null || e.getReason() == EntityTargetEvent.TargetReason.CUSTOM) return;
        if (e.getTarget().getType() != EntityType.PLAYER) return;
        if (!(e.getEntity() instanceof Monster)) return;
        var p = (Player) e.getTarget();
        if (hasFullGodArmor(p)) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void damage(EntityDamageEvent e) {
        if (e.getCause() != EntityDamageEvent.DamageCause.FALL && e.getCause() != EntityDamageEvent.DamageCause.FLY_INTO_WALL) return;
        if (!(e.getEntity() instanceof Player)) return;
        var p = (Player) e.getEntity();
        if (hasFullGodArmor(p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void damage(EntityDamageByEntityEvent e) {
        Player p = null;
        if (e.getDamager() instanceof Player ) {
            p = (Player) e.getDamager();

        }
        else if (e.getDamager() instanceof Projectile) {
            var s = ((Projectile) e.getDamager()).getShooter();
            if (s instanceof Player) {
                p = (Player) s;
            }
        }
        if (p == null) return;
        if (hasFullGodArmor(p)) {
            for (var et : ENTITY_BLACKLIST) {
                if (e.getEntity().getType() == et) {
                    e.setCancelled(true);
                    break;
                }
            }
            if (e.getEntityType() != EntityType.PLAYER) {
                if (e.getEntity() instanceof Monster) {
                    ((Monster) e.getEntity()).setTarget(p);
                }

            }
        }

    }
}
