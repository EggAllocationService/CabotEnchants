package dev.cabotmc.cabotenchants;

import dev.cabotmc.cabotenchants.blockengine.BlockEngine;
import dev.cabotmc.cabotenchants.blockengine.BlockRegistration;
import dev.cabotmc.cabotenchants.blockengine.blocks.TestBlock;
import dev.cabotmc.cabotenchants.uncraftingtable.UncraftingTableBlock;
import dev.cabotmc.cabotenchants.util.Models;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("ALL")
public class CEBootstrap implements PluginBootstrap {

    public static final Key ENCHANTMENT_GOD = Key.key("cabot", "god");
    public static final Key ENCHANTMENT_RAILGUN = Key.key("cabot", "railgun");
    public static final Key ENCHANTMENT_FLIGHT = Key.key("cabot", "flight");
    public static final Key ENCHANTMENT_SENTIENCE = Key.key("cabot", "sentience");
    public static final Key ENCHANTMENT_VEINMINER = Key.key("cabot", "veinminer");
    public static final Key ENCHANTMENT_REACH = Key.key("cabot", "reach");

    public static final NamespacedKey BLOCK_TEST = new NamespacedKey("cabot", "test");
    public static final NamespacedKey BLOCK_UNCRAFTING_TABLE = new NamespacedKey("cabot", "uncrafting_table");

    @Override
    public void bootstrap(BootstrapContext bootstrapContext) {
        BlockEngine.registerBlock(BLOCK_TEST, new BlockRegistration(TestBlock.class, new NamespacedKey("minecraft", "crafting_table")));
        BlockEngine.registerBlock(BLOCK_UNCRAFTING_TABLE, new BlockRegistration(UncraftingTableBlock.class, Models.UNCRAFTING_TABLE));

        var lifecycleManager = bootstrapContext.getLifecycleManager();

        lifecycleManager.registerEventHandler(RegistryEvents.ENCHANTMENT.freeze().newHandler(event -> {
            event.registry().register(
                    EnchantmentKeys.create(ENCHANTMENT_GOD),
                    b -> b.description(Component.text("God"))
                            .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_ARMOR))
                            .activeSlots(EquipmentSlotGroup.ANY)
                            .anvilCost(1)
                            .maxLevel(1)
                            .weight(1)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(9999, 9999))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(99991, 9999))
            );

            event.registry().register(
                    EnchantmentKeys.create(ENCHANTMENT_RAILGUN),
                    b -> b.description(Component.text("Railgun"))
                            .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_CROSSBOW))
                            .primaryItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_CROSSBOW))
                            .activeSlots(EquipmentSlotGroup.ANY)
                            .anvilCost(1)
                            .maxLevel(1)
                            .weight(7)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(25, 0))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 0))
            );

            event.registry().register(
                    EnchantmentKeys.create(ENCHANTMENT_FLIGHT),
                    b -> b.description(Component.text("Flight"))
                            .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_CHEST_ARMOR))
                            .activeSlots(EquipmentSlotGroup.ANY)
                            .anvilCost(1)
                            .maxLevel(1)
                            .weight(1)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(9999, 9999))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(99991, 9999))
            );


            event.registry().register(
                    EnchantmentKeys.create(ENCHANTMENT_SENTIENCE),
                    b -> b.description(Component.text("Sentience"))
                            .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_CHEST_ARMOR))
                            .activeSlots(EquipmentSlotGroup.ANY)
                            .anvilCost(1)
                            .maxLevel(1)
                            .weight(1)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(9999, 9999))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(99991, 9999))
            );

            event.registry().register(
                    EnchantmentKeys.create(ENCHANTMENT_VEINMINER),
                    b -> b.description(Component.text("Veinminer"))
                            .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_CHEST_ARMOR))
                            .activeSlots(EquipmentSlotGroup.ANY)
                            .anvilCost(1)
                            .maxLevel(1)
                            .weight(1)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(9999, 9999))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(99991, 9999))
            );

            event.registry().register(
                    EnchantmentKeys.create(ENCHANTMENT_REACH),
                    b -> b.description(Component.text("Reach"))
                            .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_WEAPON))
                            .primaryItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_MINING))
                            .activeSlots(EquipmentSlotGroup.MAINHAND)
                            .anvilCost(10)
                            .maxLevel(3)
                            .weight(1)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(9999, 9999))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(99991, 9999))

            );
        }));

        // make railgun enchant tradable in the jungle
        lifecycleManager.registerEventHandler(LifecycleEvents.TAGS.postFlatten(EnchantmentTagKeys.TRADES_JUNGLE_COMMON.registryKey()).newHandler(event -> {
            var enchTag = TypedKey.create(RegistryKey.ENCHANTMENT, ENCHANTMENT_RAILGUN);
            event.registrar().addToTag(EnchantmentTagKeys.TRADES_JUNGLE_COMMON, (Collection) List.of(enchTag));
        }));

        // make veinminer enchant tradable in the swamp
        lifecycleManager.registerEventHandler(LifecycleEvents.TAGS.postFlatten(EnchantmentTagKeys.TRADES_SWAMP_COMMON.registryKey()).newHandler(event -> {
            var enchTag = TypedKey.create(RegistryKey.ENCHANTMENT, ENCHANTMENT_VEINMINER);
            event.registrar().addToTag(EnchantmentTagKeys.TRADES_SWAMP_COMMON, (Collection) List.of(enchTag));
        }));

        // add railgun enchant to enchantment table
        lifecycleManager.registerEventHandler(LifecycleEvents.TAGS.postFlatten(EnchantmentTagKeys.IN_ENCHANTING_TABLE.registryKey()).newHandler(event -> {
            var enchTag = TypedKey.create(RegistryKey.ENCHANTMENT, ENCHANTMENT_RAILGUN);
            event.registrar().addToTag(EnchantmentTagKeys.IN_ENCHANTING_TABLE, (Collection) List.of(enchTag));

        }));

    }


}
