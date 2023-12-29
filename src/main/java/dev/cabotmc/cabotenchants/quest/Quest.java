package dev.cabotmc.cabotenchants.quest;

import org.bukkit.plugin.java.JavaPlugin;

public class Quest {
  QuestStep[] steps;
  int questId;

  public QuestStep[] getSteps() {
    return steps;
  }

  public Quest(QuestStep... steps) {
    this.steps = steps;
    for (int i= 0; i < steps.length; i++) {
      steps[i].setStepNum(i);
      steps[i].setQuest(this);
    }
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
  public int getId() {
    return questId;
  }
}
