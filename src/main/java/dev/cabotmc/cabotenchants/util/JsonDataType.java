package dev.cabotmc.cabotenchants.util;

import com.google.gson.Gson;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

public class JsonDataType<C> implements PersistentDataType<String, C> {
    private Class<C> target;
    private Gson g = new Gson();

    public JsonDataType(Class<C> target) {
        this.target = target;
    }


    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public Class<C> getComplexType() {
        return target;
    }

    @Override
    public String toPrimitive(C complex, PersistentDataAdapterContext context) {
        return g.toJson(complex);
    }

    @Override
    public C fromPrimitive(String primitive, PersistentDataAdapterContext context) {
        return g.fromJson(primitive, target);
    }
}
