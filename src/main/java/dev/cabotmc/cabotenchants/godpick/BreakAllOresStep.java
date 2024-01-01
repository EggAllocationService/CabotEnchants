package dev.cabotmc.cabotenchants.godpick;

import com.google.gson.Gson;
import dev.cabotmc.cabotenchants.quest.ChecklistTracker;
import dev.cabotmc.cabotenchants.quest.QuestStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BreakAllOresStep extends QuestStep {
  static final NamespacedKey ORES_TRACKER_KEY = new NamespacedKey("cabot", "ores_tracker");

  @Override
  protected ItemStack internalCreateStepItem() {
    var i = new ItemStack(Material.STICK);
    var m = i.getItemMeta();
    m.displayName(
            Component.text("Broken Pickaxe Head")
                    .color(TextColor.color(0x4C4C4C))
                            .decoration(TextDecoration.ITALIC, false)
    );
    m.lore(
            List.of(
                    Component.text("I found this broken pickaxe head embedded in some ore.")
                            .color(TextColor.color(NamedTextColor.GRAY))
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("It must have gotten stuck and was abandoned by a miner long ago")
                            .color(TextColor.color(NamedTextColor.GRAY))
                            .decoration(TextDecoration.ITALIC, false),
                    Component.empty(),
                    createChecklistLore(new OresTracker())

            )
    );
    m.getPersistentDataContainer()
                    .set(ORES_TRACKER_KEY, OresTrackerDataType.CODEC, new OresTracker());
    m.setCustomModelData(1);
    i.setItemMeta(m);
    return i;
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void breakOre(BlockBreakEvent e) {
    var b = e.getBlock();
    if (!OresTracker.isOre(b.getType())) return;
    var items = getStepItems(e.getPlayer(), false);
    if (items.isEmpty()) return;
    var didSomething = false;
    for (var questItem : items) {
      var m = questItem.item();
      var meta = m.getItemMeta();
      var data = meta.getPersistentDataContainer().get(ORES_TRACKER_KEY, OresTrackerDataType.CODEC);
      if (data == null) continue;

      if (data.updateProgress(b.getType())) {

        if (data.isComplete())  {
          replaceWithNextStep(e.getPlayer(), questItem.slot());
        } else {
            var l = meta.lore();
            l.set(l.size() - 1, createChecklistLore(data));
            meta.lore(l);
            meta.getPersistentDataContainer().set(ORES_TRACKER_KEY, OresTrackerDataType.CODEC, data);
            m.setItemMeta(meta);
            didSomething = true;
        }
      }

    }
    if (didSomething) {
      e.getPlayer()
              .getWorld()
              .spawnParticle(
                      Particle.END_ROD,
                      e.getBlock()
                              .getLocation()
                              .toCenterLocation(),
                      50,
                      0.25,
                      0.25,
                      0.25,
                      0.0003

              );
    }
  }

  static class OresTracker implements ChecklistTracker {
    static final Material[][] ORES = {
            {Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE},
            {Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE},
            {Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE},
            {Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE},
            {Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE},
            {Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE},
            {Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE},
            {Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE},
            {Material.NETHER_GOLD_ORE},
            {Material.NETHER_QUARTZ_ORE},
            {Material.ANCIENT_DEBRIS}
    };
    static boolean isOre(Material m) {
        for (Material[] arr : ORES) {
            for (Material ore : arr) {
            if (ore == m) return true;
            }
        }
        return false;
    }
    boolean[] progress = new boolean[ORES.length];

    public boolean updateProgress(Material m) {
      for (int i = 0; i < ORES.length; i++) {
        for (Material ore : ORES[i]) {
          if (ore == m) {
            if (progress[i]) return false;
            progress[i] = true;
            return true;
          }
        }
      }
      return false;
    }
    public boolean isComplete() {
        for (boolean b : progress) {
            if (!b) return false;
        }
        return true;
    }

    @Override
    public boolean[] getChecklist() {
      return progress;
    }
  }

  static class OresTrackerDataType implements PersistentDataType<String, OresTracker> {
    static final OresTrackerDataType CODEC = new OresTrackerDataType();
    Gson g = new Gson();

    @Override
    public @NotNull Class<String> getPrimitiveType() {
      return String.class;
    }

    @Override
    public @NotNull Class<OresTracker> getComplexType() {
      return OresTracker.class;
    }

    @Override
    public @NotNull String toPrimitive(@NotNull OresTracker complex, @NotNull PersistentDataAdapterContext context) {
      return g.toJson(complex);
    }

    @Override
    public @NotNull OresTracker fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
      return g.fromJson(primitive, OresTracker.class);
    }
  }
}
