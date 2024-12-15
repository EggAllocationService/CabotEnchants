package dev.cabotmc.cabotenchants.shrinkray;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import dev.cabotmc.cabotenchants.quest.QuestStep;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class KillCreakingStep extends QuestStep {
    @Override
    protected ItemStack internalCreateStepItem() {
        return null;
    }

    @EventHandler
    public void death(EntityRemoveFromWorldEvent e) {
        if (e.getEntityType() == EntityType.CREAKING) {
            e.getEntity().getWorld()
                    .dropItemNaturally(e.getEntity().getLocation(), getNextStep().createStepItem());
        }
    }
}
