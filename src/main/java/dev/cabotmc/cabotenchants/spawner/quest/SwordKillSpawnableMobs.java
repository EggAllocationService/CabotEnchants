package dev.cabotmc.cabotenchants.spawner.quest;

import com.google.gson.Gson;
import dev.cabotmc.cabotenchants.quest.QuestStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SwordKillSpawnableMobs extends QuestStep {

  static final NamespacedKey STEP_DATA_KEY = new NamespacedKey("cabot", "sword_kill_spawnable_mobs");
  static final NamespacedKey SPAWNED_FROM_SPAWNER_KEY = new NamespacedKey("cabot", "spawned_from_spawner");

  void updateLore(ItemMeta m ) {
    var data = m.getPersistentDataContainer().get(STEP_DATA_KEY, StepData.CODEC);
    Component base = Component.empty();
    for (int i = 0; i < data.types.length; i++) {
      var done = data.done[i];
      var color = !done ? TextColor.color(0x333333) : TextColor.color(0x00ff00);
      base = base.append(Component.text("\u2022 ").color(color).decoration(TextDecoration.ITALIC, false));
    }
    m.lore(
            List.of(
                    Component.empty(),
                    base
            )
    );
  }
  @Override
  protected ItemStack internalCreateStepItem() {
    var i = new ItemStack(Material.IRON_NUGGET);
    var meta = i.getItemMeta();
    meta.displayName(
            Component.text("Spawner Fragment")
                    .decoration(TextDecoration.ITALIC, false)
                    .color(TextColor.color(0x34f723))
    );
    meta.getPersistentDataContainer()
            .set(STEP_DATA_KEY, StepData.CODEC, new StepData());

    updateLore(meta);
    meta.setCustomModelData(1);
    i.setItemMeta(meta);
    return i;
  }

  @EventHandler
  public void kill(EntityDeathEvent e) {
    var p = e.getEntity().getKiller();
    if (p == null) return;
    if (!e.getEntity().getPersistentDataContainer().has(SPAWNED_FROM_SPAWNER_KEY, PersistentDataType.BYTE)) return;
    var items = getStepItems(p, false);
    if (items.isEmpty()) return;
    for (var itemResult : items) {
        var item = itemResult.item();
        var meta = item.getItemMeta();
        var data = meta.getPersistentDataContainer().get(STEP_DATA_KEY, StepData.CODEC);
        var type = e.getEntityType();
        var isDone = true;
        var changed = false;
        for (int i = 0; i < data.types.length; i++) {
            if (data.types[i] == type && !data.done[i]) {
              data.done[i] = true;
              changed = true;
            }
            isDone = isDone && data.done[i];
        }
        if (isDone) {
          replaceWithNextStep(p, itemResult.slot());
          continue;
        }

        meta.getPersistentDataContainer().set(STEP_DATA_KEY, StepData.CODEC, data);
        updateLore(meta);
        item.setItemMeta(meta);
        if (changed) {
          e.getEntity()
                  .getWorld()
                  .spawnParticle(
                          Particle.SOUL,
                            e.getEntity().getLocation().add(0, e.getEntity().getHeight() / 2, 0),
                            10,
                            0.5,
                            0.5,
                            0.5,
                          0.003
                  );
        }

    }
  }

  @EventHandler
  public void spawn(CreatureSpawnEvent e) {
      if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) {
        e.getEntity().getPersistentDataContainer().set(SPAWNED_FROM_SPAWNER_KEY, PersistentDataType.BYTE, (byte) 1);
      }
  }
  static class StepData {
    static final StepCodec CODEC = new StepCodec();
    EntityType[] types = new EntityType[]
            {EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER, EntityType.CAVE_SPIDER,
            EntityType.SILVERFISH, EntityType.BLAZE, EntityType.MAGMA_CUBE};
    boolean[] done = new boolean[types.length];
  }

  static class StepCodec implements PersistentDataType<String, StepData> {
    Gson g = new Gson();
    @Override
    public @NotNull Class<String> getPrimitiveType() {
      return String.class;
    }

    @Override
    public @NotNull Class<StepData> getComplexType() {
      return StepData.class;
    }

    @Override
    public @NotNull String toPrimitive(@NotNull StepData complex, @NotNull PersistentDataAdapterContext context) {
      return g.toJson(complex);
    }

    @Override
    public @NotNull StepData fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
      return g.fromJson(primitive, StepData.class);
    }
  }
}
