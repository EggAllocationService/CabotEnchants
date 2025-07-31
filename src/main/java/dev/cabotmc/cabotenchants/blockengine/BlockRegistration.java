package dev.cabotmc.cabotenchants.blockengine;

import org.bukkit.NamespacedKey;

public record BlockRegistration(Class<? extends CabotBlock> blockClass, NamespacedKey defaultModel) {
}
