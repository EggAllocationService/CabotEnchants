package dev.cabotmc.cabotenchants.godarmor;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import dev.cabotmc.cabotenchants.CEBootstrap;
import dev.cabotmc.cabotenchants.quest.QuestStep;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GodChestplate extends QuestStep {

    private static final double PROTECTION_RADIUS = 1.5d;
    private static final NamespacedKey PROJECTILE_TAG = new NamespacedKey("cabot", "projectile_owner");

    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.NETHERITE_CHESTPLATE);
        var meta = (ArmorMeta) i.getItemMeta();
        meta.displayName(
                MiniMessage
                        .miniMessage()
                        .deserialize("<!i><rainbow>Cosmic Chestplate")
        );
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ARMOR_TRIM);
        meta.setUnbreakable(true);
        meta.addEnchant(Enchantment.THORNS, 3, false);
        meta.addEnchant(RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(CEBootstrap.ENCHANTMENT_FLIGHT), 1, false);
        meta.lore(
                List.of(
                        Component.text("Projectile Protection \u221E")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false),
                        Component.text("Thorns III")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false),
                        Component.text("Flight")
                                .color(NamedTextColor.GRAY)
                                .decoration(TextDecoration.ITALIC, false),
                        Component.empty(),
                        Component.text("Like bug spray for arrows")
                                .color(NamedTextColor.DARK_GRAY)
                )
        );
        meta.addAttributeModifier(Attribute.MAX_HEALTH,
                new AttributeModifier(new NamespacedKey("cabot", "god_health"), 2.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST));
        i.setItemMeta(meta);
        return i;
    }

    @EventHandler
    public void tagProjectiles(ProjectileLaunchEvent e) {
        UUID shooterId;
        if (e.getEntity().getShooter() instanceof Entity) {
            shooterId = ((Entity) e.getEntity().getShooter()).getUniqueId();
        } else {
            shooterId = UUID.randomUUID();
        }

        e.getEntity().getPersistentDataContainer().set(PROJECTILE_TAG, PersistentDataType.LONG_ARRAY,
                new long[]{shooterId.getMostSignificantBits(), shooterId.getLeastSignificantBits()});
    }

    @EventHandler
    public void catchProjectiles(ServerTickStartEvent e) {
        // find all players wearing the chestplate
        // for each player, find all projectiles within PROTECTION_RADIUS
        // for each projectile, check if the owner is the player
        // if not, deflect the projectile away from the center of the player

        ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers().size());
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isStepItem(p.getInventory().getChestplate())) {
                players.add(p);
            }
        }

        for (Player p : players) {
            var centerLocation = p.getLocation().add(0, 1, 0);
            for (Entity projectile : p.getNearbyEntities(PROTECTION_RADIUS * 3, PROTECTION_RADIUS * 3, PROTECTION_RADIUS * 3)) {
                if (!(projectile instanceof Projectile)) continue;

                // check if the projectile is within the protection radius (spherical)
                if (projectile.getLocation().distanceSquared(centerLocation) > PROTECTION_RADIUS * PROTECTION_RADIUS) {
                    continue;
                }

                if (!projectile.getPersistentDataContainer().has(PROJECTILE_TAG, PersistentDataType.LONG_ARRAY)) {
                    continue;
                }
                var uuid_parts = projectile.getPersistentDataContainer().get(PROJECTILE_TAG, PersistentDataType.LONG_ARRAY);
                UUID shooterId = new UUID(
                        uuid_parts[0],
                        uuid_parts[1]
                );

                if (shooterId.equals(p.getUniqueId())) {
                    continue;
                }

                // deflect

                var norm = projectile.getLocation().toVector().subtract(centerLocation.toVector()).normalize();
                projectile.setVelocity(norm.multiply(projectile.getVelocity().length()));
            }
        }
    }

    @EventHandler
    public void hit(ProjectileHitEvent e) {
        // catch projectiles that were too fast
        if (e.getHitEntity() == null || !(e.getHitEntity() instanceof Player)) {
            return;
        }

        if (!e.getEntity().getPersistentDataContainer().has(PROJECTILE_TAG, PersistentDataType.LONG_ARRAY)) {
            return;
        }
        var uuid_parts = e.getEntity().getPersistentDataContainer().get(PROJECTILE_TAG, PersistentDataType.LONG_ARRAY);
        UUID shooterId = new UUID(
                uuid_parts[0],
                uuid_parts[1]
        );

        if (shooterId.equals(e.getHitEntity().getUniqueId())) {
            return;
        }
        if (!isStepItem(((Player) e.getHitEntity()).getInventory().getChestplate())) {
            return;
        }
        e.setCancelled(true);
    }
}
