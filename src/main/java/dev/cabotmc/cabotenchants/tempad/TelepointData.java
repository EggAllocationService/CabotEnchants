package dev.cabotmc.cabotenchants.tempad;

import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;

public class TelepointData {
    public String material;
    public String name;

    public int x;
    public int y;
    public int z;

    public String world;

    public Location constructLocation() {
        var world = Bukkit.getWorld(Key.key(this.world));
        return new Location(world, x + 0.5, y + 1.5, z + 0.5);
    }
}
