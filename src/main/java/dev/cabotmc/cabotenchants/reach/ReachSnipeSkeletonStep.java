package dev.cabotmc.cabotenchants.reach;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

public class ReachSnipeSkeletonStep extends QuestStep {


    @Override
    protected ItemStack internalCreateStepItem() {
        return null;
    }

    @EventHandler
    public void hit(ProjectileHitEvent e) {
        Entity hitEntity = e.getHitEntity();

        if (!(hitEntity instanceof Skeleton)) return;
        if (e.getEntity().getType() != EntityType.ARROW) return;
        if (!(e.getEntity().getShooter() instanceof Player player)) return;

        if (player.getLocation().distanceSquared(hitEntity.getLocation()) >= (100 * 100)) {
            ((LivingEntity) hitEntity).damage(10000);
            hitEntity.getWorld().dropItemNaturally(
                    hitEntity.getLocation(),
                    getNextStep().createStepItem()
            );
        }
    }
}
