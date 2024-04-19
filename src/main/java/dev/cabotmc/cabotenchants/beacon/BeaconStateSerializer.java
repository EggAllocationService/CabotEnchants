package dev.cabotmc.cabotenchants.beacon;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

public class BeaconStateSerializer {
  public static String toJson(UpgradedBeaconState s) {
    var g = new Gson();
    var base = new JsonObject();
    var data = g.toJsonTree(s).getAsJsonObject();
    base.add("data", data);
    var arr = new JsonArray();

    for (var upgrade : s.upgrades) {
      var upgObj = new JsonObject();
      upgObj.addProperty("class", upgrade.getClass().getName());
      upgObj.add("data", g.toJsonTree(upgrade));
      arr.add(upgObj);
    }

    base.add("upgrades", arr);
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

    state.upgrades = upgrades;

    return state;

  }
}
