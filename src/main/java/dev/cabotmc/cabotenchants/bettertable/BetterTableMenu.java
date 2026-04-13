package dev.cabotmc.cabotenchants.bettertable;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import dev.cabotmc.cabotenchants.CabotEnchants;
import dev.cabotmc.cabotenchants.quest.QuestStep;
import dev.cabotmc.cabotenchants.util.Models;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static dev.cabotmc.cabotenchants.bettertable.BetterTableConstants.*;

public class BetterTableMenu implements Listener {

    Inventory inventory;
    Player player;
    List<TableCostDefinition> activeOptions;
    int scrollStart;

    public BetterTableMenu(Player player) {
        this.player = player;
        this.inventory = player.getServer().createInventory(null, 54, BLANK_MENU);
        this.scrollStart = 0;
        updateAvailableListings();
    }

    ItemStack createXpItem(int num) {
        var item = new ItemStack(Material.ORANGE_DYE);
        var meta = item.getItemMeta();
        meta.displayName(
                Component.text("Your Experience")
                        .color(TextColor.color(0x9000FF))
                        .decoration(TextDecoration.ITALIC, false)
        );
        meta.setItemModel(Models.UI_FONT_NUMBERS[num]);
        item.setItemMeta(meta);
        return item;
    }

    void renderExperience() {
        int xp = Math.min(player.getLevel(), 99);
        int tens = xp / 10;
        int ones = xp % 10;
        inventory.setItem(XP_TENS_SLOT, createXpItem(tens));
        inventory.setItem(XP_ONES_SLOT, createXpItem(ones));
    }

    private static Component darkGreyNoItalic(String msg) {
        return Component.text(msg)
                .color(NamedTextColor.DARK_GRAY)
                .decoration(TextDecoration.ITALIC, false);
    }

    ItemStack createButton(Enchantment e, int level, boolean active, int levelDelta) {
        var item = new ItemStack(active ? Material.LIME_DYE : Material.GRAY_DYE);
        var meta = item.getItemMeta();
        meta.displayName(
                e.displayName(level)
                        .color(TextColor.color(active ? 0x00FF00 : 0x9032FF))
                        .decoration(TextDecoration.ITALIC, false)
        );
        if (active) {
            meta.lore(
                    List.of(
                            Component.empty(),
                            darkGreyNoItalic("Click to remove"),
                            Component.text("+" + levelDelta)
                                    .color(NamedTextColor.GREEN)
                                    .decoration(TextDecoration.ITALIC, false)
                    )
            );
        } else {
            meta.lore(
                    List.of(
                            Component.empty(),
                            darkGreyNoItalic("Click to add"),
                            darkGreyNoItalic("Cost: ")
                                    .append(
                                            Component.text("" + levelDelta)
                                                    .color(
                                                            TextColor.color(
                                                                    player.getLevel() >= levelDelta ?
                                                                            0x00FF00 : 0xFF0000
                                                            )
                                                    )
                                    )
                    )
            );
        }
        meta.setItemModel(active ? Models.BUTTONS_GREEN[level - 1] : Models.BUTTONS_GREY[level - 1]);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Renders a row of enchantment buttons in the menu.
     * @param startSlot The starting inventory slot for this row.
     * @param def The definition for the enchantment in this row.
     * @param appliedLevel The current level of this enchantment on the item.
     */
    private void renderRow(int startSlot, TableCostDefinition def, int appliedLevel) {
        if (def == null) {
            for (int slot = startSlot; slot < startSlot + 6; slot++) {
                inventory.setItem(slot, null);
            }
            return;
        }

        var items = new ItemStack[6];
        items[0] = new ItemStack(def.getDisplayMaterial());
        var meta = items[0].getItemMeta();
        meta.displayName(
                def.getEnchant().description()
                        .color(TextColor.color(NamedTextColor.GREEN))
                        .decoration(TextDecoration.ITALIC, false)
        );
        items[0].setItemMeta(meta);
        var curCost = def.getCost(appliedLevel);

        for (int i = 0; i < 5; i++) {
            if (i + 1 > def.getEnchant().getMaxLevel()) {
                items[i + 1] = null;
            } else {
                var deltaCost = def.getCost(i + 1) - curCost;
                if (appliedLevel == i + 1) {
                    deltaCost = def.getCost(i + 1);
                }
                items[i + 1] = createButton(def.getEnchant(), i + 1, appliedLevel == i + 1, deltaCost);
            }
        }

        for (int slot = 0; slot < 6; slot++) {
            inventory.setItem(startSlot + slot, items[slot]);
        }
    }

    TableCostDefinition[] getViewableOptions() {
        var options = new TableCostDefinition[3];
        for (int i = 0; i < 3; i++) {
            var index = i + scrollStart;
            if (index >= activeOptions.size()) {
                options[i] = null;
            } else {
                options[i] = activeOptions.get(index);
            }
        }
        return options;
    }

    private void renderScrollButtons() {
        if (scrollStart == 0) {
            inventory.setItem(UP_BUTTON_SLOT, null);
        } else {
            inventory.setItem(UP_BUTTON_SLOT, createScrollButton("Scroll Up", Models.BUTTON_DOWN));
        }

        if (scrollStart + 3 >= activeOptions.size()) {
            inventory.setItem(DOWN_BUTTON_SLOT, null);
        } else {
            inventory.setItem(DOWN_BUTTON_SLOT, createScrollButton("Scroll Down", Models.BUTTON_UP));
        }
    }

    private ItemStack createScrollButton(String text, org.bukkit.NamespacedKey model) {
        var item = new ItemStack(Material.ARROW);
        var meta = item.getItemMeta();
        meta.displayName(
                Component.text(text)
                        .color(TextColor.color(0x9000FF))
                        .decoration(TextDecoration.ITALIC, false)
        );
        meta.setItemModel(model);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Re-renders the entire menu based on the current state.
     */
    private void render() {
        var item = inventory.getItem(ITEM_SLOT);

        if (item == null) {
            inventory.clear();
            return;
        }

        if (scrollStart > 0 && scrollStart + 3 > activeOptions.size()) {
            scrollStart = Math.max(0, activeOptions.size() - 3);
        }

        var visibleOptions = getViewableOptions();
        int[] starts = {ROW_ONE_SLOT_START, ROW_TWO_SLOT_START, ROW_THREE_SLOT_START};

        for (int i = 0; i < 3; i++) {
            var def = visibleOptions[i];
            var level = (def != null && item.getItemMeta() != null) ? item.getEnchantmentLevel(def.getEnchant()) : 0;
            renderRow(starts[i], def, level);
        }

        renderScrollButtons();
        renderExperience();
    }

    Sound requestChangeEnchantmentLevel(TableCostDefinition ench, int level) {
        var item = inventory.getItem(ITEM_SLOT);
        var appliedLevel = item.getEnchantmentLevel(ench.getEnchant());
        if (appliedLevel == level && level != 0) {
            item.removeEnchantment(ench.getEnchant());
            player.setLevel(player.getLevel() + ench.getCost(level));
            updateAvailableListings(false);
            return Sound.UI_BUTTON_CLICK;
        }
        var costDelta = ench.getCost(level) - ench.getCost(appliedLevel);
        if (player.getLevel() - costDelta < 0) return Sound.ENTITY_ITEM_BREAK; // not enough levels
        player.setLevel(player.getLevel() - costDelta);
        item.addUnsafeEnchantment(ench.getEnchant(), level);
        updateAvailableListings(false);
        return Sound.UI_BUTTON_CLICK;
    }

    void handleButtonPress(InventoryClickEvent e) {
        Sound soundToPlay = null;
        int old = scrollStart;
        if (e.getSlot() == UP_BUTTON_SLOT) {
            scrollStart = Math.max(0, scrollStart - 1);
            soundToPlay = scrollStart != old ? Sound.UI_BUTTON_CLICK : null;
        } else if (e.getSlot() == DOWN_BUTTON_SLOT) {
            scrollStart = Math.min(activeOptions.size() - 3, scrollStart + 1);
            soundToPlay = scrollStart != old ? Sound.UI_BUTTON_CLICK : null;
        } else {
            var item = inventory.getItem(ITEM_SLOT);
            if (item == null || inventory.getItem(e.getSlot()) == null) return;

            var visible = getViewableOptions();
            if (e.getSlot() > ROW_THREE_SLOT_START && visible[2] != null) {
                soundToPlay = requestChangeEnchantmentLevel(visible[2], e.getSlot() - ROW_THREE_SLOT_START);
            } else if (e.getSlot() > ROW_TWO_SLOT_START && visible[1] != null
                    && e.getSlot() < ROW_TWO_SLOT_START + 6) {
                soundToPlay = requestChangeEnchantmentLevel(visible[1], e.getSlot() - ROW_TWO_SLOT_START);
            } else if (e.getSlot() > ROW_ONE_SLOT_START && visible[0] != null
                    && e.getSlot() < ROW_ONE_SLOT_START + 6) {
                soundToPlay = requestChangeEnchantmentLevel(visible[0], e.getSlot() - ROW_ONE_SLOT_START);
            } else {
                soundToPlay = null;
            }
        }
        if (soundToPlay != null) {
            player.playSound(player.getLocation(), soundToPlay, 1, 1);
        }
    }

    /**
     * Updates the list of available enchantments based on the item in the slot.
     */
    void updateAvailableListings() {
        updateAvailableListings(true);
    }

    /**
     * Updates the list of available enchantments based on the item in the slot.
     * @param reset Whether to reset the scroll position to the top.
     */
    void updateAvailableListings(boolean reset) {
        var item = inventory.getItem(ITEM_SLOT);
        if (item == null || item.getItemMeta().getPersistentDataContainer().has(QuestStep.QUEST_STEP_KEY)) {
            activeOptions = List.of();
            return;
        }
        activeOptions = AVAILABLE_ENCHANTMENTS.stream()
                .filter(def -> def.shouldDisplayLine(item))
                .toList();
        if (reset) scrollStart = 0;
        if (activeOptions.isEmpty()) {
            player.getWorld().dropItemNaturally(
                    player.getLocation(),
                    inventory.getItem(ITEM_SLOT)
            );
            inventory.setItem(ITEM_SLOT, null);
        }
    }

    List<InventoryAction> permittedActionsFilter =
            List.of(
                    InventoryAction.PICKUP_ONE,
                    InventoryAction.PICKUP_SOME,
                    InventoryAction.PICKUP_HALF,
                    InventoryAction.PICKUP_ALL,
                    InventoryAction.PLACE_ONE,
                    InventoryAction.PLACE_SOME,
                    InventoryAction.PLACE_ALL,
                    InventoryAction.SWAP_WITH_CURSOR,
                    InventoryAction.HOTBAR_SWAP,
                    InventoryAction.MOVE_TO_OTHER_INVENTORY
            );

    @EventHandler
    public void click(InventoryClickEvent e) {

        if (e.getInventory().equals(inventory) && !permittedActionsFilter.contains(e.getAction())) {
            e.setCancelled(true);
            return;
        }
        if (e.getInventory().equals(inventory) && e.getClickedInventory() == null) {
            e.setCancelled(true);
            return;
        }

        if (e.getClickedInventory() != null && e.getClickedInventory().equals(inventory)) {
            e.setCancelled(true);
            if (e.getSlot() != ITEM_SLOT) {
                handleButtonPress(e);
                defer(this::render);
            } else {
                e.setCancelled(false);
            }
        } else if (e.getInventory().equals(inventory)) {
            if (e.getClick().isShiftClick() && inventory.getItem(ITEM_SLOT) != null) {
                e.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void drag(InventoryDragEvent e) {
        if (e.getInventory() == inventory) {
            e.setCancelled(true);
        }
    }

    Material last;

    @EventHandler
    public void tick(ServerTickStartEvent e) {
        var item = inventory.getItem(ITEM_SLOT);
        if ((last == null && item != null) || (item != null && item.getType() != last)) {
            last = item.getType();
            updateAvailableListings();
            render();
            if (!activeOptions.isEmpty()) {
                sendTitle(player, WITH_BOOK_MENU);
            }
        } else if (item == null && last != null) {
            last = null;
            updateAvailableListings();
            render();
            sendTitle(player, BLANK_MENU);
        }
    }

    /**
     * Sends a custom title to the player to update the GUI appearance.
     * Uses NMS to update the title of the currently open inventory.
     */
    private void sendTitle(Player p, Component title) {

        var serialized = JSONComponentSerializer.json().serialize(title);
        var nms = net.minecraft.network.chat.Component.Serializer.fromJson(serialized, MinecraftServer.getServer()
                .registryAccess());


        var player = ((CraftPlayer) p).getHandle();
        player.connection.send(
                new ClientboundOpenScreenPacket(player.containerMenu.containerId, player.containerMenu.getType(), nms)
        );

        ((CraftPlayer) p).updateInventory();
    }

    void defer(Runnable task) {
        player.getServer().getScheduler().runTaskLater(CabotEnchants.getPlugin(CabotEnchants.class), task, 1);
    }

    @EventHandler
    public void close(InventoryCloseEvent e) {
        if (e.getInventory() == inventory) {
            HandlerList.unregisterAll(this);
            if (inventory.getItem(ITEM_SLOT) != null) {
                if (!player.getInventory().addItem(inventory.getItem(ITEM_SLOT)).isEmpty()) {
                    player.getWorld().dropItem(player.getLocation(), inventory.getItem(ITEM_SLOT));
                }
            }
        }
    }

    public void open() {
        player.openInventory(inventory);
    }
}
