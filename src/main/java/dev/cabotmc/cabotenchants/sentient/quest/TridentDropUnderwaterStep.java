package dev.cabotmc.cabotenchants.sentient.quest;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import dev.cabotmc.cabotenchants.CabotEnchants;
import dev.cabotmc.cabotenchants.quest.QuestStep;
import io.papermc.paper.event.entity.EntityMoveEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Item;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TridentDropUnderwaterStep extends QuestStep {
  static final NamespacedKey PRE_SPAWN_TAG = new NamespacedKey("cabot", "t_pre_spawn");

  @Override
  protected ItemStack internalCreateStepItem() {
    var i = new ItemStack(Material.SCUTE);
    var meta = i.getItemMeta();
    meta.displayName(
            Component.text("Repaired Ancient Trident")
                    .color(TextColor.color(TridentKillAquaticEnemiesStep.TARGET_COLOR))
                    .decoration(TextDecoration.ITALIC, false)
    );
    meta.setCustomModelData(2);
    meta.lore(
            List.of(
                    Component.text("The trident has repaired itself, but it's still not quite right.")
                            .color(NamedTextColor.DARK_GRAY)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("It's still completely useless as a weapon, but it's a start.")
                            .color(NamedTextColor.DARK_GRAY)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("I think adding some powerful aquatic energy to it may help")
                            .color(NamedTextColor.DARK_GRAY)
                            .decoration(TextDecoration.ITALIC, false)

            )
    );

    i.setItemMeta(meta);

    return i;
  }

  @EventHandler
  public void tick(ServerTickStartEvent e) {
    Bukkit.getWorlds()
            .get(0)
            .getEntities()
            .parallelStream()
            .filter(i -> i.getType() == EntityType.DROPPED_ITEM)
            .map(i -> (Item) i)
            .filter(i -> i.getItemStack().getType() == Material.SCUTE)
            .filter(i -> isStepItem(i.getItemStack()))
            .filter(i -> i.getWorld().getBiome(i.getLocation()).getKey().getKey().contains("ocean"))
            .filter(i -> i.isOnGround() && i.getLocation().getBlock().getType() == Material.WATER)
            .filter(i -> !i.getPersistentDataContainer().has(PRE_SPAWN_TAG))
            .sequential()
            .forEach(i -> {
              i.getPersistentDataContainer().set(PRE_SPAWN_TAG, PersistentDataType.BYTE, (byte) 1);
              Bukkit.getScheduler().scheduleSyncDelayedTask(CabotEnchants.getPlugin(CabotEnchants.class), new TridentGuardianAnimation(i), 20);

            });
  }

  @EventHandler
  public void move(EntityMoveEvent e) {
    if (e.getEntity().getPersistentDataContainer().has(PRE_SPAWN_TAG) && e.getEntityType() == EntityType.DROPPED_ITEM) {
      if (e.getTo().getY() > e.getFrom().getY()) {
        e.setCancelled(true);
      }
    }
  }
  @EventHandler
  public void target(EntityTargetEvent e) {
    if (e.getEntity().getPersistentDataContainer().has(PRE_SPAWN_TAG)) {
      e.setCancelled(true);
    }
  }

  class TridentGuardianAnimation implements Runnable {
    Item itemEntity;
    Guardian animationEntity;
    int jobId;

    public TridentGuardianAnimation(Item itemEntity) {
      this.itemEntity = itemEntity;
    }

    public void setJobId(int jobId) {
      this.jobId = jobId;
    }

    @Override
    public void run() {
      if (animationEntity == null) {
        if (itemEntity.isDead()) {
          Bukkit.getScheduler().cancelTask(jobId);
          return;
        }
        itemEntity.setPickupDelay(999999);
        Location spawnPoint;
        for (int i = 0; i < 5; i++) {
          spawnPoint = itemEntity.getLocation().clone()
                  .add(
                          // choose random direction 20 blocks out
                          Math.random() * 20 - 10,
                          0,
                          Math.random() * 20 - 10
                  );
          spawnPoint = spawnPoint.getWorld()
                  .getHighestBlockAt(spawnPoint, HeightMap.OCEAN_FLOOR)
                  .getLocation();
          if (spawnPoint.getY() >= 60) {
            spawnPoint = null;
            continue;
          }
          var g = (Guardian) spawnPoint.getWorld().spawnEntity(spawnPoint, EntityType.GUARDIAN);
          g.getPersistentDataContainer().set(PRE_SPAWN_TAG, PersistentDataType.BYTE, (byte) 1);
          var result = g.getPathfinder().findPath(
                  itemEntity.getLocation()
                          .add(ThreadLocalRandom.current().nextInt(3, 5), 3, 0)
          );
          if (result == null) {
            spawnPoint = null;
            g.remove();
            continue;
          }
          animationEntity = g;
          animationEntity.getPathfinder().moveTo(result);

          break;
        }
      } else {
        if (animationEntity.getPathfinder().hasPath()) return;

        // once the guardian has reached the item, it's time to do the animation
        var fakeItem = (Silverfish) itemEntity.getWorld().spawnEntity(itemEntity.getLocation(), EntityType.SILVERFISH);
        fakeItem.setInvulnerable(true);
        fakeItem.setSilent(true);
        fakeItem.setGravity(false);
        fakeItem.setInvisible(true);
        animationEntity.setAI(false);
        animationEntity.setTarget(fakeItem);
        animationEntity.lookAt(itemEntity.getLocation());
        animationEntity.setLaser(true);
        Bukkit.getScheduler().cancelTask(jobId);
        Bukkit.getScheduler()
                .scheduleSyncDelayedTask(
                        CabotEnchants.getPlugin(CabotEnchants.class),
                        () -> {
                          itemEntity.setPickupDelay(20);
                          fakeItem.remove();
                          animationEntity.remove();
                          itemEntity.getWorld()
                                  .playSound(
                                          itemEntity.getLocation(),
                                          Sound.ENTITY_PLAYER_LEVELUP,
                                          SoundCategory.HOSTILE,
                                          1,
                                          1
                                  );
                          itemEntity.getWorld()
                                  .spawnParticle(
                                          Particle.TOTEM,
                                          itemEntity.getLocation(),
                                          100,
                                          0.2,
                                          0.2,
                                          0.2,
                                          0.1
                                  );
                          itemEntity.setItemStack(
                                  getNextStep().createStepItem()
                          );
                        },
                        40
                );
      }
    }
  }
}
