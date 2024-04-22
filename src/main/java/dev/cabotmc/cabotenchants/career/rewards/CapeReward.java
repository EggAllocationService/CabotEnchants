package dev.cabotmc.cabotenchants.career.rewards;

import dev.cabotmc.cabotenchants.career.Reward;
import dev.cabotmc.cabotenchants.util.GameProfileUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class CapeReward implements Reward {

    final int customModelId;
    final String capeURL;

    public CapeReward(int customModelId, String capeURL) {
        this.customModelId = customModelId;
        this.capeURL = capeURL;
    }


    @Override
    public void activate(Player target) {
        var profile = GameProfileUtil.profileBuilder(target.getPlayerProfile())
                .capeURL(capeURL)
                .buildPlayerProfile();
        target.setPlayerProfile(profile);
    }

    @Override
    public void deactivate(Player target) {
        var profile = GameProfileUtil.profileBuilder(target.getPlayerProfile())
                .capeURL(null)
                .buildPlayerProfile();
        target.setPlayerProfile(profile);
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public ItemStack createDisplayItem(boolean selected, Player viewer) {
        var i = new ItemStack(Material.LEATHER);
        var meta = i.getItemMeta();
        meta.setCustomModelData(customModelId);
        decorateItem(meta, viewer);
        i.setItemMeta(meta);
        return i;
    }

    protected  abstract void decorateItem(ItemMeta meta, Player viewer);

    @Override
    public String getName() {
        return "Cape";
    }
}
