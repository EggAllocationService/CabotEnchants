package dev.cabotmc.cabotenchants.beacon;

import dev.cabotmc.cabotenchants.beacon.upgrades.InvisibilityBeaconUpgrade;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Beacon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.EventListener;

public class BeaconListener implements Listener {
  @EventHandler
  public void onClick(PlayerInteractEvent e) {
    if (e.getClickedBlock().getType() != Material.BEACON || e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
    e.setCancelled(true);
    var beacon = (Beacon) e.getClickedBlock().getState();

    if (e.getItem() == null) {
      if (!beacon.getPersistentDataContainer().has(UpgradedBeaconState.BEACON_KEY)) return;
      var state = beacon.getPersistentDataContainer().get(UpgradedBeaconState.BEACON_KEY, UpgradedBeaconState.Encoder.INSTANCE);
      e.getPlayer()
              .sendMessage(Component.text("Owner uuid: " + state.owner.toString()));
      e.getPlayer().sendMessage(Component.text("Upgrades: " + state.upgrades.size()));
      for (var u : state.upgrades) {
        e.getPlayer().sendMessage(Component.text(u.getClass().getName()));
      }

    } else if (e.getItem().getType() == Material.NETHER_STAR) {
      var state = new UpgradedBeaconState();
      state.upgrades.add(new InvisibilityBeaconUpgrade());
      beacon.getPersistentDataContainer().set(UpgradedBeaconState.BEACON_KEY, UpgradedBeaconState.Encoder.INSTANCE, state);
    }

  }
}
