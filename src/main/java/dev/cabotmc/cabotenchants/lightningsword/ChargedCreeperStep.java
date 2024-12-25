package dev.cabotmc.cabotenchants.lightningsword;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class ChargedCreeperStep extends QuestStep {

    @Override
    protected ItemStack internalCreateStepItem() {
        return null;
    }
    @EventHandler
    public void chargedCreeperKill(EntityDeathEvent e) {
        if (e.getEntity().getKiller() == null) {
            return;
        }
        //having variable name after type creates a casted variable of the type
        if (e.getEntity() instanceof Creeper creeper)  {
            if (creeper.isPowered()) {
                e.getDrops().add(getNextStep().createStepItem());
            }
        }
    }
}
