package dev.cabotmc.cabotenchants.commands;

import dev.cabotmc.cabotenchants.CabotEnchants;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GiveQuestItemCommand implements BasicCommand {

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 2) {
            return false;
        }
        if (!sender.hasPermission("cabotenchants.givequestitem")) {
            return false;
        }

        var questId = args[0];
        var stepId = Integer.parseInt(args[1]);
        var quest = CabotEnchants.q.getQuest(questId);
        ((Player) sender).getInventory().addItem(quest.getStep(stepId).createStepItem());

        return true;
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        onCommand(commandSourceStack.getSender(), null, "thing", args);
    }
}
