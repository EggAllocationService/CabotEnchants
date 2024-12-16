package dev.cabotmc.cabotenchants.god;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import dev.cabotmc.cabotenchants.quest.impl.KillEntityTypeStep;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class GodWardenStep extends KillEntityTypeStep {
    public GodWardenStep() {
        super(1, EntityType.WARDEN);
    }

    @Override
    protected ItemStack internalCreateStepItem() {

        return null;
    }

    long lastTick;
    GodListener gl = new GodListener();

    @EventHandler
    public void tick(ServerTickStartEvent e) {
        lastTick++;

        if (lastTick % 6 != 0) return;
        Bukkit.getOnlinePlayers()
                .stream()
                .filter(Player::isOnGround)
                .filter(gl::hasFullGodArmor)
                .forEach(p -> {
                    p.getWorld()
                            .spawnParticle(
                                    Particle.TRIAL_SPAWNER_DETECTION,
                                    p.getLocation(),
                                    1,
                                    0.2,
                                    0,
                                    0.2,
                                    0.0003
                            );
                    p.setExhaustion(0f);
                });

    }
}
