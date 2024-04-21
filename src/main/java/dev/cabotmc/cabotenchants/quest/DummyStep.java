package dev.cabotmc.cabotenchants.quest;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DummyStep extends QuestStep {
    @Override
    protected ItemStack internalCreateStepItem() {
        return new ItemStack(Material.DIRT);
    }
}
