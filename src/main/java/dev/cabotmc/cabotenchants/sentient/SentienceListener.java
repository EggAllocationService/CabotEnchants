package dev.cabotmc.cabotenchants.sentient;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import dev.cabotmc.cabotenchants.CEBootstrap;
import dev.cabotmc.cabotenchants.CabotEnchants;
import io.papermc.paper.event.player.PlayerStopUsingItemEvent;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

public class SentienceListener implements Listener {
  Enchantment SENTIENCE_ENCHANT = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
          .get(CEBootstrap.ENCHANTMENT_SENTIENCE);


  HashMap<UUID, Long> lastDrawTimes = new HashMap<>();
  static final double TARGET_ACQUIRE_ANGLE_RAD = Math.toRadians(30);
  static final int TARGET_ACQUIRE_DISTANCE = 40;

  @EventHandler
  public void draw(PlayerInteractEvent e) {
    if (!e.getAction().isRightClick()) return;
    var item = e.getItem();
    if (item == null || item.getType() != Material.TRIDENT) return;
    if (!item.containsEnchantment(SENTIENCE_ENCHANT)) return;

    var player = e.getPlayer();
    var playerPos = e.getPlayer().getLocation().toVector();
    var playerLook = e.getPlayer().getLocation().getDirection();
    player.getNearbyEntities(TARGET_ACQUIRE_DISTANCE, TARGET_ACQUIRE_DISTANCE, TARGET_ACQUIRE_DISTANCE)
            .stream()
            .filter(entity -> entity.getType().isAlive())
            .filter(entity -> entity instanceof Enemy)
            .filter(entity -> {
              // check if the entity is within TARGET_AQUIRE_ANGLE_RAD radians of the player's look direction
                var entityPos = entity.getLocation().toVector();
                var entityDir = entityPos.subtract(playerPos).normalize();
                var angle = Math.acos(entityDir.dot(playerLook));
                return angle < TARGET_ACQUIRE_ANGLE_RAD;
            })
            .forEach(entity -> {
              TargetManager.addTarget(player.getUniqueId(), (Enemy) entity);
            });
    lastDrawTimes.put(player.getUniqueId(), System.currentTimeMillis());
  }

  @EventHandler
  public void playerStopItem(PlayerStopUsingItemEvent e) {
    if (e.getItem().getType() != Material.TRIDENT) return;
    Bukkit.getScheduler().scheduleSyncDelayedTask(CabotEnchants.getPlugin(CabotEnchants.class),
            () -> {
              TargetManager.clearTargets(e.getPlayer().getUniqueId());
            }, 2);
  }
  @EventHandler
  public void swap(PlayerItemHeldEvent e) {
    var oldItem = e.getPlayer().getInventory().getItem(e.getPreviousSlot());
    if (oldItem == null || oldItem.getType() != Material.TRIDENT) return;
    TargetManager.clearTargets(e.getPlayer().getUniqueId());
  }
  @EventHandler
  public void tick(ServerTickEndEvent e) {
    List<UUID> toRemove = new ArrayList<>();
    lastDrawTimes.forEach((uuid, time) -> {
      if (System.currentTimeMillis() - time > 1200) {
        TargetManager.clearTargets(uuid);
        var p = Bukkit.getPlayer(uuid);
        toRemove.add(uuid);
      }
    });
    toRemove.forEach(uuid -> lastDrawTimes.remove(uuid));
  }

  @EventHandler
  public void launch(PlayerLaunchProjectileEvent e) {
    var item = e.getItemStack();
    if (item == null || item.getType() != Material.TRIDENT) return;
    if (!item.containsEnchantment(SENTIENCE_ENCHANT)) return;
    var targets = TargetManager.getTargets(e.getPlayer().getUniqueId());
    if (targets == null || targets.isEmpty()) return;

    var queue = new LinkedBlockingQueue<LivingEntity>();
    var startVec = e.getProjectile().getLocation().toVector();
    targets.stream()
            .sorted((a, b) -> {
              var aPos = a.getLocation().toVector();
              var bPos = b.getLocation().toVector();
              var aDist = aPos.distanceSquared(startVec);
              var bDist = bPos.distanceSquared(startVec);
              return (int) (aDist - bDist);
            })
            .forEach(queue::add);

    var job = new SentientProjectile(e.getItemStack().clone(), e.getProjectile().getVelocity(), e.getProjectile().getLocation(),
            queue, e.getPlayer());
    job.setJobId(Bukkit.getScheduler().scheduleSyncRepeatingTask(CabotEnchants.getPlugin(CabotEnchants.class), job, 0, 1));

    e.getProjectile().remove();
    e.getItemStack().setAmount(0);

    TargetManager.clearTargets(e.getPlayer().getUniqueId());

  }
}
