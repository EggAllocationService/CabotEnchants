package dev.cabotmc.cabotenchants.flight;

import dev.cabotmc.cabotenchants.quest.impl.EnchantedBookRewardStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;

import java.util.ArrayList;

public class FlightRewardStep extends EnchantedBookRewardStep {
    public FlightRewardStep() {
        super(Enchantment.getByKey(new NamespacedKey("cabot", "flight")), 1);
    }

    @Override
    protected void applyLore(ArrayList<Component> lore) {
        lore.add(Component.empty());
        lore.add(
                Component.text("Look how majestically you soar through the air.")
                        .color(TextColor.color(0x333333))
        );
        lore.add(
                Component.text("Like an eagle. Piloting a blimp.")
                        .color(TextColor.color(0x333333))
        );
        lore.add(
                Component.text("  -  GLaDOS")
                        .color(TextColor.color(0x333333))
                        .decoration(TextDecoration.ITALIC, false)
        );
    }

    @EventHandler
    public void onPickup(PlayerAttemptPickupItemEvent e) {
        if (isStepItem(e.getItem().getItemStack()) && !e.getItem().hasGravity()) {
            getQuest().markCompleted(e.getPlayer());
        }
    }
}
