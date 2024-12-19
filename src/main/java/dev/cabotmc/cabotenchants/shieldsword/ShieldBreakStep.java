package dev.cabotmc.cabotenchants.shieldsword;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ShieldBreakStep extends QuestStep {

    private float chance;

    @Override
    protected ItemStack internalCreateStepItem() {
        return null;
    }

    @Override
    protected void onConfigUpdate() {
        var c = getConfig(ShieldConfig.class);
        chance = c.BREAK_DROP_CHANCE;
    }

    Random random = new Random();

    @EventHandler
    public void shieldBreak(PlayerItemBreakEvent e) {
        if (e.getBrokenItem().getType() != Material.SHIELD) {
            return;
        }

        if (random.nextDouble() < chance) {
            var nextItem = getNextStep().createStepItem();
            e.getPlayer()
                    .getWorld()
                    .dropItemNaturally(
                            e.getPlayer().getLocation(),
                            nextItem
                    );
            e.getPlayer()
                    .playSound(
                            e.getPlayer().getLocation(),
                            Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST,
                            0.8f,
                            1f
                    );
        }
    }
}
