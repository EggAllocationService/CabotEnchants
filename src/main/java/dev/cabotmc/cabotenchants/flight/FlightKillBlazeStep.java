package dev.cabotmc.cabotenchants.flight;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import dev.cabotmc.cabotenchants.quest.impl.KillEntityStep;
import dev.cabotmc.cabotenchants.quest.impl.KillEntityTypeStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FlightKillBlazeStep extends KillEntityTypeStep {
    public FlightKillBlazeStep() {
        super(25, EntityType.BLAZE);
    }

    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.FEATHER);
        var m = i.getItemMeta();
        m.setCustomModelData(1);
        m.displayName(
                Component.text("Golden Feather")
                        .color(TextColor.color(0xf5c13d))
                        .decoration(TextDecoration.ITALIC, false)
        );

        m.lore(
                List.of(
                        Component.text("This strange feather appeared out of an egg.")
                                .decoration(TextDecoration.ITALIC, false)
                                        .color(NamedTextColor.DARK_GRAY),
                        Component.text("It seems to be imbued with some sort of energy.")
                                .decoration(TextDecoration.ITALIC, false)
                                        .color(NamedTextColor.DARK_GRAY),
                        Component.text("Looking deep into it, you can see a faint blazing glow.")
                                .decoration(TextDecoration.ITALIC, false)
                                        .color(NamedTextColor.DARK_GRAY),
                        Component.text("I wonder if there's a way to imbue it with more power?")
                                .decoration(TextDecoration.ITALIC, false)
                                        .color(NamedTextColor.DARK_GRAY)
                )

        );
        i.setItemMeta(m);
        return i;
    }
}
