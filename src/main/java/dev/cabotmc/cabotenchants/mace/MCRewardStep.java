package dev.cabotmc.cabotenchants.mace;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.Repairable;

import java.util.List;

public class MCRewardStep extends QuestStep {
  @Override
  protected ItemStack internalCreateStepItem() {
    var i = new ItemStack(Material.IRON_AXE);
    var m = (Repairable) i.getItemMeta();
    m.setCustomModelData(1);
    m.displayName(
            MiniMessage
                    .miniMessage().deserialize("<!i><rainbow>Mace")
    );
    m.addEnchant(Enchantment.DURABILITY, 3, true);
    m.addEnchant(Enchantment.MENDING, 1, true);
    m.setRepairCost(999999);
    m.lore(
            List.of(
                    Component.text("Density II")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.empty(),
                    Component.text("+ This weapon deals extra damage the farther you have fallen")
                            .color(NamedTextColor.GREEN)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.empty(),
                    Component.text("Mace to the mace to the mace to the face!")
                            .color(NamedTextColor.DARK_GRAY)
            )
    );

    i.setItemMeta(m);
    return i;
  }
}
