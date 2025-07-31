package dev.cabotmc.cabotenchants.blockengine;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.UUID;

public abstract class CabotBlock<T> {

    private UUID id;
    private Location location;

    public CabotBlock(UUID id, Location location) {
        this.location = location;
        this.id = id;
    }

    protected Location getLocation() {
        return location.clone();
    }

    protected UUID getId() {
        return id;
    }

    protected World getWorld() {
        return location.getWorld();
    }

    /// Called every tick
    public abstract void tick();

    /// Called when a player right clicks the block
    public abstract void interact(Player cause, Action action);

    /// Called when the block is destroyed
    public abstract void destroy();

    /// Called when the block is placed
    public abstract void placed();

    /// Called when the block is loaded or freshly placed
    public void load() {

    }

    /// Called when the block is unloaded or destroyed
    public void unload() {

    }

    public abstract Class<T> getDataType();

    public T getData() {
        return null;
    }

    public void setData(T data) {

    }

}
