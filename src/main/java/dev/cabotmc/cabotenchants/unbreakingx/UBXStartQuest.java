package dev.cabotmc.cabotenchants.unbreakingx;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import dev.cabotmc.cabotenchants.quest.QuestStep;
import io.papermc.paper.event.block.DragonEggFormEvent;
import io.papermc.paper.event.world.StructuresLocateEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.data.worldgen.Structures;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.structures.*;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class UBXStartQuest extends QuestStep {

  static final NamespacedKey VISITED_KEY = new NamespacedKey("cabot", "structures_visited");

  public static int NETHER_FORTRESS_MASK = 1;
  public static int END_CITY_MASK = 2;
  public static int OCEAN_MONUMENT_MASK = 4;
  public static int DESERT_PYRAMID_MASK = 8;

  public Component createStatusComponent(int progress) {
    var base = Component.empty()
            .decoration(
                    TextDecoration.ITALIC, false
            );
    for (int i = 0; i < 4; i++) {
      var color = (progress & (1 << i)) == 0 ? TextColor.color(0x333333) : TextColor.color(0x00ff00);
      base = base.append(Component.text("\u2022 ").color(color).decoration(TextDecoration.ITALIC, false));
    }
    return base;
  }
  @Override
  protected ItemStack internalCreateStepItem() {
    var i = new ItemStack(Material.DRAGON_EGG);
    var m = i.getItemMeta();
    var lore = new ArrayList<Component>();
    lore.add(
            Component.text("The infant dragon in this egg yearns to see the world.")
                    .color(TextColor.color(0x333333))
                    .decoration(TextDecoration.ITALIC, false)
    );
    lore.add(
            Component.text("Perhaps you could help it?")
                    .color(TextColor.color(0x333333))
                    .decoration(TextDecoration.ITALIC, false)
    );
    lore.add(Component.empty());
    lore.add(createStatusComponent(0));
    m.lore(lore);
    m.getPersistentDataContainer()
                    .set(VISITED_KEY, PersistentDataType.INTEGER, 0);
    i.setItemMeta(m);
    return i;
  }
  @EventHandler
  public void pickup(DragonEggFormEvent e) {
    e.setCancelled(false);
  }
  @EventHandler
  public void drop(EntityDropItemEvent e) {
    if (e.getItemDrop().getItemStack().getType() == Material.DRAGON_EGG) {
      e.getItemDrop().setItemStack(createStepItem());
    }
  }
  @EventHandler
  public void tick(PlayerMoveEvent e) {
    if (e.getFrom().equals(e.getTo())) return;
    var p = e.getPlayer();
    var items = this.getStepItems(p, false);
    if (items.isEmpty()) return;
    var chunk = ((CraftChunk) p.getLocation().getChunk()).getHandle(ChunkStatus.STRUCTURE_STARTS);
    var start = chunk.getAllReferences();
    var world =  ((CraftWorld) p.getWorld()).getHandle();

    var toAdd = 0;
    for (var struct : start.keySet()) {


      if (struct instanceof EndCityStructure) {
        toAdd |= END_CITY_MASK;
      } else if (struct instanceof NetherFortressStructure) {
        toAdd |= NETHER_FORTRESS_MASK;
      } else if (struct instanceof OceanMonumentStructure) {
        toAdd |= OCEAN_MONUMENT_MASK;
      } else if (struct instanceof DesertPyramidStructure) {
        toAdd |= DESERT_PYRAMID_MASK;
      } else {
        continue;
      }
      var isInside = world.structureManager()
              .getStartForStructure(SectionPos.of(p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ()), struct, chunk);
      if (isInside == null) toAdd = 0;
    }
    if (toAdd == 0) return;
    for (var itemResult : items) {
        var item = itemResult.item();
        var m = item.getItemMeta();
        var lore = m.lore();

        var existing = (int) item.getItemMeta().getPersistentDataContainer().get(VISITED_KEY, PersistentDataType.INTEGER);
        if ((existing | toAdd) == existing) continue;
        existing |= toAdd;
        if (existing == 15) {
          replaceWithNextStep(p, itemResult.slot());
        } else {
            lore.set(3, createStatusComponent(existing));
            m.lore(lore);
            m.getPersistentDataContainer().set(VISITED_KEY, PersistentDataType.INTEGER, existing);
            item.setItemMeta(m);
        }
        item.setItemMeta(m);
    }
  }

  @EventHandler
  public void place(BlockPlaceEvent e) {
    if (e.getItemInHand().getType() != Material.DRAGON_EGG) return;
    if (isStepItem(e.getItemInHand()) && !e.getPlayer().isSneaking()) {
        e.setCancelled(true);
        e.getPlayer().sendMessage(
                Component.text("Placing this egg will make you lose all quest progress. Sneak if you really want to place it")
                        .color(TextColor.color(0xff0000))
        );
    }
  }
}
