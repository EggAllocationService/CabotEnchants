package dev.cabotmc.cabotenchants.boss;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import dev.cabotmc.cabotenchants.CabotEnchants;
import dev.cabotmc.cabotenchants.boss.traits.BossTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RiftWorldListener implements Listener {

    public static final NamespacedKey RIFT_WORLD = new NamespacedKey("cabot", "rift");
    @EventHandler
    public void started(ServerLoadEvent e) {
        if (e.getType() == ServerLoadEvent.LoadType.STARTUP) {
            Bukkit.createWorld(
                    new WorldCreator(RIFT_WORLD)
                            .generator( new NetheriteFlatGenerator())
                            .biomeProvider(new NetheriteFlatGenerator.NetheriteBiomeProvider())
                            .environment(World.Environment.NETHER)
            ).setAutoSave(false);
            CitizensAPI.getTraitFactory()
                    .registerTrait(TraitInfo.create(BossTrait.class));
            CabotEnchants.npcRegistry = CitizensAPI.createInMemoryNPCRegistry("cabot");
        }
    }
    @EventHandler
    public void save(ChunkUnloadEvent e) {
        if (e.getWorld().getKey().equals(new NamespacedKey("cabot", "rift"))) {
            e.setSaveChunk(false);
        }
    }

    @EventHandler
    public void changeWorld(EntityAddToWorldEvent e) {
        if (e.getWorld().getKey().equals(RIFT_WORLD)) {
            if (e.getEntity() instanceof Player && !e.getEntity().hasMetadata("NPC")) {
                var p = (Player) e.getEntity();
                p.setGameMode(GameMode.ADVENTURE);
                p.addPotionEffect(new PotionEffect(
                        PotionEffectType.NIGHT_VISION,
                        PotionEffect.INFINITE_DURATION,
                        0,
                        true,
                        false,
                        false
                ));
            }
        }
    }

    @EventHandler
    public void remove(EntityRemoveFromWorldEvent e) {
        if (e.getWorld().getKey().equals(RIFT_WORLD)) {
            if (e.getEntity() instanceof Player && !e.getEntity().hasMetadata("NPC")) {
                var p = (Player) e.getEntity();
                p.setGameMode(GameMode.SURVIVAL);
                p.hideBossBar(KyleFight.healthBar);
                p.removePotionEffect(PotionEffectType.NIGHT_VISION);
            }
        }
    }
    @EventHandler
    public void join(PlayerJoinEvent e) {
        if (e.getPlayer().getWorld().getKey().equals(RIFT_WORLD)) {
            ((CraftPlayer) e.getPlayer()).getHandle().kill();
        }
    }

    boolean debounce = false;
    @EventHandler
    public void interact(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        if (e.getClickedBlock().getType() != Material.OAK_BUTTON) return;
        if (!e.getPlayer().getWorld().getKey().equals(RIFT_WORLD)) return;
        if (debounce) return;
        debounce = true;
        Bukkit.getScheduler()
                .scheduleSyncDelayedTask(CabotEnchants.getPlugin(CabotEnchants.class), () -> {
                    var b = e.getClickedBlock();
                    b.setType(Material.AIR);
                    b.getLocation()
                            .add(1, 0, 0)
                            .getBlock()
                            .setType(Material.AIR);
                    debounce = false;
                    KyleFight.preStart();
                }, 80);
    }

    @EventHandler
    public void tick(ServerTickStartEvent e) {
        var world = Bukkit.getWorld(RIFT_WORLD);
        if (world == null) return;
        if (world.getPlayers().isEmpty()) return;
        var safePos = KyleFight.getDarknessSafePoint();
        if (safePos == null) return;
        for (var p : world.getPlayers()) {
            if (p.getLocation().distanceSquared(safePos) > 30 * 30) {
                p.addPotionEffect(
                        new PotionEffect(
                                PotionEffectType.WITHER,
                                40,
                                2,
                                false,
                                false,
                                false
                        )
                );
                // calculate launch vector to put them back in safe radius
                var vec = safePos.toVector().subtract(p.getLocation().toVector());
                vec.setY(0);
                vec.normalize();
                vec.setY(0.8);
                p.setVelocity(vec);
            }
        }
    }
    @EventHandler
    public void command(PlayerCommandPreprocessEvent e) {
        if (e.getPlayer().getWorld().getKey().equals(RIFT_WORLD) && !KyleFight.safe) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(
                    Component.text("A nearby force blocks your command")
                            .color(NamedTextColor.RED)
            );
        }
    }

    @EventHandler
    public void pickup(PlayerAttemptPickupItemEvent e) {
        if (!e.getPlayer().getWorld().getKey().equals(RIFT_WORLD)) return;
        if (!e.getPlayer().canSee(e.getItem())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void potion(EntityPotionEffectEvent e) {
        if (e.getEntity().getWorld().getKey().equals(RIFT_WORLD) && e.getCause() == EntityPotionEffectEvent.Cause.WARDEN) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void resetYLevel(ServerTickStartEvent e) {
        if (KyleFight.boss != null && KyleFight.boss.isSpawned() && !KyleFight.boss.getEntity().isDead() && e.getTickNumber() % 20 == 0) {
            var ent = KyleFight.boss.getEntity();
            ent.teleport(new Location(ent.getWorld(), ent.getLocation().getX(), 64, ent.getLocation().getZ()));
        }
    }

    @EventHandler
    public void failure(PlayerDeathEvent e) {
        if (e.getEntity().getWorld().getKey().equals(RIFT_WORLD)) {
            var num_alive = e.getEntity().getWorld()
                    .getPlayers()
                    .stream()
                    .filter(p -> !p.isDead())
                    .filter(p -> !p.hasMetadata("NPC"))
                    .count();
            if (num_alive == 0) {
                // last player died
                Bukkit.broadcast(
                        Component.text("The rift feeds on the souls of the fallen")
                                .color(NamedTextColor.DARK_RED)
                );
                Bukkit.getScheduler()
                        .scheduleSyncDelayedTask(CabotEnchants.getPlugin(CabotEnchants.class), KyleFight::reset, 20 * 5);
            }
        }
    }
}
