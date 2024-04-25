package dev.cabotmc.cabotenchants.boss;

import dev.cabotmc.cabotenchants.CabotEnchants;
import dev.cabotmc.cabotenchants.util.JsonDataType;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BossDropCalculator {
  private static final NamespacedKey DROP_TRACKER_KEY = new NamespacedKey("cabot", "boss_drops");

  public static void createDropForPlayer(Player player, Location spawnLoc) {
    // This method is responsible for creating a drop for a player when they defeat a boss.
    var data = player.getPersistentDataContainer()
            .getOrDefault(DROP_TRACKER_KEY, DropTracker.CODEC, new DropTracker());
    int index;
    if (data.allDone()) {
      // pick random number 0, 1, 2, or 3
      index = (int) (Math.random() * 4);

    } else {
      // pick one of the ones not gotten yet
      do {
        index = (int) (Math.random() * 4);
      } while (data.helmet && index == 0 || data.chestplate && index == 1 || data.leggings && index == 2 || data.boots && index == 3);
    }
    ItemStack drop = null;
    switch (index) {
      case 0:
        drop = CabotEnchants.GOD_HELMET.createStepItem();
        break;
      case 1:
        drop = CabotEnchants.GOD_CHESTPLATE.createStepItem();
        break;
      case 2:
        drop = CabotEnchants.GOD_LEGGINGS.createStepItem();
        break;
      case 3:
        drop = CabotEnchants.GOD_BOOTS.createStepItem();
        break;
    }
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