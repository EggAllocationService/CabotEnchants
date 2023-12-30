package dev.cabotmc.cabotenchants.god;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import dev.cabotmc.cabotenchants.quest.impl.KillEntityStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class GodWardenStep extends KillEntityStep {
  public GodWardenStep() {
    super(EntityType.WARDEN, 1);
  }

  @Override
  protected ItemStack internalCreateStepItem() {

    return null;
  }
  long lastTick;
  GodListener gl = new GodListener();
  @EventHandler
  public void tick(ServerTickStartEvent e) {
    lastTick ++;

    if (lastTick % 6 != 0) return;
    Bukkit.getOnlinePlayers()
            .stream()
            .filter(Player::isOnGround)
            .filter(gl::hasFullGodArmor)
            .forEach(p -> {
              p.getWorld()
                      .spawnParticle(
                              Particle.TRIAL_SPAWNER_DETECTION,
                              p.getLocation(),
                              1,
                              0.2,
                              0,
                              0.2,
                              0.0003
                      );
            });

  }
}
