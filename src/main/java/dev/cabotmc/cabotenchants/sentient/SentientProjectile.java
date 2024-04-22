package dev.cabotmc.cabotenchants.sentient;

import io.papermc.paper.configuration.GlobalConfiguration;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Queue;

public class SentientProjectile implements Runnable{
  ItemStack returnStack;
  Vector velocity;
  Vector position;
  Queue<LivingEntity> targets;
  Player shooter;
  World world;

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
    this.targets = targets;
    this.shooter = shooter;
    currentTarget = targets.poll();
  }

  static final Particle.DustOptions IN_FLIGHT_PARTICLE = new Particle.DustOptions(Color.fromRGB(255, 0, 255), 2.0f);
    static final double PARTICLE_SEPARATION = 0.3f;
    static final double VELOCITY = 2.5;
    static final double VELOCITY_SQUARED = VELOCITY * VELOCITY;

  @Override
  public void run() {
    if (! shooter.isOnline()) {
      die();
      return;
    }


    var oldPosition = position.clone();
    position.add(velocity);
    // spawn a particle every PARTICLE_SEPARATION blocks between oldPosition and position
    var distance = position.distance(oldPosition);
    var direction = position.clone().subtract(oldPosition).normalize();
    var numParticles = (int) (distance / PARTICLE_SEPARATION);
    for (int i = 0; i < numParticles; i++) {
      var particlePos = oldPosition.clone().add(direction.clone().multiply(i * PARTICLE_SEPARATION));
      world.spawnParticle(
              Particle.REDSTONE,
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
    if (distance < VELOCITY_SQUARED) {
      entityCollision(currentTarget);
    } else {
        // rotate the velocity vector towards the target
        // maximum rotation of MAX_CHANGE_RADIANS
        velocity = targetPos.subtract(position).normalize()
                .multiply(VELOCITY);
    }

  }
  static final double DAMAGE = 12.0;
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
    } else  {
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
