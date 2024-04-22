package dev.cabotmc.cabotenchants.career;

import dev.cabotmc.cabotenchants.career.rewards.DummyReward;
import dev.cabotmc.cabotenchants.career.rewards.cape.AllQuestsCape;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.ListPersistentDataTypeProvider;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.*;

public class RewardManager {

    private static final NamespacedKey REWARDS_KEY = new NamespacedKey("cabotenchants", "unlocked_rewards");
    private static final NamespacedKey CURRENT_KEY = new NamespacedKey("cabotenchants", "current_reward");
    private static final HashMap<String, Reward> REGISTRY = new HashMap<>();
    static {
        REGISTRY.put("quest_master", new AllQuestsCape());
    }

    public static Reward getReward(String name) {
        return REGISTRY.get(name);
    }
    public static Set<String> getRewards() {
        return REGISTRY.keySet();
    }

    public static void unlockReward(String name, Player target) {
        var type = PersistentDataType.LIST.strings();

        var unlocked = target.getPersistentDataContainer().getOrDefault(REWARDS_KEY, type, new ArrayList<>());

        if (!unlocked.contains(name)) {
            unlocked.add(name);
            target.getPersistentDataContainer().set(REWARDS_KEY, type, unlocked);
            target.sendMessage(Component.text("Unlocked " + name));
        }
    }

    public static List<String> getUnlockedRewards(Player target) {
        return target.getPersistentDataContainer()
                .getOrDefault(REWARDS_KEY, PersistentDataType.LIST.strings(), new ArrayList<>());
    }

    public static @Nullable String getCurrentReward(Player target) {
        return target.getPersistentDataContainer()
                .get(CURRENT_KEY, PersistentDataType.STRING);
    }

    public static boolean equipReward(Player p, @Nullable String reward) {
        if (reward != null && !getUnlockedRewards(p).contains(reward)) {
            return false; // don't do anything if the player hasn't unlocked the reward
        }

        var current = getCurrentReward(p);
        if (current != null) {
            getReward(current).deactivate(p);
        }
        if (reward != null) {
            getReward(reward).activate(p);
            p.getPersistentDataContainer().set(CURRENT_KEY, PersistentDataType.STRING, reward);
        } else {
            p.getPersistentDataContainer().remove(CURRENT_KEY);
        }
        return true;

    }
}
