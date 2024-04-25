package dev.cabotmc.cabotenchants.boss.traits;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import dev.cabotmc.cabotenchants.boss.KyleFight;
import dev.cabotmc.cabotenchants.spawner.AwakenedSouldrinkerReward;
import net.citizensnpcs.api.event.EntityTargetNPCEvent;
import net.citizensnpcs.api.event.NPCDamageEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCKnockbackEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.eclipse.sisu.Priority;

@TraitName("cabot_boss")
public class BossTrait extends Trait{

    private int maxHealth;
    public BossTrait() {
        super("cabot_boss");
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    @Override
    public void onSpawn() {
        var ent = (Player) getNPC().getEntity();


        System.out.println("Spawned");
        ent.getAttribute(
                org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH
        ).setBaseValue(maxHealth);
        ent.setHealth(maxHealth);
        ent.setGravity(false);
        ent.setInvulnerable(false);
        getNPC().data()
                .setPersistent(NPC.Metadata.DEFAULT_PROTECTED, false);
    }

    @EventHandler
    public void death(NPCDeathEvent e) {
        if (!e.getNPC().equals(getNPC())) return;

        e.getDrops().clear();

        var ent = e.getNPC().getEntity();

        var i = (Item) ent.getWorld().spawnEntity(ent.getLocation(), EntityType.DROPPED_ITEM);
        i.setItemStack(new ItemStack(Material.NETHER_STAR));
        // needed for some reason, client bug maybe?
        i.teleport(new Location(ent.getWorld(), ent.getLocation().getX(), 65, ent.getLocation().getZ()));
        i.setVelocity(new Vector(0, 0, 0));
        i.setGravity(false);

        KyleFight.healthBar.progress(0f);
        KyleFight.safe = true;
        Bukkit.broadcast(Component.text("DONE"));
    }



    @EventHandler
    public void target(EntityTargetNPCEvent e) {
        if (e.getEntity().getType() != EntityType.WOLF) {
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void damage(EntityDamageEvent e) {
        if (!e.getEntity().equals(getNPC().getEntity())) return;
        if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            e.setCancelled(true);
            return;
        }
        if (!(e.getDamageSource().getCausingEntity() instanceof Player)) {
            e.setCancelled(true);
            return;
        }
        e.setCancelled(false);
        var ent = (Player) getNPC().getEntity();

        var newHealthPercent = (ent.getHealth() - e.getDamage()) / ent.getAttribute(
                org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH
        ).getBaseValue();
        var oldHealthPercent = ent.getHealth() / ent.getAttribute(
                org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH
        ).getBaseValue();
        KyleFight.healthBar.progress(Math.max((float) newHealthPercent, 0f));

        if (Math.random() < 0.5) {
            var offsetX = Math.random() * 20 - 10;
            var offsetZ = Math.random() * 20 - 10;

            // make sure offsetX and offsetZ are not between -5 and 5
            if (offsetX > -5 && offsetX < 5) {
                offsetX += 5 * Integer.signum((int) offsetX);
            }
            if (offsetZ > -5 && offsetZ < 5) {
                offsetZ += 5 * Integer.signum((int) offsetZ);
            }
            ent.teleport(ent.getLocation().add(offsetX, 0, offsetZ));
        }
        if (Math.random() < 0.25) {
            spawnSeekers();
        }

        if (newHealthPercent < 0.5 && oldHealthPercent >= 0.5) {
            // spawn a warden
            // random location minimum 5 blocks from the boss
            var offsetX = Math.random() * 5 + 5;
            var offsetZ = Math.random() * 5 + 5;
            if (Math.random() < 0.5) {
                offsetX *= -1;
            }
            if (Math.random() < 0.5) {
                offsetZ *= -1;
            }
            var loc = ent.getLocation().add(offsetX, 0, offsetZ);
            var warden = (Warden) ent.getWorld().spawnEntity(loc, EntityType.WARDEN);
            warden.getPersistentDataContainer().set(AwakenedSouldrinkerReward.NO_INSTAKILL_KEY, PersistentDataType.BYTE, (byte) 1);
            warden.setPose(Pose.EMERGING);
            ent.getWorld().strikeLightningEffect(loc);
        }


    }

    @EventHandler
    public void knockback(NPCKnockbackEvent e) {
        if (!e.getNPC().equals(getNPC())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void tick(ServerTickEndEvent e) {
        if (!getNPC().isSpawned()) return;
        if (getNPC().getEntity().isDead()) return;

        if (Math.random() < 0.5 && e.getTickNumber() % 100 == 0) {
            spawnSeekers();
        }
    }


    void spawnSeekers() {
        getNPC().getEntity().getNearbyEntities(30, 30, 30)
                .stream()
                .filter(ent -> ent instanceof Player)
                .map(ent -> (Player) ent)
                .filter(ent -> !ent.isDead())
                .filter(ent -> !ent.hasMetadata("NPC"))
                .filter(ent -> ent.getLocation().distanceSquared(getNPC().getEntity().getLocation()) > 4)
                .limit(4)
                .map(ent -> ent.getLocation().add(0, 1,0))
                .forEach(loc -> {
                    var seeker = new BossSeeker(
                            getNPC().getEntity().getLocation().add(0, 1, 0),
                            loc
                    );
                    seeker.cancelToken = Bukkit.getScheduler()
                            .scheduleSyncRepeatingTask(
                                    Bukkit.getPluginManager().getPlugin("CabotEnchants"),
                                    seeker,
                                    1,
                                    1
                            );
                });
    }
    void spawnVexes() {
        var center = getNPC().getEntity().getLocation().add(0, 1, 0);
        var count = (int) (Math.random() * 6 + 4);
        for (int i = 0; i < count; i++) {
            var offsetX = Math.random() * 10 - 5;
            var offsetZ = Math.random() * 10 - 5;
            var loc = center.add(offsetX, 0, offsetZ);
            var vex = (Vex) center.getWorld().spawnEntity(loc, EntityType.VEX);
        }
    }
}
