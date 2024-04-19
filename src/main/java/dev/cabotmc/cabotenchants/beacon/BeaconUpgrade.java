package dev.cabotmc.cabotenchants.beacon;

import dev.cabotmc.cabotenchants.CabotEnchants;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.function.Consumer;

public abstract class BeaconUpgrade {
  int level;

  public void playerEnteredRange(Player p) {

  }

  public void playerLeftRange(Player p) {
  }

  public abstract void onTickAsync(Location beaconLocation, Set<Player> inRange);

  public abstract int getMaxLevel();

  public int getLevel() {
    return level;
  }

  public void execSync(Runnable task) {
    Bukkit.getScheduler().runTask(CabotEnchants.getProvidingPlugin(CabotEnchants.class), task);
  }

}
