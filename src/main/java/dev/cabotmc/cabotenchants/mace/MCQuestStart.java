package dev.cabotmc.cabotenchants.mace;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import org.bukkit.Material;
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
        if (e.getEntity().getType() != EntityType.IRON_GOLEM) return;
        if (e.getEntity().getKiller() == null) return;
        if (e.getEntity().getKiller().getInventory().getItemInMainHand().getType() != Material.NETHERITE_AXE) return;
        var ent = (IronGolem) e.getEntity();
        if (ent.isPlayerCreated()) {
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
}
