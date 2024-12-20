package dev.cabotmc.cabotenchants.blockengine.blocks;

import dev.cabotmc.cabotenchants.blockengine.CabotBlock;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.UUID;

public class TestBlock extends CabotBlock<Integer> {
    private int interactionCount = 0;

    public TestBlock(UUID id, Location location) {
        super(id, location);
    }

    @Override
    public void tick() {

    }

    @Override
    public void interact(Player cause, Action action) {
        interactionCount++;
        cause.sendMessage(Component.text("Hello! You've clicked me " + interactionCount + " times"));
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
    public void placed() {

    }

    @Override
    public Class<Integer> getDataType() {
        return Integer.class;
    }

    @Override
    public Integer getData() {
        return interactionCount;
    }

    @Override
    public void setData(Integer data) {
        interactionCount = data;
    }
}
