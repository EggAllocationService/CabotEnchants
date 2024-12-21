package dev.cabotmc.cabotenchants.uncraftingtable;

import dev.cabotmc.cabotenchants.CabotEnchants;
import dev.cabotmc.cabotenchants.quest.QuestStep;
import dev.cabotmc.cabotenchants.util.Models;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.view.AnvilView;

import java.util.List;

public class UncraftingCombineWithDragonStep extends QuestStep {
    @Override
    protected ItemStack internalCreateStepItem() {
        var item = new ItemStack(Material.CARROT_ON_A_STICK);
        item.editMeta(meta -> {
            meta.setItemModel(Models.COSMIC_ORB);

            meta.displayName(
                    MiniMessage.miniMessage().deserialize("<!i><#999999>Antimatter Orb")
            );

            meta.lore(
                    List.of(
                            Component.text("Perhaps you should try combining it with another object of immense power")
                                    .color(NamedTextColor.GRAY)
                                    .decoration(TextDecoration.ITALIC, false)
                    )
            );
        });

        return item;
    }

    @EventHandler
    public void anvil(PrepareAnvilEvent e) {
        var inv = e.getInventory();
        if (inv.getFirstItem() != null && inv.getFirstItem().getType() == Material.DRAGON_EGG && isStepItem(inv.getSecondItem())) {
            e.setResult(getNextStep().createStepItem());
            e.getView()
                    .setRepairCost(0);
        }
    }
}
