package dev.cabotmc.cabotenchants.lightningsword;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import dev.cabotmc.cabotenchants.util.JsonDataType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.io.Serializable;
import java.util.List;

public class KillLightningMobsStep extends QuestStep {
    private static NamespacedKey STEP_DATA_KEY = new NamespacedKey("cabot", "step_progress");

    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.IRON_SWORD);
        i.editMeta(m -> {
            m.displayName(
                    MiniMessage
                            .miniMessage().deserialize("<!i><grey>Conductive Sword")
            );

            var tracker = new Tracker();

            m.getPersistentDataContainer()
                    .set(STEP_DATA_KEY, Tracker.CODEC, tracker);

            m.lore(
                    List.of(
                            tracker.render()
                    )
            );

        });

        return i;
    }

    @EventHandler
    public void kill(EntityDeathEvent e) {
        var type = e.getEntityType();

        if (type != EntityType.MOOSHROOM && type != EntityType.WITCH && type != EntityType.PIGLIN) {
            return;
        }

        if (e.getEntity().getKiller() != null) {
            var p = e.getEntity().getKiller();
            for (var i : getStepItems(p, false)) {
                var data = i.item().getPersistentDataContainer()
                        .get(STEP_DATA_KEY, Tracker.CODEC);

                if (data == null) {
                    Bukkit.getLogger().warning("Malformed step progress tag");
                    continue;
                }

                switch (type) {
                    case EntityType.MOOSHROOM -> data.mooshroom = true;
                    case EntityType.WITCH -> data.witch = true;
                    case EntityType.PIGLIN -> data.piglin = true;
                }

                if (data.mooshroom && data.witch && data.piglin) {
                    replaceWithNextStep(p, i.slot());
                } else {
                    i.item().editMeta(
                            meta -> {
                                meta.lore(
                                        List.of(
                                                data.render()
                                        )
                                );
                                meta.getPersistentDataContainer()
                                        .set(STEP_DATA_KEY, Tracker.CODEC, data);
                            }
                    );
                    e.getEntity()
                            .getWorld()
                            .spawnParticle(
                                    Particle.SOUL,
                                    e.getEntity().getLocation(),
                                    10,
                                    0.5,0.5,0.5
                            );
                }
            }
        }
    }



    public static class Tracker implements Serializable {
        public static PersistentDataType<String, Tracker> CODEC = new JsonDataType<>(Tracker.class);

        public boolean witch;
        public boolean mooshroom;
        public boolean piglin;


        public Component render() {
            return Component.empty()
                    .append(
                            Component.text("\u2022 ")
                                    .color(TextColor.color(witch ? 0x953fe0 : 0x4c2073))
                    )
                    .append(
                            Component.text("\u2022 ")
                                    .color(TextColor.color(mooshroom ? 0xed3221 : 0x751910))
                    )
                    .append(
                            Component.text("\u2022 ")
                                    .color(TextColor.color(piglin ? 0xde21ac : 0x4a0a39))
                    );
        }
    }
}