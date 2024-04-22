package dev.cabotmc.cabotenchants.career;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UnlockRewardCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("cabot.enchants.unlock")) {
            sender.sendMessage("You do not have permission to use this command");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("Usage: /unlock <reward>");
            return true;
        }

        var reward = RewardManager.getReward(args[0]);
        if (reward == null) {
            sender.sendMessage("Unknown reward: " + args[0]);
            return true;
        }

        RewardManager.unlockReward(args[0], (Player) sender);
        return true;
    }
}
