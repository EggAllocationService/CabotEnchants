package dev.cabotmc.cabotenchants.lightningsword;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SwordHitByLightningStep extends QuestStep {


    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.IRON_SWORD);
        i.editMeta(m -> {
            m.displayName(
                    MiniMessage
                            .miniMessage().deserialize("<!i><grey>Conductive Sword")
            );

            m.lore(
                    List.of(
                            Component.text("Could use a little more spark...")
                                    .decoration(TextDecoration.ITALIC, false)
                                    .color(NamedTextColor.DARK_GRAY)
                    )
            );

            m.setEnchantmentGlintOverride(true);
        });

        return i;
    }

    @EventHandler
    public void strike(EntityDamageEvent e) {
        if (e.getEntityType() == EntityType.ITEM && e.getCause() == EntityDamageEvent.DamageCause.LIGHTNING) {
            Item i = (Item) e.getEntity();
            if (isStepItem(i.getItemStack())) {
                i.setItemStack(getNextStep().createStepItem());
                i.getWorld()
                        .spawnParticle(
                                Particle.ELECTRIC_SPARK,
                                i.getLocation(),
                                100,
                                0.5, 0.5, 0.5
                        );
            }
        }
    }
}
