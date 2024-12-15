package dev.cabotmc.cabotenchants.god;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.UUID;

public class GodQuestListener implements Listener {
    static final NamespacedKey NON_STACKABLE_KEY = new NamespacedKey("cabot", "stacknonce");
    static final NamespacedKey GOD_QUEST_KEY = new NamespacedKey("cabot", "godquest");
    static void makeNonStackable(ItemStack i) {
        var m = i.getItemMeta();
        m.getPersistentDataContainer()
                .set(NON_STACKABLE_KEY, PersistentDataType.STRING, UUID.randomUUID().toString());
        i.setItemMeta(m);
    }

    static ItemStack createStage1Item() {
        var i = new ItemStack(Material.PAPER);
        var m = i.getItemMeta();
        m.displayName(
                Component.text("???")
                        .color(TextColor.color(0xf5c13d))
                        .decoration(TextDecoration.ITALIC, false)
                        .decorate(TextDecoration.OBFUSCATED)
        );

        var arr =new ArrayList<Component>();

        arr.add(
                Component.text(
                        "The Warden dropped this old, withered piece of paper."
                )
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        );
        arr.add(
                Component.text(
                        "It seems to have untapped power held within."
                )
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        );
        arr.add(
                Component.text(
                        "I wonder if exposing it to another powerful entity will reveal its secrets..."
                )
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        );
        m.lore(arr);
        m.getPersistentDataContainer()
                        .set(GOD_QUEST_KEY, PersistentDataType.INTEGER, 1); // stage 1
        i.setItemMeta(m);
        m.setCustomModelData(1);
        makeNonStackable(i);
        return i;
    }

    static ItemStack createStage2Item() {
        var i = new ItemStack(Material.PAPER);
        var m = i.getItemMeta();
        m.displayName(
                Component.text("Go")
                        .color(TextColor.color(0xf5c13d))
                        .decoration(TextDecoration.ITALIC, false)
                        .decorate(TextDecoration.OBFUSCATED)
                        .append(
                                Component.text("d")
                                        .decoration(TextDecoration.OBFUSCATED, false)
                        )
        );

        var arr =new ArrayList<Component>();

        arr.add(
                Component.text(
                        "After killing the Wither, the paper transformed!"
                )
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        );
        arr.add(
                Component.text(
                        "Fragments of writing are now visible, but it's still mostly illegible."
                )
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        );
        arr.add(
                Component.text(
                        "It still thirsts for power, I wonder how it can be sated..."
                )
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        );

        arr.add(Component.empty());

        arr.add(
                MiniMessage.miniMessage().deserialize(
                        "<!i><dark_gray>E<obf>nchan</obf>tmen<obf>t</obf>: <obf>Go</obf>d"
                )
        );
        arr.add(
                MiniMessage.miniMessage().deserialize(
                        "<!i><dark_gray>When app<obf>ied</obf> to a fu<obf>ll</obf> set of <obf>armor, it</obf> will grant you <obf>the power of a god</obf>."
                )
        );
        m.lore(arr);
        m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        m.setCustomModelData(2);
        m.getPersistentDataContainer()
                        .set(GOD_QUEST_KEY, PersistentDataType.INTEGER, 2); // stage 2
        i.setItemMeta(m);
        i.addUnsafeEnchantment(Enchantment.UNBREAKING, 1);
        makeNonStackable(i);
        return i;
    }

    static ItemStack createBook() {
        var i = new ItemStack(Material.ENCHANTED_BOOK);
        var m = (EnchantmentStorageMeta) i.getItemMeta();
        m.addStoredEnchant(Enchantment.getByKey(new NamespacedKey("cabot", "god")), 1, false);

        m.displayName(
                MiniMessage.miniMessage().deserialize(
                        "<!i><rainbow>Enchanted Book"
                )
        );

        var lore = new ArrayList<Component>();
        lore.add(
                MiniMessage.miniMessage().deserialize(
                        "<!i><gray>When applied to a full set of armor, you are immune to damage."
                )
        );
        lore.add(
                Component.text("  - While active, you cannot attack other players")
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        );
        lore.add(
                Component.text("  - This enchantment conflicts with all other enchantments")
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        );

        lore.add(Component.empty());
        lore.add(
                Component.text("What good is a god who holds the stars in place, but")
                        .color(NamedTextColor.DARK_GRAY)
        );
        lore.add(
                Component.text("fails to cradle the broken pieces of a human heart?")
                        .color(NamedTextColor.DARK_GRAY)
        );

        m.getPersistentDataContainer()
                        .set(GOD_QUEST_KEY, PersistentDataType.INTEGER, 99); // final stage
        m.lore(lore);
        m.setCustomModelData(1);
        i.setItemMeta(m);
        return i;
    }
    @EventHandler
    public void death(EntityDeathEvent e) {
        if (e.getEntityType() == EntityType.WARDEN) {
            e.getDrops().clear();
            e.setDroppedExp(1);
            e.getEntity().getWorld().dropItem(e.getEntity().getLocation().add(0, 0.5, 0), createStage1Item(), i -> {
                i.setVelocity(new Vector(0, 0.5, 0));
            });
        }
    }
    @EventHandler
    public void kill(EntityDeathEvent e) {
        if (e.getEntityType() == EntityType.WITHER) {
            var killer = e.getEntity().getKiller();
            if (killer == null) {
                return;
            }
            var inv = killer.getInventory();
            var didChange = false;
            for (int i =0; i < inv.getSize() - 1; i++) {
                var item = inv.getItem(i);
                if (item == null) continue;
                var m = item.getItemMeta();
                if (m == null) continue;
                var pdc = m.getPersistentDataContainer();
                if (pdc.has(GOD_QUEST_KEY, PersistentDataType.INTEGER) && pdc.get(GOD_QUEST_KEY, PersistentDataType.INTEGER) == 1) {
                    inv.setItem(i, createStage2Item());
                    didChange = true;
                    break;
                }
            }

            if (didChange) {
                killer.playSound(killer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2f, 1.5f);
                killer.spawnParticle(Particle.GLOW, killer.getLocation(), 30);
            }
        } else if (e.getEntityType() == EntityType.ENDER_DRAGON) {
            var killer = e.getEntity().getKiller();
            if (killer == null) {
                return;
            }
            var inv = killer.getInventory();
            var didChange = false;
            for (int i =0; i < inv.getSize() - 1; i++) {
                var item = inv.getItem(i);
                if (item == null) continue;
                var m = item.getItemMeta();
                if (m == null) continue;
                var pdc = m.getPersistentDataContainer();
                if (pdc.has(GOD_QUEST_KEY, PersistentDataType.INTEGER) && pdc.get(GOD_QUEST_KEY, PersistentDataType.INTEGER) == 2) {
                    var newItem = createBook();
                    inv.setItem(i, newItem);

                    didChange = true;
                    break;
                }
            }

            if (didChange) {
                killer.playSound(killer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2f, 1.5f);
                killer.spawnParticle(Particle.GLOW, killer.getLocation(), 30);
                killer.getAdvancementProgress(Bukkit.getAdvancement(new NamespacedKey("cabot", "quest/quest1")))
                        .awardCriteria("a");
            }
        }
    }
    GodListener gl = new GodListener();
    long lastTick = 0;

}
