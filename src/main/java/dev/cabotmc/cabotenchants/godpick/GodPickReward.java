package dev.cabotmc.cabotenchants.godpick;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import dev.cabotmc.cabotenchants.quest.QuestStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Repairable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class GodPickReward extends QuestStep {
  static final int MAX_BLOCKS = 64;
  static final int[][] PORTAL_CHECK_OFFSETS
          = new int[][]{
          {1, 0, 0},
          {0, 0, 1},
          {-1, 0, 0},
          {0, 0, -1}
  };
  static List<Material> WHITELIST = List.of(
          Material.COAL_ORE,
          Material.DEEPSLATE_COAL_ORE,
          Material.IRON_ORE,
          Material.DEEPSLATE_IRON_ORE,
          Material.COPPER_ORE,
          Material.DEEPSLATE_COPPER_ORE,
          Material.GOLD_ORE,
          Material.DEEPSLATE_GOLD_ORE,
          Material.REDSTONE_ORE,
          Material.DEEPSLATE_REDSTONE_ORE,
          Material.LAPIS_ORE,
          Material.DEEPSLATE_LAPIS_ORE,
          Material.DIAMOND_ORE,
          Material.DEEPSLATE_DIAMOND_ORE,
          Material.EMERALD_ORE,
          Material.DEEPSLATE_EMERALD_ORE,
          Material.NETHER_GOLD_ORE,
          Material.NETHER_QUARTZ_ORE,
          Material.ANCIENT_DEBRIS,
          Material.STONE,
          Material.DEEPSLATE,
          Material.TUFF,
          Material.NETHERRACK
  );
  Enchantment VEINMINER = Enchantment.getByKey(new NamespacedKey("cabot", "veinminer"));
  boolean lock = false;
  Location tpLoc = null;
  Map<Location, Integer> progressMap = new HashMap<>();
  Map<Material, Integer> miningSpeeds = Map.of(
          Material.BEDROCK, 100,
          Material.END_PORTAL_FRAME, 40
  );

  @Override
  protected ItemStack internalCreateStepItem() {
    var i = new ItemStack(Material.NETHERITE_PICKAXE);
    var m = (Repairable) i.getItemMeta();
    m.setCustomModelData(1);
    m.displayName(
            MiniMessage
                    .miniMessage().deserialize("<!i><rainbow>The Excavator")
    );
    m.addEnchant(Enchantment.DIG_SPEED, 7, true);
    m.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 5, true);
    m.addEnchant(Enchantment.DURABILITY, 3, true);
    m.addEnchant(Enchantment.MENDING, 1, true);
    m.addEnchant(VEINMINER, 1, true);
    m.setRepairCost(999999);
    m.lore(
            List.of(
                    Component.empty(),
                    Component.text("+ This item can break bedrock and end portals")
                            .color(NamedTextColor.GREEN)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.empty(),
                    Component.text("People saying you've dug yourself into a hole?")
                            .color(NamedTextColor.DARK_GRAY),
                    Component.text("Don't worry, this thing can dig you out of it.")
                            .color(NamedTextColor.DARK_GRAY)
            )
    );

    i.setItemMeta(m);
    return i;
  }

  @EventHandler
  public void onBreak(BlockBreakEvent e) {
    if (lock) return;
    var p = e.getPlayer();

    if (isStepItem(e.getPlayer().getInventory().getItemInMainHand()) && p.isSneaking()) {
      if (e.getBlock().getType().equals(Material.BEDROCK)) {
        return;
      }
      if (!WHITELIST.contains(e.getBlock().getType())) {
        return;
      }
      var consideringQueue = new LinkedBlockingQueue<Block>();
      var toBreakQueue = new LinkedBlockingQueue<Block>();
      consideringQueue.add(e.getBlock());
      while (!consideringQueue.isEmpty() && toBreakQueue.size() < MAX_BLOCKS) {
        var block = consideringQueue.poll();
        if (block == null) continue;
        if (!WHITELIST.contains(block.getType())) continue;
        toBreakQueue.add(block);
        for (var face : BlockFace.values()) {
          var relative = block.getRelative(face);
          if (block.getType() != relative.getType() || toBreakQueue.contains(relative)) continue;
          consideringQueue.add(relative);
        }
      }
      var item = p.getInventory().getItemInMainHand();
      var hungerToInflict = Math.max(toBreakQueue.size() / 4, 1);
      if (p.getSaturation() + p.getFoodLevel() < hungerToInflict) {
        return;
      }
      lock = true;
      tpLoc = e.getBlock().getLocation().toCenterLocation();
      for (var block : toBreakQueue) {
        e.getPlayer().breakBlock(block);
        if (e.getPlayer().getInventory().getItemInMainHand().isEmpty()) break;
      }
      lock = false;
      tpLoc = null;

      // inflict to saturation first
      if (p.getSaturation() < hungerToInflict) {
        p.setFoodLevel((int) Math.max(p.getFoodLevel() - (hungerToInflict - p.getSaturation()), 0));
        p.setSaturation(0);
      } else {
        p.setSaturation(p.getSaturation() - hungerToInflict);
      }
    }
  }

  @EventHandler
  public void dropItems(BlockDropItemEvent e) {
    if (lock) {
      e.getItems()
              .forEach(i -> i.teleport(tpLoc));
    }
  }

  @EventHandler
  public void dig(PlayerInteractEvent e) {
    if (e.getAction() == Action.LEFT_CLICK_BLOCK && isStepItem(e.getItem())) {
      var block = e.getClickedBlock();
      if (block.getType() == Material.END_PORTAL_FRAME) {
        // check if end portal frame is lit
        for (var offset : PORTAL_CHECK_OFFSETS) {
            var relative = block.getRelative(offset[0], offset[1], offset[2]);
            if (relative.getType() == Material.END_PORTAL) {
                return;
            }
        }
      }
      if (miningSpeeds.containsKey(block.getType())) {
        progressMap.putIfAbsent(block.getLocation(), 0);
      }
    }
  }

  @EventHandler
  public void tick(ServerTickEndEvent e) {
    var toRemove = new ArrayList<Location>();
    for (var entry : progressMap.entrySet()) {
      var loc = entry.getKey();
      var progress = entry.getValue();
      if (progress >= miningSpeeds.get(loc.getBlock().getType())) {
        loc.getBlock().getWorld()
                .dropItemNaturally(loc, new ItemStack(loc.getBlock().getType()));

        if (loc.getBlock().getType() == Material.END_PORTAL_FRAME) {
          if (((EndPortalFrame) loc.getBlock().getBlockData()).hasEye()) {
            loc.getBlock().getWorld()
                    .dropItemNaturally(loc, new ItemStack(Material.ENDER_EYE));
          }
        }

        loc.getBlock().breakNaturally(true);
        toRemove.add(loc);
      } else {
        progressMap.put(loc, progress + 1);
        loc.getNearbyPlayers(50)
                .forEach(p -> {
                  p.sendBlockDamage(loc, ((float) progress) / miningSpeeds.get(loc.getBlock().getType()), -100);
                });
      }
    }
    toRemove.forEach(progressMap::remove);
  }

  @EventHandler
  public void abort(BlockDamageAbortEvent e) {
    if (isStepItem(e.getItemInHand())) {
      progressMap.remove(e.getBlock().getLocation());
      e.getBlock().getLocation().getNearbyPlayers(50)
              .forEach(p -> p.sendBlockDamage(e.getBlock().getLocation(), 0, -100));
    }
  }
}
