package dev.cabotmc.cabotenchants.tempad;

import dev.cabotmc.cabotenchants.CabotEnchants;
import dev.cabotmc.cabotenchants.blockengine.BlockEngine;
import dev.cabotmc.cabotenchants.blockengine.CabotBlock;
import dev.cabotmc.cabotenchants.quest.Quest;
import dev.cabotmc.cabotenchants.quest.QuestStep;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.UUID;

public class TelepointBlock extends CabotBlock<Object> {
    public TelepointBlock(UUID id, Location location) {
        super(id, location);
    }

    @Override
    public void tick() {

    }

    @Override
    public void interact(Player cause, Action action) {
        if (action == Action.RIGHT_CLICK_BLOCK) {
            if (!cause.isSneaking()) return;
            var item = cause.getInventory().getItemInMainHand();
            if (item == null || item.isEmpty()) return;
            var data = CabotEnchants.TELEPOINT_DATABASE.get(getId());
            var meta = item.getItemMeta();
            if (meta.getPersistentDataContainer().has(QuestStep.QUEST_ID_KEY)) {
                return;
            }
            if (meta.hasCustomName()) {
                data.name = PlainTextComponentSerializer.plainText().serialize(meta.customName());
            }
            data.material = item.getType().toString();

            cause.playSound(
                    cause.getLocation(),
                    Sound.ENTITY_PLAYER_LEVELUP,
                    0.8f, 1.3f
            );

            getWorld()
                    .spawnParticle(
                            Particle.TRIAL_SPAWNER_DETECTION,
                            getLocation().add(0.5, 1, 0.5),
                            50,
                            0.5, 0, 0.5,
                            0.0003
                    );

            CabotEnchants.TELEPOINT_DATABASE.save();
        } else if (action == Action.LEFT_CLICK_BLOCK && Tag.ITEMS_PICKAXES.isTagged(cause.getInventory().getItemInMainHand().getType()) && cause.isSneaking()) {
            BlockEngine.breakBlock(getLocation().getBlock());
            getWorld()
                    .dropItemNaturally(getLocation(), CabotEnchants.TELEPOINT_REWARD.createStepItem());
        }
    }

    @Override
    public void destroy() {
        CabotEnchants.TELEPOINT_DATABASE.remove(getId());
    }

    @Override
    public void placed() {
        var data = new TelepointData();
        var loc = getLocation();
        data.x = loc.getBlockX();
        data.y = loc.getBlockY();
        data.z = loc.getBlockZ();
        data.material = "ENDER_PEARL";
        data.world = loc.getWorld().getKey().toString();
        data.name = NameGenerator.generateRandomName();
        CabotEnchants.TELEPOINT_DATABASE.register(
                getId(),
                data
        );
    }

    @Override
    public Class<Object> getDataType() {
        return null;
    }
}
