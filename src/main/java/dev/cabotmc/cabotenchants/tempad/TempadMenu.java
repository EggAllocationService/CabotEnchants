package dev.cabotmc.cabotenchants.tempad;

import dev.cabotmc.cabotenchants.CabotEnchants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class TempadMenu implements Listener {
    public static TempadMenu open(Player target, EquipmentSlot slot) {
        var m = new TempadMenu(target, slot);
        target.openInventory(m.inventory);
        Bukkit.getPluginManager().registerEvents(m, CabotEnchants.instance);
        return m;
    }


    Player p;
    Inventory inventory;
    EquipmentSlot itemSlot;

    private Consumer<InventoryClickEvent>[] handlers = new Consumer[54];
    public TempadMenu(Player p, EquipmentSlot slot) {
        this.p = p;
        this.itemSlot = slot;
        inventory = p.getServer().createInventory(null, 54, Component.text("Select a destination"));
        render();

    }

    @EventHandler
    public void close(InventoryCloseEvent e) {
        if (e.getInventory() == inventory) {
            HandlerList.unregisterAll(this);
        }
    }

    void render() {
        inventory.clear();
        var item = p.getInventory().getItem(itemSlot);
        var data = item.getPersistentDataContainer()
                .get(TempadData.KEY, TempadData.CODEC);
        var discovered = data.discovered
                .stream()
                .filter(i -> CabotEnchants.TELEPOINT_DATABASE.get(i) != null)
                .toArray();
        for (int i = 0; i < 54; i++) {
            handlers[i] = null;
            if (discovered.length <= i) {
                continue;
            }
            var point = CabotEnchants.TELEPOINT_DATABASE.get((UUID) discovered[i]);
            var displayItem = new ItemStack(Material.getMaterial(point.material));
            int finalI = i;
            displayItem.editMeta(m -> {
                m.displayName(
                        MiniMessage.miniMessage().deserialize(
                                "<!i>" + point.name
                        )
                );
                m.lore(
                        List.of(
                                Component.text("Left-click to teleport")
                                        .color(NamedTextColor.GRAY)
                                        .decoration(TextDecoration.ITALIC, false),
                                Component.text("Middle-click to forget")
                                        .color(NamedTextColor.GRAY)
                                        .decoration(TextDecoration.ITALIC, false),
                                Component.empty(),
                                Component.text(discovered[finalI].toString())
                                        .color(NamedTextColor.DARK_GRAY)
                                        .decoration(TextDecoration.ITALIC, false)
                        )
                );
            });
            inventory.setItem(i, displayItem);

            handlers[i] = (event) -> {
                if (event.isLeftClick()) {
                    event.getWhoClicked().closeInventory();
                    event.getWhoClicked().teleportAsync(point.constructLocation());
                    ((Player) event.getWhoClicked()).playSound(
                            event.getWhoClicked().getLocation(),
                            Sound.UI_BUTTON_CLICK,
                            0.7f, 1.0f
                    );
                } else if (event.getClick() == ClickType.MIDDLE) {
                    var id = (UUID) discovered[finalI];
                    data.discovered.remove(id);
                    item.editMeta(m -> {
                        m.getPersistentDataContainer()
                                .set(TempadData.KEY, TempadData.CODEC, data);
                    });
                    p.getInventory().setItem(itemSlot, item);
                    ((Player) event.getWhoClicked()).playSound(
                            event.getWhoClicked().getLocation(),
                            Sound.UI_BUTTON_CLICK,
                            0.7f, 1.0f
                    );
                    event.getWhoClicked().closeInventory();
                }
            };
        }
    }

    @EventHandler
    public void click(InventoryClickEvent e) {
        if (e.getInventory() == inventory) {
            e.setCancelled(true);

            if (e.getClickedInventory() == inventory) {
                var slot = e.getSlot();
                if (handlers[slot] != null) {
                    handlers[slot].accept(e);
                }
            }
        }
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
