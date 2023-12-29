package dev.cabotmc.cabotenchants.table;

import dev.cabotmc.cabotenchants.CabotEnchants;
import dev.cabotmc.cabotenchants.packet.EnchantmentLoreAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.EnchantingInventory;

import java.util.ArrayList;

public class TableListenener implements Listener {
    @EventHandler
    public void onEnchant(EnchantItemEvent e) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(CabotEnchants.getPlugin(CabotEnchants.class),
                () -> {
                    var inv = (EnchantingInventory) e.getInventory();
                    if (inv.getItem() == null) return;
                    EnchantmentLoreAdapter.modify(inv.getItem());
                }, 1);
    }
    @EventHandler
    public void onGrindstone(org.bukkit.event.inventory.InventoryClickEvent e) {
        if (e.getInventory().getType() != org.bukkit.event.inventory.InventoryType.GRINDSTONE) return;
        if (e.getSlotType() != InventoryType.SlotType.RESULT) return;
        var item = e.getInventory().getItem(e.getSlot());
        if (item == null) return;
        var m = item.getItemMeta();
        m.lore(new ArrayList<>());
        item.setItemMeta(m);
    }
    @EventHandler
    public void onAnvil(PrepareAnvilEvent e) {
        var item = e.getResult();
        if (item == null) return;
        EnchantmentLoreAdapter.modify(item);
    }

    @EventHandler
    public void click(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (e.getClickedInventory().getType() == InventoryType.PLAYER) {
            var item = e.getClickedInventory().getItem(e.getSlot());
            if (item == null) return;
            EnchantmentLoreAdapter.modify(item);
        }
    }

    @EventHandler
    public void trades(VillagerAcquireTradeEvent e) {
        var trade = e.getRecipe();
        if (trade == null) return;
        var result = trade.getResult();
        if (result == null) return;
        EnchantmentLoreAdapter.modify(result);
    }
    @EventHandler
    public void career(VillagerCareerChangeEvent e) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(
                CabotEnchants.getPlugin(CabotEnchants.class),
                () -> {
                    var recipes = e.getEntity().getRecipes();
                    for (var r : recipes) {
                        var result = r.getResult();
                        if (result == null) continue;
                        EnchantmentLoreAdapter.modify(result);
                    }
                }
        );
    }
}
