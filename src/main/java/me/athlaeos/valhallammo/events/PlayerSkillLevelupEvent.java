package me.athlaeos.valhallammo.events;

import me.athlaeos.valhallammo.skills.Skill;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerSkillLevelupEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Skill leveledSkill;
    private final int levelTo;
    private final Player player;

    public PlayerSkillLevelupEvent(Player player, Skill leveledSkill, int levelTo){
        this.player = player;
        this.leveledSkill = leveledSkill;
        this.levelTo = levelTo;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public Skill getLeveledSkill() {
        return leveledSkill;
    }

    public int getLevelTo() {
        return levelTo;
    }

    public Player getPlayer() {
        return player;
    }

}
