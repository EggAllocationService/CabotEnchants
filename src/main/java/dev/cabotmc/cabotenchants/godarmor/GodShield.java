package dev.cabotmc.cabotenchants.godarmor;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
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
            Attribute.GENERIC_MAX_HEALTH,
            new AttributeModifier(
                    UUID.randomUUID(),
                    "generic.maxHealth",
                    10,
                    AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlot.OFF_HAND
            )
    );

    meta.addAttributeModifier(
            Attribute.GENERIC_ARMOR,
            new AttributeModifier(
                    UUID.randomUUID(),
                    "generic.armor",
                    10,
                    AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlot.OFF_HAND
            )
    );
    i.setItemMeta(meta);
    return i;


  }
}
