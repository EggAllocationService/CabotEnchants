package dev.cabotmc.cabotenchants.bettertable;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.List;

public class BetterTableConstants {
    public static final int ITEM_SLOT = 0;
    public static final int ROW_ONE_SLOT_START = 11;
    public static final int ROW_TWO_SLOT_START = 29;
    public static final int ROW_THREE_SLOT_START = 47;

    public static final int UP_BUTTON_SLOT = 36;
    public static final int DOWN_BUTTON_SLOT = 45;

    public static final int XP_TENS_SLOT = 7;
    public static final int XP_ONES_SLOT = 8;

    public static final Component BLANK_MENU = Component.text("203")
            .font(Key.key("cabot", "bettertable"))
            .color(NamedTextColor.WHITE)
            .append(
                    Component.text("Enchanting Table")
                            .color(NamedTextColor.DARK_GRAY)
                            .font(Key.key("minecraft", "default"))
            );

    public static final Component WITH_BOOK_MENU = Component.text("213")
            .font(Key.key("cabot", "bettertable"))
            .color(NamedTextColor.WHITE)
            .append(
                    Component.text("Enchanting Table")
                            .color(NamedTextColor.DARK_GRAY)
                            .font(Key.key("minecraft", "default"))
            );

    public static final List<TableCostDefinition> AVAILABLE_ENCHANTMENTS = List.of(
            new TableCostDefinition(Enchantment.SHARPNESS, Material.DIAMOND_SWORD),
            new TableCostDefinition(Enchantment.BANE_OF_ARTHROPODS, Material.COBWEB),
            new TableCostDefinition(Enchantment.SMITE, Material.ZOMBIE_HEAD),
            new TableCostDefinition(Enchantment.FIRE_ASPECT, Material.BLAZE_POWDER, 10, 18),
            new TableCostDefinition(Enchantment.KNOCKBACK, Material.PISTON),
            new TableCostDefinition(Enchantment.SWEEPING_EDGE, Material.IRON_SWORD),
            new TableCostDefinition(Enchantment.LOOTING, Material.EXPERIENCE_BOTTLE, 10, 25, 35),

            new TableCostDefinition(Enchantment.EFFICIENCY, Material.FEATHER),
            new TableCostDefinition(Enchantment.FORTUNE, Material.DIAMOND),
            new TableCostDefinition(Enchantment.SILK_TOUCH, Material.GLASS, 10),

            new TableCostDefinition(Enchantment.LUCK_OF_THE_SEA, Material.ENCHANTED_BOOK),
            new TableCostDefinition(Enchantment.LURE, Material.TROPICAL_FISH),

            new TableCostDefinition(Enchantment.POWER, Material.DIAMOND_SWORD),
            new TableCostDefinition(Enchantment.FLAME, Material.BLAZE_POWDER),
            new TableCostDefinition(Enchantment.INFINITY, Material.ARROW),
            new TableCostDefinition(Enchantment.PUNCH, Material.PISTON),

            new TableCostDefinition(Enchantment.MULTISHOT, Material.DISPENSER),
            new TableCostDefinition(Enchantment.PIERCING, Material.TRIDENT),
            new TableCostDefinition(Enchantment.QUICK_CHARGE, Material.RABBIT_FOOT),

            new TableCostDefinition(Enchantment.PROTECTION, Material.IRON_BLOCK),
            new TableCostDefinition(Enchantment.BLAST_PROTECTION, Material.TNT),
            new TableCostDefinition(Enchantment.FIRE_PROTECTION, Material.LAVA_BUCKET),
            new TableCostDefinition(Enchantment.PROJECTILE_PROTECTION, Material.ARROW),
            new TableCostDefinition(Enchantment.THORNS, Material.CACTUS),
            new TableCostDefinition(Enchantment.FEATHER_FALLING, Material.FEATHER),
            new TableCostDefinition(Enchantment.AQUA_AFFINITY, Material.WATER_BUCKET, 20),
            new TableCostDefinition(Enchantment.DEPTH_STRIDER, Material.DIAMOND_BOOTS),
            new TableCostDefinition(Enchantment.FROST_WALKER, Material.ICE, 20, 30),
            new TableCostDefinition(Enchantment.RESPIRATION, Material.CONDUIT),

            new TableCostDefinition(Enchantment.UNBREAKING, Material.IRON_BARS),
            new TableCostDefinition(Enchantment.MENDING, Material.EXPERIENCE_BOTTLE, 25)
    );
}
