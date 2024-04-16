package dev.cabotmc.cabotenchants.beacon.upgrades;

import net.minecraft.world.entity.player.Player;

public abstract class BeaconUpgrade {
  int level;

  public void onActivate() {

  }

  public void onDeactivate() {

  }

  public abstract void onTickAsync();

  public abstract void applyToPlayer(Player p);

  public abstract int getMaxLevel();

  public int getLevel() {
    return level;
  }

}
