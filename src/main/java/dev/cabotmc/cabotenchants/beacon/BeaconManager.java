package dev.cabotmc.cabotenchants.beacon;

import com.google.common.collect.HashBiMap;
import dev.cabotmc.cabotenchants.beacon.upgrades.InvisibilityBeaconUpgrade;
import org.bukkit.Location;

public class BeaconManager {
  static HashBiMap<Location, UpgradedBeaconState> loadedBeacons = HashBiMap.create();

  static volatile boolean locked;

  public static void loadBeacon(Location pos, UpgradedBeaconState state) {
      if (locked) {
        while(locked) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
      }
      loadedBeacons.put(pos, state);
  }

  public static UpgradedBeaconState removeBeacon(Location pos) {
    if (locked) {
        while(locked) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    return loadedBeacons.remove(pos);
  }

  public static void tickAsync() {
    locked = true;
    for (var entry : loadedBeacons.entrySet()) {
        var state = entry.getValue();
        state.tickAsync(entry.getKey());
    }
    locked = false;
  }

  public static void upgradeBeacon(Location l) {
    if (l.getBlock().getType() != org.bukkit.Material.BEACON) throw new IllegalArgumentException("Block is not a beacon");
    var beacon = (org.bukkit.block.Beacon) l.getBlock().getState();
    if (!beacon.getPersistentDataContainer().has(UpgradedBeaconState.BEACON_KEY)) {
        var state = new UpgradedBeaconState();
        state.upgrades.add(new InvisibilityBeaconUpgrade());
        beacon.getPersistentDataContainer().set(UpgradedBeaconState.BEACON_KEY, UpgradedBeaconState.Encoder.INSTANCE, state);
        beacon.update(true);
    }
    if (!loadedBeacons.containsKey(l.toBlockLocation())) {
        var state = beacon.getPersistentDataContainer().get(UpgradedBeaconState.BEACON_KEY, UpgradedBeaconState.Encoder.INSTANCE);
        loadBeacon(l.toBlockLocation(), state);
    }
  }


}
