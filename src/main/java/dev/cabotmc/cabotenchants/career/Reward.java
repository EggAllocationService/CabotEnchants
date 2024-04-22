package dev.cabotmc.cabotenchants.career;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Reward {
    void activate(Player target);
    void deactivate(Player target);
    boolean isHidden();
    ItemStack createDisplayItem(boolean selected);
    String getName();
}
