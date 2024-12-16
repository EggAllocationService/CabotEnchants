package dev.cabotmc.cabotenchants.spawner.quest;

import com.google.gson.Gson;
import dev.cabotmc.cabotenchants.quest.QuestStep;
import dev.cabotmc.cabotenchants.util.Models;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class DepletedSwordReward extends QuestStep {

    private static final NamespacedKey PROGRESS_KEY = new NamespacedKey("cabot", "depleted_sword_progress");

    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.STONE_SWORD);
        var m = i.getItemMeta();
        m.displayName(
                Component.text("Depleted Souldrinker")
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        );
        m.getPersistentDataContainer()
                .set(PROGRESS_KEY, BooleanCodec.INSTANCE, new boolean[3]);
        m.setUnbreakable(true);
        m.setItemModel(Models.DEPLETED_SOULDRINKER);
        renderDots(m);
        i.setItemMeta(m);
        return i;
    }

    static final TextColor WARDEN_DONE_COLOR = TextColor.color(0x284EF2);
    static final TextColor WARDEN_NOT_DONE_COLOR = TextColor.color(0x152152);
    static final TextColor WITHER_DONE_COLOR = TextColor.color(0x9e2d2d);
    static final TextColor WITHER_NOT_DONE_COLOR = TextColor.color(0x3a0f0f);
    static final TextColor DRAGON_DONE_COLOR = TextColor.color(0x9f36eb);
    static final TextColor DRAGON_NOT_DONE_COLOR = TextColor.color(0x37194F);


    void renderDots(ItemMeta m) {
        var progress = m.getPersistentDataContainer()
                .getOrDefault(PROGRESS_KEY, BooleanCodec.INSTANCE, new boolean[3]);
        var lore = new ArrayList<Component>();

        lore.add(
                Component.text("This sword has given its all to create a spawner.")
                        .color(NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        );
        lore.add(
                Component.text("You think you can change that.")
                        .color(NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        );
        lore.add(Component.empty());
        lore.add(
                Component.text("Kill each boss using ONLY this sword:")
                        .color(NamedTextColor.GREEN)
                        .decoration(TextDecoration.ITALIC, false)
        );

        // render a dot for each boss
        Component base = Component.empty();
        // Warden
        var wardenColor = progress[0] ? WARDEN_DONE_COLOR : WARDEN_NOT_DONE_COLOR;
        base = base.append(Component.text("\u2022 ").color(wardenColor).decoration(TextDecoration.ITALIC, false));
        // Wither
        var witherColor = progress[1] ? WITHER_DONE_COLOR : WITHER_NOT_DONE_COLOR;
        base = base.append(Component.text("\u2022 ").color(witherColor).decoration(TextDecoration.ITALIC, false));
        // Dragon
        var dragonColor = progress[2] ? DRAGON_DONE_COLOR : DRAGON_NOT_DONE_COLOR;
        base = base.append(Component.text("\u2022 ").color(dragonColor).decoration(TextDecoration.ITALIC, false));
        lore.add(base);
        m.lore(lore);
    }

    public static final NamespacedKey DIRTY_KEY = new NamespacedKey("cabot", "dirty_attacked");

    @EventHandler
    public void damage(EntityDamageByEntityEvent e) {
        if (e.getDamager().getType() != EntityType.PLAYER) return;

        // only obsses
        if (e.getEntityType() != EntityType.WARDEN
                && e.getEntityType() != EntityType.WITHER
                && e.getEntityType() != EntityType.ENDER_DRAGON) return;

        var p = (Player) e.getDamager();
        var item = p.getInventory().getItemInMainHand();
        // only wither, ender dragon, and warden
        if (!isStepItem(item)) {
            e.getEntity().getPersistentDataContainer()
                    .set(DIRTY_KEY, PersistentDataType.BYTE, (byte) 1);
        }
    }

    @EventHandler
    public void kill(EntityDeathEvent e) {
        var p = e.getEntity().getKiller();
        if (p == null) return;
        var item = p.getInventory().getItemInMainHand();
        if (!isStepItem(item)) return;

        if (e.getEntity().getPersistentDataContainer().has(DIRTY_KEY)) {
            return;
        }

        var meta = item.getItemMeta();
        var progress = meta.getPersistentDataContainer().getOrDefault(PROGRESS_KEY, BooleanCodec.INSTANCE, new boolean[3]);
        var type = e.getEntityType();
        var changed = false;
        if (type == EntityType.WITHER) {
            if (!progress[1]) {
                progress[1] = true;
                changed = true;
            }
        } else if (type == EntityType.ENDER_DRAGON) {
            if (!progress[2]) {
                progress[2] = true;
                changed = true;
            }
        } else if (type == EntityType.WARDEN) {
            if (!progress[0]) {
                progress[0] = true;
                changed = true;
            }
        }
        if (changed) {
            meta.getPersistentDataContainer().set(PROGRESS_KEY, BooleanCodec.INSTANCE, progress);
            renderDots(meta);
            item.setItemMeta(meta);
            p.playSound(
                    p.getLocation(),
                    Sound.ENTITY_PLAYER_LEVELUP,
                    1.0f,
                    1.0f
            );

            if (progress[0] && progress[1] && progress[2]) {
                getQuest().markCompleted(p);
                replaceWithNextStep(p, p.getInventory().getHeldItemSlot());
            }
        }
    }


    public static class BooleanCodec implements PersistentDataType<String, boolean[]> {
        public static final BooleanCodec INSTANCE = new BooleanCodec();

        @Override
        public @NotNull Class<String> getPrimitiveType() {
            return String.class;
        }

        @Override
        public @NotNull Class<boolean[]> getComplexType() {
            return boolean[].class;
        }

        @Override
        public @NotNull String toPrimitive(boolean @NotNull [] complex, @NotNull PersistentDataAdapterContext context) {
            return new Gson().toJson(complex);
        }

        @Override
        public boolean @NotNull [] fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
            return new Gson().fromJson(primitive, boolean[].class);
        }
    }
}
