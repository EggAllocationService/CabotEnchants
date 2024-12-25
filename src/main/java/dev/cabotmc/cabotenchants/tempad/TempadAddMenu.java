package dev.cabotmc.cabotenchants.tempad;

import dev.cabotmc.cabotenchants.CabotEnchants;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventoryAnvil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.view.AnvilView;

public class TempadAddMenu implements Listener {
    static final Component MENU_TITLE = Component.text("4a3")
            .font(Key.key("cabot", "tempad"))
            .color(NamedTextColor.WHITE)
            .append(
                    Component.text("Add a location")
                            .color(NamedTextColor.DARK_GRAY)
                            .font(Key.key("minecraft", "default"))
            );

    public static TempadAddMenu open(Player target) {
        var m = new TempadAddMenu(target);
        target.openInventory(m.i);
        Bukkit.getPluginManager().registerEvents(m, CabotEnchants.instance);
        return m;
    }


    Player p;
    Inventory i;

    public TempadAddMenu(Player p) {
        this.p = p;
        i = p.getServer().createInventory(null, InventoryType.ANVIL, MENU_TITLE);
        render();
    }

    @EventHandler
    public void close(InventoryCloseEvent e) {
        if (e.getInventory() == i) {
            HandlerList.unregisterAll(this);
        }
    }

    void render() {

    }
}
