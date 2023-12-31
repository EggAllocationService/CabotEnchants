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
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpawnerSwordReward extends QuestStep {
  static final NamespacedKey SOULDRINKER_TAG = new NamespacedKey("cabot", "souldrinker");
  static final int NUM_KILLS = 5;
  @Override
  protected ItemStack internalCreateStepItem() {
    var i = new ItemStack(Material.GOLDEN_SWORD);
    var meta = i.getItemMeta();
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
    i.setItemMeta(meta);

    i.addEnchantment(Enchantment.DAMAGE_ALL, 5);
    return i;
  }

  ItemStack createSpawner(EntityType i) {
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
      e.getEntity()
              .getWorld()
              .dropItemNaturally(location, spawner);
      var firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
      var m = firework.getFireworkMeta();
      m.addEffects(
              FireworkEffect.builder()
                      .withColor(Color.fromRGB(0x15F570))
                      .with(FireworkEffect.Type.BALL_LARGE)
                      .withFlicker()
                      .build(),
                FireworkEffect.builder()
                        .withColor(Color.fromRGB(0x6b03fc))
                        .with(FireworkEffect.Type.BALL_LARGE)
                        .withTrail()
                        .build(),
              FireworkEffect.builder()
                      .with(FireworkEffect.Type.BURST)
                        .withColor(Color.fromRGB(0xfc4503))
                        .withTrail()
                      .withFade(Color.fromRGB(0x2874d1))
                      .build()
      );
      firework.setFireworkMeta(m);
      firework.detonate();

      data.type = null;
      data.amount = 0;


    }
    meta.getPersistentDataContainer()
            .set(SOULDRINKER_TAG, SpawnerSwordDataType.CODEC, data);
    updateLore(meta);
    item.setItemMeta(meta);
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
    }
    lore.add(Component.empty());
    lore.add(
            Component.text("Tastes kinda funky...")
                    .color(NamedTextColor.DARK_GRAY)
    );
    meta.lore(lore);
  }
}
