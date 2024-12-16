package dev.cabotmc.cabotenchants.career.rewards.cape;

import dev.cabotmc.cabotenchants.career.CareerListener;
import dev.cabotmc.cabotenchants.career.RewardManager;
import dev.cabotmc.cabotenchants.career.rewards.CapeReward;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class AllQuestsCape extends CapeReward {
    public AllQuestsCape() {
        super(1, "https://objects.cabotmc.dev/galaxy.png");
    }

    @Override
    protected void decorateItem(ItemMeta meta, Player viewer) {
        var mm = MiniMessage.miniMessage();
        meta.displayName(
                mm
                        .deserialize("<!i><gradient:#271e3b:#7637a6:#4a4161>Quest Master</gradient>")
        );

        var max = viewer.getPersistentDataContainer()
                .get(CareerListener.NEEDED_QUESTS_KEY, PersistentDataType.INTEGER);
        var completed = Integer.bitCount(viewer.getPersistentDataContainer()
                .getOrDefault(CareerListener.COMPLETED_QUESTS_KEY, PersistentDataType.INTEGER, 0));

        if (!RewardManager.getUnlockedRewards(viewer).contains("quest_master")) {
            meta.lore(
                    List.of(
                            mm.deserialize("<!i><grey>Quests Completed: <yellow>" + completed + "</yellow>/<yellow>" + max + "</yellow></grey>")
                    )
            );
        } else {
            meta.lore(
                    List.of(
                            mm.deserialize("<!i><green>Unlocked</green>")
                    )
            );
        }
    }

    @Override
    public String getName() {
        return "Quest Master Cape";
    }
}
