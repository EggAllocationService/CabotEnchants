package dev.cabotmc.cabotenchants.railgun;

import dev.cabotmc.cabotenchants.CabotEnchants;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import org.bukkit.*;
import org.bukkit.block.data.Rail;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;

public class RailgunListener implements Listener {
    public RailgunListener() {
        RAILGUN = Enchantment.getByKey(new NamespacedKey("cabot", "railgun"));
    }

    public static Enchantment RAILGUN;

    @EventHandler
    public void shoot(EntityShootBowEvent e) {
        if (e.getBow().getType() != Material.CROSSBOW || e.getEntityType() != EntityType.PLAYER) return;

        var p = (org.bukkit.entity.Player) e.getEntity();
        var item = e.getBow();
        if (e.getBow().getEnchantments().containsKey(RAILGUN)) {
            fireRailgun(p, e.getProjectile());
        }
    }

    final static float RAILGUN_DAMAGE = 18.0f;
    final static float RAILGUN_STEP_DISTANCE = 0.2f;
    final static int RAILGUN_STEP_COUNT = 100;

    void fireRailgun(Player p, Entity arr) {
        var look = p.getLocation().getDirection().clone();
        look.multiply(-1);
        look.normalize();
        p.setVelocity(look);
        var lookVector = p.getLocation().getDirection().clone().normalize();
        var baseLoc = p.getEyeLocation().toVector();
        var lh = p.rayTraceBlocks(40.0d);
        Vector lookHit;
        if (lh == null) {
            lookHit = baseLoc.clone().add(
                    p.getLocation().getDirection().normalize().multiply(40)
            );
        } else {
            lookHit = lh.getHitPosition();
        }

        var max = lookHit.distance(p.getLocation().toVector());

        var opts = new Particle.DustOptions(Color.fromRGB(50, 157, 168), 0.5f);
        HashSet<Entity> hitTargets = new HashSet<>();
        boolean wasInBlock = false;
        for (double distanceMultipler = 0.0d; distanceMultipler < max; distanceMultipler += 0.1d) {
            var curr = baseLoc.clone().add(
                    lookVector.clone().multiply(distanceMultipler)
            );
            var loc = curr.toLocation(p.getWorld());
            p.getWorld().spawnParticle(
                    Particle.REDSTONE,
                    loc,
                    3,
                    0.05d,
                    0.05d,
                    0.05d,
                    0.05d,
                    opts
            );
            if (loc.getBlock().getType().isSolid()) {
                // solid block
                if (!wasInBlock) {
                    // passing from solid block into air
                    p.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.MASTER, 0.7f, 1f);
                    p.getWorld().spawnParticle(Particle.FLASH,
                            loc,
                            10,
                            0.5d,
                            0.5d,
                            0.5d);
                    wasInBlock = true;
                }
            } else {
                // not solid
                if (wasInBlock) {
                    // passing from solid to non-solid
                    p.getWorld().spawnParticle(Particle.FLASH,
                            loc,
                            10,
                            0.5d,
                            0.5d,
                            0.5d);
                    wasInBlock = false;
                }
            }
            // check if theres a player there
            for (var possibleHit : p.getWorld().getEntities()) {
                if (possibleHit == p) continue;
                if (possibleHit.getBoundingBox().clone().expand(0.3d).contains(curr)) {
                    hitTargets.add(possibleHit);
                }
            }


            for (var possibleHit : p.getWorld().getEntities()) {
                if (possibleHit == p) continue;
                if (possibleHit.getBoundingBox().clone().expand(0.8d).contains(lookHit)) {
                    hitTargets.add(possibleHit);
                }
            }
        }
        for (var pp : Bukkit.getOnlinePlayers()) {
            pp.hideEntity(CabotEnchants.getProvidingPlugin(CabotEnchants.class), arr);
        }
        hitTargets.forEach(c -> {
            if (!(c instanceof LivingEntity)) return;
            var le = (LivingEntity) c;
            le.damage(RAILGUN_DAMAGE, p);
            p.getWorld().spawnParticle(Particle.FLASH,
                    c.getLocation(),
                    10,
                    0.5d,
                    0.5d,
                    0.5d);
        });
        arr.remove();
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.MASTER, 1f, 2f);

    }
}
