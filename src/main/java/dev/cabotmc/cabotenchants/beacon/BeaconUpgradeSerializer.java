package dev.cabotmc.cabotenchants.beacon;

import com.google.gson.*;
import dev.cabotmc.cabotenchants.beacon.upgrades.BeaconUpgrade;

import java.lang.reflect.Type;

public class BeaconUpgradeSerializer implements JsonSerializer<BeaconUpgrade>, JsonDeserializer<BeaconUpgrade> {

  @Override
  public BeaconUpgrade deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    var base = json.getAsJsonObject();
    var type = base.get("type").getAsString();
    Class clazz;
    try {
      clazz = Class.forName(type);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    return context.deserialize(base.get("value").getAsJsonObject(), clazz);

  }

  @Override
  public JsonElement serialize(BeaconUpgrade src, Type typeOfSrc, JsonSerializationContext context) {
    var x = new JsonObject();
    x.addProperty("type", src.getClass().getName());
    x.add("value", context.serialize(src));
    return x;
  }
}
