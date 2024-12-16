package dev.cabotmc.cabotenchants.sentient.quest;

import dev.cabotmc.cabotenchants.quest.QuestStep;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Repairable;

import java.util.List;

public class TridentRewardItem extends QuestStep {

    Enchantment SENTIENCE = Enchantment.getByKey(new NamespacedKey("cabot", "sentience"));
    @Override
    protected ItemStack internalCreateStepItem() {
        var i = new ItemStack(Material.TRIDENT);
        var m = (Repairable) i.getItemMeta();
        m.displayName(
                MiniMessage
                        .miniMessage()
                        .deserialize("<!i><rainbow>Sentient Ancient Trident")
        );

        m.lore(
                List.of(
                        Component.empty(),
                        Component.text("Duck")
                                .color(NamedTextColor.DARK_GRAY)
                )
        );
        m.setRepairCost(9999);
        i.setItemMeta(m);
        i.addUnsafeEnchantment(SENTIENCE, 1);
        i.addUnsafeEnchantment(Enchantment.SHARPNESS, 5);
        i.addUnsafeEnchantment(Enchantment.LOYALTY, 3);
        i.addUnsafeEnchantment(Enchantment.UNBREAKING, 5);
        i.addUnsafeEnchantment(Enchantment.MENDING, 1);
        return i;
    }
}
