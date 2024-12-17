package dev.cabotmc.cabotenchants.godarmor;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import dev.cabotmc.cabotenchants.util.Models;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;

import java.util.List;

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
        meta.addEnchant(Enchantment.PROTECTION, 4, false);
        meta.lore(
                List.of(
                        Component.empty(),
                        Component.text("Rush B do not stop my friends")
                                .color(NamedTextColor.DARK_GRAY)

                )
        );

        var equippable = meta.getEquippable();
        equippable.setSlot(EquipmentSlot.LEGS);
        equippable.setModel(new NamespacedKey("cabot", "cosmic"));
        meta.setEquippable(equippable);

        meta.setItemModel(Models.COSMIC_LEGGINGS_ITEM);

        meta.addAttributeModifier(Attribute.MOVEMENT_SPEED,
                new AttributeModifier(new NamespacedKey("cabot", "god_speed"), 0.15, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS));

        meta.addAttributeModifier(Attribute.MAX_HEALTH,
                new AttributeModifier(new NamespacedKey("cabot", "god_health"), 2.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS));
        i.setItemMeta(meta);
        return i;
    }
}
