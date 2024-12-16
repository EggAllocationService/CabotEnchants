package dev.cabotmc.cabotenchants.career.rewards.cape;

import dev.cabotmc.cabotenchants.career.rewards.CapeReward;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

public class DevCape extends CapeReward {
  public DevCape() {
    super(0,"https://cabotmc.dev/capes/dev_2.png");
  }

  @Override
  public boolean isHidden() {
    return true;
  }

  @Override
  protected void decorateItem(ItemMeta meta, Player viewer) {

  }
}
