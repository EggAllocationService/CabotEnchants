package dev.cabotmc.cabotenchants.tempad;

import dev.cabotmc.cabotenchants.blockengine.BlockEngine;
import dev.cabotmc.cabotenchants.quest.QuestStep;
import dev.cabotmc.cabotenchants.util.Models;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TempadRewardItem extends QuestStep {
    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.STICK);

        i.editMeta(m -> {
            m.displayName(
                    MiniMessage.miniMessage().deserialize("<!i><rainbow>Tempad")
            );

            m.setItemModel(Models.TEMPAD);

            m.getPersistentDataContainer()
                    .set(TempadData.KEY, TempadData.CODEC, new TempadData());

            m.lore(
                    List.of(
                            Component.text("Shift-right click on a Netherite block to craft a Telepoint")
                                    .decoration(TextDecoration.ITALIC, false)
                                    .color(NamedTextColor.GRAY),
                            Component.empty(),
                            Component.text("We're all writing our own stories now. Go write yours.")
                                    .color(NamedTextColor.DARK_GRAY)
                    )
            );
        });

        return i;
    }

    @EventHandler
    public void interact(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR && isStepItem(e.getItem())) {
            TempadMenu.open(e.getPlayer(), e.getHand());
        } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK && isStepItem(e.getItem())) {
            if (BlockEngine.isCustomBlock(e.getClickedBlock())) {
                var customBlock = BlockEngine.getCustomBlock(e.getClickedBlock());
                if (customBlock.getBlock() instanceof TelepointBlock) {
                    var data = e.getItem().getPersistentDataContainer().get(TempadData.KEY, TempadData.CODEC);
                    if (data.discovered.contains(customBlock.getId())) {
                        return;
                    }
                    data.discovered.add(customBlock.getId());

                    e.getItem().editMeta(m -> {
                        m.getPersistentDataContainer().set(TempadData.KEY, TempadData.CODEC, data);
                    });

                    e.getPlayer()
                            .playSound(
                                    e.getClickedBlock().getLocation(),
                                    Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                                    1.0f, 1.1f
                            );
                }
            } else if (e.getClickedBlock().getType() == Material.NETHERITE_BLOCK && e.getPlayer().isSneaking()) {
                e.getClickedBlock()
                        .setType(Material.AIR);
                e.getClickedBlock()
                        .getWorld()
                        .dropItemNaturally(e.getClickedBlock().getLocation(), getNextStep().createStepItem());
                e.getClickedBlock()
                        .getWorld()
                        .spawnParticle(
                                Particle.ELECTRIC_SPARK,
                                e.getClickedBlock().getLocation().toCenterLocation(),
                                30,
                                0.5, 0.5, 0.5,
                                0.0001
                        );
                e.getClickedBlock()
                        .getWorld()
                        .playSound(
                                e.getClickedBlock().getLocation(),
                                Sound.BLOCK_LAVA_EXTINGUISH,
                                0.8f, 1.0f
                        );
            }
        }
    }
}
