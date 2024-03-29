package me.athlaeos.valhallammo.perkrewards.account;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.events.PlayerSkillExperienceGainEvent;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.Skill;
import org.bukkit.entity.Player;

public class SkillLevelsAddReward extends PerkReward {
    private int levels = 0;
    private final Skill skill;
    public SkillLevelsAddReward(String name, Skill skill) {
        super(name, 0F);
        this.skill = skill;
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        if (skill == null) return;
        if (levels <= 0) return;
        Profile p = ProfileManager.getManager().getProfile(player, skill.getType());
        if (p == null) return;
        double totalExpRequired =  -p.getExp();
        for (int level = p.getLevel() + 1; level <= p.getLevel() + levels; level++){
            totalExpRequired += skill.expForlevel(level);
        }
        skill.addEXP(player, Math.max(0, Math.ceil(totalExpRequired)), true, PlayerSkillExperienceGainEvent.ExperienceGainReason.COMMAND);
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Double){
                this.levels = (int) Math.floor((Double) argument);
            }
            if (argument instanceof Float){
                levels = (int) argument;
            }
            if (argument instanceof Integer){
                levels = (Integer) argument;
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.INTEGER;
    }
}
