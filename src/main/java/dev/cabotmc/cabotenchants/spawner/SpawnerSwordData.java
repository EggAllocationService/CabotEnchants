package dev.cabotmc.cabotenchants.spawner;

import org.bukkit.entity.EntityType;

public class SpawnerSwordData {
  EntityType type;
  int amount;

  public SpawnerSwordData() {
    this.type = null;
    this.amount = 0;
  }

  public SpawnerSwordData(EntityType type, int amount) {
    this.type = type;
    this.amount = amount;
  }
}
