package dev.cabotmc.cabotenchants.career;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CosmeticsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("cabot.enchants.cosmetics")) {
            sender.sendMessage("You do not have permission to use this command");
            return true;
        }
        var gui = new RewardsGUI((Player) sender);
        Bukkit.getPluginManager().registerEvents(gui, Bukkit.getPluginManager().getPlugin("CabotEnchants"));
        gui.render();
        return true;
    }
}
