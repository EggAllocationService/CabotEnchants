package dev.cabotmc.cabotenchants.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.UUID;

public class TitleHandler {

  private HashMap<UUID, InventoryPlayer> inventoryPlayers = new HashMap<>();

  private ProtocolManager protocolManager;
  private JavaPlugin plugin;

  public TitleHandler(JavaPlugin plugin, ProtocolManager protocolManager) {
    this.plugin = plugin;
    this.protocolManager = protocolManager;
  }

  // -- Listening to packets --

  public void registerPacketListeners() {
    protocolManager.addPacketListener(getOpenWindowPacketListener());
    protocolManager.addPacketListener(getCloseWindowPacketListener());
  }

  private PacketListener getOpenWindowPacketListener() {
    return new PacketAdapter(plugin, ListenerPriority.HIGH, PacketType.Play.Server.OPEN_WINDOW) {
      @Override
      public void onPacketSending(PacketEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();

        final int windowId = event.getPacket().getIntegers().read(0);
        final Object containerType = ((ClientboundOpenScreenPacket) event.getPacket().getHandle()).getType();

        // Create our custom holder object (defined at the end of this class) and put it in a HashMap
        InventoryPlayer player = new InventoryPlayer(windowId, containerType);
        inventoryPlayers.put(uuid, player);
      }
    };
  }

  private PacketListener getCloseWindowPacketListener() {
    return new PacketAdapter(plugin, ListenerPriority.HIGH, PacketType.Play.Client.CLOSE_WINDOW) {
      @Override
      public void onPacketReceiving(PacketEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();

        // Remove the player from logged inventories, because a menu has been closed.
        inventoryPlayers.remove(uuid);
      }
    };
  }


  // -- API --

  public void setPlayerInventoryTitle(Player player, Component title) {
    final InventoryType type = player.getOpenInventory().getType();
    if (type == InventoryType.CRAFTING || type == InventoryType.CREATIVE)
      return;

    InventoryPlayer inventoryPlayer = inventoryPlayers.getOrDefault(player.getUniqueId(), null);

    if (inventoryPlayer == null)
      return;

    final int windowId = inventoryPlayer.getWindowId();
    if (windowId == 0)
      return;

    final Object windowType = inventoryPlayer.getContainerType();
    final String titleJson = JSONComponentSerializer.json().serialize(title);

    // Send the packet
    sendOpenScreenPacket(player, windowId, windowType, titleJson);
    // Update the inventory for the client (to show items)
    player.updateInventory();
  }


  // -- Utility --

  private void sendOpenScreenPacket(Player player, int windowId, Object windowType, String titleJson) {
    var nmsComponent = net.minecraft.network.chat.Component.Serializer
            .fromJson(titleJson);
    var packet = new ClientboundOpenScreenPacket(windowId, (MenuType<?>) windowType, nmsComponent);
    ((CraftPlayer) player).getHandle().connection.send(packet);
  }

  class InventoryPlayer {
    private int windowId;
    private Object containerType;

    public InventoryPlayer(int windowId, Object containerType) {
      this.windowId = windowId;
      this.containerType = containerType;
    }

    public int getWindowId() {
      return windowId;
    }

    public Object getContainerType() {
      return containerType;
    }

  }


}