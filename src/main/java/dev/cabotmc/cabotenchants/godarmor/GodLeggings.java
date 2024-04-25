package dev.cabotmc.cabotenchants.godarmor;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;

import java.util.List;
import java.util.UUID;

public class GodLeggings extends QuestStep {
  @Override
  protected ItemStack internalCreateStepItem() {
    var i = new ItemStack(Material.NETHERITE_LEGGINGS);
    var meta = (ArmorMeta) i.getItemMeta();
    meta.displayName(
            MiniMessage
                    .miniMessage()
                    .deserialize("<!i><rainbow>Cosmic Leggings")
    );
    meta.addItemFlags(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ARMOR_TRIM);
    meta.setUnbreakable(true);
    meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, false);
    meta.lore(
            List.of(
                    Component.empty(),
                    Component.text("Rush B do not stop my friends")
                            .color(NamedTextColor.DARK_GRAY)

            )
    );
    meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED,
            new AttributeModifier(UUID.randomUUID(), "god_armor", 2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS));
    i.setItemMeta(meta);
    return i;
  }
}
