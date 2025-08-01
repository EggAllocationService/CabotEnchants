package dev.cabotmc.cabotenchants.blockengine;

import com.google.gson.Gson;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.ItemDisplay;

import java.util.UUID;

public class ActiveBlock {
    private static Gson gson = new Gson();

    protected UUID id;
    protected NamespacedKey type;
    protected CabotBlock block;
    protected ItemDisplay renderer;

    // absolute position
    protected World world;
    protected int x;
    protected int y;
    protected int z;

    public void setRenderedModel(NamespacedKey model) {
        var newItem = renderer.getItemStack();
        newItem.editMeta(m -> {
            m.setItemModel(model);
        });
        renderer.setItemStack(newItem);
    }

    public SerializedBlockData serialize() {
        var result = new SerializedBlockData();

        result.id = id;
        result.x = x & 15;
        result.y = y;
        result.z = z & 15;

        result.type = type;
        result.model = renderer.getItemStack().getItemMeta().getItemModel();

        result.data = gson.toJson(block.getData());
        return result;
    }

    public UUID getId() {
        return id;
    }

    public NamespacedKey getType() {
        return type;
    }

    public CabotBlock getBlock() {
        return block;
    }

    public ItemDisplay getRenderer() {
        return renderer;
    }

    public World getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
