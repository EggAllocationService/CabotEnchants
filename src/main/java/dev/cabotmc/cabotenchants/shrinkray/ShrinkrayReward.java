package dev.cabotmc.cabotenchants.shrinkray;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

public class ShrinkrayReward extends QuestStep {

    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.STICK);

        i.editMeta(m -> {
            m.displayName(
                    MiniMessage.miniMessage()
                            .deserialize("<!i><rainbow>Shrinking Gadget</rainbow>")

            );

            m.lore(
                    List.of(
                        Component.empty(),
                        Component.text("You like it? I was going to call it a Shrinkinator, but I've done that whole '-inator' thing before")
                                .color(NamedTextColor.DARK_GRAY)
                                .decoration(TextDecoration.ITALIC, false)
                    )
            );
        });

        return i;
    }

    public static final NamespacedKey CANNOT_SHRINK_MARKER = new NamespacedKey("cabot", "cannot_shrink");
    private static final NamespacedKey MODIFIER_KEY = new NamespacedKey("cabot", "shrink_modifier");
    private static final double[] SIZES = new double[] {0.2, 0.4, 1.0, 2.0, 5.0};
    private static final int DEFAULT_SIZE = 2;

    private static final EntityType[] BANNED_ENTITIES = new EntityType[]{EntityType.PLAYER, EntityType.ENDER_DRAGON, EntityType.WITHER, EntityType.WARDEN};
    @EventHandler
    public void interact(PlayerInteractEvent event) {
        var p = event.getPlayer();
        if (!isStepItem(p.getInventory().getItemInMainHand())) {
            return;
        }

        // right click == down, left click == increase
        ShrinkDirection direction;

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            direction = ShrinkDirection.DECREASE;
        } else {
            direction = ShrinkDirection.INCREASE;
        }

        // sneaking = target self
        if (p.isSneaking()) {
            if (canModifyScale(p, direction, true)) {
                modifyScale(p, direction);
            }
        } else {
            var target = p.rayTraceEntities(50);
            if (target == null || target.getHitEntity() == null) {
                return;
            }
            var entity = target.getHitEntity();
            if (!(entity instanceof LivingEntity)) {
                return;
            }
            if (!canModifyScale((LivingEntity) entity, direction, false)) {
                return;
            }

            modifyScale((LivingEntity) entity, direction);
            drawParticles((LivingEntity) entity, p, direction);
        }
    }

    // reset on death
    @EventHandler
    public void death(PlayerRespawnEvent event) {
        setEntitySize(event.getPlayer(), DEFAULT_SIZE);
    }

    private void drawParticles(LivingEntity target, Player cause, ShrinkDirection direction) {


        var plugin = Bukkit.getPluginManager().getPlugin("CabotEnchants");
        var task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            var sourceLoc = direction == ShrinkDirection.DECREASE ?
                    target.getLocation().add(0, target.getHeight() / 2.0, 0) : cause.getLocation().add(0, cause.getHeight() / 2.0, 0);
            var targetLoc = direction == ShrinkDirection.INCREASE ?
                    target.getLocation().add(0, target.getHeight() / 2.0, 0) : cause.getLocation().add(0, cause.getHeight() / 2.0, 0);

            var options = new Particle.Trail(
                    targetLoc,
                    Color.fromRGB(direction == ShrinkDirection.INCREASE ? 0x53e6b7 : 0xde3a3a),
                    10
            );

            sourceLoc.getWorld()
                    .spawnParticle(
                            Particle.TRAIL,
                            sourceLoc,
                            20,
                            0.4,
                            0.4,
                            0.4,
                            options
                    );
        }, 0, 1);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, task::cancel, 10);
    }

    private boolean canModifyScale(LivingEntity entity, ShrinkDirection direction, boolean isSelf) {
        if (!isSelf && Arrays.stream(BANNED_ENTITIES).anyMatch(e -> e == entity.getType())) {
            return false;
        }

        if (entity.getPersistentDataContainer().has(CANNOT_SHRINK_MARKER)) {
            return false;
        }

        int index = entity.getPersistentDataContainer()
                .getOrDefault(MODIFIER_KEY, PersistentDataType.INTEGER, DEFAULT_SIZE);
        if (direction == ShrinkDirection.INCREASE) {
            index += 1;
        } else {
            index -= 1;
        }

        if (index < 0 || index >= SIZES.length) {
            return false;
        }

        return true;
    }

    private void modifyScale(LivingEntity entity, ShrinkDirection direction) {
        int index = entity.getPersistentDataContainer()
                .getOrDefault(MODIFIER_KEY, PersistentDataType.INTEGER, DEFAULT_SIZE);

        if (direction == ShrinkDirection.INCREASE) {
            index += 1;
        } else {
            index -= 1;
        }

        if (index >= SIZES.length || index < 0) {
            return;
        }

        setEntitySize(entity, index);

        entity.getWorld().spawnParticle(
                Particle.TRIAL_SPAWNER_DETECTION_OMINOUS,
                entity.getLocation().add(0, entity.getHeight() / 2, 0),
                (int) (100 * SIZES[index]),
                SIZES[index] * 0.5,
                SIZES[index] * 0.5,
                SIZES[index] * 0.5,
                0.01
        );

        entity.getWorld().playSound(
                Sound.sound(
                        Key.key("minecraft", "item.trident.thunder"),
                        Sound.Source.PLAYER,
                        0.8f,
                        1.0f
                ),
                entity
        );

    }

    private static void setEntitySize(LivingEntity entity, int index) {
        entity.getAttribute(Attribute.SCALE)
                .setBaseValue(SIZES[index]);

        entity.getPersistentDataContainer().set(MODIFIER_KEY, PersistentDataType.INTEGER, index);
    }
}
