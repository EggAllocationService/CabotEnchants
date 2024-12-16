package dev.cabotmc.cabotenchants.god;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class GodRewardStep extends QuestStep {
  @Override
  protected ItemStack internalCreateStepItem() {
    var i = new ItemStack(Material.ENCHANTED_BOOK);
    var m = (EnchantmentStorageMeta) i.getItemMeta();
    m.addStoredEnchant(Enchantment.getByKey(new NamespacedKey("cabot", "god")), 1, false);

    m.displayName(
            MiniMessage.miniMessage().deserialize(
                    "<!i><rainbow>Enchanted Book"
            )
    );

    var lore = new ArrayList<Component>();
    lore.add(
            MiniMessage.miniMessage().deserialize(
                    "<!i><gray>When applied to a full set of armor, gain the following benefits:"
            )
    );
    lore.add(
            Component.text("  - Gain permanent Resistance 3, Night Vision, Water Breathing, and Fire Resistance")
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
    );
    lore.add(
            Component.text("  - Gain immunity to fall damage")
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
    );
    lore.add(
            Component.text("  - Mobs will not target you unless you attack them first")
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
    );
    lore.add(Component.empty());
    lore.add(
            Component.text("  - While active, you cannot attack other players or bosses")
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
    );
    lore.add(
            Component.text("  - This enchantment conflicts with all other enchantments")
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
    );

    lore.add(Component.empty());
    lore.add(
            Component.text("What good is a god who holds the stars in place, but")
                    .color(NamedTextColor.DARK_GRAY)
    );
    lore.add(
            Component.text("fails to cradle the broken pieces of a human heart?")
                    .color(NamedTextColor.DARK_GRAY)
    );
    m.lore(lore);
    m.setItemModel(new NamespacedKey());
    i.setItemMeta(m);
    return i;
  }
}
