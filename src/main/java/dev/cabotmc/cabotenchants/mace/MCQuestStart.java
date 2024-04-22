package dev.cabotmc.cabotenchants.mace;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import org.bukkit.Material;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class MCQuestStart extends QuestStep {
    @Override
    protected ItemStack internalCreateStepItem() {
        return null;
    }

    @EventHandler
    public void onKill(EntityDeathEvent e) {
        if (e.getEntityType() != EntityType.BLAZE) return;

        var loc = e.getEntity().getLocation();
        var temp = e.getEntity().getWorld().getTemperature(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        if (temp > 0) {
            return;
        }

        var ent = e.getEntity();
        var spawnLoc = ent.getLocation().toCenterLocation();

        // create random direction vector, random direction and 45 degrees upwards
        var velocity = new Vector(1, 1, 0);
        velocity.rotateAroundY(Math.toRadians(Math.random() * 360));
        velocity.normalize();
        velocity.multiply(0.5);

        var item = ent.getWorld()
                .dropItem(spawnLoc, getNextStep().createStepItem());
        item.setVelocity(velocity);

    }
}
