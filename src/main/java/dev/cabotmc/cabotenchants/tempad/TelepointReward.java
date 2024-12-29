package dev.cabotmc.cabotenchants.tempad;

import dev.cabotmc.cabotenchants.CEBootstrap;
import dev.cabotmc.cabotenchants.blockengine.CustomBlockItems;
import dev.cabotmc.cabotenchants.quest.QuestStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

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
                            Component.empty(),
                            Component.text("The cooler waystone")
                                    .color(NamedTextColor.DARK_GRAY)
                    )
            );
        });
    }

    private static final Material[] RECIPE = {
            Material.IRON_BLOCK, Material.POLISHED_BASALT, Material.IRON_BLOCK,
            Material.POLISHED_BASALT, Material.ENDER_EYE, Material.POLISHED_BASALT,
            Material.IRON_BLOCK, Material.POLISHED_BASALT, Material.IRON_BLOCK
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
