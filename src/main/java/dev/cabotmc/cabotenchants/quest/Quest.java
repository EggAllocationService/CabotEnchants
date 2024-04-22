package dev.cabotmc.cabotenchants.quest;

import dev.cabotmc.cabotenchants.career.CareerListener;
import dev.cabotmc.cabotenchants.config.CEConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class Quest {
  QuestStep[] steps;
  int questId;

  Object config;

  String name;
  public String getName() {
      return name;
  }
  public void setConfig(CEConfig config) {
      this.config = config;
      for (var step: this.steps) {
        step.onConfigUpdate();
      }
      if (!config.enabled) {
        HandlerList.unregisterAll(steps[0]);
      }

  }
  public <T extends CEConfig> T getConfig() {
      return (T) config;
  }

  Class<? extends CEConfig> configClass;
    public Class<? extends CEConfig> getConfigType() {
        return configClass;
    }
  public QuestStep[] getSteps() {
    return steps;
  }

  public Quest(String name, Class<? extends CEConfig> configClass, QuestStep... steps) {
    this.steps = steps;
    for (int i= 0; i < steps.length; i++) {
      steps[i].setStepNum(i);
      steps[i].setQuest(this);
    }
    this.configClass = configClass;
    try {
      this.config = configClass.getDeclaredConstructor()
              .newInstance();
    } catch(Exception e) {
      e.printStackTrace();
      this.config = null;
    }
    this.name = name;
  }

  public QuestStep getStep(int num) {
    if (num >= steps.length) return null;
    return steps[num];
  }

  void registerSteps(JavaPlugin p) {
    for  (QuestStep step : steps) {

      p.getServer().getPluginManager().registerEvents(step, p);
    }
  }

  void markCompleted(Player p) {
      int mask = 1 << questId;
      var current = p.getPersistentDataContainer()
              .getOrDefault(CareerListener.COMPLETED_QUESTS_KEY, PersistentDataType.INTEGER, 0);
      p.getPersistentDataContainer()
              .set(CareerListener.COMPLETED_QUESTS_KEY, PersistentDataType.INTEGER, current | mask);
    if ((current | mask) != current) {
      var event = new QuestCompleteEvent(this, p);
        p.getServer().getPluginManager().callEvent(event);

    }
  }
  public int getId() {
    return questId;
  }
}
