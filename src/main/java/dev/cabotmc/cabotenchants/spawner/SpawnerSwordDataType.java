package dev.cabotmc.cabotenchants.spawner;

import com.google.gson.Gson;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class SpawnerSwordDataType implements PersistentDataType<String, SpawnerSwordData> {
  static SpawnerSwordDataType CODEC = new SpawnerSwordDataType();
  Gson g = new Gson();

  @Override
  public @NotNull Class<String> getPrimitiveType() {
    return String.class;
  }

  @Override
  public @NotNull Class<SpawnerSwordData> getComplexType() {
    return SpawnerSwordData.class;
  }

  @Override
  public @NotNull String toPrimitive(@NotNull SpawnerSwordData complex, @NotNull PersistentDataAdapterContext context) {
    return g.toJson(complex);
  }

  @Override
  public @NotNull SpawnerSwordData fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
    return g.fromJson(primitive, SpawnerSwordData.class);
  }
}
