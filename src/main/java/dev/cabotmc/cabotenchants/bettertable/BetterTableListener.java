package dev.cabotmc.cabotenchants.bettertable;

import dev.cabotmc.cabotenchants.CabotEnchants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class BetterTableListener implements Listener {
  @EventHandler
  public void click(PlayerInteractEvent e) {
    if (e.getClickedBlock() == null) return;
    if (e.getClickedBlock().getType() == Material.ENCHANTING_TABLE && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
        e.setCancelled(true);
        var menu = new BetterTableMenu(e.getPlayer());
      Bukkit.getPluginManager().registerEvents(menu, CabotEnchants.getPlugin(CabotEnchants.class));
        menu.open();
    }
  }
}
