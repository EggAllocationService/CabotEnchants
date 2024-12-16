package dev.cabotmc.cabotenchants.shrinkray;

import dev.cabotmc.cabotenchants.quest.impl.KillEntityStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class KillRavengerStep extends KillEntityStep {
    public KillRavengerStep() {
        super(1);
    }

    @Override
    protected boolean isValidKill(LivingEntity e) {
        return e.getType() == EntityType.RAVAGER;
    }

    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.GOLD_NUGGET);

        i.editMeta(m -> {
                    m.customName(
                            Component.text("Tiny Device")
                                    .color(TextColor.color(0x63758a))
                                    .decoration(TextDecoration.ITALIC, false)
                    );

                    m.lore(
                            List.of(
                                    Component.text("Killing all those small things worked! Poor turtle...")
                                            .decoration(TextDecoration.ITALIC, false)
                                            .color(NamedTextColor.DARK_GRAY),
                                    Component.text("The only problem is that now the device is absolutely tiny. I can barely see it!")
                                            .decoration(TextDecoration.ITALIC, false)
                                            .color(NamedTextColor.DARK_GRAY),
                                    Component.text("Doing the reverse must be the key. There has to be a big hulking beast somewhere around...")
                                            .decoration(TextDecoration.ITALIC, false)
                                            .color(NamedTextColor.DARK_GRAY)
                            )
                    );
                }
        );

        return i;
    }
}
