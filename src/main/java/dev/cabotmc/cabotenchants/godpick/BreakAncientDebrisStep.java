package dev.cabotmc.cabotenchants.godpick;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class BreakAncientDebrisStep extends QuestStep {
  static final int NUM_DEBRIS = 35;
  static final NamespacedKey NUM_DEBRIS_KEY = new NamespacedKey("cabot", "num_debris");

  Component createLoreProgressBar(int num) {
    var numGreen = Math.round(((float) num / NUM_DEBRIS) * 10);
    var numGrey = 10 - numGreen;

    var s = new StringBuilder();
    for (int i = 0; i < numGreen; i++) {
      s.append("|");
    }
    var base = Component.text(s.toString(), NamedTextColor.GREEN)
            .decoration(TextDecoration.ITALIC, false);
    var s2 = new StringBuilder();
    for (int i = 0; i < numGrey; i++) {
      s2.append("|");
    }
    return base.append(Component.text(s2.toString(), NamedTextColor.GRAY)
            .decoration(TextDecoration.ITALIC, false));
  }

  @Override
  protected ItemStack internalCreateStepItem() {
    var i = new ItemStack(Material.STICK);
    var m = i.getItemMeta();
    m.setCustomModelData(2);
    m.displayName(
            Component.text("Activated Pickaxe Head")
                    .decoration(TextDecoration.ITALIC, false)
                    .color(TextColor.color(0x634DE0))
    );
    m.getPersistentDataContainer()
            .set(NUM_DEBRIS_KEY, PersistentDataType.INTEGER, 0);
    m.lore(

            List.of(
                    Component.text("After exposing the pickaxe head to every type of ore, it seems to have changed slightly")
                            .decoration(TextDecoration.ITALIC, false)
                            .color(NamedTextColor.GRAY),
                    Component.text("Perhaps exposing it to even more of the rarest ore will entice a larger change")
                            .decoration(TextDecoration.ITALIC, false)
                            .color(NamedTextColor.GRAY),
                    Component.empty(),
                    createLoreProgressBar(0)
            )
    );
    i.setItemMeta(m);
    return i;

  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void breakBlock(BlockBreakEvent e) {
    var b = e.getBlock();
    if (b.getType() != Material.ANCIENT_DEBRIS) return;
    var items = getStepItems(e.getPlayer(), false);
    if (items.isEmpty()) return;
    var item = items.get(0);
    var m = item.item().getItemMeta();
    var num = m.getPersistentDataContainer().get(NUM_DEBRIS_KEY, PersistentDataType.INTEGER);
    if (num == null) return;
    num++;
    if (num == NUM_DEBRIS) {
      replaceWithNextStep(e.getPlayer(), item.slot());
    } else {
      m.getPersistentDataContainer().set(NUM_DEBRIS_KEY, PersistentDataType.INTEGER, num);
      var lore = m.lore();
      lore.set(lore.size() - 1, createLoreProgressBar(num));
      m.lore(lore);
      item.item().setItemMeta(m);
      e.setDropItems(false);
        e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.ANCIENT_DEBRIS));
        e.getBlock()
                .getWorld()
                .spawnParticle(
                        org.bukkit.Particle.END_ROD,
                        e.getBlock().getLocation().toCenterLocation(),
                        10,
                        0.25,
                        0.25,
                        0.25,
                        0.003
                );
    }
  }
}
