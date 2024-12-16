package dev.cabotmc.cabotenchants.flight;

import dev.cabotmc.cabotenchants.quest.impl.KillEntityTypeStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FlightKillFlyingMobsStep extends KillEntityTypeStep {
    public FlightKillFlyingMobsStep() {
        super(30, EntityType.ENDER_DRAGON, EntityType.ALLAY, EntityType.GHAST, EntityType.BAT,
                EntityType.BEE, EntityType.PARROT, EntityType.PHANTOM, EntityType.VEX);
    }

    @Override
    protected void onConfigUpdate() {
        updateAmount(getConfig(CEFlightConfig.class).NUM_FLYING_KILLS);
    }

    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.FEATHER);
        var m = i.getItemMeta();
        m.displayName(
                Component.text("Blazing Golden Feather")
                        .color(TextColor.color(0x2786F5))
                        .decoration(TextDecoration.ITALIC, false)
        );

        m.lore(
                List.of(
                        Component.text("Now imbued with the power of blazes, this feather is ready to fly!")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.DARK_GRAY),
                        Component.text("Yet, it doesn't seem to be able to hold any weight.")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.DARK_GRAY),
                        Component.text("Perhaps I'll have to infuse it with the essence of flight.")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.DARK_GRAY)
                )

        );
        m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        i.setItemMeta(m);
        i.addUnsafeEnchantment(Enchantment.UNBREAKING, 1);

        return i;
    }
}
