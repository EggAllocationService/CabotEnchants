package dev.cabotmc.cabotenchants.reach;

import dev.cabotmc.cabotenchants.CEBootstrap;
import dev.cabotmc.cabotenchants.quest.impl.EnchantedBookRewardStep;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ReachEnchantBookReward extends EnchantedBookRewardStep {
    private static final NamespacedKey MODIFIER_KEY = new NamespacedKey("cabot", "reach_modifier");

    public ReachEnchantBookReward() {
        super(RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
                .get(CEBootstrap.ENCHANTMENT_REACH), 2);
    }

    @Override
    protected void applyLore(ArrayList<Component> lore) {

    }

    @EventHandler
    public void apply(PrepareAnvilEvent e) {
        if (e.getResult() == null) return;

        var result = e.getResult();
        var meta = e.getResult().getItemMeta();

        removeAttributeModifiers(meta);

        if (!meta.hasEnchant(this.enchantment)) {
            return;
        }

        addAppropriateModifier(result.getType(), meta, meta.getEnchantLevel(this.enchantment)) ;
        result.setItemMeta(meta);
    }

    @EventHandler
    public void grind(PrepareGrindstoneEvent e) {
        if (e.getResult() == null) return;

        var result = e.getResult();
        var meta = e.getResult().getItemMeta();
        removeAttributeModifiers(meta);
        result.setItemMeta(meta);   
    }


    private static void removeAttributeModifiers(ItemMeta meta) {

        if (meta.getAttributeModifiers() == null) return;
        if (meta.getAttributeModifiers(Attribute.BLOCK_INTERACTION_RANGE) != null) {
            var modifiers = meta.getAttributeModifiers(Attribute.BLOCK_INTERACTION_RANGE)
                    .stream()
                    .filter(m -> m.getKey().equals(MODIFIER_KEY))
                    .toList();
            for (var m : modifiers) {
                meta.removeAttributeModifier(Attribute.BLOCK_INTERACTION_RANGE, m);
            }
        }

        if (meta.getAttributeModifiers(Attribute.ENTITY_INTERACTION_RANGE) != null) {
            var modifiers_2 = meta.getAttributeModifiers(Attribute.ENTITY_INTERACTION_RANGE)
                    .stream()
                    .filter(m -> m.getKey().equals(MODIFIER_KEY))
                    .toList();


            for (var m : modifiers_2) {
                meta.removeAttributeModifier(Attribute.ENTITY_INTERACTION_RANGE, m);
            }
        }
    }

    void addAppropriateModifier(Material itemType, ItemMeta meta, int level) {
        if (itemType == Material.ENCHANTED_BOOK) {
            return;
        }

        if (Tag.ITEMS_SWORDS.isTagged(itemType)) {
            meta.addAttributeModifier(Attribute.ENTITY_INTERACTION_RANGE,
                        new AttributeModifier(
                                MODIFIER_KEY,
                                level * 2,
                                AttributeModifier.Operation.ADD_NUMBER,
                                EquipmentSlotGroup.MAINHAND
                        )
                    );
        } else {
            meta.addAttributeModifier(Attribute.BLOCK_INTERACTION_RANGE,
                    new AttributeModifier(
                            MODIFIER_KEY,
                            level * 2,
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.MAINHAND
                    )
            );
        }
    }
}
