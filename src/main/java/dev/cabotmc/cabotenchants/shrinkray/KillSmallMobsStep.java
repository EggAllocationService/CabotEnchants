package dev.cabotmc.cabotenchants.shrinkray;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import dev.cabotmc.cabotenchants.util.JsonDataType;
import dev.cabotmc.cabotenchants.util.Models;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Turtle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.List;

public class KillSmallMobsStep extends QuestStep {

    private static final NamespacedKey STEP_DATA_KEY = new NamespacedKey("cabot", "step_progress");

    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.GHAST_TEAR);

        i.editMeta(m -> {
            m.displayName(
                    Component.text("Sap-covered Device")
                            .color(TextColor.color(0x46544c))
                            .decoration(TextDecoration.ITALIC, false)
            );

            var data = new ProgressChecklist();

            m.getPersistentDataContainer()
                    .set(STEP_DATA_KEY, ProgressChecklist.CODEC, data);

            m.lore(
                    generateLore(data)
            );

            m.setItemModel(Models.SHRINKRAY_GOOP);
        });

        return i;
    }

    private List<Component> generateLore(ProgressChecklist data) {
        return List.of(
                Component.text("This weird device covered in sap fell out of the Creaking as it disintegrated.")
                        .color(NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("There's no way to dislodge it. Maybe if I could shrink the device it could be extracted.")
                        .color(NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.empty(),
                data.render()
        );
    }

    @EventHandler
    public void kill(EntityDeathEvent e) {
        var type = e.getEntityType();

        if (type != EntityType.TURTLE && type != EntityType.ENDERMITE && type != EntityType.SILVERFISH) {
            return;
        }

        if (e.getEntity().getKiller() != null) {
            var p = e.getEntity().getKiller();
            for (var i : getStepItems(p, false)) {
                var data = i.item().getPersistentDataContainer()
                        .get(STEP_DATA_KEY, ProgressChecklist.CODEC);

                if (data == null) {
                    Bukkit.getLogger().warning("Malformed step progress tag");
                    continue;
                }

                switch (type) {
                    case EntityType.TURTLE -> {
                        var turtle = (Turtle) e.getEntity();
                        if (!turtle.isAdult()) {
                            data.babyTurtle = true;
                        }
                    }
                    case EntityType.ENDERMITE -> data.endermite = true;
                    case EntityType.SILVERFISH -> data.silverfish = true;
                }

                if (data.babyTurtle && data.endermite && data.silverfish) {
                    replaceWithNextStep(p, i.slot());
                } else {
                    i.item().editMeta(
                            meta -> {
                                meta.lore(
                                        generateLore(data)
                                );
                                meta.getPersistentDataContainer()
                                        .set(STEP_DATA_KEY, ProgressChecklist.CODEC, data);
                            }
                    );
                }
            }
        }
    }


    private static class ProgressChecklist implements Serializable {

        public static final JsonDataType<ProgressChecklist> CODEC = new JsonDataType<>(ProgressChecklist.class);

        public boolean babyTurtle = false;
        public boolean endermite = false;
        public boolean silverfish = false;

        public Component render() {
            var base = Component.empty()
                    .decoration(TextDecoration.ITALIC, false);

            // turtle
            base = base.append(
                    Component.text("\u2022 ")
                            .color(TextColor.color(babyTurtle ? 0x50d14b : 0x1a3b19))

            );

            // endermite
            base = base.append(
                    Component.text("\u2022 ")
                            .color(TextColor.color(endermite ? 0xb564e8 : 0x2e193b))

            );

            // silverfish
            base = base.append(
                    Component.text("\u2022 ")
                            .color(TextColor.color(silverfish ? 0xd6d6d6 : 0x262626))

            );

            return base;
        }
    }
}
