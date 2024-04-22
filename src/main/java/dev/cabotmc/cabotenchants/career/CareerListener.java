package dev.cabotmc.cabotenchants.career;

import dev.cabotmc.cabotenchants.CabotEnchants;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

public class CareerListener {

    public static final NamespacedKey COMPLETED_QUESTS_KEY = new NamespacedKey("cabotenchants", "completed_quests");
    public static final NamespacedKey NEEDED_QUESTS_KEY = new NamespacedKey("cabotenchants", "needed_quests");

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            player.getPersistentDataContainer().set(COMPLETED_QUESTS_KEY, PersistentDataType.INTEGER, 0);
            player.getPersistentDataContainer().set(NEEDED_QUESTS_KEY, PersistentDataType.INTEGER, CabotEnchants.q.getActiveQuestCount());
        }
    }


}
