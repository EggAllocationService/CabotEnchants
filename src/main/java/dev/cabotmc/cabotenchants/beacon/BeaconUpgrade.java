package dev.cabotmc.cabotenchants.beacon;

import dev.cabotmc.cabotenchants.CabotEnchants;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public abstract class BeaconUpgrade {
    int level = 1;

    public void playerEnteredRange(Player p) {

    }

    public void playerLeftRange(Player p) {
    }

    public abstract void onTickAsync(Location beaconLocation, Set<Player> inRange);

    public abstract int getMaxLevel();

    public abstract ItemStack getDisplayItem();

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public abstract int getCost(int level);

    public void execSync(Runnable task) {
        Bukkit.getScheduler().runTask(CabotEnchants.getProvidingPlugin(CabotEnchants.class), task);
    }

}
