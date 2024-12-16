package dev.cabotmc.cabotenchants.unbreakingx;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import dev.cabotmc.cabotenchants.util.Models;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;

public class UBXRewardStep extends QuestStep {

  @Override
  protected ItemStack internalCreateStepItem() {
    var i = new ItemStack(Material.ENCHANTED_BOOK);
    var m = (EnchantmentStorageMeta) i.getItemMeta();
    m.addStoredEnchant(Enchantment.UNBREAKING, 10, true);

    m.displayName(
            MiniMessage.miniMessage().deserialize(
                    "<!i><rainbow>Enchanted Book"
            )
    );

    var lore = new ArrayList<Component>();

    lore.add(Component.empty());
    lore.add(
            Component.text("Now Finn can stop malding about breaking elytra")
                    .color(NamedTextColor.DARK_GRAY)
    );
    m.lore(lore);
    m.setItemModel(Models.COSMIC_BOOK);
    i.setItemMeta(m);
    return i;
  }

  @EventHandler
  public void pickup(PlayerAttemptPickupItemEvent e) {
    if (isStepItem(e.getItem().getItemStack())) {
      getQuest().markCompleted(e.getPlayer());
    }
  }
}
