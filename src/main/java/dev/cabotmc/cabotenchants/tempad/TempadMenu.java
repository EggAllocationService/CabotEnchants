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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class TempadMenu implements Listener {
    static final Component MENU_TITLE = Component.text("213")
            .font(Key.key("cabot", "tempad"))
            .color(NamedTextColor.WHITE)
            .append(
                    Component.text("Tempad")
                            .color(NamedTextColor.DARK_GRAY)
                            .font(Key.key("minecraft", "default"))
            );

    public static TempadMenu open(Player target) {
        var m = new TempadMenu(target);
        target.openInventory(m.i);
        Bukkit.getPluginManager().registerEvents(m, CabotEnchants.instance);
        return m;
    }


    Player p;
    Inventory i;
    public TempadMenu(Player p) {
        this.p = p;
        i = p.getServer().createInventory(null, 54, MENU_TITLE);
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


    private void sendTitle(Player p, Component title) {

        var serialized = JSONComponentSerializer.json().serialize(title);
        var nms = net.minecraft.network.chat.Component.Serializer.fromJson(serialized, MinecraftServer.getServer()
                .registryAccess());


        var player = ((CraftPlayer) p).getHandle();
        player.connection.send(
                new ClientboundOpenScreenPacket(player.containerMenu.containerId, player.containerMenu.getType(), nms)
        );

        ((CraftPlayer) p).updateInventory();
    }
}
