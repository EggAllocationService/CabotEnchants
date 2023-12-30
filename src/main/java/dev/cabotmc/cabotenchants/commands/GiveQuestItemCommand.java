package dev.cabotmc.cabotenchants.commands;

import dev.cabotmc.cabotenchants.CabotEnchants;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GiveQuestItemCommand implements CommandExecutor {
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (args.length != 2) {
      return false;
    }
    if (!sender.hasPermission("cabotenchants.givequestitem")) {
      return false;
    }
    var questId = Integer.parseInt(args[0]);
    var stepId = Integer.parseInt(args[1]);
    var quest = CabotEnchants.q.getQuest(questId);
    ((Player) sender).getInventory().addItem(quest.getStep(stepId).createStepItem());

    return true;
  }
}
