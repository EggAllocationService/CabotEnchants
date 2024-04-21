package dev.cabotmc.cabotenchants.beacon;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;

public class BeaconStateSerializer {
  public static String toJson(UpgradedBeaconState s) {
    var base = new JsonObject();
    var data = g.toJsonTree(s).getAsJsonObject();
    base.add("data", data);
    var arr = new JsonArray();

    for (var upgrade : s.upgrades) {
      if (upgrade == null) {
        arr.add(JsonNull.INSTANCE);
        continue;
      }
      var upgObj = new JsonObject();
      upgObj.addProperty("class", upgrade.getClass().getName());
      upgObj.add("data", g.toJsonTree(upgrade));
      arr.add(upgObj);
    }
    base.add("upgrades", arr);

    var available = new JsonArray();
    for (var upgrade : s.unlockedUpgrades) {
      available.add(upgrade.getName());
    }
    base.add("availableUpgrades", available);

    return g.toJson(base);
  }

  public static UpgradedBeaconState fromJson(String json) {
    var g = new Gson();
    var obj = JsonParser.parseString(json).getAsJsonObject();
    var dataObj = obj.get("data").getAsJsonObject();
    var state = g.fromJson(dataObj, UpgradedBeaconState.class);

    var upgrades = new ArrayList<BeaconUpgrade>();
    var upgradesArr = obj.get("upgrades").getAsJsonArray();

    for (var u : upgradesArr) {
      if (u.isJsonNull()) {
        upgrades.add(null);
        continue;
      }
      var upgObj = u.getAsJsonObject();
      var upgClass = upgObj.get("class").getAsString();
      var upgData = upgObj.get("data").getAsJsonObject();
      Object upgrade;
      try {
        var clazz = Class.forName(upgClass);
        upgrade = g.fromJson(upgData, clazz);
        upgrades.add((BeaconUpgrade) upgrade);
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }

    state.upgrades = upgrades.toArray(new BeaconUpgrade[3]);

    var availableUpgrades = obj.get("availableUpgrades").getAsJsonArray();
    for (var u : availableUpgrades) {
      try {
        var clazz = Class.forName(u.getAsString());
        state.unlockedUpgrades.add((Class<? extends BeaconUpgrade>) clazz);
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }

    return state;
  }

  private static final Gson g = new GsonBuilder()
          .registerTypeAdapter(Class.class, new ClassTypeAdapter())
          .create();

  private static class ClassTypeAdapter extends TypeAdapter<Class<?>> {

    @Override
    public void write(JsonWriter out, Class value) throws IOException {
        out.value(value.getName());
    }

    @Override
    public Class<?> read(JsonReader in) throws IOException {
      try {
        return Class.forName(in.nextString());
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
