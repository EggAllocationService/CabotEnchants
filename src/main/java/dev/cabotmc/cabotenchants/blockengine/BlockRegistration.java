package dev.cabotmc.cabotenchants.blockengine;

import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;

public record BlockRegistration(Class<? extends CabotBlock> blockClass, NamespacedKey defaultModel) {
}
