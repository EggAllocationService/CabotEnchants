package dev.cabotmc.cabotenchants.mace;

import dev.cabotmc.cabotenchants.util.YAxisFalldamageGate;
import org.bukkit.Location;

public class WindChargeExplosion {
    public static final float PUSH_FORCE = 1.0f;
    public static void spawn(Location l) {
        var entities = l.getWorld().getNearbyEntities(l, 5, 5, 5);
        for (var e : entities) {
            var push_force = e.getLocation().toVector().subtract(l.toVector()).normalize().multiply(PUSH_FORCE);
            // attenuate linearly with distance
            push_force.multiply(1.0 / e.getLocation().distance(l));
            e.setVelocity(
                    push_force
            );
            e.getPersistentDataContainer()
                    .set(YAxisFalldamageGate.Y_AXIS_FALLDAMAGE, org.bukkit.persistence.PersistentDataType.INTEGER, (int) l.getY());
        }
        // particle effects: some smoke and sword slashes and stuff
        l.getWorld()
                .spawnParticle(
                        org.bukkit.Particle.EXPLOSION_LARGE,
                        l,
                        10,
                        0.5,
                        0.5,
                        0.5,
                        0.1
                );
        l.getWorld()
                .spawnParticle(
                        org.bukkit.Particle.SWEEP_ATTACK,
                        l,
                        4,
                        1.0,
                        1.0,
                        1.0,
                        0.1
                );

        l.getWorld()
                .playSound(
                        l,
                        org.bukkit.Sound.ENTITY_GENERIC_EXPLODE,
                        0.5f,
                        1.0f
                );
    }
}
