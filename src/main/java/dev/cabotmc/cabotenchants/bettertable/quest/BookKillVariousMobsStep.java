package dev.cabotmc.cabotenchants.bettertable.quest;

import com.google.gson.Gson;
import dev.cabotmc.cabotenchants.CabotEnchants;
import dev.cabotmc.cabotenchants.quest.QuestStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BookKillVariousMobsStep extends QuestStep {

    static final NamespacedKey TRACKER_KEY = new NamespacedKey("cabotenchants", "bookmobs");

    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.BOOK);
        var m = i.getItemMeta();
        m.displayName(Component.text("Mysterious Book")
                .color(NamedTextColor.AQUA)
                .decoration(TextDecoration.ITALIC, false));
        m.getPersistentDataContainer()
                .set(TRACKER_KEY, BookMobsTracker.CODEC, new BookMobsTracker());
        m.lore(
                List.of(
                        Component.text("This weird book appeared with a bang after enchanting an item")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false),
                        Component.text("It must be connected to my enchanting table, but it doesn't do anything")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false),
                        Component.text("I'm going to try to expose it to as many types of experience as I can")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false),
                        Component.empty(),
                        Component.empty(),
                        Component.empty(),
                        Component.empty()
                )
        );
        updateLore(m, new BookMobsTracker());
        i.setItemMeta(m);
        return i;
    }

    void updateLore(ItemMeta meta, BookMobsTracker tracker) {
        var lore = meta.lore();
        var data = tracker.renderProgress();
        lore.set(lore.size() - 3, data[0]);
        lore.set(lore.size() - 2, data[1]);
        lore.set(lore.size() - 1, data[2]);
        meta.lore(lore);
    }

    @EventHandler
    public void kill(EntityDeathEvent e) {
        if (e.getEntity().getKiller() == null) return;
        var killer = e.getEntity().getKiller();
        var items = getStepItems(killer, false);
        if (items.isEmpty()) return;
        var shouldShowParticles = false;
        for (var itemEntry : items) {
            var item = itemEntry.item();
            var data = item.getItemMeta().getPersistentDataContainer()
                    .get(TRACKER_KEY, BookMobsTracker.CODEC);
            if (data == null) {
                data = new BookMobsTracker();
            }
            if (data.trackProgress(e.getEntityType())) {
                shouldShowParticles = true;
                if (data.isComplete()) {
                    replaceWithNextStep(killer, itemEntry.slot());
                    getQuest().markCompleted(killer);
                    continue;
                }

                var meta = item.getItemMeta();
                meta.getPersistentDataContainer()
                        .set(TRACKER_KEY, BookMobsTracker.CODEC, data);
                updateLore(meta, data);
                item.setItemMeta(meta);
            }
        }
        if (shouldShowParticles) {
            killer.getWorld().spawnParticle(
                    Particle.FLASH,
                    e.getEntity().getLocation(),
                    1,
                    0.0,
                    0.0,
                    0.0,
                    0.0
            );
            killer.getWorld()
                    .playSound(
                            e.getEntity().getLocation(),
                            Sound.ENTITY_FIREWORK_ROCKET_BLAST,
                            1f,
                            1.0f
                    );
            e.setCancelled(true);
            Bukkit.getScheduler().scheduleSyncDelayedTask(
                    CabotEnchants.getPlugin(CabotEnchants.class),
                    () -> e.getEntity().remove()
            );
        }
    }


    static class TrackerDataType implements PersistentDataType<String, BookMobsTracker> {

        Gson g = new Gson();

        @Override
        public @NotNull Class<String> getPrimitiveType() {
            return String.class;
        }

        @Override
        public @NotNull Class<BookMobsTracker> getComplexType() {
            return BookMobsTracker.class;
        }

        @Override
        public @NotNull String toPrimitive(@NotNull BookMobsTracker complex, @NotNull PersistentDataAdapterContext context) {
            return g.toJson(complex);
        }

        @Override
        public @NotNull BookMobsTracker fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
            return g.fromJson(primitive, BookMobsTracker.class);
        }
    }

    static class BookMobsTracker {
        public static final TrackerDataType CODEC = new TrackerDataType();
        static final EntityType[] OVERWORLD_TARGETS = new EntityType[]
                {EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER,
                        EntityType.CREEPER, EntityType.PILLAGER, EntityType.RAVAGER,
                        EntityType.EVOKER, EntityType.WITCH,
                        EntityType.GUARDIAN, EntityType.DROWNED};
        static final EntityType[] NETHER_TARGETS = new EntityType[]
                {EntityType.ZOMBIFIED_PIGLIN, EntityType.PIGLIN, EntityType.HOGLIN,
                        EntityType.BLAZE, EntityType.GHAST, EntityType.MAGMA_CUBE,
                        EntityType.WITHER_SKELETON};
        static final EntityType[] END_TARGETS = new EntityType[]
                {EntityType.ENDERMAN, EntityType.SHULKER};

        boolean[] overworldProgress = new boolean[OVERWORLD_TARGETS.length];
        boolean[] netherProgress = new boolean[NETHER_TARGETS.length];
        boolean[] endProgress = new boolean[END_TARGETS.length];


        /**
         * @return if the progress changed as a result of this kill
         */
        boolean trackProgress(EntityType killed) {
            for (int i = 0; i < OVERWORLD_TARGETS.length; i++) {
                if (OVERWORLD_TARGETS[i] == killed) {
                    if (!overworldProgress[i]) {
                        overworldProgress[i] = true;
                        return true;
                    }
                }
            }
            for (int i = 0; i < NETHER_TARGETS.length; i++) {
                if (NETHER_TARGETS[i] == killed) {
                    if (!netherProgress[i]) {
                        netherProgress[i] = true;
                        return true;
                    }
                }
            }
            for (int i = 0; i < END_TARGETS.length; i++) {
                if (END_TARGETS[i] == killed) {
                    if (!endProgress[i]) {
                        endProgress[i] = true;
                        return true;
                    }
                }
            }
            return false;
        }

        boolean isComplete() {
            for (var b : overworldProgress) {
                if (!b) {
                    return false;
                }
            }
            for (var b : netherProgress) {
                if (!b) {
                    return false;
                }
            }
            for (var b : endProgress) {
                if (!b) {
                    return false;
                }
            }
            return true;
        }

        static final TextColor OVERWORLD_DONE_COLOR = TextColor.color(0x38f269);
        static final TextColor OVERWORLD_NOT_DONE_COLOR = TextColor.color(0x103319);
        static final TextColor NETHER_DONE_COLOR = TextColor.color(0x9e2d2d);
        static final TextColor NETHER_NOT_DONE_COLOR = TextColor.color(0x3a0f0f);
        static final TextColor END_DONE_COLOR = TextColor.color(0x9f36eb);
        static final TextColor END_NOT_DONE_COLOR = TextColor.color(0x37194F);

        Component[] renderProgress() {
            var ret = new Component[3];
            var overworld = Component.empty();
            for (int i = 0; i < OVERWORLD_TARGETS.length; i++) {
                var color = overworldProgress[i] ? OVERWORLD_DONE_COLOR : OVERWORLD_NOT_DONE_COLOR;
                overworld = overworld.append(Component.text("\u2022 ", color));
            }

            var nether = Component.empty();
            for (int i = 0; i < NETHER_TARGETS.length; i++) {
                var color = netherProgress[i] ? NETHER_DONE_COLOR : NETHER_NOT_DONE_COLOR;
                nether = nether.append(Component.text("\u2022 ", color));
            }

            var end = Component.empty();
            for (int i = 0; i < END_TARGETS.length; i++) {
                var color = endProgress[i] ? END_DONE_COLOR : END_NOT_DONE_COLOR;
                end = end.append(Component.text("\u2022 ", color));
            }
            ret[0] = overworld;
            ret[1] = nether;
            ret[2] = end;
            return ret;
        }
    }
}
