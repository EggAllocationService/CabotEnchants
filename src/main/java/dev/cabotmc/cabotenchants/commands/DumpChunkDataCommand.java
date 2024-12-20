package dev.cabotmc.cabotenchants.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class DumpChunkDataCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        var player = (Player) commandSourceStack.getSender();

        var chunk = player.getLocation().getChunk();

        var container = chunk.getPersistentDataContainer();
        for (var key : container.getKeys()) {
            player.sendMessage(key.toString() + " -> " + container.get(key, PersistentDataType.STRING));
        }
    }
}
