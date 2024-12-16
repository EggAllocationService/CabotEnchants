package dev.cabotmc.cabotenchants.god;

import dev.cabotmc.cabotenchants.quest.impl.KillEntityTypeStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class GodWitherStep extends KillEntityTypeStep {
    public GodWitherStep() {
        super(1, EntityType.WITHER);
    }

    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.PAPER);
        var m = i.getItemMeta();
        m.displayName(
                Component.text("???")
                        .color(TextColor.color(0xf5c13d))
                        .decoration(TextDecoration.ITALIC, false)
                        .decorate(TextDecoration.OBFUSCATED)
        );

        var arr = new ArrayList<Component>();

        arr.add(
                Component.text(
                                "The Warden dropped this old, withered piece of paper."
                        )
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        );
        arr.add(
                Component.text(
                                "It seems to have untapped power held within."
                        )
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        );
        arr.add(
                Component.text(
                                "I wonder if exposing it to another powerful entity will reveal its secrets..."
                        )
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        );
        m.lore(arr);
        i.setItemMeta(m);
        return i;
    }
}
