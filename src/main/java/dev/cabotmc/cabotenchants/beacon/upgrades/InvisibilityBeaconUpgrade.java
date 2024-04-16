package dev.cabotmc.cabotenchants.beacon.upgrades;

import net.minecraft.world.entity.player.Player;

import java.io.Serializable;

public class InvisibilityBeaconUpgrade extends BeaconUpgrade implements Serializable {
  boolean active = false;

  @Override
  public void onTickAsync() {

  }

  @Override
  public void applyToPlayer(Player p) {

  }

  @Override
  public int getMaxLevel() {
    return 1;
  }
}
