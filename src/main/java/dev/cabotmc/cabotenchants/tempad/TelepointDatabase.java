package dev.cabotmc.cabotenchants.tempad;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class TelepointDatabase {
    private Path file;
    private HashMap<UUID, TelepointData> data;
    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();


    public TelepointDatabase(Path file) {
        this.file = file;

        var f = file.toFile();

        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            data = new HashMap<>();
        } else {
            try {
                var str = Files.readString(file);
                data = (HashMap<UUID, TelepointData>) gson.fromJson(str, TypeToken.getParameterized(HashMap.class, UUID.class, TelepointData.class));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void remove(UUID id) {
        data.remove(id);
        save();
    }

    public void register(UUID id, TelepointData item) {
        if (data.containsKey(id)) {
            throw new RuntimeException("Registering conflicting data");
        }

        data.put(id, item);
        save();
    }

    public Set<UUID> getAvailable() {
        return data.keySet();
    }

    public TelepointData get(UUID id) {
        return data.get(id);
    }

    public void save() {
        var result = gson.toJson(data);

        try {
            Files.writeString(file, result, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
