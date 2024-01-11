package dev.cabotmc.cabotenchants.spawner;

import dev.cabotmc.cabotenchants.CabotEnchants;
import dev.cabotmc.cabotenchants.quest.QuestStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpawnerSwordReward extends QuestStep {
  public static final NamespacedKey SOULDRINKER_TAG = new NamespacedKey("cabot", "souldrinker");
   int NUM_KILLS = 500;
   boolean SHOULD_LOOP = false;

  @Override
  protected void onConfigUpdate() {
    NUM_KILLS = getConfig(CESpawnerConfig.class).NUM_MOB_KILLS_FOR_SPAWNER;
    SHOULD_LOOP = getConfig(CESpawnerConfig.class).ENABLE_LOOPING;
  }

  @Override
  protected ItemStack internalCreateStepItem() {
    var i = new ItemStack(Material.GOLDEN_SWORD);
    var meta = (Repairable) i.getItemMeta();
    meta.displayName(
            MiniMessage.miniMessage()
                    .deserialize("<!i><rainbow>Souldrinker")
    );

    meta.addItemFlags(
            ItemFlag.HIDE_ENCHANTS
    );
    meta.getPersistentDataContainer()
                    .set(SOULDRINKER_TAG, SpawnerSwordDataType.CODEC, new SpawnerSwordData());
    meta.setUnbreakable(true);
    updateLore(meta);
    meta.setCustomModelData(1);
    meta.setRepairCost(999999);
    i.setItemMeta(meta);

    i.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 8);
    return i;
  }

  public static ItemStack createSpawner(EntityType i) {
    var item = new ItemStack(Material.SPAWNER);
    var meta = (BlockStateMeta) item.getItemMeta();
    meta.displayName(
            MiniMessage.miniMessage()
                    .deserialize("<!i><rainbow>Monster Spawner")
    );

    var cs = (CreatureSpawner) meta.getBlockState();

    cs.setSpawnedType(i);
    meta.setBlockState(cs);
    meta.getPersistentDataContainer()
                    .set(QUEST_ID_KEY, PersistentDataType.INTEGER, -1);
    meta.getPersistentDataContainer().set(NO_STACK_KEY, PersistentDataType.STRING, UUID.randomUUID().toString());
    item.setItemMeta(meta);
    return item;
  }

  @EventHandler
  public void place(BlockPlaceEvent e) {
    var stack = e.getItemInHand();
    if (stack.getType() != Material.SPAWNER) return;
    var meta = stack.getItemMeta();
    if (meta == null) return;
    if (!meta.getPersistentDataContainer().has(QUEST_ID_KEY)) return;
    var cs = (CreatureSpawner) ((BlockStateMeta) meta).getBlockState();
    var newBlock = e.getBlock();
    var newMeta = (CreatureSpawner) newBlock.getState();
    Bukkit.getScheduler()
            .scheduleSyncDelayedTask(
                    CabotEnchants.getPlugin(CabotEnchants.class),
                    () -> {
                      newMeta.setSpawnedType(cs.getSpawnedType());
                      newMeta
                              .getPersistentDataContainer()
                                      .set(SOULDRINKER_TAG, PersistentDataType.BOOLEAN, true);
                      newMeta.update();
                    },
                    2
            );
  }

  @EventHandler(ignoreCancelled = true)
  public void kill(EntityDeathEvent e) {
    if (e.getEntityType() == EntityType.PLAYER) return;
    var killer = e.getEntity().getKiller();
    if (killer == null) return;
    var item = killer.getInventory().getItemInMainHand();
    if (item == null) return;
    var meta = item.getItemMeta();
    if (meta == null) return;
    var data = meta.getPersistentDataContainer()
            .get(SOULDRINKER_TAG, SpawnerSwordDataType.CODEC);
    if (data == null) return;
    if (data.type == null) {
      data.type = e.getEntityType();
      data.amount = 1;
    } else if (data.type == e.getEntityType()) {
      data.amount ++;
    } else {
      return;
    }
    if (data.amount >= NUM_KILLS) {

      var spawner = createSpawner(data.type);
      var location = e.getEntity().getLocation().add(0, e.getEntity().getHeight() / 2, 0);
      if (SHOULD_LOOP) {
        e.getEntity()
                .getWorld()
                .dropItemNaturally(location, spawner);
        data.amount = 0 ;
        data.type = null;
      } else {
        killer.getInventory().setItemInMainHand(spawner);
      }
      var firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
      var m = firework.getFireworkMeta();
      m.addEffects(
              FireworkEffect.builder()
                      .withColor(Color.fromRGB(0x15F570))
                      .with(FireworkEffect.Type.BALL)
                      .withFlicker()
                      .build(),
              FireworkEffect.builder()
                      .with(FireworkEffect.Type.BURST)
                        .withColor(Color.fromRGB(0xfc4503))
                        .withTrail()
                      .withFade(Color.fromRGB(0x2874d1))
                      .build()
      );
      firework.getPersistentDataContainer().set(NO_STACK_KEY, PersistentDataType.STRING, UUID.randomUUID().toString());
      firework.setFireworkMeta(m);
      firework.detonate();
    }
    meta.getPersistentDataContainer()
            .set(SOULDRINKER_TAG, SpawnerSwordDataType.CODEC, data);
    updateLore(meta);
    item.setItemMeta(meta);
  }
  @EventHandler
  public void damage(EntityDamageByEntityEvent e) {
    if (e.getDamager().getPersistentDataContainer().has(NO_STACK_KEY) && e.getDamager().getType() == EntityType.FIREWORK) {
      e.setCancelled(true);
    }
  }

  static final TextColor THEME_COLOR = TextColor.color(0xd175ff);
  void updateLore(ItemMeta meta) {
    var data = meta.getPersistentDataContainer()
            .get(SOULDRINKER_TAG, SpawnerSwordDataType.CODEC);
    var lore = new ArrayList<Component>();
    if (data == null) return;
    if (data.type != null) {
      lore.add(
              Component.translatable(data.type.translationKey())
                      .decoration(TextDecoration.ITALIC, false)
                      .color(THEME_COLOR)
                      .append(
                                Component.text(": " + data.amount + "/" + NUM_KILLS )
                                        .color(NamedTextColor.WHITE)
                                        .decoration(TextDecoration.ITALIC, false)
                      )
      );
    } else {
      lore.add(
              Component.text("Be warned: this sword will attune itself to the first enemy you kill")
                      .color(NamedTextColor.DARK_GRAY)
                      .decoration(TextDecoration.ITALIC, false)
      );
      lore.add(
              Component.text("This is not reversible!")
                      .color(NamedTextColor.DARK_GRAY)
                      .decoration(TextDecoration.ITALIC, false)
      );
    }
    lore.add(Component.empty());
    lore.add(
            Component.text("Extinguish the stars. Devour the planets. Soar through a universe of utter dark.")
                    .color(NamedTextColor.DARK_GRAY)
    );
    meta.lore(lore);
  }
}
