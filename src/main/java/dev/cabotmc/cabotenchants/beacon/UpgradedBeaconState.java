package dev.cabotmc.cabotenchants.beacon;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UpgradedBeaconState implements Serializable {
  public static final NamespacedKey BEACON_KEY = new NamespacedKey("cabot", "custom_beacon_data");

  // stored data
  UUID owner = new UUID(0, 0);

  protected transient HashSet<Player> affectedPlayers = new HashSet<>();
  transient ArrayList<BeaconUpgrade> upgrades = new ArrayList<>();

  /**
   * Called every 20ms on another thread
   *
   * @param beaconLocation - position and world of the beacon
   */
  public void tickAsync(Location beaconLocation) {
    if (affectedPlayers == null) {
      affectedPlayers = new HashSet<>();
    }

    var block = beaconLocation.getBlock();
    if (block.getType() != Material.BEACON) return;
    var beacon = (org.bukkit.block.Beacon) block.getState();
    var players = Bukkit.getOnlinePlayers();

    for (var p : players) {
      // fast fail if player is not in the same world
      if (p.getWorld() != beaconLocation.getWorld()) continue;
      var isAffected = beacon.getTier() != 0 && // dont count deactivated beacons
              p.getLocation().distanceSquared(beaconLocation) < (beacon.getEffectRange() * beacon.getEffectRange());
      if (isAffected && !affectedPlayers.contains(p)) {
        // player entered the range
        affectedPlayers.add(p);
        for (var u : upgrades) {
          u.playerEnteredRange(p);
        }
      } else if (!isAffected && affectedPlayers.contains(p)) {
        // player left the range
        affectedPlayers.remove(p);
        for (var u : upgrades) {
          u.playerLeftRange(p);
        }
      }
    }

    for (var u : upgrades) {
      u.onTickAsync(beaconLocation, affectedPlayers);
    }
  }

  public static class Encoder implements PersistentDataType<String, UpgradedBeaconState> {
    public static final Encoder INSTANCE = new Encoder();

    @Override
    public @NotNull Class<String> getPrimitiveType() {
      return String.class;
    }

    @Override
    public @NotNull Class<UpgradedBeaconState> getComplexType() {
      return UpgradedBeaconState.class;
    }

    @Override
    public @NotNull String toPrimitive(@NotNull UpgradedBeaconState complex, @NotNull PersistentDataAdapterContext context) {
      return BeaconStateSerializer.toJson(complex);
    }

    @Override
    public @NotNull UpgradedBeaconState fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
      return BeaconStateSerializer.fromJson(primitive);
    }
  }
}
