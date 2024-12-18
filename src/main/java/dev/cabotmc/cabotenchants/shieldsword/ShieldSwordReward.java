package dev.cabotmc.cabotenchants.shieldsword;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import dev.cabotmc.cabotenchants.util.Models;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ShieldSwordReward extends QuestStep {
    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.NETHERITE_SWORD);

        i.editMeta(meta -> {
           meta.setItemModel(Models.AURORA_SWORD);

           meta.setEnchantmentGlintOverride(false);
           meta.setUnbreakable(true);
           meta.displayName(
                   MiniMessage
                           .miniMessage().deserialize("<!i><rainbow>Aura Sword")
           );

           meta.addEnchant(Enchantment.SHARPNESS, 5, true);
        });


        return i;
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (isStepItem(event.getPlayer().getInventory().getItemInMainHand())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("Holding " + Bukkit.getServer().getCurrentTick());
            }
        }
    }
}
