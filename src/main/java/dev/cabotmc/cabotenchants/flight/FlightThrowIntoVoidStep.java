package dev.cabotmc.cabotenchants.flight;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import dev.cabotmc.cabotenchants.CabotEnchants;
import dev.cabotmc.cabotenchants.quest.QuestStep;
import dev.cabotmc.cabotenchants.util.Models;
import io.papermc.paper.event.entity.EntityMoveEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class FlightThrowIntoVoidStep extends QuestStep {

    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.FEATHER);
        var m = i.getItemMeta();
        m.setItemModel(Models.FLIGHT_ESSENCE);
        m.displayName(
                Component.text("Essence of Flight")
                        .color(TextColor.color(0x15F570))
                        .decoration(TextDecoration.ITALIC, false)
        );

        m.lore(
                List.of(
                        Component.text("The feather has to turned into the pure essence of flight.")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.DARK_GRAY),
                        Component.text("Yet, it seems to be too young to be able to do anything")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.DARK_GRAY),
                        Component.text("I think I must force it to grow up, like a mother bird kicking a baby out of its nest")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(NamedTextColor.DARK_GRAY)
                )

        );
        i.setItemMeta(m);
        return i;
    }
    private static final NamespacedKey Y_LEVEL_KEY = new NamespacedKey("cabot", "flight_y_level");
    @EventHandler
    public void onThrowItem(PlayerDropItemEvent e) {
        var item = e.getItemDrop();
        var stack = item.getItemStack();
        if (stack.getType() == Material.FEATHER && isStepItem(stack)) {
            item.getPersistentDataContainer()
                    .set(Y_LEVEL_KEY, PersistentDataType.INTEGER,  e.getPlayer().getLocation().getBlockY() + 1);

        }
    }

    @EventHandler
    public void onItemVoid(EntityRemoveFromWorldEvent e) {
        if (e.getEntity().getType() != EntityType.ITEM) return;
        var item = (Item) e.getEntity();
        if (item.getLocation().getY() > 0.0) return;
        if (!item.getPersistentDataContainer().has(Y_LEVEL_KEY)) return;

        var loc = item.getLocation();
        loc = loc.set(loc.getX(), -20.0, loc.getZ());
        var job = new RiseAnimation(getNextStep().createStepItem(), item.getPersistentDataContainer().get(Y_LEVEL_KEY, PersistentDataType.INTEGER), loc);
        job.setTaskId(Bukkit.getScheduler().scheduleSyncRepeatingTask(CabotEnchants.getPlugin(CabotEnchants.class), job, 0, 1));

    }

    static class RiseAnimation implements Runnable {
        ItemStack item;
        double targetYLevel;
        Location location;

        int taskId;

        public void setTaskId(int taskId) {
            this.taskId = taskId;
        }

        public RiseAnimation(ItemStack item, double targetYLevel, Location location) {
            this.item = item;
            this.targetYLevel = targetYLevel;
            this.location = location;
        }

        @Override
        public void run() {

            if (location.getY() + 0.5 >= targetYLevel) {
                var loc = location
                        .set(location.getX(), targetYLevel + 0.5, location.getZ())
                        .toCenterLocation();
                var i = (Item) location.getWorld()
                                .spawnEntity(loc, EntityType.ITEM);
                i.teleport(loc);
                i.setItemStack(item);
                i.setGravity(false);
                i.setVelocity(new org.bukkit.util.Vector(0, 0, 0));
                var firework = (Firework) location.getWorld().spawnEntity(loc, EntityType.FIREWORK_ROCKET);
                var m =firework.getFireworkMeta();
                m.addEffect(
                        FireworkEffect.builder()
                                .withColor(Color.fromRGB(0x15F570))
                                .with(FireworkEffect.Type.BALL_LARGE)
                                .withFlicker()
                                .build()
                );
                firework.setFireworkMeta(m);
                firework.detonate();
                Bukkit.getScheduler().cancelTask(taskId);
            } else {
                location.getWorld()
                        .spawnParticle(
                                Particle.EXPLOSION,
                                location,
                                5,
                                0.5,
                                0.5,
                                0.5,
                                0.1
                        );
                location
                        .getWorld()
                                .playSound(
                                        location,
                                        Sound.ENTITY_GENERIC_EXPLODE,
                                        0.2f,
                                        1.5f
                                );
                location.add(0, 0.5, 0);
            }
        }
    }
}
