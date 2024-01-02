package dev.cabotmc.cabotenchants.bettertable;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import dev.cabotmc.cabotenchants.CabotEnchants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
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
  static final List<TableCostDefinition> AVAILABLE_ENCHANTMENTS =
          List.of(

                  new TableCostDefinition(Enchantment.DAMAGE_ALL),
                  new TableCostDefinition(Enchantment.DAMAGE_ARTHROPODS),
                  new TableCostDefinition(Enchantment.DAMAGE_UNDEAD),
                  new TableCostDefinition(Enchantment.FIRE_ASPECT, 10, 18),
                  new TableCostDefinition(Enchantment.KNOCKBACK),

                  new TableCostDefinition(Enchantment.DIG_SPEED),
                  new TableCostDefinition(Enchantment.LOOT_BONUS_BLOCKS),
                  new TableCostDefinition(Enchantment.SILK_TOUCH, 10),

                  new TableCostDefinition(Enchantment.LUCK),
                  new TableCostDefinition(Enchantment.LURE),

                  new TableCostDefinition(Enchantment.ARROW_DAMAGE),
                  new TableCostDefinition(Enchantment.ARROW_FIRE),
                  new TableCostDefinition(Enchantment.ARROW_INFINITE),
                  new TableCostDefinition(Enchantment.ARROW_KNOCKBACK),

                  new TableCostDefinition(Enchantment.MULTISHOT),
                  new TableCostDefinition(Enchantment.PIERCING),
                  new TableCostDefinition(Enchantment.QUICK_CHARGE),
                  //new TableCostDefinition(RailgunListener.RAILGUN, 20),

                  new TableCostDefinition(Enchantment.PROTECTION_ENVIRONMENTAL),
                  new TableCostDefinition(Enchantment.PROTECTION_EXPLOSIONS),

                  new TableCostDefinition(Enchantment.PROTECTION_FIRE),
                  new TableCostDefinition(Enchantment.PROTECTION_PROJECTILE),
                  new TableCostDefinition(Enchantment.THORNS),
                  new TableCostDefinition(Enchantment.PROTECTION_FALL),

                  new TableCostDefinition(Enchantment.DURABILITY),
                  new TableCostDefinition(Enchantment.MENDING, 25)

          );

  Inventory i;
  Player p;
  List<TableCostDefinition> activeOptions;
  int scrollStart;

  public BetterTableMenu(Player p) {
    this.p = p;
    i = p.getServer().createInventory(null, 54, Component.text("Better Table"));
    int scrollStart = 0;
    updateAvailableListings();
  }


  private static Component darkGreyNoItalic(String msg) {
    return Component.text(msg)
            .color(NamedTextColor.DARK_GRAY)
            .decoration(TextDecoration.ITALIC, false);
  }

  ItemStack createButton(Enchantment e, int level, boolean active, int levelDelta) {
    var item = new ItemStack(active ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
    var meta = item.getItemMeta();
    meta.displayName(
            e.displayName(level)
                    .color(TextColor.color(0x9000FF))
    );
    if (active) {
      meta.lore(
              List.of(
                      Component.empty(),
                      darkGreyNoItalic("Click to remove")
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
                                                              levelDelta < 0 ?
                                                                      0x00FF00 : 0xFF0000
                                                      )
                                              )
                              )
              )
      );
    }
    item.setItemMeta(meta);
    return item;
  }

  ItemStack[] renderRow(TableCostDefinition def, int appliedLevel) {
    var items = new ItemStack[6];
    items[0] = new ItemStack(Material.CARROT);
    var meta = items[0].getItemMeta();
    var curCost = def.getCost(appliedLevel);

    for (int i = 0; i < 5; i++) {
      if (i + 1 > def.getEnchant().getMaxLevel()) {
        items[i + 1] = null;
      } else {
        var deltaCost = def.getCost(i + 1) - curCost;
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
  }
  void requestChangeEnchantmentLevel(TableCostDefinition ench, int level) {
    var item = i.getItem(ITEM_SLOT);
    var appliedLevel = item.getEnchantments().getOrDefault(ench.getEnchant(), 0);
    if (appliedLevel == level && level != 0) {
        item.removeEnchantment(ench.getEnchant());
        p.setLevel(p.getLevel() + ench.getCost(level));
        return;
    }
    var costDelta = ench.getCost(level) - ench.getCost(appliedLevel);
    if (p.getLevel() - costDelta < 0) return; // not enough levels
    p.setLevel(p.getLevel() - costDelta);
    item.addUnsafeEnchantment(ench.getEnchant(), level);
  }

  void handleButtonPress(InventoryClickEvent e) {
    if (e.getSlot() == UP_BUTTON_SLOT) {
      scrollStart = Math.max(0, scrollStart - 3);
    } else if (e.getSlot() == DOWN_BUTTON_SLOT) {
      scrollStart = Math.min(activeOptions.size() - 3, scrollStart + 3);
    } else {
      var item = i.getItem(ITEM_SLOT);
      if (item == null || i.getItem(e.getSlot()) == null) return;
      var visible = getViewableOptions();
      if (e.getSlot() > ROW_THREE_SLOT_START && visible[2] != null) {
        requestChangeEnchantmentLevel(visible[2], e.getSlot() - ROW_THREE_SLOT_START);
      } else if (e.getSlot() > ROW_TWO_SLOT_START && visible[1] != null
      && e.getSlot() < ROW_TWO_SLOT_START + 6) {
        requestChangeEnchantmentLevel(visible[1], e.getSlot() - ROW_TWO_SLOT_START);
      } else if (e.getSlot() > ROW_ONE_SLOT_START && visible[0] != null
      && e.getSlot() < ROW_ONE_SLOT_START + 6) {
        requestChangeEnchantmentLevel(visible[0], e.getSlot() - ROW_ONE_SLOT_START);
      }
    }
  }

  void updateAvailableListings() {
    var item = i.getItem(ITEM_SLOT);
    if (item == null) {
      activeOptions = List.of();
      return;
    }
    activeOptions = AVAILABLE_ENCHANTMENTS.stream()
            .filter(def -> def.shouldDisplayLine(item))
            .toList();
    scrollStart = 0;
  }

  @EventHandler
  public void click(InventoryClickEvent e) {
    if (e.getClickedInventory() == null) return;
    if (e.getClickedInventory().equals(i)) {
      e.setResult(Event.Result.DENY);
      if (e.getSlot() != ITEM_SLOT) {
        handleButtonPress(e);
        render();
      } else {
        e.setResult(Event.Result.DEFAULT);
      }
    } else if (e.getInventory().equals(i)) {
      if (e.getClick().isShiftClick() && i.getItem(ITEM_SLOT) != null) {
        e.setCancelled(true);
      }

    }
  }
  ItemStack last;
  @EventHandler
  public void tick(ServerTickStartEvent e) {
    var item = i.getItem(ITEM_SLOT);
    if (last == null && item != null) {
        last = item;
        updateAvailableListings();
        render();
    } else if (item == null && last != null) {
        last = null;
        updateAvailableListings();
        render();
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
  void open() {
    p.openInventory(i);
  }

}
