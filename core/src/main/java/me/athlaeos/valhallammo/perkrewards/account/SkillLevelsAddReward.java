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
        double totalExpRequired = 0;
        for (int level = p.getLevel(); level <= p.getLevel() + levels; levels++){
            if (level == p.getLevel()) {
                totalExpRequired += skill.expForlevel(level) - p.getExp();
            } else {
                totalExpRequired += skill.expForlevel(level);
            }
        }
        skill.addEXP(player, totalExpRequired, true, PlayerSkillExperienceGainEvent.ExperienceGainReason.COMMAND);
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Double){
                this.levels = (int) Math.floor((Double) argument);
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
