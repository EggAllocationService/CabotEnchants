package dev.cabotmc.cabotenchants.boss;

import dev.cabotmc.cabotenchants.CabotEnchants;
import dev.cabotmc.cabotenchants.util.JsonDataType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class BossDropCalculator {
  private static final NamespacedKey DROP_TRACKER_KEY = new NamespacedKey("cabot", "boss_drops");

  public static void createDropForPlayer(Player player, Location spawnLoc) {
    // This method is responsible for creating a drop for a player when they defeat a boss.
    var data = player.getPersistentDataContainer()
            .getOrDefault(DROP_TRACKER_KEY, DropTracker.CODEC, new DropTracker());
    ItemStack drop = null;
    if (data.allDone()) {
      // pick random number 0, 1, 2, or 3
      int index = (int) (Math.random() * 4);
      switch (index) {
        case 0:
          drop = CabotEnchants.GOD_HELMET.createStepItem();
          data.helmet = true;
          break;
        case 1:
          drop = CabotEnchants.GOD_CHESTPLATE.createStepItem();
          data.chestplate = true;
          break;
        case 2:
          drop = CabotEnchants.GOD_LEGGINGS.createStepItem();
          data.leggings = true;
          break;
        case 3:
          drop = CabotEnchants.GOD_BOOTS.createStepItem();
          data.boots = true;
          break;
      }
    } else {
      // pick one of the ones not gotten yet
      var items = new ArrayList<ItemStack>();
        if (!data.helmet) {
            items.add(CabotEnchants.GOD_HELMET.createStepItem());
        }
        if (!data.chestplate) {
            items.add(CabotEnchants.GOD_CHESTPLATE.createStepItem());
        }
        if (!data.leggings) {
            items.add(CabotEnchants.GOD_LEGGINGS.createStepItem());
        }
        if (!data.boots) {
            items.add(CabotEnchants.GOD_BOOTS.createStepItem());
        }
        drop = items.get((int) (Math.random() * items.size()));

        if (drop.getType() == Material.NETHERITE_HELMET) {
          data.helmet = true;
        } else if (drop.getType() == Material.NETHERITE_CHESTPLATE) {
          data.chestplate = true;
        } else if (drop.getType() == Material.NETHERITE_LEGGINGS) {
          data.leggings = true;
        } else if (drop.getType() == Material.NETHERITE_BOOTS) {
          data.boots = true;
        }

    }

    var i = (Item) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.DROPPED_ITEM);
    i.setItemStack(drop);
    // needed for some reason, client bug maybe?
    i.teleport(new Location(spawnLoc.getWorld(), spawnLoc.getX(), 65, spawnLoc.getZ()));
    i.setVelocity(new Vector(0, 0, 0));
    i.setGravity(false);
    i.setPickupDelay(80);

    CabotEnchants.GOD_HELMET.getQuest()
            .markCompleted(player);

    for (var p : spawnLoc.getWorld().getPlayers()) {
      if (p == player) {
        continue;
      }
      p.hideEntity(CabotEnchants.getPlugin(CabotEnchants.class), i);
    }
    player.getPersistentDataContainer()
            .set(DROP_TRACKER_KEY, DropTracker.CODEC, data);
  }

  private static class DropTracker {
    private static final JsonDataType<DropTracker> CODEC = new JsonDataType<>(DropTracker.class);
    boolean helmet;
    boolean chestplate;
    boolean leggings;
    boolean boots;

    public DropTracker() {
      helmet = false;
      chestplate = false;
      leggings = false;
      boots = false;
    }

    public boolean allDone() {
      return helmet && chestplate && leggings && boots;
    }
  }
}
