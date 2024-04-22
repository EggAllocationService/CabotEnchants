package dev.cabotmc.cabotenchants.career.rewards;

import dev.cabotmc.cabotenchants.career.Reward;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DummyReward implements Reward {
    @Override
    public void activate(Player target) {
        target.sendMessage(Component.text("Dummy reward activated"));
    }

    @Override
    public void deactivate(Player target) {
        target.sendMessage(Component.text("Dummy reward deactivated"));
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public ItemStack createDisplayItem(boolean selected, Player viewer) {
        return new ItemStack(selected ? Material.OAK_SAPLING :  Material.DEAD_BUSH);
    }

    @Override
    public String getName() {
        return "Dummy";
    }
}
