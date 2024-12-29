package dev.cabotmc.cabotenchants.uncraftingtable;

import dev.cabotmc.cabotenchants.CabotEnchants;
import dev.cabotmc.cabotenchants.blockengine.BlockEngine;
import dev.cabotmc.cabotenchants.blockengine.CabotBlock;
import dev.cabotmc.cabotenchants.tempad.TelepointReward;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.UUID;

public class UncraftingTableBlock extends CabotBlock<Object> implements Listener {
    private ItemDisplay[] items = new ItemDisplay[9];
    private Interaction[] interactions = new Interaction[9];
    private ItemDisplay inventoryDisplay;

    private ItemStack inventory;
    private boolean locked = false;

    public UncraftingTableBlock(UUID id, Location location) {
        super(id, location);
    }

    @Override
    public void load() {
        // spawn crafting table slots
        for (int i = 0; i < 9; i++) {
            var spawnLoc = getCraftingGridPosition(getLocation(), i);
            items[i] = getWorld().spawn(spawnLoc, ItemDisplay.class);
            var transformation = new Transformation(
                    new Vector3f(0, -3 * (1f/16f), 0),
                    new AxisAngle4f((float) Math.toRadians(270), 1, 0, 0),
                    new Vector3f(0.125f, 0.125f, 0.125f),
                    new AxisAngle4f(0, 0, 0, 1)
            );
            items[i].setTransformation(transformation);

            interactions[i] = getWorld().spawn(spawnLoc, Interaction.class);
            interactions[i].setInteractionWidth(0.125f);
            interactions[i].setInteractionHeight(0.125f);

            items[i].setPersistent(false);
            interactions[i].setPersistent(false);
        }

        inventoryDisplay = getWorld().spawn(getCraftingGridPosition(getLocation().add(0, 1/16.0, 0), 4), ItemDisplay.class);
        inventoryDisplay.setPersistent(false);
        inventoryDisplay.setTransformation(
                new Transformation(
                        new Vector3f(0, 0, 0),
                        new AxisAngle4f((float) Math.toRadians(270), 1, 0, 0),
                        new Vector3f(1,1,1),
                        new AxisAngle4f(0, 0, 0, 1)
                )
        );
        inventoryDisplay.setPersistent(false);
    }

    public void setInventory(ItemStack inventory) {
        this.inventory = inventory;
        if (inventory != null) {
            inventoryDisplay.setItemStack(inventory);
        }

        float scale = inventory == null ? 0.0f : 1.0f;

        var transform = inventoryDisplay.getTransformation();
        var newTransform = new Transformation(
                new Vector3f(0, inventory == null ? -0.8f : 0, 0),
                transform.getLeftRotation(),
                new Vector3f(scale, scale, scale),
                transform.getRightRotation()
        );
        inventoryDisplay.setInterpolationDuration(10);
        inventoryDisplay.setInterpolationDelay(-1);
        inventoryDisplay.setTransformation(newTransform);
    }

    private void setLocked(boolean newLocked) {
        if (locked == newLocked) {
            return;
        }

        locked = newLocked;

        Vector3f translation = new Vector3f(0, 0, 0);
        if (!locked) {
            translation = new Vector3f(0, -3 * (1/16f), 0);
        }

        for (var i : items) {
            var transform = i.getTransformation();
            var newTransform = new Transformation(
                    translation,
                    transform.getLeftRotation(),
                    transform.getScale(),
                    transform.getRightRotation()
            );
            i.setInterpolationDuration(10);
            i.setInterpolationDelay(-1);
            i.setTransformation(newTransform);
        }

        if (locked) {
            // consume item
            setInventory(null);
        }
    }

    @Override
    public void unload() {

        if (inventory != null) {
            getWorld().dropItemNaturally(getLocation().add(0, 1, 0), inventory);
        } else {
            for (var i : items) {
                if (!i.getItemStack().isEmpty()) {
                    getWorld().dropItemNaturally(getLocation().add(0, 1, 0), i.getItemStack());
                }
            }
        }
        for (var e : interactions) {
            e.remove();
        }
        for (var e : items) {
            e.remove();
        }

        inventoryDisplay.remove();
    }

    @Override
    public void tick() {

    }

    @Override
    public void interact(Player player, Action action) {
        if (action == Action.RIGHT_CLICK_BLOCK) {
            if (Arrays.stream(items).allMatch(j -> j.getItemStack().isEmpty())) {
                setLocked(false);
            }
            if (locked) {
                return;
            }
            if (player.isSneaking() && inventory != null) {
                player.getInventory().addItem(inventory).forEach((i, k) -> player.getWorld().dropItemNaturally(player.getLocation(), k));
                setInventory(null);
                for (var i : items) {
                    i.setItemStack(new ItemStack(Material.AIR));
                }
            } else {
                if (!player.getInventory().getItemInMainHand().isEmpty() && inventory == null) {

                    var item = player.getInventory().getItemInMainHand();
                    if (item.getAmount() > 1) {
                        return;
                    }

                    if (renderRecipe(item)) {
                        setInventory(item);
                        player.getInventory().setItemInMainHand(null);
                    }
                }
            }
        } else if (action == Action.LEFT_CLICK_BLOCK && player.isSneaking()) {
            BlockEngine.breakBlock(getLocation().getBlock());
            getWorld()
                    .dropItemNaturally(getLocation(), CabotEnchants.UNCRAFTING_TABLE_QUEST.getStep(2).createStepItem());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {

        if (e.getRightClicked().getType() != EntityType.INTERACTION) {
            return;
        }

        for (int i = 0; i < 9; i++) {
            if (interactions[i].equals(e.getRightClicked())) {
                setLocked(true);
                var itemToGive = items[i].getItemStack();
                if (itemToGive.isEmpty()) {
                    return;
                }

                e.getPlayer().getInventory().addItem(itemToGive)
                        .forEach((j, k) -> e.getPlayer().getWorld().dropItemNaturally(getLocation().add(0, 1, 0), k));
                items[i].setItemStack(null);

                if (Arrays.stream(items).allMatch(j -> j.getItemStack().isEmpty())) {
                    setLocked(false);
                }
                return;
            }
        }

    }


    private boolean renderRecipe(ItemStack target) {
        var recipe = getRecipe(target);

        if (CabotEnchants.TELEPOINT_REWARD.isQuestItem(target)) {
            for (int i = 0; i < 9; i++) {
                items[i].setItemStack(new ItemStack(TelepointReward.RECIPE[i]));
            }
            return true;
        }

        if (recipe == null) return false;
        if (!(recipe instanceof CraftingRecipe craftingRecipe)) {
            return false;
        }
        if (craftingRecipe.getResult().getAmount() != 1) {
            return false;
        }

        if (recipe instanceof ShapelessRecipe shapeless) {
            var ingredients = shapeless.getIngredientList();
            for (int j = 0; j < 9; j++) {
                if (j >= ingredients.size()) {
                    items[j].setItemStack(new ItemStack(Material.AIR));
                } else {
                    items[j].setItemStack(ingredients.get(j));
                }
            }
        } else if (recipe instanceof ShapedRecipe shaped) {
            var ingredients = shaped.getIngredientMap();
            for (int j = 0; j < 9; j++) {
                var key = switch (j) {
                    case 0, 1, 2 -> "abc".charAt(j);
                    case 3, 4, 5 -> "def".charAt(j - 3);
                    case 6, 7, 8 -> "ghi".charAt(j - 6);
                    default -> throw new IllegalStateException("Unexpected value: " + j);
                };
                if (ingredients.containsKey(key)) {
                    items[j].setItemStack(ingredients.get(key));
                } else {
                    items[j].setItemStack(new ItemStack(Material.AIR));
                }
            }
        } else {
            Bukkit.getLogger().info("Can't render recipe of type " + recipe.getClass().getName());
            return false;
        }

        return true;
    }

    private static @Nullable Recipe getRecipe(ItemStack i) {
        var recipes = Bukkit.getRecipesFor(i);
        if (recipes.isEmpty()) {
            return null;
        }

        var recipe = recipes.get(0);
        return recipe;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void placed() {

    }

    @Override
    public Class<Object> getDataType() {
        return null;
    }

    // Constants for crafting table dimensions (in pixels)
    private static final int TEXTURE_SIZE = 16;
    private static final int BORDER_SIZE = 3;
    private static final int CELL_SIZE = 2;
    private static final int CELL_SPACING = 1;

    /**
     * Modifies a location to position an entity at the center of a crafting grid cell
     * @param location The base location of the crafting table block
     * @param index Grid index (0-8), where 0 is top-left and 8 is bottom-right
     * @return The modified location
     */
    public static Location getCraftingGridPosition(Location location, int index) {
        if (index < 0 || index > 8) {
            throw new IllegalArgumentException("Index must be between 0 and 8");
        }

        // Calculate row and column from index
        int row = index / 3;
        int col = index % 3;

        // Calculate the center point of the crafting table (in block coordinates)
        double centerX = 0.5;
        double centerZ = 0.5;

        // Calculate pixel offsets from center
        // First, convert grid position to pixels from the left/top edge
        double pixelsFromLeft = BORDER_SIZE + (col * (CELL_SIZE + CELL_SPACING)) + (CELL_SIZE / 2.0);
        double pixelsFromTop = BORDER_SIZE + (row * (CELL_SIZE + CELL_SPACING)) + (CELL_SIZE / 2.0);

        // Convert to offset from center (in pixels)
        double pixelOffsetX = pixelsFromLeft - (TEXTURE_SIZE / 2.0);
        double pixelOffsetZ = pixelsFromTop - (TEXTURE_SIZE / 2.0);

        // Convert pixel offsets to block coordinates (pixels / texture_size = percentage of block)
        double blockOffsetX = pixelOffsetX / TEXTURE_SIZE;
        double blockOffsetZ = pixelOffsetZ / TEXTURE_SIZE;

        // Apply offsets to the location
        return location.add(centerX + blockOffsetX + 0.0625, 1, centerZ + blockOffsetZ + 0.0625);
    }
}
