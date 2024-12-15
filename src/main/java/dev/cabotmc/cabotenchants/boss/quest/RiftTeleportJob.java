package dev.cabotmc.cabotenchants.boss.quest;

import dev.cabotmc.cabotenchants.boss.WillFight;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class RiftTeleportJob implements Runnable {
  int taskId;
  Location beacon;
  public void setTaskId(int taskId) {
    this.taskId = taskId;
  }

  public RiftTeleportJob(Location beacon) {
    this.beacon = beacon;
  }
  public static final float RADIUS = 5.0f;
  public static final int TICK_COUNT = 20 * 10;


  int ticks = 0;
  private static final Particle.DustOptions DUST_OPTIONS = new Particle.DustOptions(
          org.bukkit.Color.fromRGB(0x644DFF), 1
  );
  @Override
  public void run() {
    ticks++;
    // draw circle
    for (double i = 0; i < Math.PI * 2; i += Math.PI / 32) {
      double x = Math.cos(i) * RADIUS;
      double z = Math.sin(i) * RADIUS;
      beacon.getWorld().spawnParticle(org.bukkit.Particle.DUST,
              beacon.getX() + x, beacon.getY(), beacon.getZ() + z,
              1, 0, 0, 0, DUST_OPTIONS);
    }

    var players = new ArrayList<Player>();
    for (var p : beacon.getNearbyPlayers(RADIUS)) {
      if (p.getLocation().distanceSquared(beacon) <= RADIUS * RADIUS) {
        players.add(p);
      }
    }
    if (ticks >= TICK_COUNT) {
      Bukkit.getScheduler().cancelTask(taskId);
      if (!players.isEmpty()) {
        WillFight.prepareFight(players);
      }

    } else {
      if (ticks % 20 == 0) {
        var seconds = (TICK_COUNT - ticks) / 20;
        for (var p : players) {
          p.playSound(p.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 0.6f, 1);
          p.sendActionBar(
                  Component.text("Rift opening in " + seconds + " seconds")
                          .color(NamedTextColor.BLUE)
          );
        }
      }
    }
  }
}
