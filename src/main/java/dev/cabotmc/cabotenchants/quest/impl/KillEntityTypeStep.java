package dev.cabotmc.cabotenchants.quest.impl;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public abstract class KillEntityTypeStep extends KillEntityStep {
    EntityType[] types;

    public KillEntityTypeStep(int amount, EntityType... types) {
        super(amount);
        this.types = types;
    }

    @Override
    protected boolean isValidKill(LivingEntity e) {
        var t = e.getType();
        for (var type : types) {
            if (t == type) return true;
        }
        return false;
    }
}
