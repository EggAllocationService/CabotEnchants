package dev.cabotmc.cabotenchants.mace;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import io.papermc.paper.event.player.PlayerPickItemEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MCGetBedrockStep extends QuestStep {
    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.STICK);
        var m = i.getItemMeta();
        m.setCustomModelData(3);
        m.displayName(Component.text("Iron Rod").decoration(TextDecoration.ITALIC, false));

        m.lore(
                List.of(
                        Component.text("An incredibly dense rod made from the iron of a sentient golem.")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false),
                        Component.text("Combined with the densest material in the universe, it could")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false),
                        Component.text("create a formidable weapon.")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false),

                        Component.empty(),
                        Component.text("\u2022 ")
                                .color(NamedTextColor.DARK_GRAY)
                                .decoration(TextDecoration.ITALIC, false)
                                .append(Component.text("Obtain a block of bedrock")
                                        .color(NamedTextColor.GRAY)
                                        .decoration(TextDecoration.ITALIC, false)
                                )
                )
        );

        i.setItemMeta(m);
        return i;
    }

    @EventHandler
    public void pickup(PlayerAttemptPickupItemEvent e) {
        if (e.getItem().getItemStack().getType() == Material.BEDROCK) {
            var items = getStepItems(e.getPlayer(), true);
            if (items.size() == 0) return;
            e.setCancelled(true);
            e.getItem().remove();
            e.getPlayer()
                    .getInventory()
                    .setItem(items.get(0).slot(), getNextStep().createStepItem());

            e.getPlayer()
                    .playSound(
                            e.getPlayer().getLocation(),
                            Sound.ENTITY_ITEM_PICKUP,
                            1.0f,
                            1.0f
                    );
            e.getPlayer()
                    .playSound(
                            e.getPlayer().getLocation(),
                            Sound.ENTITY_PLAYER_LEVELUP,
                            1.0f,
                            1.0f
                    );
        }
    }
}
