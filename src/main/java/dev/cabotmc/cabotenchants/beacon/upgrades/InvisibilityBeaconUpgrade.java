package dev.cabotmc.cabotenchants.beacon.upgrades;



import dev.cabotmc.cabotenchants.beacon.BeaconUpgrade;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.Serializable;
import java.util.Set;

public class InvisibilityBeaconUpgrade extends BeaconUpgrade implements Serializable {
  transient int ticks = 0;

  @Override
  public void playerEnteredRange(Player p) {
    execSync(() -> {
      p.sendMessage(Component.text("Entered range"));
    });
  }

  @Override
  public void playerLeftRange(Player p) {
    execSync(() -> {
      p.sendMessage(Component.text("Left range"));
    });
  }

  @Override
  public void onTickAsync(Location beaconLocation, Set<Player> inRange) {
    if (ticks % 20 == 0) {
      execSync(() -> {
        for (var p : inRange) {
          p.sendMessage(Component.text("Hello " + ticks));
        }
      });
    }
    ticks++;
  }

  @Override
  public int getMaxLevel() {
    return 1;
  }
}
