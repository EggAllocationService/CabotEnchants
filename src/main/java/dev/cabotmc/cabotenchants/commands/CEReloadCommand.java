package dev.cabotmc.cabotenchants.commands;

import dev.cabotmc.cabotenchants.CabotEnchants;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;

public class CEReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("cabotenchants.reload")) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }
        sender.sendMessage("Reloading CabotEnchants...");
        try {
            CabotEnchants.q.loadConfigs(
                    Files.readString(CabotEnchants.configFile)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sender.sendMessage("Reloaded CabotEnchants.");
        return true;
    }
}
