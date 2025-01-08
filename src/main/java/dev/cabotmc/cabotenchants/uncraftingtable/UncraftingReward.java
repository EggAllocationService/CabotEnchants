package dev.cabotmc.cabotenchants.uncraftingtable;

import dev.cabotmc.cabotenchants.util.CEBootstrap;
import dev.cabotmc.cabotenchants.blockengine.CustomBlockItems;
import dev.cabotmc.cabotenchants.quest.QuestStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class UncraftingReward extends QuestStep {
    @Override
    protected ItemStack internalCreateStepItem() {
        return CustomBlockItems.create(CEBootstrap.BLOCK_UNCRAFTING_TABLE, null, meta -> {
            meta.displayName(MiniMessage.miniMessage().deserialize(
                    "<!i><rainbow>Uncrafting Table"
            ));

            meta.lore(
                    List.of(
                            Component.empty(),
                            Component.text("Ctrl-Z")
                                    .color(NamedTextColor.DARK_GRAY)
                    )
            );
        });
    }
}
