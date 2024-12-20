package dev.cabotmc.cabotenchants.blockengine.blocks;

import dev.cabotmc.cabotenchants.blockengine.CabotBlock;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.UUID;

public class TestBlock extends CabotBlock<Integer> {

    private ItemDisplay topDisplay;
    private ItemStack inventory;

    public TestBlock(UUID id, Location location) {
        super(id, location);
    }

    @Override
    public void tick() {

    }

    @Override
    public void interact(Player cause, Action action) {
        if (action == Action.RIGHT_CLICK_BLOCK) {
            if (!cause.getInventory().getItemInMainHand().isEmpty()) {
                inventory = cause.getInventory().getItemInMainHand();
                topDisplay.setItemStack(inventory);
            }
        }
    }

    @Override
    public void destroy() {
        getWorld()
                .spawnParticle(
                        Particle.EXPLOSION,
                        getLocation(),
                        10,
                        1,1,1
                );
    }

    @Override
    public void load() {
        topDisplay = getWorld().spawn(getLocation().add(0.5, 1, 0.5), ItemDisplay.class);
        // transformation: no scaling, 90 degree rotation around x, 0.25 scale
        var transformation = new Transformation(
                new Vector3f(0, 0, 0),
                new AxisAngle4f((float) Math.toRadians(90), 1, 0, 0),
                new Vector3f(0.25f, 0.25f, 0.25f),
                new AxisAngle4f(0, 0, 0, 1)
        );
        topDisplay.setTransformation(transformation);
    }

    @Override
    public void unload() {
        topDisplay.remove();
        getWorld()
                .dropItemNaturally(getLocation(), inventory);
    }

    @Override
    public void placed() {

    }

    @Override
    public Class<Integer> getDataType() {
        return null;
    }
}
