package dev.cabotmc.cabotenchants.eternalrocket;

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import dev.cabotmc.cabotenchants.quest.QuestStep;
import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;

public class ERReward extends QuestStep {
  @Override
  protected ItemStack internalCreateStepItem() {
    var i = new ItemStack(Material.FIREWORK_ROCKET, 1);
    var m = (FireworkMeta) i.getItemMeta();
    m.setPower(2);

    m.displayName(
            MiniMessage.miniMessage().deserialize("<!i><rainbow>Everlasting Firework")
    );
    var lore = new ArrayList<Component>();
    lore.add(Component.text("Never gets used up!")
            .color(NamedTextColor.GRAY)
            .decoration(TextDecoration.ITALIC, false));
    lore.add(Component.empty());
    lore.add(
            Component.text("The legend of Icarus is a cautionary tale of hubris and overconfidence")
                    .color(NamedTextColor.DARK_GRAY)
    );
    lore.add(
            Component.text("Thankfully, this rocket contains an integrated bottle of SPF 50 sunscreen")
                    .color(NamedTextColor.DARK_GRAY)
    );
    m.lore(lore);
    m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    i.setItemMeta(m);
    i.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
    return i;
  }
  @EventHandler
  public void rocket(PlayerElytraBoostEvent e) {
    var f = e.getItemStack();
    if (isStepItem(f)) {
      e.setShouldConsume(false);
    }
  }

  @EventHandler
  public void launchFirework(PlayerInteractEvent e) {
    if (e.getItem() == null) return;
    if (e.getItem().getType() != Material.FIREWORK_ROCKET) return;
    if (e.getAction() == Action.RIGHT_CLICK_AIR) return;
    if (isStepItem(e.getItem())) {
      e.setUseItemInHand(Event.Result.DENY);
    }
  }
  @EventHandler
  public void load(EntityLoadCrossbowEvent e) {
    if (e.getEntity().getType() != EntityType.PLAYER) return;
    var p = (org.bukkit.entity.Player) e.getEntity();
    if (isStepItem(p.getInventory().getItemInMainHand())) {
      e.setCancelled(true);
    } else if (isStepItem(p.getInventory().getItemInOffHand())) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void pickup(PlayerAttemptPickupItemEvent e) {
    if (isStepItem(e.getItem().getItemStack())) {
      getQuest().markCompleted(e.getPlayer());
    }
  }
}
