package dev.cabotmc.cabotenchants.boss.traits;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;

public class BossSeeker implements Runnable {
    protected int cancelToken;

    public Location location;
    public Location target;

    public BossSeeker(Location location, Location target) {
        this.location = location;
        this.target = target;
    }

    public static final float MOVE_SPEED = 0.3f;

    @Override
    public void run() {
        if (location == null || target == null) {
            return;
        }
        if (location.distanceSquared(target) < 1) {
            explode();
            Bukkit.getScheduler()
                    .cancelTask(cancelToken);
            return;
        }
        var direction = target.toVector().subtract(location.toVector()).normalize();
        direction.multiply(MOVE_SPEED);
        location.add(direction);

        // play particles
        location.getWorld()
                .spawnParticle(
                        Particle.GLOW,
                        location,
                        1,
                        0, 0, 0
                );
    }


    void explode() {
        location.getWorld().createExplosion(location, 2, false, false);
    }
}
