package dev.cabotmc.cabotenchants.flight;

import com.destroystokyo.paper.event.entity.ThrownEggHatchEvent;
import dev.cabotmc.cabotenchants.quest.QuestStep;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class FlightQuestStart extends QuestStep {
    @Override
    protected ItemStack internalCreateStepItem() {
        return null;
    }

    @EventHandler
    public void eggImpact(ThrownEggHatchEvent e) {
        if (Math.random() > 0.005) return;
        e.setHatching(false);
        e.getEgg()
                .getWorld()
                .dropItemNaturally(e.getEgg().getLocation(), getNextStep().createStepItem());
    }
}
