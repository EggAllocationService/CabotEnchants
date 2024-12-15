package dev.cabotmc.cabotenchants.eternalrocket;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ERExplosionStep extends QuestStep {
  @Override
  protected ItemStack internalCreateStepItem() {
    var i = new ItemStack(Material.GUNPOWDER, 1);
    var m = i.getItemMeta();
    m.displayName(
            Component.text("Empowered Strange Gunpowder")
                    .color(TextColor.color(0xFDFF21))
                    .decoration(TextDecoration.ITALIC, false)
    );

    var lore = new ArrayList<Component>();
    lore.add(
            Component.text("Killing enough Creepers seems to have empowered the gunpowder.")
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
    );
    lore.add(
            Component.text("It still isn't enough though...")
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
    );
    lore.add(
            Component.text("There must be some way to push it over the edge.")
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
    );
    m.lore(lore);
    m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    i.setItemMeta(m);
    i.addUnsafeEnchantment(Enchantment.POWER, 1);
    return i;
  }

  @EventHandler
  public void explosion(EntityDamageEvent e) {
    if (e.getEntity() instanceof Item) {
      Item i = (Item) e.getEntity();
      if (i.getItemStack().getType() != Material.GUNPOWDER) return;
      if (isStepItem(i.getItemStack())) {
        if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
          e.setCancelled(true);
          i.setItemStack(getNextStep().createStepItem());
          // play particles and festive noise
          i.getWorld()
                  .playSound(
                          i.getLocation(),
                          Sound.ENTITY_PLAYER_LEVELUP,
                            1.0f,
                            1.0f
                  );
          i.getWorld()
                  .spawnParticle(
                          Particle.TOTEM_OF_UNDYING,
                            i.getLocation(),
                            100,
                          0,
                          0,
                          0,
                          0.3
                  );
        }
      }
    }
  }
}
