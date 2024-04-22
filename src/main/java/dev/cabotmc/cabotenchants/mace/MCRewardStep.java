package dev.cabotmc.cabotenchants.mace;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.Repairable;

import java.util.List;

public class MCRewardStep extends QuestStep {
  @Override
  protected ItemStack internalCreateStepItem() {
    var i = new ItemStack(Material.IRON_AXE);
    var m = (Repairable) i.getItemMeta();
    m.setCustomModelData(1);
    m.displayName(
            MiniMessage
                    .miniMessage().deserialize("<!i><gold>Mace")
    );
    m.addEnchant(Enchantment.DURABILITY, 3, true);
    m.addEnchant(Enchantment.MENDING, 1, true);
    m.setRepairCost(999999);
    m.lore(
            List.of(
                    Component.text("Density II")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("Wind Burst")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.empty(),
                    Component.text("+ This weapon deals extra damage the farther you have fallen")
                            .color(NamedTextColor.GREEN)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.empty(),
                    Component.text("Mace to the mace to the mace to the mace to the face!")
                            .color(NamedTextColor.DARK_GRAY)
            )
    );

    i.setItemMeta(m);
    return i;
  }


  public static final int DAMAGE_PER_BLOCK = 7;
  @EventHandler
  public void attack(EntityDamageByEntityEvent e) {
    if (e.getDamager().getType() != EntityType.PLAYER) return;
    var p = (Player) e.getDamager();
    if (!isStepItem(p.getInventory().getItemInMainHand())) return;
    if (p.getFallDistance() > 3.0) {
        // has been falling
        var extraDamage = (Math.round(p.getFallDistance()) - 1) * DAMAGE_PER_BLOCK;
        e.setDamage(e.getDamage() + extraDamage);
        p.setFallDistance(0);
        WindChargeExplosion.spawn(e.getDamager().getLocation().subtract(0, 1, 0));
    }

  }
}
