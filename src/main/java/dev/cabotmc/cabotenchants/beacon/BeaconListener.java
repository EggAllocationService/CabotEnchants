package dev.cabotmc.cabotenchants.beacon;

import dev.cabotmc.cabotenchants.beacon.gui.BeaconUpgradeGui;
import org.bukkit.Material;
import org.bukkit.block.Beacon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class BeaconListener implements Listener {
    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        if (e.getClickedBlock().getType() != Material.BEACON || e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        e.setCancelled(true);
        var beacon = (Beacon) e.getClickedBlock().getState();

        if (e.getItem() == null) {
            if (!beacon.getPersistentDataContainer().has(UpgradedBeaconState.BEACON_KEY)) return;
            var state = BeaconManager.loadedBeacons.get(beacon.getLocation());
            var gui = new BeaconUpgradeGui(state);
            gui.open(e.getPlayer());

        } else if (e.getItem().getType() == Material.NETHER_STAR) {
            BeaconManager.upgradeBeacon(beacon.getLocation());
        }
    }

    @EventHandler
    public void load(ChunkLoadEvent e) {
        var ents = e.getChunk().getTileEntities();
        for (var be : ents) {
            if (be instanceof Beacon beacon) {
                if (beacon.getPersistentDataContainer().has(UpgradedBeaconState.BEACON_KEY)) {
                    var data = beacon.getPersistentDataContainer().get(UpgradedBeaconState.BEACON_KEY, UpgradedBeaconState.Encoder.INSTANCE);
                    BeaconManager.loadBeacon(beacon.getLocation(), data);
                }
            }
        }
    }

    private void beaconRemove(Beacon beacon) {
        if (beacon.getPersistentDataContainer().has(UpgradedBeaconState.BEACON_KEY)) {
            var ourBeacon = BeaconManager.removeBeacon(beacon.getLocation());
            for (var p : ourBeacon.affectedPlayers) {
                for (var u : ourBeacon.upgrades) {
                    if (u == null) continue;
                    u.playerLeftRange(p);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void unload(ChunkUnloadEvent e) {
        var ents = e.getChunk().getTileEntities();
        for (var be : ents) {
            if (be instanceof Beacon beacon) {
                beaconRemove(beacon);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() != Material.BEACON) return;
        beaconRemove((Beacon) e.getBlock().getState());
    }
}
