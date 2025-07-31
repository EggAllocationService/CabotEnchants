package dev.cabotmc.cabotenchants.quest.impl;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.function.Function;

public abstract class ChecklistStep extends QuestStep {
    private static final NamespacedKey QUEST_DATA = new NamespacedKey("cabot", "checklist_data");
    private CheckboxRequirement[] requirements;

    protected void setRequirements(CheckboxRequirement... req) {
        this.requirements = req;
    }

    @Override
    protected abstract ItemStack internalCreateStepItem();

    protected void completeRequirement(ItemMeta meta, int index) {
        var data = meta.getPersistentDataContainer()
                .getOrDefault(QUEST_DATA, PersistentDataType.LONG, 0L);
        meta.getPersistentDataContainer()
                .set(QUEST_DATA, PersistentDataType.LONG, data | (1L << index));
    }

    protected boolean isCompleted(ItemMeta meta) {
        var data = meta.getPersistentDataContainer()
                .getOrDefault(QUEST_DATA, PersistentDataType.LONG, 0L);
        return Long.bitCount(data) >= requirements.length;
    }

    protected Component renderCheckboxes(ItemMeta item) {
        var data = item.getPersistentDataContainer()
                .getOrDefault(QUEST_DATA, PersistentDataType.LONG, 0L);

        var base = Component.empty();
        int i = 0;
        for (var requirement: requirements) {
            var completed = (data & (1L << i)) > 0;
            base = base.append(
                    Component.text("\u2022 ")
                            .color(TextColor.color(completed ? requirement.completed : requirement.uncompleted))
                            .decoration(TextDecoration.ITALIC, false)
            );
            i++;
        }
        return base;
    }

    protected void completeRequirementForAll(Player target, int index, Function<ItemMeta, List<Component>> loreGenerator) {
        var items = getStepItems(target, false);
        for (var i : items) {
            var item = i.item();
            var meta = item.getItemMeta();
            completeRequirement(meta, index);
            if (isCompleted(meta)) {
                replaceWithNextStep(target, i.slot());
            } else {
                meta.lore(loreGenerator.apply(meta));
                item.setItemMeta(meta);
            }
        }
    }

    public record CheckboxRequirement(int completed, int uncompleted) {

    }
}
