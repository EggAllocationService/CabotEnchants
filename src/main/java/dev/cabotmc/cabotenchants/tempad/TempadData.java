package dev.cabotmc.cabotenchants.tempad;

import dev.cabotmc.cabotenchants.util.JsonDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

public class TempadData {
    public static final NamespacedKey KEY = new NamespacedKey("cabotenchants", "tempad_data");
    public static final PersistentDataType<String, TempadData> CODEC = new JsonDataType<>(TempadData.class);

    public static class TempadLocation {
        public double x;
        public double y;
        public double z;

        public String world;
        public String name;
        public String item;
    }
}
