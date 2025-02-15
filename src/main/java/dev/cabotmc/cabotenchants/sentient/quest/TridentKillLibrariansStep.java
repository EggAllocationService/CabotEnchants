package dev.cabotmc.cabotenchants.sentient.quest;

import dev.cabotmc.cabotenchants.quest.impl.KillEntityStep;
import dev.cabotmc.cabotenchants.sentient.CETridentConfig;
import dev.cabotmc.cabotenchants.util.Models;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TridentKillLibrariansStep extends KillEntityStep {
    public TridentKillLibrariansStep() {
        super(5);
    }

    @Override
    protected void onConfigUpdate() {
        updateAmount(getConfig(CETridentConfig.class).NUM_LIBRARIAN_KILLS);
    }

    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.TURTLE_SCUTE);
        var meta = i.getItemMeta();
        meta.displayName(
                Component.text("Empowered Ancient Trident")
                        .color(TextColor.color(TridentKillAquaticEnemiesStep.TARGET_COLOR))
                        .decoration(TextDecoration.ITALIC, false)
        );
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setItemModel(Models.REPAIRED_TRIDENT);
        meta.lore(
                List.of(
                        Component.text("Now infused with the power of the Guardians, this trident is a little more useful")
                                .color(NamedTextColor.DARK_GRAY)
                                .decoration(TextDecoration.ITALIC, false),
                        Component.text("Touching it seems to give a sensation of life, as if it were sentient. Yet it is weak.")
                                .color(NamedTextColor.DARK_GRAY)
                                .decoration(TextDecoration.ITALIC, false),
                        Component.text("Perhaps it needs to be fed? What would be an adequate feast for an intelligent weapon?")
                                .color(NamedTextColor.DARK_GRAY)
                                .decoration(TextDecoration.ITALIC, false)

                )
        );

        i.setItemMeta(meta);
        i.addUnsafeEnchantment(Enchantment.POWER, 1);
        return i;
    }

    @Override
    protected boolean isValidKill(LivingEntity e) {
        if (e.getType() != EntityType.VILLAGER) return false;
        var v = (org.bukkit.entity.Villager) e;
        if (v.getProfession() != Villager.Profession.LIBRARIAN) return false;
        return true;
    }

    @Override
    protected void onCompleted(Player p) {
        getQuest().markCompleted(p);
    }
}
