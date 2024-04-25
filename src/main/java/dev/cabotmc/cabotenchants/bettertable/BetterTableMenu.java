package dev.cabotmc.cabotenchants.bettertable;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import dev.cabotmc.cabotenchants.CabotEnchants;
import dev.cabotmc.cabotenchants.protocol.TitleHandler;
import dev.cabotmc.cabotenchants.quest.QuestStep;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
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

public class BetterTableMenu implements Listener {
    static final int ITEM_SLOT = 0;
    static final int ROW_ONE_SLOT_START = 11;
    static final int ROW_TWO_SLOT_START = 29;
    static final int ROW_THREE_SLOT_START = 47;

    static final int UP_BUTTON_SLOT = 36;
    static final int DOWN_BUTTON_SLOT = 45;

    static final int XP_TENS_SLOT = 7;
    static final int XP_ONES_SLOT = 8;


    static final Component BLANK_MENU = Component.text("203")
            .font(Key.key("cabot", "bettertable"))
            .color(NamedTextColor.WHITE)
            .append(
                    Component.text("Enchanting Table")
                            .color(NamedTextColor.DARK_GRAY)
                            .font(Key.key("minecraft", "default"))
            );
    static final Component WITH_BOOK_MENU = Component.text("213")
            .font(Key.key("cabot", "bettertable"))
            .color(NamedTextColor.WHITE)
            .append(
                    Component.text("Enchanting Table")
                            .color(NamedTextColor.DARK_GRAY)
                            .font(Key.key("minecraft", "default"))
            );
    static final List<TableCostDefinition> AVAILABLE_ENCHANTMENTS =
            List.of(

                    new TableCostDefinition(Enchantment.DAMAGE_ALL, Material.DIAMOND_SWORD),
                    new TableCostDefinition(Enchantment.DAMAGE_ARTHROPODS, Material.COBWEB),
                    new TableCostDefinition(Enchantment.DAMAGE_UNDEAD, Material.ZOMBIE_HEAD),
                    new TableCostDefinition(Enchantment.FIRE_ASPECT, Material.BLAZE_POWDER, 10, 18),
                    new TableCostDefinition(Enchantment.KNOCKBACK, Material.PISTON),
                    new TableCostDefinition(Enchantment.SWEEPING_EDGE, Material.IRON_SWORD),
                    new TableCostDefinition(Enchantment.LOOT_BONUS_MOBS, Material.EXPERIENCE_BOTTLE, 10, 25, 35),

                    new TableCostDefinition(Enchantment.DIG_SPEED, Material.FEATHER),
                    new TableCostDefinition(Enchantment.LOOT_BONUS_BLOCKS, Material.DIAMOND),
                    new TableCostDefinition(Enchantment.SILK_TOUCH, Material.GLASS, 10),

                    new TableCostDefinition(Enchantment.LUCK, Material.ENCHANTED_BOOK),
                    new TableCostDefinition(Enchantment.LURE, Material.TROPICAL_FISH),

                    new TableCostDefinition(Enchantment.ARROW_DAMAGE, Material.DIAMOND_SWORD),
                    new TableCostDefinition(Enchantment.ARROW_FIRE, Material.BLAZE_POWDER),
                    new TableCostDefinition(Enchantment.ARROW_INFINITE, Material.ARROW),
                    new TableCostDefinition(Enchantment.ARROW_KNOCKBACK, Material.PISTON),

                    new TableCostDefinition(Enchantment.MULTISHOT, Material.DISPENSER),
                    new TableCostDefinition(Enchantment.PIERCING, Material.TRIDENT),
                    new TableCostDefinition(Enchantment.QUICK_CHARGE, Material.RABBIT_FOOT),
                    //new TableCostDefinition(RailgunListener.RAILGUN, 20),

                    new TableCostDefinition(Enchantment.PROTECTION_ENVIRONMENTAL, Material.IRON_BLOCK),
                    new TableCostDefinition(Enchantment.PROTECTION_EXPLOSIONS, Material.TNT),
                    new TableCostDefinition(Enchantment.PROTECTION_FIRE, Material.LAVA_BUCKET),
                    new TableCostDefinition(Enchantment.PROTECTION_PROJECTILE, Material.ARROW),
                    new TableCostDefinition(Enchantment.THORNS, Material.CACTUS),
                    new TableCostDefinition(Enchantment.PROTECTION_FALL, Material.FEATHER),
                    new TableCostDefinition(Enchantment.WATER_WORKER, Material.WATER_BUCKET, 20),
                    new TableCostDefinition(Enchantment.DEPTH_STRIDER, Material.DIAMOND_BOOTS),
                    new TableCostDefinition(Enchantment.FROST_WALKER, Material.ICE, 20, 30),
                    new TableCostDefinition(Enchantment.OXYGEN, Material.CONDUIT),

                    new TableCostDefinition(Enchantment.DURABILITY, Material.IRON_BARS),
                    new TableCostDefinition(Enchantment.MENDING, Material.EXPERIENCE_BOTTLE, 25)

            );

    Inventory i;
    Player p;
    List<TableCostDefinition> activeOptions;
    int scrollStart;

    public BetterTableMenu(Player p) {
        this.p = p;
        i = p.getServer().createInventory(null, 54, BLANK_MENU);
        scrollStart = 0;
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
        meta.setCustomModelData(num + 1);
        item.setItemMeta(meta);
        return item;
    }

    void renderExperience() {
        int xp = Math.min(p.getLevel(), 99);
        int tens = xp / 10;
        int ones = xp % 10;
        i.setItem(XP_TENS_SLOT, createXpItem(tens));
        i.setItem(XP_ONES_SLOT, createXpItem(ones));
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
                                                                    p.getLevel() >= levelDelta ?
                                                                            0x00FF00 : 0xFF0000
                                                            )
                                                    )
                                    )
                    )
            );
        }
        meta.setCustomModelData(level);
        item.setItemMeta(meta);
        return item;
    }

    ItemStack[] renderRow(TableCostDefinition def, int appliedLevel) {
        var items = new ItemStack[6];
        items[0] = new ItemStack(def.getDisplayMaterial());
        var meta = items[0].getItemMeta();
        meta.displayName(
                Component.translatable(def.getEnchant().translationKey())
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
        return items;
    }

    void renderRowToStart(int start, TableCostDefinition def, int appliedLevel) {
        var items = renderRow(def, appliedLevel);
        for (int slot = 0; slot < 6; slot++) {
            i.setItem(start + slot, items[slot]);
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

    void render() {
        var item = i.getItem(ITEM_SLOT);

        if (item == null) {
            i.clear();
            return;
        }
        var visibleOptions = getViewableOptions();
        if (visibleOptions[0] == null) {
            scrollStart = Math.min(scrollStart, activeOptions.size() - 3);
        }
        renderRowToStart(ROW_ONE_SLOT_START, visibleOptions[0], item.getEnchantments().getOrDefault(visibleOptions[0].getEnchant(), 0));

        if (visibleOptions[1] != null) {
            renderRowToStart(ROW_TWO_SLOT_START, visibleOptions[1], item.getEnchantments().getOrDefault(visibleOptions[1].getEnchant(), 0));
        } else {
            for (int slot = ROW_TWO_SLOT_START; slot < ROW_TWO_SLOT_START + 6; slot++) {
                i.setItem(slot, null);
            }
        }

        if (visibleOptions[2] != null) {
            renderRowToStart(ROW_THREE_SLOT_START, visibleOptions[2], item.getEnchantments().getOrDefault(visibleOptions[2].getEnchant(), 0));
        } else {
            for (int slot = ROW_THREE_SLOT_START; slot < ROW_THREE_SLOT_START + 6; slot++) {
                i.setItem(slot, null);
            }
        }

        // buttons
        if (scrollStart == 0) {
            i.setItem(UP_BUTTON_SLOT, null);
        } else {
            var l = new ItemStack(Material.ARROW);
            var m = l.getItemMeta();
            m.displayName(
                    Component.text("Scroll Up")
                            .color(TextColor.color(0x9000FF))
                            .decoration(TextDecoration.ITALIC, false)
            );
            m.setCustomModelData(1);
            l.setItemMeta(m);
            i.setItem(UP_BUTTON_SLOT, l);
        }
        if (scrollStart + 3 >= activeOptions.size()) {
            i.setItem(DOWN_BUTTON_SLOT, null);
        } else {
            var l = new ItemStack(Material.ARROW);
            var m = l.getItemMeta();
            m.displayName(
                    Component.text("Scroll Down")
                            .color(TextColor.color(0x9000FF))
                            .decoration(TextDecoration.ITALIC, false)
            );
            m.setCustomModelData(2);
            l.setItemMeta(m);
            i.setItem(DOWN_BUTTON_SLOT, l);
        }
        renderExperience();
    }

    Sound requestChangeEnchantmentLevel(TableCostDefinition ench, int level) {
        var item = i.getItem(ITEM_SLOT);
        var appliedLevel = item.getEnchantments().getOrDefault(ench.getEnchant(), 0);
        if (appliedLevel == level && level != 0) {
            item.removeEnchantment(ench.getEnchant());
            p.setLevel(p.getLevel() + ench.getCost(level));
            updateAvailableListings(false);
            return Sound.UI_BUTTON_CLICK;
        }
        var costDelta = ench.getCost(level) - ench.getCost(appliedLevel);
        if (p.getLevel() - costDelta < 0) return Sound.ENTITY_ITEM_BREAK; // not enough levels
        p.setLevel(p.getLevel() - costDelta);
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
            var item = i.getItem(ITEM_SLOT);
            if (item == null || i.getItem(e.getSlot()) == null) return;

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
            p.playSound(p.getLocation(), soundToPlay, 1, 1);
        }
    }

    void updateAvailableListings() {
        updateAvailableListings(true);
    }

    void updateAvailableListings(boolean reset) {
        var item = i.getItem(ITEM_SLOT);
        if (item == null || item.getItemMeta().getPersistentDataContainer().has(QuestStep.QUEST_STEP_KEY)) {
            activeOptions = List.of();
            return;
        }
        activeOptions = AVAILABLE_ENCHANTMENTS.stream()
                .filter(def -> def.shouldDisplayLine(item))
                .toList();
        if (reset) scrollStart = 0;
        if (activeOptions.isEmpty()) {
            p.getWorld().dropItemNaturally(
                    p.getLocation(),
                    i.getItem(ITEM_SLOT)
            );
            i.setItem(ITEM_SLOT, null);
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
                    InventoryAction.HOTBAR_MOVE_AND_READD,
                    InventoryAction.MOVE_TO_OTHER_INVENTORY
            );

    @EventHandler
    public void click(InventoryClickEvent e) {

        if (e.getInventory().equals(i) && !permittedActionsFilter.contains(e.getAction())) {
            e.setCancelled(true);
            return;
        }
        if (e.getInventory().equals(i) && e.getClickedInventory() == null) {
            e.setCancelled(true);
            return;
        }

        if (e.getClickedInventory() != null && e.getClickedInventory().equals(i)) {
            e.setCancelled(true);
            if (e.getSlot() != ITEM_SLOT) {
                handleButtonPress(e);
                defer(this::render);
            } else {
                e.setCancelled(false);
            }
        } else if (e.getInventory().equals(i)) {
            if (e.getClick().isShiftClick() && i.getItem(ITEM_SLOT) != null) {
                e.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void drag(InventoryDragEvent e) {
        if (e.getInventory() == i) {
            e.setCancelled(true);
        }
    }

    Material last;

    @EventHandler
    public void tick(ServerTickStartEvent e) {
        var item = i.getItem(ITEM_SLOT);
        if ((last == null && item != null) || (item != null && item.getType() != last)) {
            last = item.getType();
            updateAvailableListings();
            render();
            if (!activeOptions.isEmpty()) {
                CabotEnchants.titleHandler.setPlayerInventoryTitle(p, WITH_BOOK_MENU);
            }
        } else if (item == null && last != null) {
            last = null;
            updateAvailableListings();
            render();
            CabotEnchants.titleHandler.setPlayerInventoryTitle(p, BLANK_MENU);
        }
    }

    void defer(Runnable task) {
        p.getServer().getScheduler().runTaskLater(CabotEnchants.getPlugin(CabotEnchants.class), task, 1);
    }

    @EventHandler
    public void close(InventoryCloseEvent e) {
        if (e.getInventory() == i) {
            HandlerList.unregisterAll(this);
            if (i.getItem(ITEM_SLOT) != null) {
                if (!p.getInventory().addItem(i.getItem(ITEM_SLOT)).isEmpty()) {
                    p.getWorld().dropItem(p.getLocation(), i.getItem(ITEM_SLOT));
                }
            }
        }
    }

    public void open() {
        p.openInventory(i);
    }
}
