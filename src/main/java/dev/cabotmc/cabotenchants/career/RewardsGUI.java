package dev.cabotmc.cabotenchants.career;

import dev.cabotmc.cabotenchants.beacon.gui.GuiClickHandler;
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

import java.util.HashMap;
import java.util.HashSet;

public class RewardsGUI implements Listener {
    Player target;
    Inventory i;
    HashMap<Integer, GuiClickHandler> handlers = new HashMap<>();

    public RewardsGUI(Player p) {
        i = Bukkit.createInventory(null, 9, Component.text("Cosmetics"));
        target = p;
        p.openInventory(i);


    }

    void render() {
        i.clear();
        handlers.clear();

        int index = 0;
        for (var rewardName: RewardManager.getRewards()) {
            var reward = RewardManager.getReward(rewardName);
            if (reward.isHidden() && !RewardManager.getUnlockedRewards(target).contains(rewardName)) continue;
            i.setItem(index, reward.createDisplayItem(rewardName.equals(RewardManager.getCurrentReward(target)), target));

            if (rewardName.equals(RewardManager.getCurrentReward(target))) {
                handlers.put(index, (player, item, slot, type) -> {
                    if (type != ClickType.LEFT) return;
                    RewardManager.equipReward(player, null);
                });
            } else {
                handlers.put(index, (player, item, slot, type) -> {
                    if (type != ClickType.LEFT) return;
                    if (!RewardManager.equipReward(player, rewardName)) {
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);
                    }
                });
            }

            index++;
        }
    }

    @EventHandler
    public void click(InventoryClickEvent e) {
        if (e.getInventory() != i) return;
        e.setCancelled(true);
        if (e.getClickedInventory() != i) return;
        var handler = handlers.get(e.getSlot());
        if (handler != null) {
            target.closeInventory();
            handler.onClick(target, e.getCurrentItem(), e.getSlot(), e.getClick());
        }
    }

    @EventHandler
    public void close(InventoryCloseEvent e) {
        if (e.getInventory() != i) return;
        HandlerList.unregisterAll(this);
    }
}
