package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.experience;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DuoArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.events.PlayerSkillExperienceGainEvent;
import me.athlaeos.valhallammo.items.MaterialClass;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class SkillEXPModifier extends DuoArgDynamicItemModifier {
    private final String skillType;
    private final String camelCaseSkill;
    public SkillEXPModifier(String name, String skillType) {
        super(name, 0D, 0D, ModifierPriority.NEUTRAL);

        this.name = name;
        this.skillType = skillType;

        this.category = ModifierCategory.EXPERIENCE;
        this.bigStepDecrease = 25D;
        this.bigStepIncrease = 25D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 1000000D;

        this.bigStepDecrease2 = 1;
        this.bigStepIncrease2 = 1;
        this.smallStepDecrease2 = 1;
        this.smallStepIncrease2 = 1;
        this.defaultStrength2 = 0;
        this.minStrength2 = 0;
        this.maxStrength2 = MaterialClass.values().length;

        camelCaseSkill = Utils.toPascalCase(skillType);

        this.description = Utils.chat("&7Grants the player an amount of EXP as additional " + camelCaseSkill + " EXP. " +
                (skillType.equals("SMITHING") ? "In the case of smithing, this experience may also scale with a player's material proficiency" : ""));
        this.displayName = Utils.chat("&7&lGive player " + camelCaseSkill + " EXP");
        this.icon = Material.EXPERIENCE_BOTTLE;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<exp_reward_base>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        if (outputItem == null) return null;
        if (crafter == null) return null;

        if (this.use){
            Skill skill = SkillProgressionManager.getInstance().getSkill(skillType);
            if (skill != null){
                double materialMultiplier = 1D;
                if (skillType.equals("SMITHING")){
                    if ((int) strength2 > 0){
                        MaterialClass materialClass = MaterialClass.values()[(int) strength2 - 1];
                        materialMultiplier = (AccumulativeStatManager.getInstance().getStats("SMITHING_EXP_GAIN_" + materialClass, crafter, true)) / 100D;
                    }
                }
                skill.addEXP(crafter, timesExecuted * strength * materialMultiplier, false, PlayerSkillExperienceGainEvent.ExperienceGainReason.SKILL_ACTION);
            }
        }
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Gives the player %.1f additional " + camelCaseSkill + " EXP. %s", strength,
                skillType.equals("SMITHING") && (int) strength2 > 0 ? String.format("This experience is then multiplied by the player's " +
                        "%s crafting proficiency EXP multiplier", MaterialClass.values()[(int) strength2 - 1]) : ""));
    }
}
