package dev.cabotmc.cabotenchants.eternalrocket;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class ERMilkMooshroomStep extends QuestStep {
  @Override
  protected ItemStack internalCreateStepItem() {
    return null;
  }
  @EventHandler
  public void milk(PlayerInteractEntityEvent e) {
    if (e.getRightClicked().getType() != org.bukkit.entity.EntityType.MUSHROOM_COW) return;
    if (e.getPlayer().getInventory().getItem(e.getHand()).getType() != Material.BOWL) return;
    if (ThreadLocalRandom.current().nextDouble() > 0.1) return;
    e.getRightClicked().getWorld()
            .dropItem(e.getRightClicked().getLocation(), getNextStep().createStepItem(), i -> {
              i.setVelocity(new Vector(0, 0.1, 0));
            });
    var loc = e.getRightClicked().getLocation();
    e.getRightClicked().remove();
    loc.getWorld().spawnParticle(
            Particle.EXPLOSION_LARGE,
            loc.add(0, 1, 0),
            7,
            0.3f,
            0.3f,
            0.3f,
            0.003
    );
    loc.getWorld()
            .playSound(
                    loc,
                    Sound.ENTITY_GENERIC_EXPLODE,
                    1.0f,
                    1.0f
            );
  }
}
