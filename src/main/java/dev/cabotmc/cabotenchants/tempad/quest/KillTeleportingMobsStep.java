package dev.cabotmc.cabotenchants.tempad.quest;

import dev.cabotmc.cabotenchants.quest.impl.ChecklistStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class KillTeleportingMobsStep extends ChecklistStep {
    public KillTeleportingMobsStep() {
        super();
        setRequirements(
                new CheckboxRequirement(0x7b28d4, 0x391263),
                new CheckboxRequirement(0x7b28d4, 0x391263)
        );
    }
    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.STICK);
        i.editMeta(m -> {
            m.displayName(
                    Component.text("Strange Eye")
                            .decoration(TextDecoration.ITALIC, false)
            );
            m.lore(renderLore(m));
            m.setItemModel(new NamespacedKey("minecraft", "ender_eye"));
            m.setEnchantmentGlintOverride(true);
        });
        return i;
    }

    @EventHandler
    public void kill(EntityDeathEvent e) {
        if (e.getEntity().getKiller() == null) return;
        if (e.getEntityType() != EntityType.ENDERMAN && e.getEntityType() != EntityType.SHULKER) return;

        var items =  getStepItems(e.getEntity().getKiller(), false);

        for (var i : items) {
            var item = i.item();
            var meta = item.getItemMeta();
            if (e.getEntityType() == EntityType.ENDERMAN) {
                completeRequirement(meta, 0);
            } else if (e.getEntityType() == EntityType.SHULKER) {
                completeRequirement(meta, 1);
            } else {
                return;
            }

            e.getEntity()
                    .getWorld()
                    .spawnParticle(
                            Particle.SOUL,
                            e.getEntity().getLocation(),
                            10,
                            0.5, 0.5, 0.5,
                            0.0001
                    );

            if (isCompleted(meta)) {
                replaceWithNextStep(e.getEntity().getKiller(), i.slot());
            } else {
                meta.lore(renderLore(meta));
                item.setItemMeta(meta);
            }
        }
    }

    private List<Component> renderLore(ItemMeta meta) {
        return List.of(
            Component.text("This strange eye was left in the vault ages ago, by someone long forgotten.")
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false),
                Component.text("Despite its age, it seems really eager to teleport out of my hands.")
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
            Component.empty(),
            renderCheckboxes(meta)
        );
    }
}
