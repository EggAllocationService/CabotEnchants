package dev.cabotmc.cabotenchants.sentient;

import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class SentientProjectile implements Runnable {
    ItemStack returnStack;
    Vector velocity;
    Vector position;
    Queue<LivingEntity> targets;
    Player shooter;
    World world;
    int ticksSinceLastHit = 0;

    LivingEntity currentTarget;
    int jobId = 0;

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public SentientProjectile(ItemStack returnStack, Vector velocity, Location position, Queue<LivingEntity> targets, Player shooter) {
        this.returnStack = returnStack;
        this.velocity = velocity;
        this.position = position.toVector();
        this.world = position.getWorld();
        //this.targets = targets;
        this.shooter = shooter;
        currentTarget = targets.poll();

        var list = new ArrayList<LivingEntity>(targets);
        Collections.shuffle(list);

        this.targets = new LinkedBlockingQueue<LivingEntity>(list);
    }

    static final Particle.DustOptions IN_FLIGHT_PARTICLE = new Particle.DustOptions(Color.fromRGB(255, 0, 255), 2.0f);
    static final double PARTICLE_SEPARATION = 0.3f;
    static final double VELOCITY = 3.5;
    static final double VELOCITY_SQUARED = VELOCITY * VELOCITY;

    static final double MAX_TURN_RADIUS_DEGREES = 25.0;

    @Override
    public void run() {
        if (!shooter.isOnline()) {
            die();
            return;
        }


        var oldPosition = position.clone();
        position.add(velocity.clone().multiply(VELOCITY));
        // spawn a particle every PARTICLE_SEPARATION blocks between oldPosition and position
        var distance = position.distance(oldPosition);
        var direction = position.clone().subtract(oldPosition).normalize();
        var numParticles = (int) (distance / PARTICLE_SEPARATION);
        for (int i = 0; i < numParticles; i++) {
            var particlePos = oldPosition.clone().add(direction.clone().multiply(i * PARTICLE_SEPARATION));
            world.spawnParticle(
                    Particle.DUST,
                    particlePos.getX(),
                    particlePos.getY(),
                    particlePos.getZ(),
                    4,
                    0.0f,
                    0.0f,
                    0.0f,
                    1.0f,
                    IN_FLIGHT_PARTICLE,
                    true
            );
        }

        // calculate distance to target
        if (currentTarget == null) {
            die();
            return;
        }
        var targetPos = currentTarget.getLocation().toVector()
                .add(new Vector(0, currentTarget.getHeight() / 2, 0));
        distance = targetPos.distanceSquared(position);
        if (distance <= VELOCITY_SQUARED) {
            entityCollision(currentTarget);
            ticksSinceLastHit = 0;
        } else {
            // rotate the velocity vector towards the target
            // maximum rotation of MAX_CHANGE_RADIANS

            velocity = rotateVectorTowards(velocity, targetPos.subtract(position).normalize(), MAX_TURN_RADIUS_DEGREES + (ticksSinceLastHit * 2));
            ticksSinceLastHit += 1;
        }

    }


    /**
     * Rotates a vector towards another vector by up to a specific maximum angle in degrees.
     *
     * @param from      The original vector (the one to be rotated).
     * @param to        The target vector (the direction we want to rotate towards).
     * @param maxAngle  The maximum angle in degrees we are allowed to rotate.
     * @return The rotated vector.
     */
    public static Vector rotateVectorTowards(Vector from, Vector to, double maxAngle) {
        // Normalize the input vectors
        from = from.clone().normalize();
        to = to.clone().normalize();

        // Calculate the angle between the two vectors in degrees
        double angle = Math.toDegrees(from.angle(to));

        // If the angle between them is less than or equal to maxAngle, no rotation is needed
        if (angle <= maxAngle) {
            return to.clone();
        }

        // Otherwise, we need to rotate the "from" vector towards the "to" vector
        double angleToRotate = Math.toRadians(maxAngle); // Convert maxAngle to radians
        Vector axis = from.clone().crossProduct(to).normalize(); // Axis of rotation (cross product of "from" and "to")

        // Calculate the rotation quaternion (Rodrigues' rotation formula)
        // Using the formula: v_rot = v * cos(θ) + (k x v) * sin(θ) + k * (k . v) * (1 - cos(θ))
        // Where:
        //   v is the vector to rotate
        //   k is the unit vector along the axis of rotation
        //   θ is the angle to rotate
        Vector rotated = from.clone().multiply(Math.cos(angleToRotate)) // v * cos(θ)
                .add(axis.clone().crossProduct(from).multiply(Math.sin(angleToRotate))) // (k x v) * sin(θ)
                .add(axis.clone().multiply(axis.clone().dot(from) * (1 - Math.cos(angleToRotate)))); // k * (k . v) * (1 - cos(θ))

        return rotated.normalize(); // Normalize the resulting vector
    }

    static final double DAMAGE = 52.0;

    void entityCollision(LivingEntity entity) {
        if (entity == shooter) {
            if (!shooter.getInventory().addItem(returnStack).isEmpty()) {
                shooter.getWorld().dropItemNaturally(shooter.getLocation(), returnStack);
            } else {
                shooter.playSound(
                        shooter.getLocation(),
                        Sound.ENTITY_ITEM_PICKUP,
                        1.0f,
                        1.0f
                );
            }
            die();
            return;
        }
        currentTarget.damage(DAMAGE, shooter);
        world.spawnParticle(
                Particle.FLASH,
                entity.getLocation().add(0, entity.getHeight() / 2, 0),
                70,
                0.4,
                0.4,
                0.4,
                0.0005,
                null,
                true
        );
        world.playSound(
                entity.getLocation(),
                Sound.ENTITY_FIREWORK_ROCKET_BLAST,
                4.0f,
                1
        );
        if (targets.isEmpty()) {
            currentTarget = shooter;
        } else {
            currentTarget = targets.poll();
        }
    }

    void die() {
        Bukkit.getScheduler().cancelTask(this.jobId);
        // tell player average time
    }

    double magnitudeSquared(Vector v) {
        return v.getX() * v.getX() + v.getY() * v.getY() + v.getZ() * v.getZ();
    }
}
