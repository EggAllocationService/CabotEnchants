package dev.cabotmc.cabotenchants.util;

import net.kyori.adventure.resource.ResourcePackInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.UUID;

public class ResourcepackSender implements Listener {
    static final UUID MAIN_ID = UUID.fromString("d4db0f44-c3d8-4c24-8502-26255bf3d302");
    static final UUID RODDY_ID = UUID.fromString("309d59f3-dcb4-461f-af6c-ac0c34484e32");

    static final ResourcePackInfo MAIN = ResourcePackInfo.resourcePackInfo
            (MAIN_ID, URI.create("https://objects.cabotmc.dev/dh_4_0.zip"), "D38D82BAB21B21E1B50D81D7FFA55DAB5D7D162C");

    static final ResourcePackInfo RODDY = ResourcePackInfo.resourcePackInfo
            (RODDY_ID, URI.create("https://objects.cabotmc.dev/roddy_ricch_1.zip"), "F550D7821CC86632992DD3F4A56B41FDB9711C0D");


    @EventHandler
    public void join(PlayerJoinEvent e) {
        var conn = ((CraftPlayer) e.getPlayer()).getHandle().connection;
        conn.send(new ClientboundResourcePackPushPacket(MAIN.id(), MAIN.uri().toString(), MAIN.hash(), true, Component.literal("(Required) Core Department Head resource pack")));
        conn.send(new ClientboundResourcePackPushPacket(RODDY.id(), RODDY.uri().toString(), RODDY.hash(), false, Component.literal("(Optional) Roddy Ricch Music Disks")));
    }

    @EventHandler
    public void recv(PlayerResourcePackStatusEvent e) {
        if (!e.getID().equals(MAIN_ID)) return;
        if (e.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED
                || e.getStatus() == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
            e.getPlayer().kick(net.kyori.adventure.text.Component.text("You must accept the resource pack to play on this server."));
        }
    }
}
