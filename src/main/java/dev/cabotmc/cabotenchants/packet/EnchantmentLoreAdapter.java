package dev.cabotmc.cabotenchants.packet;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnchantmentLoreAdapter{

    public static void modify(ItemStack i) {
        var m = i.getItemMeta();
        if (m == null) return;
        var l = m.lore() == null ? new ArrayList<Component>()
                : m.lore()
                    .stream()
                .filter(component -> !(component instanceof TranslatableComponent))
                .collect(Collectors.toList());


        var nmsItem = CraftItemStack.asNMSCopy(i);


        var newList = new ArrayList<Component>();
        for (var ench : EnchantmentHelper.getEnchantments(nmsItem).keySet()) {
            if (ench.getDescriptionId().contains("cabot")) {
                var base = Component.translatable(ench.getDescriptionId())
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false);
                if (ench.getMaxLevel() != 1) {
                    base = base.append(Component.text(" " + romanNumeral(EnchantmentHelper.getEnchantments(nmsItem).get(ench)))
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false));
                }
                newList.add(base);
            }
        }
        newList.addAll(l);

        m.lore(newList);
        if (newList.isEmpty()) {
            m.lore(null);
        }
        i.setItemMeta(m);


    }

    public static String romanNumeral(int n) {
        switch (n) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            case 5: return "V";
            default: return "" + n;
        }
    }
}
