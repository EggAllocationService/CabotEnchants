package dev.cabotmc.cabotenchants.tempad;

import dev.cabotmc.cabotenchants.util.JsonDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashSet;
import java.util.UUID;

public class TempadData {
    HashSet<UUID> discovered = new HashSet<UUID>();

    public static PersistentDataType<String, TempadData> CODEC = new JsonDataType<>(TempadData.class);
    public static NamespacedKey KEY = new NamespacedKey("cabot", "tempad");
}
