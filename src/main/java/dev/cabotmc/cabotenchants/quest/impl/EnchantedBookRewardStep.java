package dev.cabotmc.cabotenchants.quest.impl;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import dev.cabotmc.cabotenchants.util.Models;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;

public abstract class EnchantedBookRewardStep extends QuestStep {
    protected Enchantment enchantment;
    int level;

    public EnchantedBookRewardStep(Enchantment e, int level) {
        this.enchantment = e;
        this.level = level;
    }

    protected abstract void applyLore(ArrayList<Component> lore);

    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.ENCHANTED_BOOK);
        i.editMeta(meta -> {
            var m = (EnchantmentStorageMeta) meta;

            m.addStoredEnchant(enchantment, level, true);

            m.displayName(
                    MiniMessage.miniMessage().deserialize(
                            "<!i><rainbow>Enchanted Book"
                    )
            );

            var lore = new ArrayList<Component>();
            applyLore(lore);
            if (!lore.isEmpty()) {
                m.lore(lore);
            }
            m.setItemModel(Models.COSMIC_BOOK);
            m.setEnchantmentGlintOverride(false);
        });
        return i;
    }
}
