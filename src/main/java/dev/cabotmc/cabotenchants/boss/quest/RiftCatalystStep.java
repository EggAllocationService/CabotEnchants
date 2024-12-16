package dev.cabotmc.cabotenchants.boss.quest;

import dev.cabotmc.cabotenchants.boss.WillFight;
import dev.cabotmc.cabotenchants.quest.QuestStep;
import dev.cabotmc.cabotenchants.util.Models;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Beacon;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RiftCatalystStep extends QuestStep {
  @Override
  protected ItemStack internalCreateStepItem() {
    var i = new ItemStack(Material.BONE);
    var meta = i.getItemMeta();
    meta.displayName(
            Component.text("Rift Catalyst")
                    .color(TextColor.color(0x644DFF))
                    .decoration(TextDecoration.ITALIC, false)
    );
    meta.setItemModel(Models.RIFT_KEY);
    meta.lore(
            List.of(
                    Component.text("This item offers a one-way ticket to the rift,")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("a location beyond time and space.")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("Your return journey is up to you.")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.empty(),
                    Component.text("Right-click on a beacon with at least one netherite layer.")
                            .color(NamedTextColor.YELLOW)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.empty(),
                    Component.text("In the rift you will be cut off from everything.")
                            .color(NamedTextColor.RED)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("The only way back is victory or death.")
                            .color(NamedTextColor.RED)
                            .decoration(TextDecoration.ITALIC, false)

            )
    );
    i.setItemMeta(meta);
    return i;
  }

  @EventHandler
  public void death(EntityDeathEvent e) {
    if (e.getEntity() instanceof Wither) {
        e.getDrops().add(createStepItem());
    }
  }

  @EventHandler
  public void useOnBeacon(PlayerInteractEvent e) {
    if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.BEACON) {
      if (isStepItem(e.getItem())) {
        e.setCancelled(true);
        /*if (!checkBeaconIsActiveAndNetherite((Beacon) e.getClickedBlock().getState())) {
          e.getPlayer().sendMessage("The key will only work on a beacon with a netherite base.");
          return;
        }*/

        if (WillFight.startLocked) {
          e.getPlayer().sendMessage("Some kind of interference prevents the key from activating");
          return;
        }

        WillFight.startLocked = true;

        var job = new RiftTeleportJob(e.getClickedBlock().getLocation().toCenterLocation());
        job.setTaskId(
                e.getPlayer().getServer().getScheduler()
                        .scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("CabotEnchants"),
                                job, 0, 1)
        );

        e.getItem().setAmount(0);
      }
    }
  }

  public boolean checkBeaconIsActiveAndNetherite(Beacon beacon) {
    for (int dx = -1; dx <= 1; dx++) {
      for (int dz = -1; dz <= 1; dz++) {
        if (beacon.getWorld().getBlockAt(beacon.getX() + dx, beacon.getY() - 1, beacon.getZ() + dz).getType() != Material.NETHERITE_BLOCK) {
          return false;
        }
      }
    }
    return true;
  }
}
