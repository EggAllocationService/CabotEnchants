package dev.cabotmc.cabotenchants.unbreakingx;

import dev.cabotmc.cabotenchants.CabotEnchants;
import dev.cabotmc.cabotenchants.quest.QuestStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.world.inventory.AnvilMenu;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class UBXThrowIntoPortalStep extends QuestStep {

  @Override
  protected ItemStack internalCreateStepItem() {
    var i = new ItemStack(Material.DRAGON_EGG);
    var m = i.getItemMeta();
    m.addItemFlags(ItemFlag.HIDE_ENCHANTS);

    var lore = new ArrayList<Component>();
    lore.add(
            Component.text("After seeing so many wondrous places, the dragon in this egg wishes for freedom.")
                    .color(TextColor.color(0x333333))
                    .decoration(TextDecoration.ITALIC, false)
    );
    lore.add(
            Component.text("You can't figure out a way to hatch it in this world, but ")
                    .color(TextColor.color(0x333333))
                    .decoration(TextDecoration.ITALIC, false)
    );
    lore.add(
            Component.text("perhaps there is a way to send it to another?")
                    .color(TextColor.color(0x333333))
                    .decoration(TextDecoration.ITALIC, false)
    );
    m.lore(lore);
    i.setItemMeta(m);
    i.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
    return i;
  }

  @EventHandler
  public void place(BlockPlaceEvent e) {
    var item = e.getItemInHand();
    if (item.getType() != Material.DRAGON_EGG) return;
    if (isStepItem(item)) {
      e.setCancelled(true);
    }
  }
  @EventHandler
  public void portal(EntityPortalEnterEvent e) {
    if (e.getEntity().getType() != EntityType.DROPPED_ITEM) return;
    var item = (Item) e.getEntity();
    if (item.getItemStack().getType() != Material.DRAGON_EGG) return;
    if (item.getWorld().getEnvironment() != World.Environment.THE_END) return;
    if( e.getLocation().getBlock().getType() != Material.END_PORTAL) return;
    if (isStepItem(item.getItemStack())) {
      e.getEntity().remove();
      Bukkit.getScheduler()
              .scheduleSyncDelayedTask(CabotEnchants.getPlugin(CabotEnchants.class),
                      new FormAnimation(e.getLocation()
                              .add(0, 1, 0)
                              .toCenterLocation()), 20);
    }
  }
  static final NamespacedKey FIREWORK_KEY = new NamespacedKey("cabot", "ubx_firework");

  @EventHandler
  public void explode(FireworkExplodeEvent e) {
    if (e.getEntity().getPersistentDataContainer().has(FIREWORK_KEY, PersistentDataType.BYTE)) {
      var item = e.getEntity().getLocation()
              .getWorld()
              .dropItem(e.getEntity().getLocation(), getNextStep().createStepItem());
      // create random upwards vector to shoot out item
        var v = new Vector(0.3, 0.3, 0.3);
        v = v.rotateAroundY(Math.random() * Math.PI * 2);
        item.setVelocity(v);
    }
  }
  class FormAnimation implements Runnable {
    private Location loc;

    public FormAnimation(Location loc) {
      this.loc = loc;
      var firework = loc.getWorld()
              .spawn(loc, Firework.class);

      firework.setTicksToDetonate(30);
        var fm = (FireworkMeta) firework.getFireworkMeta();
        fm.addEffect(FireworkEffect.builder()
                .with(FireworkEffect.Type.STAR)
                .withColor(Color.BLACK)
                .withFlicker()
                .build());
        fm.addEffect(FireworkEffect.builder()
              .with(FireworkEffect.Type.STAR)
              .withColor(Color.PURPLE)
              .withFlicker()
                .withTrail()
              .build());
        fm.setPower(2);
      firework.setFireworkMeta(fm);
      firework.getPersistentDataContainer()
                .set(FIREWORK_KEY, PersistentDataType.BYTE, (byte) 1);
      loc.getWorld()
              .playSound(
                        loc,
                        Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,
                        SoundCategory.PLAYERS,
                        1,
                        1
              );
    }

    @Override
    public void run() {

    }
  }
}
