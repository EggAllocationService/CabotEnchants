package dev.cabotmc.cabotenchants.boss;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import dev.cabotmc.cabotenchants.CabotEnchants;
import dev.cabotmc.cabotenchants.boss.traits.BossTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
            }
        }
    }

    @EventHandler
    public void remove(EntityRemoveFromWorldEvent e) {
        if (e.getWorld().getKey().equals(RIFT_WORLD)) {
            if (e.getEntity() instanceof Player && !e.getEntity().hasMetadata("NPC")) {
                var p = (Player) e.getEntity();
                p.setGameMode(GameMode.SURVIVAL);
            }
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
                                PotionEffectType.BLINDNESS,
                                20,
                                0,
                                false,
                                false,
                                false
                        )
                );
                p.addPotionEffect(
                        new PotionEffect(
                                PotionEffectType.WITHER,
                                20,
                                2,
                                false,
                                false,
                                false
                        )
                );
            }
        }
    }
}
