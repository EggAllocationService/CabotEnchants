package dev.cabotmc.cabotenchants.blockengine;

import com.google.gson.Gson;
import dev.cabotmc.cabotenchants.util.JsonDataType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.Consumer;

public class CustomBlockItems {
    public static final NamespacedKey CUSTOM_TAG = new NamespacedKey("cabot", "custom_block_data");
    public static ItemStack create(NamespacedKey key, Object storedData, Consumer<ItemMeta> metaEditor) {
        var block = BlockEngine.getBlockRegistration(key);
        if (block == null) {
            return null;
        }
        var i = new ItemStack(Material.BARRIER);

        i.editMeta(meta -> {
            var data = new CustomBlockData();
            data.type = key;
            data.data = new Gson().toJson(storedData);

            meta.getPersistentDataContainer().set(CUSTOM_TAG, CustomBlockData.CODEC, data);
            metaEditor.accept(meta);

            meta.setItemModel(block.defaultModel());
        });


        return i;
    }


    public static class CustomBlockData {
        public static final PersistentDataType<String, CustomBlockData> CODEC = new JsonDataType<>(CustomBlockData.class);

        public NamespacedKey type;
        public String data;

    }
}
