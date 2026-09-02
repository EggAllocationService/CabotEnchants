package dev.cabotmc.cabotenchants.uncraftingtable;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Random;

public class UncraftingSnifferStep extends QuestStep {
    private double chance = 0.0;
    private static final NamespacedKey FOUND_NEXT_STEP = new NamespacedKey("cabot", "found_next_step");

    private Random random = new Random();

    @Override
    protected ItemStack internalCreateStepItem() {
        return null;
    }

    @Override
    protected void onConfigUpdate() {
        chance = getConfig(UncraftingConfig.class).SNIFFER_DROP_CHANCE;
    }

    @EventHandler
    public void sniff(EntityDropItemEvent e) {
        if (e.getEntityType() == EntityType.SNIFFER && !e.getEntity().getPersistentDataContainer().has(FOUND_NEXT_STEP)) {
            if (random.nextDouble() < chance) {
                e.getItemDrop().setItemStack(
                        getNextStep().createStepItem()
                );
                e.getEntity().getPersistentDataContainer().set(FOUND_NEXT_STEP, PersistentDataType.BOOLEAN, true);
            }
        }
    }
}
