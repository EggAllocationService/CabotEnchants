package dev.cabotmc.cabotenchants.bettertable.quest;

import dev.cabotmc.cabotenchants.CabotEnchants;
import dev.cabotmc.cabotenchants.bettertable.BetterTableMenu;
import dev.cabotmc.cabotenchants.quest.QuestStep;
import dev.cabotmc.cabotenchants.util.Models;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AncientTombReward extends QuestStep {

    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.GLOWSTONE_DUST);
        var m = i.getItemMeta();
        m.setItemModel(Models.ANCIENT_TOME);
        m.displayName(
                MiniMessage
                        .miniMessage().deserialize(
                                "<!i><rainbow>Ancient Tome"
                        )
        );
        m.lore(
                List.of(
                        Component.empty(),
                        Component.text("This ancient book is completely unreadable, written in a dead language.")
                                .color(NamedTextColor.DARK_GRAY),
                        Component.text("Yet it seems to flutter and twist occasionally, as if it is alive.")
                                .color(NamedTextColor.DARK_GRAY),
                        Component.text("It reminds me a bit of the book that came with my enchanting table.")
                                .color(NamedTextColor.DARK_GRAY),
                        Component.text("I wonder if they're interchangeable?")
                                .color(NamedTextColor.DARK_GRAY)
                )
        );
        i.setItemMeta(m);
        i.addUnsafeEnchantment(Enchantment.POWER, 1);

        var enchants = i.getData(DataComponentTypes.ENCHANTMENTS);
        enchants = enchants.showInTooltip(false);
        i.setData(DataComponentTypes.ENCHANTMENTS, enchants);
        return i;
    }

    @EventHandler
    public void interact(PlayerInteractEvent e) {
        if (e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.ENCHANTING_TABLE) {
            if (e.getItem() == null || !isStepItem(e.getItem())) {
                return;
            }
            e.setCancelled(true);
            var m = new BetterTableMenu(e.getPlayer());
            Bukkit.getPluginManager().registerEvents(m, CabotEnchants.getPlugin(CabotEnchants.class));
            m.open();
        }
    }
}
