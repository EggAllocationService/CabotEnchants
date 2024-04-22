package dev.cabotmc.cabotenchants.mace;

import dev.cabotmc.cabotenchants.util.YAxisFalldamageGate;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class WindChargeExplosion {
    public static final float PUSH_FORCE = 1.8f;
    public static void spawn(Location l) {
        var entities = l.getWorld().getNearbyEntities(l, 5, 5, 5);
        for (var e : entities) {
            if (!(e instanceof LivingEntity)) continue;

            var push_force = e.getLocation().add(0.0, ((LivingEntity) e).getEyeHeight(), 0.0).toVector().subtract(l.toVector()).normalize().multiply(PUSH_FORCE);
            // attenuate linearly with distance
            push_force.multiply(1.0 / Math.max(e.getLocation().distance(l), 1.0));
            e.setVelocity(
                    push_force
            );
            e.getPersistentDataContainer()
                    .set(YAxisFalldamageGate.Y_AXIS_FALLDAMAGE, org.bukkit.persistence.PersistentDataType.INTEGER, (int) l.getY() - 5);
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
