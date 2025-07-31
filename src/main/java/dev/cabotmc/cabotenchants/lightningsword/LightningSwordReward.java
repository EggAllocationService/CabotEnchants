package dev.cabotmc.cabotenchants.lightningsword;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import dev.cabotmc.cabotenchants.util.Models;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseCooldown;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class LightningSwordReward extends QuestStep {
    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.DIAMOND_SWORD);
        i.editMeta(m -> {
            m.displayName(
                    MiniMessage
                            .miniMessage().deserialize(
                                    "<!i><rainbow>Thunderous Sword"
                            )
            );

            m.lore(
                    List.of(
                            Component.text("Thunder")
                                    .color(NamedTextColor.GRAY)
                                    .decoration(TextDecoration.ITALIC, false),
                            Component.empty(),
                            Component.text("Taste the rainbow motherfucka")
                                    .color(NamedTextColor.DARK_GRAY)
                    )
            );
            m.addEnchant(Enchantment.SHARPNESS, 8, true);
            m.addEnchant(Enchantment.LOOTING, 4, true);
            m.addEnchant(Enchantment.FIRE_ASPECT, 2, true);

            m.setItemModel(Models.LIGHTNING_SWORD);
        });

        i.setData(DataComponentTypes.USE_COOLDOWN,
                UseCooldown.useCooldown(5.0f)
                        .cooldownGroup(Key.key("cabot", "lightning_sword"))
                        .build()
        );

        return i;
    }

    @EventHandler
    public void rightclick(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR) {
            if (isStepItem(e.getItem())) {
                if (e.getPlayer().hasCooldown(e.getItem())) {
                    return;
                } else {
                    // get targeted block
                    var block = e.getPlayer().getTargetBlockExact(100);
                    if (block != null) {
                        block.getWorld().strikeLightning(block.getLocation());
                        e.getPlayer().setCooldown(e.getItem(), 100);
                    }

                }
            }
        }
    }
}
