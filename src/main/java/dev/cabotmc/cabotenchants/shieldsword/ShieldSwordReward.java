package dev.cabotmc.cabotenchants.shieldsword;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import dev.cabotmc.cabotenchants.quest.QuestStep;
import dev.cabotmc.cabotenchants.util.Models;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

public class ShieldSwordReward extends QuestStep {
    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.NETHERITE_SWORD);

        i.editMeta(meta -> {
           meta.setItemModel(Models.AURORA_SWORD);

           meta.setEnchantmentGlintOverride(false);
           meta.setUnbreakable(true);
           meta.displayName(
                   MiniMessage
                           .miniMessage().deserialize("<!i><rainbow>Aura Sword")
           );

           meta.addEnchant(Enchantment.SHARPNESS, 5, true);
        });


        return i;
    }

    private HashMap<UUID, Integer> blockingTicksRemaining = new HashMap<>();

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (isStepItem(event.getPlayer().getInventory().getItemInMainHand())) {
                blockingTicksRemaining.put(event.getPlayer().getUniqueId(), 8);
            }
        }
    }

    private static final float BOUNCE_IMPULSE_STRENGTH = 1f;

    @EventHandler
    public void damage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player player) {
            if (blockingTicksRemaining.containsKey(player.getUniqueId())) {
                if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
                    e.setCancelled(true);
                    player.playSound(
                            player.getLocation(),
                            Sound.ITEM_SHIELD_BLOCK,
                            1f,
                            1f
                    );
                }
            }
        }
    }

    @EventHandler
    public void tick(ServerTickStartEvent e) {
        List<UUID> toRemove = new ArrayList<>();
        for (UUID uuid : blockingTicksRemaining.keySet()) {
            if (Bukkit.getPlayer(uuid) == null) {
                toRemove.add(uuid);
            } else if (blockingTicksRemaining.get(uuid) <= 0) {
                toRemove.add(uuid);
            }
        }

        for (var remove : toRemove) {
            blockingTicksRemaining.remove(remove);
        }

        // decrease all by 1
        for (var uuid : blockingTicksRemaining.keySet()) {
            blockingTicksRemaining.put(uuid, blockingTicksRemaining.get(uuid) - 1);
            Player player = Bukkit.getPlayer(uuid);
            animate(player, e.getTickNumber());
            var collisions = getShieldIntersectingEntities(player);
            for (var ent: collisions) {
                if (ent instanceof Player) {
                    continue;
                }

                var dir = ent.getLocation()
                                .toVector()
                                        .subtract(player.getLocation().toVector())
                                                .normalize()
                                                        .multiply(BOUNCE_IMPULSE_STRENGTH)
                                                                .add(new Vector(0, 0.5, 0));

                ent.setVelocity(
                    dir
                );
            }
        }

    }

    private static final double SHIELD_HEIGHT_OFFSET = 1.3; // Blocks above player's feet
    private static final double SHIELD_WIDTH = 5.0;
    private static final double SHIELD_HEIGHT = 2.5;
    private static final double SHIELD_DISTANCE = 1.5;
    private static final double HEX_RADIUS = 0.5;

    private void animate(Player target, int tickNumber) {
        // Get the player's location and horizontal direction
        Location playerLoc = target.getLocation();
        Vector direction = playerLoc.getDirection();
        direction.setY(0);
        direction.normalize();

        // Hexagon grid parameters
        double hexWidth = HEX_RADIUS * Math.sqrt(3);
        double hexHeight = HEX_RADIUS * 2;

        // Calculate shield center
        Vector perpendicular = new Vector(-direction.getZ(), 0, direction.getX()).normalize();
        Location shieldCenter = playerLoc.clone();
        shieldCenter.add(0, SHIELD_HEIGHT_OFFSET, 0);
        shieldCenter.add(direction.clone().multiply(SHIELD_DISTANCE));

        // Animation parameters
        double animationSpeed = 0.15;
        double waveProgress = (tickNumber * animationSpeed) % (Math.PI * 2);

        // Create hexagonal grid points
        for (double row = -SHIELD_HEIGHT/2; row <= SHIELD_HEIGHT/2; row += hexHeight * 0.75) {
            double rowOffset = (Math.round(row / (hexHeight * 0.75)) % 2) * (hexWidth / 2);

            for (double col = -SHIELD_WIDTH/2; col <= SHIELD_WIDTH/2; col += hexWidth) {
                Location hexCenter = shieldCenter.clone()
                        .add(perpendicular.clone().multiply(col + rowOffset))
                        .add(0, row, 0);

                // Calculate distance from center for wave effect
                double distFromCenter = Math.sqrt(
                        Math.pow(col + rowOffset, 2) +
                                Math.pow(row, 2)
                );

                // Modified wave effect - ensure particles appear at center
                double particleWavePhase = (distFromCenter - waveProgress) % (Math.PI * 2);
                // New condition that ensures particles appear at center (when distFromCenter is near 0)
                if (distFromCenter < 0.2 || Math.abs(Math.sin(particleWavePhase)) > 0.7) {
                    // Calculate color based on distance and phase
                    int brightness = 150 + (int)(50 * Math.sin(particleWavePhase));
                    Color particleColor = Color.fromRGB(
                            100,
                            brightness,
                            255
                    );

                    // Draw hexagon vertices
                    for (int vertex = 0; vertex < 6; vertex++) {
                        double angle = vertex * Math.PI / 3;
                        Location particleLoc = hexCenter.clone().add(
                                perpendicular.clone().multiply(Math.cos(angle) * HEX_RADIUS)
                                        .add(new Vector(0, Math.sin(angle) * HEX_RADIUS, 0))
                        );

                        // Only spawn if within shield bounds
                        if (Math.abs(particleLoc.getY() - shieldCenter.getY()) <= SHIELD_HEIGHT/2 &&
                                Math.abs(new Vector(
                                        particleLoc.getX() - shieldCenter.getX(),
                                        0,
                                        particleLoc.getZ() - shieldCenter.getZ()
                                ).length() - SHIELD_DISTANCE) <= 0.5) {

                            target.getWorld().spawnParticle(
                                    Particle.DUST,
                                    particleLoc,
                                    1,
                                    0, 0, 0,
                                    new Particle.DustOptions(particleColor, 0.7f)
                            );
                        }
                    }
                }
            }
        }

        drawEdges(target, shieldCenter, direction, perpendicular, SHIELD_WIDTH, SHIELD_HEIGHT);
    }

    private void drawEdges(Player target, Location center, Vector direction, Vector perpendicular,
                           double width, double height) {
        Color edgeColor = Color.fromRGB(180, 200, 255);
        double particleSpacing = 0.3;

        // Top and bottom edges
        for (double w = -width/2; w <= width/2; w += particleSpacing) {
            for (int edge = 0; edge <= 1; edge++) {
                if (Math.random() < 0.3) {
                    Location edgeLoc = center.clone()
                            .add(perpendicular.clone().multiply(w))
                            .add(0, edge * height - height/2, 0);

                    target.getWorld().spawnParticle(
                            Particle.DUST,
                            edgeLoc,
                            1,
                            0, 0, 0,
                            new Particle.DustOptions(edgeColor, 0.5f)
                    );
                }
            }
        }

        // Left and right edges
        for (double h = 0; h <= height; h += particleSpacing) {
            for (double side : new double[]{-width/2, width/2}) {
                if (Math.random() < 0.3) {
                    Location edgeLoc = center.clone()
                            .add(perpendicular.clone().multiply(side))
                            .add(0, h - height/2, 0);

                    target.getWorld().spawnParticle(
                            Particle.DUST,
                            edgeLoc,
                            1,
                            0, 0, 0,
                            new Particle.DustOptions(edgeColor, 0.5f)
                    );
                }
            }
        }
    }


    /**
     * Gets all entities intersecting with the shield's area.
     * For projectiles, performs ray tracing to account for high velocities.
     *
     * @param player The player whose shield to check
     * @return List of entities intersecting the shield
     */
    private List<Entity> getShieldIntersectingEntities(Player player) {
        List<Entity> intersectingEntities = new ArrayList<>();

        // Get shield position
        Location playerLoc = player.getLocation();
        Vector direction = playerLoc.getDirection();
        direction.setY(0);
        direction.normalize();

        // Calculate shield center
        Vector perpendicular = new Vector(-direction.getZ(), 0, direction.getX()).normalize();
        Location shieldCenter = playerLoc.clone()
                .add(0, SHIELD_HEIGHT_OFFSET, 0)
                .add(direction.clone().multiply(SHIELD_DISTANCE));

        // Define shield box (slightly larger than visual shield)
        double boxWidth = SHIELD_WIDTH * 1.1;  // 10% larger than visual
        double boxHeight = SHIELD_HEIGHT * 1.1;
        double boxDepth = 0.5; // Thickness of collision box

        // Get potential entities in a slightly larger area
        double searchRadius = Math.max(boxWidth, boxHeight);
        Collection<Entity> nearbyEntities = player.getWorld().getNearbyEntities(
                shieldCenter,
                searchRadius,
                searchRadius,
                searchRadius,
                entity -> entity != player  // Exclude the shield owner
        );

        // For each nearby entity, check precise intersection
        for (Entity entity : nearbyEntities) {
            boolean intersects = false;

            if (entity instanceof Projectile) {
                Projectile projectile = (Projectile) entity;
                Vector velocity = projectile.getVelocity();

                // Calculate potential positions between ticks for fast projectiles
                Location projLoc = projectile.getLocation();
                Vector normalizedVel = velocity.clone().normalize();
                double speed = velocity.length();

                // Check multiple points along projectile path
                int checkPoints = Math.max(1, (int)(speed / 2)); // More checks for faster projectiles
                double stepSize = speed / checkPoints;

                for (int i = 0; i < checkPoints && !intersects; i++) {
                    Location checkLoc = projLoc.clone().add(
                            normalizedVel.clone().multiply(stepSize * i)
                    );
                    intersects = isPointInShieldBox(checkLoc, shieldCenter, direction, perpendicular,
                            boxWidth, boxHeight, boxDepth);
                }
            } else {
                // For non-projectiles, just check their current position
                intersects = isPointInShieldBox(entity.getLocation(), shieldCenter, direction,
                        perpendicular, boxWidth, boxHeight, boxDepth);

                // For larger entities, also check their bounding box corners
                if (entity instanceof LivingEntity) {
                    BoundingBox bbox = entity.getBoundingBox();
                    double entityWidth = bbox.getWidthX();
                    double entityHeight = bbox.getHeight();
                    double entityDepth = bbox.getWidthZ();

                    // Check corners of the entity's bounding box
                    for (double x : new double[]{-entityWidth/2, entityWidth/2}) {
                        for (double y : new double[]{0, entityHeight}) {
                            for (double z : new double[]{-entityDepth/2, entityDepth/2}) {
                                Location cornerLoc = entity.getLocation().clone().add(x, y, z);
                                if (isPointInShieldBox(cornerLoc, shieldCenter, direction,
                                        perpendicular, boxWidth, boxHeight, boxDepth)) {
                                    intersects = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            if (intersects) {
                intersectingEntities.add(entity);
            }
        }

        return intersectingEntities;
    }

    /**
     * Checks if a point is within the shield's collision box
     */
    private boolean isPointInShieldBox(Location point, Location shieldCenter,
                                       Vector direction, Vector perpendicular,
                                       double boxWidth, double boxHeight, double boxDepth) {
        // Convert point to local shield coordinates
        Vector toPoint = point.toVector().subtract(shieldCenter.toVector());

        // Project onto shield axes
        double forwardDist = toPoint.dot(direction);
        double sideDist = toPoint.dot(perpendicular);
        double vertDist = point.getY() - shieldCenter.getY();

        // Check if point is within shield box bounds
        return Math.abs(forwardDist) <= boxDepth/2 &&
                Math.abs(sideDist) <= boxWidth/2 &&
                Math.abs(vertDist) <= boxHeight/2;
    }
}
