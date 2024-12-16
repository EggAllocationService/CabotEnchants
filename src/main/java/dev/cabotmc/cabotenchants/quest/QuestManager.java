package dev.cabotmc.cabotenchants.quest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class QuestManager {
    private int counter;
    private HashMap<Integer, Quest> quests = new HashMap<>();
    private HashMap<String, Quest> questsByName = new HashMap<>();
    JavaPlugin inst;

    Gson g = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public QuestManager(JavaPlugin plugin) {
        counter = 0;
        inst = plugin;
    }

    public void registerQuest(Quest q) {
        quests.put(counter, q);
        questsByName.put(q.name, q);
        q.registerSteps(inst);
        q.questId = counter;
        counter++;
    }

    public Iterable<Quest> getActiveQuests() {
        return questsByName.values()
                .stream()
                .filter(q -> q.config.enabled)
                .toList();
    }

    public Quest getQuest(int id) {
        return quests.get(id);
    }

    public Quest getQuest(String name) {
        return questsByName.get(name);
    }

    public void loadConfigs(String json) {
        var obj = JsonParser.parseString(json)
                .getAsJsonObject()
                .asMap();
        for (var quest : quests.values()) {
            if (obj.containsKey(quest.getName())) {
                var config = g.fromJson(obj.get(quest.getName()), quest.getConfigType());
                quest.setConfig(config);
            }
        }
    }

    public String saveConfigs() {
        var g = new JsonObject();
        for (var quest : quests.values()) {
            g.add(quest.getName(), this.g.toJsonTree(quest.getConfig()));
        }
        return this.g.toJson(g);
    }

    public int getActiveQuestCount() {
        int count = 0;
        for (var quest : quests.values()) {
            if (quest.getConfig().enabled) {
                count++;
            }
        }
        return count;
    }
}
