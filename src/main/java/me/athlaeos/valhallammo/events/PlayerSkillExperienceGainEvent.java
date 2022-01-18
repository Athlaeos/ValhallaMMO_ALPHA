package me.athlaeos.valhallammo.skills.smithing.events;

import me.athlaeos.valhallammo.domain.SkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerSkillExperienceGainEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean isCancelled;
    private final Player player;
    private double amount;
    private SkillType leveledSkill;

    public PlayerSkillExperienceGainEvent(Player player, double amount, SkillType leveledSkill){
        this.player = player;
        this.amount = amount;
        this.leveledSkill = leveledSkill;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
