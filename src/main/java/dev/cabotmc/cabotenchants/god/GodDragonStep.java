package dev.cabotmc.cabotenchants.god;

import dev.cabotmc.cabotenchants.quest.impl.KillEntityStep;
import dev.cabotmc.cabotenchants.quest.impl.KillEntityTypeStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class GodDragonStep extends KillEntityTypeStep {

  public GodDragonStep() {
    super(1, EntityType.ENDER_DRAGON);
  }

  @Override
  protected ItemStack internalCreateStepItem() {
    var i = new ItemStack(Material.PAPER);
    var m = i.getItemMeta();
    m.displayName(
            Component.text("Go")
                    .color(TextColor.color(0xf5c13d))
                    .decoration(TextDecoration.ITALIC, false)
                    .decorate(TextDecoration.OBFUSCATED)
                    .append(
                            Component.text("d")
                                    .decoration(TextDecoration.OBFUSCATED, false)
                    )
    );

    var arr =new ArrayList<Component>();

    arr.add(
            Component.text(
                            "After killing the Wither, the paper transformed!"
                    )
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
    );
    arr.add(
            Component.text(
                            "Fragments of writing are now visible, but it's still mostly illegible."
                    )
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
    );
    arr.add(
            Component.text(
                            "It still thirsts for power, I wonder how it can be sated..."
                    )
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
    );

    arr.add(Component.empty());

    arr.add(
            MiniMessage.miniMessage().deserialize(
                    "<!i><dark_gray>E<obf>nchan</obf>tmen<obf>t</obf>: <obf>Go</obf>d"
            )
    );
    arr.add(
            MiniMessage.miniMessage().deserialize(
                    "<!i><dark_gray>When app<obf>ied</obf> to a fu<obf>ll</obf> set of <obf>armor, it</obf> will grant you <obf>the power of a god</obf>."
            )
    );
    m.lore(arr);
    m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    m.setCustomModelData(2);
    i.setItemMeta(m);
    i.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
    return i;
  }
}
