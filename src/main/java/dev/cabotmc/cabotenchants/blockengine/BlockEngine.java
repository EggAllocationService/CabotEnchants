package dev.cabotmc.cabotenchants.blockengine;

import com.google.gson.Gson;
import dev.cabotmc.cabotenchants.util.JsonDataType;
import net.kyori.adventure.key.Key;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.io.Serializable;
import java.util.*;

public class BlockEngine {
    private static final Gson gson = new Gson();

    private static final NamespacedKey BLOCK_DATA_KEY = new NamespacedKey("cabot", "block_data");

    private static final Map<NamespacedKey, BlockRegistration> registry = new HashMap<>();

    private static final Map<UUID, ActiveBlock> loadedBlocks = new HashMap<>();
    private static final Map<Chunk, Set<UUID>> chunkLookup = new HashMap<>();

    public static void registerBlock(NamespacedKey key, BlockRegistration data) {
        registry.put(key, data);
    }

    public static UUID placeBlock(Location location, NamespacedKey type) {
        location = location.toBlockLocation();
        var block = registry.get(type);
        if (block == null) {
            throw new IllegalArgumentException("No block registered with key " + type);
        }

        var id = UUID.randomUUID();
        var blockInstance = createBlockInstance(id, type, location);

        blockInstance.block.placed();

        location.getBlock()
                .setBlockData(Material.BARRIER.createBlockData());

        return id;
    }


    public static void loadChunk(Chunk chunk) {
        if (!chunk.getPersistentDataContainer().has(BLOCK_DATA_KEY)) {
            return;
        }

        var data = chunk.getPersistentDataContainer().get(BLOCK_DATA_KEY, BlockEngineData.CODEC);
        for (var serialized : data.blocks) {
            loadSerializedBlock(serialized, chunk);
        }
    }

    public static void saveWorld(World world, boolean unloading) {
        chunkLookup.keySet()
                .stream()
                .filter(c -> c.getWorld().equals(world))
                .forEach(c -> saveChunk(c, unloading));
    }

    public static void saveAll() {
        for (var chunk : chunkLookup.keySet().stream().toList()) {
            saveChunk(chunk, true);
        }
    }

    public static void saveChunk(Chunk chunk, boolean unload) {
        // find all ids in this chunk
        var ids = chunkLookup.get(chunk);
        if (ids == null) {
            return;
        }

        var data = new BlockEngineData();
        for (var id : ids) {
            var block = loadedBlocks.get(id);
            data.blocks.add(block.serialize());
            if (unload) {
                unloadBlock(id);
            }
        }

        chunk.getPersistentDataContainer().set(BLOCK_DATA_KEY, BlockEngineData.CODEC, data);
        if (unload) {
            chunkLookup.remove(chunk);
        }
    }

    public static boolean isCustomBlock(Block block) {
        return getCustomBlock(block) != null;
    }

    protected static void tick() {
        for (var block : loadedBlocks.values()) {
            block.block.tick();
        }
    }

    protected static ActiveBlock getCustomBlock(Block target) {
        if (!chunkLookup.containsKey(target.getChunk())) {
            return null;
        }

        var ids = chunkLookup.get(target.getChunk());
        for (var id : ids) {

            var block = loadedBlocks.get(id);
            var loc = target.getLocation().toBlockLocation();
            if (loc.getBlockX() == block.x && loc.getBlockY() == block.y && loc.getBlockZ() == block.z) {
                return loadedBlocks.get(id);
            }
        }
        return null;
    }

    private static void unloadBlock(UUID id) {
        loadedBlocks.get(id).renderer.remove();
        var block = loadedBlocks.remove(id);
        block.block.unload();

        for (var chunk : chunkLookup.values()) {
            chunk.remove(id);
        }
    }

    private static ActiveBlock loadSerializedBlock(SerializedBlockData data, Chunk container) {
        var craftBlock = container.getBlock(Math.abs(data.x), data.y, data.z);
        if (craftBlock.getType() != Material.BARRIER) {
            Bukkit.getLogger().warning("Block data loaded in chunk " + container + " at " + data.x + ", " + data.y + ", " + data.z + " but block is not a barrier");
            craftBlock.setBlockData(Material.BARRIER.createBlockData());
        }

        Location location = craftBlock.getLocation();
        var block = createBlockInstance(data.id, data.type, location);
        block.setRenderedModel(data.model);
        block.block.setData(gson.fromJson(data.data, block.block.getDataType()));
        return block;
    }

    private static ActiveBlock createBlockInstance(UUID id, NamespacedKey blockType, Location location) {
        try {
            var obj = registry.get(blockType).blockClass().getConstructor(UUID.class, Location.class).newInstance(id, location);
            var result = new ActiveBlock();
            var item = new ItemStack(Material.STICK);
            item.editMeta(m -> {
                m.setItemModel(registry.get(blockType).defaultModel());
            });

            result.id = id;
            result.block = obj;
            result.world = location.getWorld();
            result.x = location.getBlockX();
            result.y = location.getBlockY();
            result.z = location.getBlockZ();
            result.type = blockType;
            result.renderer = result.world
                    .spawn(location.toBlockLocation().add(0, 0.5, 0), ItemDisplay.class, e -> {
                        e.setItemStack(
                            item
                        );
                        e.setPersistent(false);
                    });

            result.world
                    .setBlockData(result.x, result.y, result.z, Material.BARRIER.createBlockData());

            loadedBlocks.put(id, result);

            chunkLookup.putIfAbsent(location.getChunk(), new HashSet<>());
            chunkLookup.get(location.getChunk()).add(id);

            obj.load();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static class BlockEngineData implements Serializable {
        public static final PersistentDataType<String, BlockEngineData> CODEC = new JsonDataType<>(BlockEngineData.class);

        public List<SerializedBlockData> blocks = new ArrayList<>();
    }

}
