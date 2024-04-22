package dev.cabotmc.cabotenchants.quest;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class QuestCompleteEvent extends Event {
    private Quest completed;
    private Player player;

    public QuestCompleteEvent(Quest completed, Player player) {
        this.completed = completed;
        this.player = player;
    }

    public Quest getCompletedQuest() {
        return completed;
    }

    public Player getPlayer() {
        return player;
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
