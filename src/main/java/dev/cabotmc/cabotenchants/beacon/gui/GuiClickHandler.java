package dev.cabotmc.cabotenchants.beacon.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface GuiClickHandler {
  void onClick(Player player, ItemStack item, int slot, ClickType type);
}
