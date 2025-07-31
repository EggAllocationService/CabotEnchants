package dev.cabotmc.cabotenchants.reach;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class ReachTotalDistanceStep extends QuestStep {
    private static final NamespacedKey TRACKER_KEY = new NamespacedKey("cabot", "reach_progress");

    private double totalDistance;

    @Override
    protected void onConfigUpdate() {
        var cfg = getConfig(ReachConfig.class);
        totalDistance = cfg.totalDistance;
    }

    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.BOOK);
        i.editMeta(m -> {
            m.setEnchantmentGlintOverride(true);
            m.displayName(
                    MiniMessage
                            .miniMessage()
                            .deserialize(
                                    "<!i><grey>Disenchanted Book"
                    )
            );

            m.lore(
                    renderLore(0)
            );

            m.getPersistentDataContainer()
                    .set(TRACKER_KEY, PersistentDataType.DOUBLE, 0d);
        });

        return i;
    }

    @EventHandler
    public void kill(EntityDeathEvent e) {
        if (e.getEntity().getType() == EntityType.PLAYER) return;

        var killer = e.getEntity().getKiller();
        if (killer == null) return;

        var items = getStepItems(killer, false);

        if (items.isEmpty()) {
            return;
        }

        var distance = e.getEntity().getLocation().distance(killer.getLocation());

        for (var i : items) {
            var item = i.item();
            var progress = item.getPersistentDataContainer().getOrDefault(TRACKER_KEY, PersistentDataType.DOUBLE, 0.0)
                    + distance;
            if (progress >= totalDistance) {
                // replace
                replaceWithNextStep(killer, i.slot());
            } else {
                item.editMeta(m -> {
                    m.getPersistentDataContainer()
                            .set(TRACKER_KEY, PersistentDataType.DOUBLE, progress);
                    m.lore(renderLore(progress));
                });
            }
        }

    }

    private List<Component> renderLore(double completed) {
        return List.of(
                Component.text("This book appeared out of thin air as soon as that skeleton died.")
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("You think that the cause was killing the skeleton from such an immense distance.")
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.empty(),
                MiniMessage
                        .miniMessage()
                        .deserialize(
                                String.format("<dark_grey>%.2f/%.2f KM", completed / 1000, totalDistance / 1000)
                        )
        );
    }
}
