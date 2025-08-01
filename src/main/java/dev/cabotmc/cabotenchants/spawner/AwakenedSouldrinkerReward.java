package dev.cabotmc.cabotenchants.spawner;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import dev.cabotmc.cabotenchants.util.Models;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Repairable;

import java.util.List;

public class AwakenedSouldrinkerReward extends QuestStep {

    public static final NamespacedKey NO_INSTAKILL_KEY = new NamespacedKey("cabot", "no_instakill");

    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.GOLDEN_SWORD);
        var meta = (Repairable) i.getItemMeta();
        meta.displayName(
                MiniMessage.miniMessage()
                        .deserialize("<!i><rainbow>Souldrinker Perfected")
        );
        meta.lore(
                List.of(
                        Component.text("Sharpness \u221E")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false),
                        Component.text("Looting V")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false),
                        Component.text("Sweeping Edge III")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false),
                        Component.text("Fire Aspect II")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false),
                        Component.text("Frost Aspect II")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false),
                        Component.text("Mending")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false),
                        Component.text("Unbreaking III")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false),
                        Component.empty(),
                        Component.text("Extinguish the stars. Devour the planets. Soar through a universe of utter dark.")
                                .color(NamedTextColor.DARK_GRAY)
                )
        );
        meta.setItemModel(Models.COSMIC_SWORD);
        meta.setEnchantmentGlintOverride(false);
        meta.setRepairCost(999999);

        meta.addEnchant(Enchantment.SHARPNESS, 15, false);
        meta.addEnchant(Enchantment.LOOTING, 5, false);
        meta.addEnchant(Enchantment.UNBREAKING, 3, false);
        meta.addEnchant(Enchantment.MENDING, 1, false);
        meta.addEnchant(Enchantment.SWEEPING_EDGE, 3, false);
        meta.addEnchant(Enchantment.FIRE_ASPECT, 2, false);

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        i.setItemMeta(meta);

        var enchants = i.getData(DataComponentTypes.ENCHANTMENTS);
        enchants = enchants.showInTooltip(false);
        i.setData(DataComponentTypes.ENCHANTMENTS, enchants);


        return i;
    }

    @EventHandler
    public void damage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            var p = (Player) e.getDamager();
            if (isStepItem(p.getInventory().getItemInMainHand()) && e.getEntityType() != EntityType.PLAYER) {
                // dont do bosses
                if (e.getEntityType() == EntityType.ENDER_DRAGON || e.getEntityType() == EntityType.WITHER) {
                    return;
                }
                if (e.getEntity().getPersistentDataContainer().has(NO_INSTAKILL_KEY)) {
                    return;
                }
                // full attack strength
                if (p.getAttackCooldown() != 1.0) return;

                e.setDamage(99999999.0);
                e.getEntity()
                        .getWorld()
                        .spawnParticle(
                                Particle.FLASH,
                                e.getEntity().getLocation().add(0, e.getEntity().getHeight() / 2, 0),
                                10,
                                0.3,
                                0.3,
                                0.3
                        );

                e.getEntity()
                        .getWorld()
                        .playSound(
                                e.getEntity().getLocation(),
                                Sound.ENTITY_FIREWORK_ROCKET_BLAST,
                                0.7f,
                                1.0f
                        );
            }
        }
    }
}
