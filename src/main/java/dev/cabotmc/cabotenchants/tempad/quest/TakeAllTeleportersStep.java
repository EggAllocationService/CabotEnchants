package dev.cabotmc.cabotenchants.tempad.quest;

import dev.cabotmc.cabotenchants.quest.impl.ChecklistStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class TakeAllTeleportersStep extends ChecklistStep {

    public TakeAllTeleportersStep() {
        super();
        setRequirements(
                new CheckboxRequirement(0xfa0c14, 0x700a0e),
                new CheckboxRequirement(0x8114e0, 0x2f0852),
                new CheckboxRequirement(0x8114e0, 0x2f0852)
        );
    }
    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.IRON_INGOT);
        i.editMeta(m -> {
            m.lore(renderLore(m));
            m.setEnchantmentGlintOverride(true);
            m.displayName(Component.text("Misshapen Slate").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false));
        });
        return i;
    }

    @EventHandler
    public void portal(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            completeRequirementForAll(e.getPlayer(), 0, this::renderLore);
        } else if (e.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            completeRequirementForAll(e.getPlayer(), 1, this::renderLore);
        } else if (e.getCause() == PlayerTeleportEvent.TeleportCause.END_GATEWAY) {
            completeRequirementForAll(e.getPlayer(), 2, this::renderLore);
        }
    }

    private List<Component> renderLore(ItemMeta meta) {
        return List.of(
                Component.text("Killing a shulker and an enderman changed the eye into a blank slate.")
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("It still feels like it wants to teleport out of my hands, particularly near my portal")
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.empty(),
                renderCheckboxes(meta)
        );
    }
}
