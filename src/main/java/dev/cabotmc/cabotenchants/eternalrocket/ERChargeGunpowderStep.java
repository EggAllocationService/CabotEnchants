package dev.cabotmc.cabotenchants.eternalrocket;

import dev.cabotmc.cabotenchants.quest.impl.KillEntityStep;
import dev.cabotmc.cabotenchants.quest.impl.KillEntityTypeStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ERChargeGunpowderStep extends KillEntityTypeStep {
  public ERChargeGunpowderStep() {
    super(50, EntityType.CREEPER);
  }

  @Override
  protected void onReady() {
    updateAmount(getConfig(CERocketConfig.class).NUM_CREEPER_KILLS);
  }

  @Override
  protected ItemStack internalCreateStepItem() {
    var i = new ItemStack(Material.GUNPOWDER, 1);
    var m = i.getItemMeta();
    m.displayName(
            Component.text("Strange Gunpowder")
                    .color(TextColor.color(0x00FF00))
                    .decoration(TextDecoration.ITALIC, false)
    );

    var lore = new ArrayList<Component>();
    lore.add(
            Component.text("Milking the Mooshroom dropped this strange pile of Gunpowder.")
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
    );
    lore.add(
            Component.text("It seems mundane enough, but I wonder if there's a way to empower it?")
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
    );
    m.lore(lore);
    i.setItemMeta(m);
    return i;
  }

  @Override
  protected void modifyItemOnProgress(ItemStack item, Player p, int progress) {
    if (progress == 25) {
      var m = item.getItemMeta();
        var lore = m.lore();
        lore.add(
                Component.empty()
        );
        lore.add(
                Component.text("You hear a slight crackle")
                        .color(TextColor.color(0x00FF00))
                        .decoration(TextDecoration.ITALIC, false)
        );
        m.lore(lore);
        item.setItemMeta(m);
        p.playSound(
                p.getLocation(),
                Sound.ENTITY_FIREWORK_ROCKET_TWINKLE,
                0.8f,
                1
        );

    }
  }
}
