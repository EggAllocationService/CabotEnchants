package dev.cabotmc.cabotenchants.util;

import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import java.net.URI;
import java.util.UUID;

public class ResourcepackSender implements Listener {
    static final UUID MAIN_ID = UUID.fromString("d4db0f44-c3d8-4c24-8502-26255bf3d302");
    static final UUID RODDY_ID = UUID.fromString("309d59f3-dcb4-461f-af6c-ac0c34484e32");

    static final ResourcePackInfo MAIN = ResourcePackInfo.resourcePackInfo
            (MAIN_ID, URI.create("https://objects.cabotmc.dev/dh_4_4.zip"), "fb31cd91943492c5bc5cd24fe14be0fd60ba9cc1");

    static final ResourcePackInfo RODDY = ResourcePackInfo.resourcePackInfo
            (RODDY_ID, URI.create("https://objects.cabotmc.dev/roddy_ricch_2.zip"), "ACAEBF0CBA7B7EF409930B75A140ABA44EF577D4");


    @EventHandler
    public void join(PlayerJoinEvent e) {
        e.getPlayer().sendResourcePacks(ResourcePackRequest.resourcePackRequest()
                .packs(MAIN)
                .required(true)
                .prompt(Component.text("(Required) Core DH resource pack"))
        );
        e.getPlayer().sendResourcePacks(ResourcePackRequest.resourcePackRequest()
                .packs(RODDY)
                .required(false)
                .prompt(Component.text("(Optional) Roddy Ricch Music Disks"))
        );

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
