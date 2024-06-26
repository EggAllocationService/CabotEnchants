package dev.cabotmc.cabotenchants.godarmor;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;

import java.util.List;
import java.util.UUID;

public class GodHelmet extends QuestStep {
  @Override
  protected ItemStack internalCreateStepItem() {
    var i = new ItemStack(Material.NETHERITE_HELMET);
    var meta = (ArmorMeta) i.getItemMeta();
    meta.displayName(
            MiniMessage
                    .miniMessage()
                    .deserialize("<!i><rainbow>Cosmic Helmet")
    );
    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ARMOR_TRIM);
    meta.setUnbreakable(true);
    meta.addEnchant(Enchantment.OXYGEN, 3, false);
    meta.addEnchant(Enchantment.WATER_WORKER, 1, false);

    meta.lore(
            List.of(
                    Component.text("Fire Protection \u221E")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("Aqua Affinity")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("Respiration III")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.empty(),
                    Component.text("Always remember to keep a cool head")
                            .color(NamedTextColor.DARK_GRAY)
            )
    );
    meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH,
            new AttributeModifier(UUID.randomUUID(), "god_armor_H", 2.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD));
    i.setItemMeta(meta);
    return i;
  }

  @EventHandler
  public void fire(EntityDamageEvent e) {
    if (e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK
            || e.getCause() == EntityDamageEvent.DamageCause.LAVA || e.getCause() == EntityDamageEvent.DamageCause.HOT_FLOOR) {
      if (e.getEntity() instanceof Player && isStepItem(((Player) e.getEntity()).getInventory().getHelmet())) {
        e.setCancelled(true);
      }
    }
  }
}
