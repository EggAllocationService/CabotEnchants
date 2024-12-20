package dev.cabotmc.cabotenchants.blockengine;

import org.bukkit.NamespacedKey;

import java.io.Serializable;
import java.util.UUID;

class SerializedBlockData implements Serializable {
    public UUID id;

    // x,y,z - chunk relative
    public int x;
    public int y;
    public int z;

    public NamespacedKey type;
    public NamespacedKey model;

    public String data;
}
