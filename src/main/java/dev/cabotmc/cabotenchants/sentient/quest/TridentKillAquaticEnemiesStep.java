package dev.cabotmc.cabotenchants.sentient.quest;

import dev.cabotmc.cabotenchants.quest.impl.KillEntityTypeStep;
import dev.cabotmc.cabotenchants.sentient.CETridentConfig;
import dev.cabotmc.cabotenchants.util.Models;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TridentKillAquaticEnemiesStep extends KillEntityTypeStep {
  static final int NUM_KILLS = 50;
  static final int TARGET_COLOR = 0x3afca5;
  static final int START_COLOR = 0x555555;

  public TridentKillAquaticEnemiesStep() {
    super(NUM_KILLS, EntityType.DROWNED, EntityType.ELDER_GUARDIAN, EntityType.GUARDIAN, EntityType.SQUID,
            EntityType.TROPICAL_FISH, EntityType.SALMON, EntityType.COD, EntityType.PUFFERFISH);
  }

  @Override
  protected void onConfigUpdate() {
    updateAmount(getConfig(CETridentConfig.class).NUM_AQUATIC_KILLS);
  }

  @Override
  protected ItemStack internalCreateStepItem() {
    var i = new ItemStack(Material.TURTLE_SCUTE);
    var meta = i.getItemMeta();
    meta.displayName(
            Component.text("Broken Ancient Trident")
                    .color(TextColor.color(START_COLOR))
                    .decoration(TextDecoration.ITALIC, false)
    );
    meta.setItemModel(Models.BROKEN_TRIDENT);
    meta.lore(
            List.of(
                    Component.text("The elder guardians seem to be holding on to this ancient, broken trident.")
                            .color(NamedTextColor.DARK_GRAY)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("It's not even sharp enough to cut bread with, but there has to be some reason they were keeping it.")
                            .color(NamedTextColor.DARK_GRAY)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("Maybe I can find a way to remind it of it's former glory?")
                            .color(NamedTextColor.DARK_GRAY)
                            .decoration(TextDecoration.ITALIC, false)

            )
    );

    i.setItemMeta(meta);

    return i;
  }

  @Override
  protected void modifyItemOnProgress(ItemStack item, Player p, int progress) {
    var actualProgress = (double) progress / NUM_KILLS;
    // set item name color to a gradient between START_COLOR and TARGET_COLOR
    // progress is set by actualProgress, between 0 and 1
    var r = (int) ((START_COLOR >> 16) + ((TARGET_COLOR >> 16) - (START_COLOR >> 16)) * actualProgress);
    var g = (int) (((START_COLOR >> 8) & 0xff) + (((TARGET_COLOR >> 8) & 0xff) - ((START_COLOR >> 8) & 0xff)) * actualProgress);
    var b = (int) ((START_COLOR & 0xff) + ((TARGET_COLOR & 0xff) - (START_COLOR & 0xff)) * actualProgress);
    var color = (r << 16) + (g << 8) + b;
    var meta = item.getItemMeta();
    meta.displayName(
            meta.displayName()
                    .color(TextColor.color(color))
    );
    item.setItemMeta(meta);

  }
}
