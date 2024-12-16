package dev.cabotmc.cabotenchants.util;

import com.google.gson.Gson;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class JsonDataType<C> implements PersistentDataType<String, C> {
    private Class<C> target;
    private Gson g = new Gson();

    public JsonDataType(Class<C> target) {
        this.target = target;
    }


    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NotNull Class<C> getComplexType() {
        return target;
    }

    @Override
    public @NotNull String toPrimitive(@NotNull C complex, @NotNull PersistentDataAdapterContext context) {
        return g.toJson(complex);
    }

    @Override
    public @NotNull C fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
        return g.fromJson(primitive, target);
    }
}
