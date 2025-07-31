package dev.cabotmc.cabotenchants.tempad.quest;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import org.bukkit.Material;
import org.bukkit.block.data.type.Vault;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDispenseLootEvent;
import org.bukkit.inventory.ItemStack;

public class TempadDoTrialStep extends QuestStep {
    @Override
    protected ItemStack internalCreateStepItem() {
        return null;
    }

    @EventHandler
    public void finish(BlockDispenseLootEvent e) {
        if (e.getBlock().getType() == Material.VAULT) {
            var data = (Vault) e.getBlock().getBlockData();
            if (data.isOminous()) {
                e.getDispensedLoot()
                        .add(getNextStep().createStepItem());
            }
        }
    }
}
