package dev.cabotmc.cabotenchants.quest;

import dev.cabotmc.cabotenchants.packet.EnchantmentLoreAdapter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class QuestStep implements Listener {
  public static final NamespacedKey QUEST_ID_KEY = new NamespacedKey("cabot", "quest_id");
  public static final NamespacedKey QUEST_STEP_KEY = new NamespacedKey("cabot", "quest_step");
  public static final NamespacedKey NO_STACK_KEY = new NamespacedKey("cabot", "stack_nonce");
  private int stepNum;
  private Quest quest;

  public int getStepNum() {
    return stepNum;
  }

  public void setStepNum(int stepNum) {
    this.stepNum = stepNum;
  }

  public Quest getQuest() {
    return quest;
  }

  public void setQuest(Quest quest) {
    this.quest = quest;
  }

  public QuestStep getNextStep() {
    return quest.getStep(stepNum + 1);
  }

  protected abstract ItemStack internalCreateStepItem();
  private static final TextColor LIST_NOT_DONE_COLOR = TextColor.color(0x333333);
    private static final TextColor LIST_DONE_COLOR = TextColor.color(0x00ff00);
  protected Component createChecklistLore(ChecklistTracker tracker) {
    Component base = Component.empty();
    var list = tracker.getChecklist();
    for (boolean b : list) {
      var color = b ? LIST_DONE_COLOR : LIST_NOT_DONE_COLOR;
      base = base.append(Component.text("\u2022 ").color(color)
              .decoration(TextDecoration.ITALIC, false));
    }
    return base;
  }

  public ItemStack createStepItem() {
    ItemStack item = internalCreateStepItem();
    var m = item.getItemMeta();
    m.getPersistentDataContainer().set(QUEST_ID_KEY, PersistentDataType.INTEGER, quest.questId);
    m.getPersistentDataContainer().set(QUEST_STEP_KEY, PersistentDataType.INTEGER, stepNum);
    m.getPersistentDataContainer().set(NO_STACK_KEY, PersistentDataType.STRING, UUID.randomUUID().toString());
    item.setItemMeta(m);
    EnchantmentLoreAdapter.modify(item);
    return item;
  }
  public ItemFindResult getStepItem(Player p) {
    var results = getStepItems(p, true);
    if (results.isEmpty()) return null;
    return results.get(0);
  }
  public List<ItemFindResult> getStepItems(Player p, boolean single) {
    HashMap<Integer, ItemStack> items = new HashMap<>();
    for (int i = 0; i < p.getInventory().getSize(); i++) {
      var item = p.getInventory().getItem(i);
      if (item == null) continue;
      var m = item.getItemMeta();
      if (m == null) continue;
      if (m.getPersistentDataContainer().has(QUEST_ID_KEY, PersistentDataType.INTEGER) &&
              m.getPersistentDataContainer().has(QUEST_STEP_KEY, PersistentDataType.INTEGER)) {
        if (m.getPersistentDataContainer().get(QUEST_ID_KEY, PersistentDataType.INTEGER) == quest.questId &&
                m.getPersistentDataContainer().get(QUEST_STEP_KEY, PersistentDataType.INTEGER) == stepNum) {
          items.put(i, item);
          if (single) break;
        }
      }
    }
    return items.entrySet()
            .stream()
            .map(e -> new ItemFindResult(e.getValue(), e.getKey()))
            .toList();
  }

  public boolean isStepItem(ItemStack i) {
    if (i == null) return false;
    var m = i.getItemMeta();
    if (m == null) return false;
    if (m.getPersistentDataContainer().has(QUEST_ID_KEY, PersistentDataType.INTEGER) &&
            m.getPersistentDataContainer().has(QUEST_STEP_KEY, PersistentDataType.INTEGER)) {
      if (m.getPersistentDataContainer().get(QUEST_ID_KEY, PersistentDataType.INTEGER) == quest.questId &&
              m.getPersistentDataContainer().get(QUEST_STEP_KEY, PersistentDataType.INTEGER) == stepNum) {
        return true;
      }
    }
    return false;
  }
  protected void replaceWithNextStep(Player p, int i) {
    p.getInventory().setItem(i, getNextStep().createStepItem());
    p.playSound(p.getLocation(), "minecraft:entity.player.levelup", 1, 1.5f);
  }

  public record ItemFindResult(ItemStack item, int slot) {}
}
