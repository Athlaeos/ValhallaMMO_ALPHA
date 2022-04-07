package me.athlaeos.valhallammo.events;

import me.athlaeos.valhallammo.skills.Skill;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerSkillExperienceGainEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean isCancelled;
    private final Player player;
    private double amount;
    private Skill leveledSkill;

    public PlayerSkillExperienceGainEvent(Player player, double amount, Skill leveledSkill){
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

    public Player getPlayer() {
        return player;
    }

    public double getAmount() {
        return amount;
    }

    public Skill getLeveledSkill() {
        return leveledSkill;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
