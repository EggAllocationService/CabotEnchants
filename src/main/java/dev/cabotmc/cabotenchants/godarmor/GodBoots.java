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

public class GodBoots extends QuestStep {
  @Override
  protected ItemStack internalCreateStepItem() {
    var i = new ItemStack(Material.NETHERITE_BOOTS);
    var meta = (ArmorMeta) i.getItemMeta();
    meta.displayName(
            MiniMessage
                    .miniMessage()
                    .deserialize("<!i><rainbow>Cosmic Boots")
    );
    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ARMOR_TRIM);
    meta.setUnbreakable(true);
    meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, false);
    meta.addEnchant(Enchantment.SWIFT_SNEAK, 3, false);
    meta.lore(
            List.of(
                    Component.text("Protection IV")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("Swift Sneak III")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("Feather Falling \u221E")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.empty(),
                    MiniMessage.miniMessage()
                            .deserialize("<!i><dark_grey>I've fallen and I can<strikethrough>'t</strikethrough> get up!")

            )
    );
    meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH,
            new AttributeModifier(UUID.randomUUID(), "god_armor_H", 2.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));
    i.setItemMeta(meta);
    return i;
  }

  @EventHandler
  public void fall(EntityDamageEvent e) {
    if (e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.FALL) {
      if (isStepItem(((Player) e.getEntity()).getInventory().getBoots())) {
        e.setCancelled(true);
      }
    }
  }
}
