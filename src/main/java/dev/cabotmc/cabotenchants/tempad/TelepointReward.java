package dev.cabotmc.cabotenchants.tempad;

import dev.cabotmc.cabotenchants.CEBootstrap;
import dev.cabotmc.cabotenchants.blockengine.CustomBlockItems;
import dev.cabotmc.cabotenchants.quest.QuestStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TelepointReward extends QuestStep {
    @Override
    protected ItemStack internalCreateStepItem() {
        return CustomBlockItems.create(CEBootstrap.BLOCK_TELEPOINT, null, meta -> {
            meta.displayName(MiniMessage.miniMessage().deserialize(
                    "<!i><rainbow>Telepoint"
            ));

            meta.lore(
                    List.of(
                            Component.text("Right-click with Tempad to store location")
                                    .color(NamedTextColor.GRAY)
                                    .decoration(TextDecoration.ITALIC, false),
                            Component.text("Right-click with any named item to set the icon and name")
                                    .color(NamedTextColor.GRAY)
                                    .decoration(TextDecoration.ITALIC, false),
                            Component.text("Shift-left-click with pickaxe to break")
                                    .color(NamedTextColor.GRAY)
                                    .decoration(TextDecoration.ITALIC, false),
                            Component.empty(),
                            Component.text("The cooler waystone")
                                    .color(NamedTextColor.DARK_GRAY)
                    )
            );
        });
    }

    public static final Material[] RECIPE = {
            Material.IRON_BLOCK, Material.END_STONE_BRICKS, Material.IRON_BLOCK,
            Material.END_STONE_BRICKS, Material.ENDER_EYE, Material.END_STONE_BRICKS,
            Material.IRON_BLOCK, Material.END_STONE_BRICKS, Material.IRON_BLOCK
    };

    @EventHandler
    public void craft(PrepareItemCraftEvent e) {
        var inv = e.getInventory();
        var matrix = inv.getMatrix();

        if (matrix.length != 9) return;

        for (int i = 0; i < 9; i++) {
            if (matrix[i] == null || matrix[i].isEmpty() || matrix[i].getType() != RECIPE[i]) {
                return;
            }
        }

        e.getInventory().setResult(createStepItem());
    }
}
