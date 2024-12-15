package dev.cabotmc.cabotenchants;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.TagEntry;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("ALL")
public class CEBootstrap implements PluginBootstrap {

    public static final Key ENCHANTMENT_GOD = Key.key("cabot", "god");
    public static final @NotNull Key ENCHANTMENT_RAILGUN = Key.key("cabot", "railgun");
    public static final @NotNull Key ENCHANTMENT_FLIGHT = Key.key("cabot", "flight");
    public static final @NotNull Key ENCHANTMENT_SENTIENCE = Key.key("cabot", "sentience");
    public static final @NotNull Key ENCHANTMENT_VEINMINER = Key.key("cabot", "veinminer");

    @Override
    public void bootstrap(BootstrapContext bootstrapContext) {
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
                            .activeSlots(EquipmentSlotGroup.ANY)
                            .anvilCost(1)
                            .maxLevel(1)
                            .weight(1)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(25, 0))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 1))
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
        }));

        // make railgun enchant tradable in the jungle
        lifecycleManager.registerEventHandler(LifecycleEvents.TAGS.preFlatten(EnchantmentTagKeys.TRADES_JUNGLE_COMMON.registryKey()).newHandler(event -> {
            var enchTag = TagEntry.tagEntry(TagKey.create(RegistryKey.ENCHANTMENT, ENCHANTMENT_RAILGUN));
            event.registrar().addToTag(EnchantmentTagKeys.TRADES_JUNGLE_COMMON, (Collection)List.of(enchTag));
        }));

        // make veinminer enchant tradable in the swamp
        lifecycleManager.registerEventHandler(LifecycleEvents.TAGS.preFlatten(EnchantmentTagKeys.TRADES_SWAMP_COMMON.registryKey()).newHandler(event -> {
            var enchTag = TagEntry.tagEntry(TagKey.create(RegistryKey.ENCHANTMENT, ENCHANTMENT_VEINMINER));
            event.registrar().addToTag(EnchantmentTagKeys.TRADES_SWAMP_COMMON, (Collection)List.of(enchTag));
        }));

        // add railgun enchant to enchantment table
        lifecycleManager.registerEventHandler(LifecycleEvents.TAGS.preFlatten(EnchantmentTagKeys.IN_ENCHANTING_TABLE.registryKey()).newHandler(event -> {
            var enchTag = TagEntry.tagEntry(TagKey.create(RegistryKey.ENCHANTMENT, ENCHANTMENT_RAILGUN));
            event.registrar().addToTag(EnchantmentTagKeys.IN_ENCHANTING_TABLE, (Collection)List.of(enchTag));
        }));

    }


}
