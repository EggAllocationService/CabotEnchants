package dev.cabotmc.cabotenchants.boss;

import dev.cabotmc.cabotenchants.CabotEnchants;
import dev.cabotmc.cabotenchants.boss.traits.BossTrait;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.block.EndGateway;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class KyleFight {
    public static BossBar healthBar = BossBar.bossBar(
            net.kyori.adventure.text.Component.text("ThatOneGamer999"),
            0,
            BossBar.Color.RED,
            BossBar.Overlay.PROGRESS
    );

    static NPC boss = null;

    public static boolean safe = false;

    public static Location getDarknessSafePoint() {
        if (safe) {
            return null;
        }
        if (boss == null || !boss.isSpawned()) {
            return new Location(Bukkit.getWorld(RiftWorldListener.RIFT_WORLD), 0, 65, 0);
        }

        return boss.getEntity().getLocation();
    }

    public static void prepareFight(List<Player> players) {
        if (boss != null) {
            throw new IllegalStateException("Fight already started");
        }
        if (players.isEmpty()) {
            throw new IllegalArgumentException("No players");
        }

        safe = false;

        boss = CabotEnchants.npcRegistry.createNPC(EntityType.PLAYER, "ThatOneGamer999");
        boss.getOrAddTrait(SkinTrait.class)
                .setSkinName("EggAllocationSrv");


        boss.getOrAddTrait(BossTrait.class)
                .setMaxHealth((250 * players.size()) + 150);

        var world = Bukkit.getWorld(RiftWorldListener.RIFT_WORLD);
        var gateway = Bukkit.createBlockData(Material.END_GATEWAY);
        var gatewayLoc = new Location(world, 8, 65, 0);
        world.setBlockData(
                gatewayLoc,
                gateway
        );

        var gatewayState = (EndGateway) gatewayLoc.getBlock()
                .getState();
        gatewayState.setAge(-99999999);
        gatewayState.update(true);


        Directional button = (Directional) Bukkit.createBlockData(Material.OAK_BUTTON);
        button.setFacing(BlockFace.WEST);
        world.setBlockData(
                new Location(world, 7, 65, 0),
                button
        );
        world.getEntities()
                .stream()
                .filter(e -> e.getType() != EntityType.PLAYER)
                .forEach(Entity::remove);

        for (var p : players) {
            p.teleport(
                    new Location(world, 0, 65, 0)
            );
        }
    }

    public static void preStart() {
        var world = Bukkit.getWorld(RiftWorldListener.RIFT_WORLD);
        Bukkit.getScheduler()
                .scheduleSyncDelayedTask(
                        CabotEnchants.getPlugin(CabotEnchants.class),
                        () -> {
                            world.sendMessage(
                                    Component.text(
                                            "<ThatOneGamer999> What are these monkeys doing here?"
                                    )
                            );
                        },
                        20
                );
        Bukkit.getScheduler()
                .scheduleSyncDelayedTask(
                        CabotEnchants.getPlugin(CabotEnchants.class),
                        () -> {
                            world.sendMessage(
                                    Component.text(
                                            "<ThatOneGamer999> Get out."
                                    )
                            );
                        }, 20 * 4
                );
        Bukkit.getScheduler()
                .scheduleSyncDelayedTask(
                        CabotEnchants.getPlugin(CabotEnchants.class),
                        KyleFight::healthAnimation,
                        20 * 8
                );
    }

    static int progress = 0;
    static int cancelToken = 0;
    public static void healthAnimation() {
        progress = 0;

        for (var p : Bukkit.getWorld(RiftWorldListener.RIFT_WORLD).getPlayers()) {
            p.showBossBar(healthBar);
        }

        cancelToken = Bukkit.getScheduler()
                .scheduleSyncRepeatingTask(
                        CabotEnchants.getPlugin(CabotEnchants.class),
                        () -> {
                            healthBar.progress(progress / 100f);
                            progress++;
                            if (progress > 100) {
                                Bukkit.getScheduler().cancelTask(cancelToken);
                                start();
                            }
                        },
                        1,
                        1
                );
    }

    public static void start() {
        if (boss == null) {
            throw new IllegalStateException("Fight not prepared");
        }

        boss.spawn(
                new Location(
                        Bukkit.getWorld(RiftWorldListener.RIFT_WORLD),
                        0, 64, 0
                )
        );
        Bukkit.getWorld(RiftWorldListener.RIFT_WORLD)
                .playSound(
                        boss.getEntity().getLocation(),
                        Sound.ENTITY_GENERIC_EXPLODE,
                        1,
                        1
                );
    }


    public static void reset() {
        if (boss != null) {
            boss.destroy();
            boss = null;
        }
        safe = false;
        for (var p : Bukkit.getWorld(RiftWorldListener.RIFT_WORLD).getPlayers()) {
            p.teleport(new Location(Bukkit.getWorld("world"), 0, 65, 0));
            p.hideBossBar(healthBar);
        }

        for (var e : Bukkit.getWorld(RiftWorldListener.RIFT_WORLD).getEntities()) {
            if (e.getType() != EntityType.PLAYER) {
                e.remove();
            }
        }
    }
}
