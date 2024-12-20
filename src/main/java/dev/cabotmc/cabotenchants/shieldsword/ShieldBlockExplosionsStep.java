package dev.cabotmc.cabotenchants.shieldsword;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class ShieldBlockExplosionsStep extends QuestStep {
    private static final NamespacedKey TRACKER_KEY = new NamespacedKey("cabot", "shield_block_explosions");

    private int blockCount;

    @Override
    protected void onConfigUpdate() {
        var c = getConfig(ShieldConfig.class);
        blockCount = c.EXPLOSION_BLOCK_COUNT;
    }

    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.SHIELD);

        i.editMeta(m -> {
            m.displayName(
                    MiniMessage
                            .miniMessage().deserialize("<!i><yellow>Guilded Shield")
            );
            m.setUnbreakable(true);

            m.lore(
                    List.of(
                            Component.text("This shield materialized when your old one finally broke")
                                    .color(NamedTextColor.GRAY)
                                    .decoration(TextDecoration.ITALIC, false),
                            Component.text("It's thirsty for some more intense action")
                                    .color(NamedTextColor.GRAY)
                                    .decoration(TextDecoration.ITALIC, false)
                    )
            );

            m.setEnchantmentGlintOverride(true);

            m.getPersistentDataContainer()
                    .set(TRACKER_KEY, PersistentDataType.INTEGER, 0);

        });

        i.setData(DataComponentTypes.BASE_COLOR, DyeColor.YELLOW);
        return i;
    }

    @EventHandler
    public void damage(EntityDamageEvent e) {
        if (e.getEntity().getType() != EntityType.PLAYER) {
            return;
        }
        if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            return;
        }

        var player = (Player) e.getEntity();
        if (!player.isBlocking()) return;

        var offHand = player.getInventory().getItemInOffHand();

        if (isStepItem(offHand)) {

            var current = offHand.getPersistentDataContainer()
                    .get(TRACKER_KEY, PersistentDataType.INTEGER);

            if (current + 1 >= blockCount) {
                replaceWithNextStep(player, 40);
            } else {
                offHand.editMeta(m -> {
                    m.getPersistentDataContainer()
                            .set(TRACKER_KEY, PersistentDataType.INTEGER, current + 1);
                });

                player.playSound(
                        player.getLocation(),
                        Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                        SoundCategory.MASTER,
                        0.8f,
                        1f
                    );
            }
        }
    }
}
