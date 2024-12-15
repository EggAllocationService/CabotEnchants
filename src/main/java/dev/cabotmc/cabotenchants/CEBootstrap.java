package dev.cabotmc.cabotenchants;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;

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
                            .anvilCost(1)
                            .maxLevel(1)
                            .weight(0)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(9999, 9999))
            );

            event.registry().register(
                    EnchantmentKeys.create(ENCHANTMENT_RAILGUN),
                    b -> b.description(Component.text("Railgun"))
                            .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_CROSSBOW))
                            .anvilCost(1)
                            .maxLevel(1)
                            .weight(0)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(9999, 9999))
            );

            event.registry().register(
                    EnchantmentKeys.create(ENCHANTMENT_FLIGHT),
                    b -> b.description(Component.text("Flight"))
                            .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_CHEST_ARMOR))
                            .anvilCost(1)
                            .maxLevel(1)
                            .weight(0)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(9999, 9999))
            );

            event.registry().register(
                    EnchantmentKeys.create(ENCHANTMENT_SENTIENCE),
                    b -> b.description(Component.text("Sentience"))
                            .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_CHEST_ARMOR))
                            .anvilCost(1)
                            .maxLevel(1)
                            .weight(0)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(9999, 9999))
            );

            event.registry().register(
                    EnchantmentKeys.create(ENCHANTMENT_VEINMINER),
                    b -> b.description(Component.text("Veinminer"))
                            .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_CHEST_ARMOR))
                            .anvilCost(1)
                            .maxLevel(1)
                            .weight(0)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(9999, 9999))
            );
        }));
    }
}
