package dev.cabotmc.cabotenchants.beacon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.cabotmc.cabotenchants.beacon.upgrades.BeaconUpgrade;
import dev.cabotmc.cabotenchants.beacon.upgrades.InvisibilityBeaconUpgrade;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class UpgradedBeaconState implements Serializable {
  public static final NamespacedKey BEACON_KEY = new NamespacedKey("cabot", "beacon");
  UUID owner = new UUID(0, 0);

  ArrayList<BeaconUpgrade> upgrades = new ArrayList<>();

  public static class Encoder implements PersistentDataType<byte[], UpgradedBeaconState> {
    public static final Encoder INSTANCE = new Encoder();
    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
      return byte[].class;
    }

    @Override
    public @NotNull Class<UpgradedBeaconState> getComplexType() {
      return UpgradedBeaconState.class;
    }

    @Override
    public @NotNull byte[] toPrimitive(@NotNull UpgradedBeaconState complex, @NotNull PersistentDataAdapterContext context) {
      var x = new ByteArrayOutputStream();
      ObjectOutputStream y;
      try {
        y = new ObjectOutputStream(x);
        y.writeObject(complex);
        y.flush();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      return x.toByteArray();
    }

    @Override
    public @NotNull UpgradedBeaconState fromPrimitive(@NotNull byte[] primitive, @NotNull PersistentDataAdapterContext context) {
      Object result;
        try {
            result = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(primitive)).readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return (UpgradedBeaconState) result;
    }
  }
}
