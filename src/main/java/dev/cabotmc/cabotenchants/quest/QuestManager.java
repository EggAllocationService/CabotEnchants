package dev.cabotmc.cabotenchants.quest;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.IdentityHashMap;

public class QuestManager {
  private int counter;
  private HashMap<Integer, Quest> quests = new HashMap<>();
  JavaPlugin inst;
  public QuestManager(JavaPlugin plugin) {
    counter = 0;
    inst = plugin;
  }
  public void registerQuest(Quest q) {
    quests.put(counter, q);
    q.registerSteps(inst);
    q.questId = counter;
    counter++;
  }
  public Quest getQuest(int id) {
    return quests.get(id);
  }
}
