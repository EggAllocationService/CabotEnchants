package dev.cabotmc.cabotenchants.pocket;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.util.Random;

public class PocketListener implements Listener {
    public static final NamespacedKey POCKET_WORLD = new NamespacedKey("cabot", "pocket");

    @EventHandler
    public void dragon(PortalCreateEvent e) {
        if (e.getWorld().getKey().equals(POCKET_WORLD)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void entity(CreatureSpawnEvent e) {
        if (e.getLocation().getWorld().getKey().equals(POCKET_WORLD)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void use(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR && e.getPlayer().getItemInHand().getType() == Material.STICK) {
            try {
                var place = createPocketForPlayer(e.getPlayer());
                e.getPlayer().teleport(place);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private Location createPocketForPlayer(Player player) throws IOException {
        var existingCoords = player.getPersistentDataContainer().get(POCKET_WORLD, PersistentDataType.INTEGER_ARRAY);
        var world = Bukkit.getWorld(POCKET_WORLD);
        if (existingCoords != null) {
            return new Location(world, existingCoords[0], existingCoords[1], existingCoords[2]);
        }

        var random = new Random();
        var pos = new int[] {random.nextInt(10000000), 0, random.nextInt(1000000)};

        // load schematic
        var format = ClipboardFormats.findByInputStream(() -> this.getClass().getClassLoader().getResourceAsStream("test.schem"));
        var stream = this.getClass().getClassLoader().getResourceAsStream("test.schem");
        var clipboard = format.getReader(stream).read();

        BlockVector3 worldPos = new BlockVector3(pos[0], 2, pos[2]);

        var session = WorldEdit
                .getInstance()
                .newEditSession(BukkitAdapter.adapt(world));

        var operation = new ClipboardHolder(clipboard)
                .createPaste(session)
                .to(worldPos)
                .ignoreAirBlocks(true)
                .copyEntities(true)
                .build();

        try {
            Operations.complete(operation);
            session.close();
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }

        return new Location(world, pos[0], 3, pos[2]);
    }
}
