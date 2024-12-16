package dev.cabotmc.cabotenchants.beacon.gui;

import dev.cabotmc.cabotenchants.beacon.UpgradedBeaconState;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.HashMap;

public class BeaconUpgradeGui implements Listener {

    Inventory inventory;
    UpgradedBeaconState state;
    HashMap<Integer, GuiClickHandler> clickHandlers = new HashMap<>();

    private boolean registered = false;

    public BeaconUpgradeGui(UpgradedBeaconState state) {
        this.state = state;
        this.inventory = Bukkit.createInventory(null, 54, Component.text("Beacon Upgrades"));
    }


    private final int[] UPGRADE_SLOTS = new int[]{20, 22, 24};

    void render() {
        inventory.clear();
        clickHandlers.clear();

        for (int i = 0; i < 3; i++) {
            var upgrade = state.upgrades[i];
            if (upgrade != null) {
                inventory.setItem(UPGRADE_SLOTS[i], upgrade.getDisplayItem());
                final int j = i;
                clickHandlers.put(UPGRADE_SLOTS[i], (player, item, slot, type) -> {
                    // left click to remove
                    if (type == ClickType.LEFT) {
                        state.upgrades[j] = null;
                        state.save();
                        render();
                    }
                });
            }
        }

        // add the available upgrades
        int index = 4 * 9;
        for (var upgrade : state.unlockedUpgrades) {
            if (Arrays.stream(state.upgrades).anyMatch(u -> u != null && u.getClass().equals(upgrade))) continue;
            try {
                var dummy = upgrade.getConstructor().newInstance();
                inventory.setItem(index, dummy.getDisplayItem());
                dummy.setLevel(1);
                clickHandlers.put(index, (player, item, slot, type) -> {
                    if (type == ClickType.LEFT) {
                        for (int i = 0; i < 3; i++) {
                            if (state.upgrades[i] == null) {
                                state.upgrades[i] = dummy;
                                state.save();
                                render();
                                return;
                            }
                        }
                        player.playSound(player.getLocation(),
                                Sound.BLOCK_ANVIL_BREAK, 1, 1);
                    }
                });
                index++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @EventHandler
    public void click(InventoryClickEvent e) {
        if (e.getInventory() != inventory) return;
        e.setCancelled(true);
        if (e.getClickedInventory() != inventory) return;
        var slot = e.getSlot();
        var handler = clickHandlers.get(slot);
        if (handler != null) {
            handler.onClick((Player) e.getWhoClicked(), e.getCurrentItem(), slot, e.getClick());
        }
    }

    @EventHandler
    public void close(InventoryCloseEvent e) {
        state.save();
        HandlerList.unregisterAll(this);
    }

    public void open(Player target) {
        render();
        target.openInventory(inventory);
        if (!registered) {
            Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugin("CabotEnchants"));
            registered = true;
        }
    }
}
