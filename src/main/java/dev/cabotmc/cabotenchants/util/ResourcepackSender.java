package dev.cabotmc.cabotenchants.util;

import net.kyori.adventure.resource.ResourcePackInfo;
import net.minecraft.SharedConstants;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import java.net.URI;
import java.util.HexFormat;
import java.util.UUID;

public class ResourcepackSender implements Listener {
    static final UUID MAIN_ID = UUID.fromString("d4db0f44-c3d8-4c24-8502-26255bf3d302");
    static final UUID RODDY_ID = UUID.fromString("309d59f3-dcb4-461f-af6c-ac0c34484e32");

    static final ResourcePackInfo MAIN = ResourcePackInfo.resourcePackInfo
            (MAIN_ID, URI.create("https://objects.cabotmc.dev/dh_5_10.zip"), "6E855F79A7F35E2665598AF0CC845FA3B38C1979");

    static final ResourcePackInfo RODDY = ResourcePackInfo.resourcePackInfo
            (RODDY_ID, URI.create("https://objects.cabotmc.dev/roddy_ricch_2.zip"), "ACAEBF0CBA7B7EF409930B75A140ABA44EF577D4");


    @EventHandler
    public void join(PlayerJoinEvent e) {
       e.getPlayer().addResourcePack(MAIN_ID, MAIN.uri().toString(), HexFormat.of().parseHex(MAIN.hash()), "(Required) DH pack", true);

        e.getPlayer().setNoDamageTicks(40);
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
