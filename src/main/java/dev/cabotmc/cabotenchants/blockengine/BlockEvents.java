package dev.cabotmc.cabotenchants.blockengine;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import dev.cabotmc.cabotenchants.CEBootstrap;
import dev.cabotmc.cabotenchants.CabotEnchants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldSaveEvent;

public class BlockEvents implements Listener {
    @EventHandler
    public void tick(ServerTickStartEvent e) {
        BlockEngine.tick();
    }

    @EventHandler
    public void interact(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) {
            return;
        }

        var customBlock = BlockEngine.getCustomBlock(e.getClickedBlock());
        if (customBlock != null) {
            customBlock.block.interact(e.getPlayer(), e.getAction());
        }
    }

    @EventHandler
    public void destroy(BlockBreakEvent e) {
        if (e.getBlock().getType() != Material.BARRIER) return;
        var customBlock = BlockEngine.getCustomBlock(e.getBlock());
        if (customBlock != null) {
            BlockEngine.breakBlock(e.getBlock());
        }
    }

    @EventHandler
    public void save(WorldSaveEvent e) {
        BlockEngine.saveWorld(e.getWorld(), false);
    }

    @EventHandler
    public void unload(ChunkUnloadEvent e) {
        if (e.isSaveChunk()) {
            BlockEngine.saveChunk(e.getChunk(), true);
        }
    }

    @EventHandler
    public void load(ChunkLoadEvent e) {
        BlockEngine.loadChunk(e.getChunk());
    }

    // DEBUG
    @EventHandler
    public void place(BlockPlaceEvent e) {
        if (e.getBlock().getType() == Material.STONE) {
            e.setCancelled(true);
            Bukkit.getServer()
                    .getScheduler()
                    .runTaskLater(CabotEnchants.getPlugin(CabotEnchants.class), () -> {
                        BlockEngine.placeBlock(e.getBlock().getLocation(), CEBootstrap.BLOCK_TEST);
                    }, 1);
        }
    }

}
