package dev.cabotmc.cabotenchants.bettertable.quest;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class EnchantRandomStep extends QuestStep {

    @Override
    protected ItemStack internalCreateStepItem() {
        return null;
    }

    @EventHandler
    public void enchant(EnchantItemEvent e) {
        if (Math.random() <= probFunc(e.getExpLevelCost())) {
            var item = e.getEnchanter().getWorld().dropItem(e.getEnchantBlock().getLocation().add(0, 1, 0), getNextStep().createStepItem());
            item.setVelocity(new Vector(0, 0.1, 0));
            e.getEnchantBlock()
                    .getWorld()
                    .spawnParticle(
                            Particle.FLASH,
                            e.getEnchantBlock().getLocation().add(0, 1, 0),
                            5,
                            0.0,
                            0.0,
                            0.0
                    );
            e.getEnchantBlock()
                    .getWorld()
                    .playSound(
                            e.getEnchantBlock().getLocation().add(0, 1, 0),
                            Sound.ENTITY_FIREWORK_ROCKET_BLAST,
                            1.0f,
                            1.0f
                    );
        }
    }

    double probFunc(int level) {
        return 0.005 + (0.01 * level / 30.00);
    }
}
