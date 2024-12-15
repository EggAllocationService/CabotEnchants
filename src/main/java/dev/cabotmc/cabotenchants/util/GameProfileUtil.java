package dev.cabotmc.cabotenchants.util;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GameProfileUtil {
    public static GameProfile convertPlayerProfile(PlayerProfile profile) {
        if (profile instanceof CraftPlayerProfile) {
            return ((CraftPlayerProfile) profile).getGameProfile();
        }
        var g = new GameProfile(profile.getId(), profile.getName());
        for (var property : profile.getProperties()) {
            if (property.isSigned()) {
                g.getProperties().put(property.getName(), new Property(property.getName(), property.getValue(), property.getSignature()));
            } else {
                g.getProperties().put(property.getName(), new Property(property.getName(), property.getValue()));
            }
        }
        return g;
    }

    public static GameProfile getProfileFromPlayer(Player p) {
        return ((CraftPlayer) p).getHandle().getGameProfile();
    }
    public static GameProfile createProfile(String name, UUID id, String skinURL, String capeURL) {
        var profile = new GameProfile(id, name);
        var g = new Gson();
        var json = new JsonObject();
        json.addProperty("timestamp", 0);
        json.addProperty("profileId", id.toString().replaceAll("-", ""));
        json.addProperty("profileName", name);
        json.addProperty("signatureRequired", true);
        var textures = new JsonObject();
        var skin = new JsonObject();
        skin.addProperty("url", skinURL);
        textures.add("SKIN", skin);
        if (capeURL != null) {
            var cape = new JsonObject();
            cape.addProperty("url", capeURL);
            textures.add("CAPE", cape);
        }
        json.add("textures", textures);

        //base 64 encode the json
        var encoded = g.toJson(json);
        var encodedBytes = encoded.getBytes();
        var encoded64 = java.util.Base64.getEncoder().encodeToString(encodedBytes);

        profile.getProperties().put("textures", new Property("textures", encoded64, encoded64));

        return profile;
    }

    public static ProfileBuilder profileBuilder(String name) {
        return profileBuilder(name, UUID.randomUUID());
    }
    public static ProfileBuilder profileBuilder(String name, UUID id) {
        return new ProfileBuilder().name(name).id(id);
    }
    public static ProfileBuilder profileBuilder(GameProfile existing) {
        var result = new ProfileBuilder();
        result.name = existing.getName();
        result.id = existing.getId();
        for (var property : existing.getProperties().get("textures")) {
            if (property.name().equals("textures")) {
                var g = new Gson();
                var json = g.fromJson(new String(java.util.Base64.getDecoder().decode(property.value())), JsonObject.class);
                var textures = json.getAsJsonObject("textures");
                var skin = textures.getAsJsonObject("SKIN");
                result.skinURL = skin.get("url").getAsString();
                if (textures.has("CAPE")) {
                    var cape = textures.getAsJsonObject("CAPE");
                    result.capeURL = cape.get("url").getAsString();
                }
            }
        }
        return result;
    }
    public static ProfileBuilder profileBuilder(PlayerProfile existing) {
        return profileBuilder(convertPlayerProfile(existing));
    }

    public static class ProfileBuilder {
        String name;
        UUID id;
        String skinURL;
        String capeURL;
        private ProfileBuilder() {}
        public ProfileBuilder name(String name) {
            this.name = name;
            return this;
        }
        public ProfileBuilder id(UUID id) {
            this.id = id;
            return this;
        }
        public ProfileBuilder skinURL(String skinURL) {
            this.skinURL = skinURL;
            return this;
        }
        public ProfileBuilder capeURL(String capeURL) {
            this.capeURL = capeURL;
            return this;
        }
        public GameProfile build() {
            if (name == null || id == null || skinURL == null) throw new IllegalStateException("Missing required fields");
            return createProfile(name, id, skinURL, capeURL);
        }
        public PlayerProfile buildPlayerProfile() {
            return new CraftPlayerProfile(build());
        }
    }

}
