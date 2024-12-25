package dev.cabotmc.cabotenchants.tempad;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import dev.cabotmc.cabotenchants.util.Models;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TempadRewardItem extends QuestStep {
    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.STICK);

        i.editMeta(m -> {
            m.displayName(
                    MiniMessage.miniMessage().deserialize("<!i><rainbow>Tempad")
            );

            m.setItemModel(Models.TEMPAD);
        });

        return i;
    }

    @EventHandler
    public void interact(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR && isStepItem(e.getItem())) {
            TempadAddMenu.open(e.getPlayer());
        }
    }
}
