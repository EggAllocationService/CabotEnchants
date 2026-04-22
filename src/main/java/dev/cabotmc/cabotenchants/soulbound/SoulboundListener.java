package dev.cabotmc.cabotenchants.soulbound;

import dev.cabotmc.cabotenchants.CEBootstrap;
import dev.cabotmc.cabotenchants.CabotEnchants;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SoulboundListener implements Listener  {
    HashMap<UUID, List<ItemStack>> returnQueue = new HashMap<>();

    @EventHandler(priority = EventPriority.LOW)
    public void death(PlayerDeathEvent e) {
        var p = e.getEntity();

        var items = Arrays.stream(p.getInventory().getContents())
                .filter(Objects::nonNull)
                .filter(ItemStack::hasItemMeta)
                .filter(i -> i.getItemMeta().hasEnchants())
                .filter(i -> i.getEnchantments().keySet().stream()
                        .anyMatch(x -> x.getKey().equals(CEBootstrap.ENCHANTMENT_GREATER_SOULBOUND) || x.getKey().equals(CEBootstrap.ENCHANTMENT_LESSER_SOULBOUND))
                )
                .toList();

        if (items.isEmpty()) return;

        e.getDrops().removeAll(items);

        // remove one level of lesser soulbound
        items.stream()
                .filter(i -> i.getItemMeta().getEnchants().keySet().stream().anyMatch(x -> x.getKey().equals(CEBootstrap.ENCHANTMENT_LESSER_SOULBOUND)))
                .forEach(i -> {
                    var enchants = i.getData(DataComponentTypes.ENCHANTMENTS);
                    var enchantRef = enchants.enchantments().keySet().stream()
                            .filter(x -> x.getKey().equals(CEBootstrap.ENCHANTMENT_LESSER_SOULBOUND))
                            .findFirst();
                    if (enchantRef.isEmpty()) return;

                    var curLevel = enchants.enchantments().get(enchantRef.get());
                    var clone = new HashMap<>(enchants.enchantments());

                    if (curLevel == 1) {
                        clone.remove(enchantRef.get());
                    } else {
                        clone.put(enchantRef.get(), curLevel - 1);
                    }

                    i.setData(DataComponentTypes.ENCHANTMENTS, ItemEnchantments.itemEnchantments(clone));
                });

        Bukkit.getScheduler().scheduleSyncDelayedTask(CabotEnchants.instance, () -> {
            if (e.isCancelled()) return;

            p.getInventory().addItem(items.toArray(ItemStack[]::new));
        }, 2);
    }

    @EventHandler
    public void respawn(PlayerRespawnEvent e) {
        if (returnQueue.containsKey(e.getPlayer().getUniqueId())) {
            e.getPlayer().getInventory().addItem(returnQueue.get(e.getPlayer().getUniqueId()).toArray(ItemStack[]::new));
            returnQueue.remove(e.getPlayer().getUniqueId());
        }
    }
}
