package me.athlaeos.valhallammo.perkrewards.account;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.events.PlayerSkillExperienceGainEvent;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.Skill;
import org.bukkit.entity.Player;

public class SkillExperienceAddReward extends PerkReward {
    private double experience = 0;
    private final Skill skill;
    public SkillExperienceAddReward(String name, Skill skill) {
        super(name, 0F);
        this.skill = skill;
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        if (skill == null) return;
        skill.addEXP(player, experience, true, PlayerSkillExperienceGainEvent.ExperienceGainReason.COMMAND);
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Integer){
                experience = (float) (Integer) argument;
            }
            if (argument instanceof Float){
                experience = (float) argument;
            }
            if (argument instanceof Double){
                double temp = (Double) argument;
                experience = (float) temp;
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.DOUBLE;
    }
}
