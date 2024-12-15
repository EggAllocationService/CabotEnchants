package dev.cabotmc.cabotenchants.godarmor;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class GodShield extends QuestStep {
  @Override
  protected ItemStack internalCreateStepItem() {
    var i = new ItemStack(Material.SHIELD);
    var meta = i.getItemMeta();
    meta.displayName(
            MiniMessage
                    .miniMessage().deserialize("<!i><rainbow>Cosmic Shield")
    );
    meta.setUnbreakable(true);
    meta.addAttributeModifier(
            Attribute.MAX_HEALTH,
            new AttributeModifier(
                    new NamespacedKey("cabot", "god_health"),

                    10,
                    AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlotGroup.OFFHAND
            )
    );

    meta.addAttributeModifier(
            Attribute.ARMOR,
            new AttributeModifier(
                    new NamespacedKey("cabot", "god_armor"),
                    10,
                    AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlotGroup.OFFHAND
            )
    );
    i.setItemMeta(meta);
    return i;


  }
}
