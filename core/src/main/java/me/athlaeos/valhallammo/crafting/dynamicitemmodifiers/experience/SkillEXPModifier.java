package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.experience;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.events.PlayerSkillExperienceGainEvent;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class SkillEXPModifier extends DynamicItemModifier {
    private final String skillType;
    private final String camelCaseSkill;
    public SkillEXPModifier(String name, double strength, ModifierPriority priority, String skillType) {
        super(name, strength, priority);

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
        camelCaseSkill = Utils.toPascalCase(skillType.toString());

        this.description = Utils.chat("&7Grants the player an amount of EXP as additional " + camelCaseSkill + " EXP");
        this.displayName = Utils.chat("&7&lGive player " + camelCaseSkill + " EXP");
        this.icon = Material.EXPERIENCE_BOTTLE;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<exp_reward_base>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        if (crafter == null) return null;

        if (this.use){
            Skill skill = SkillProgressionManager.getInstance().getSkill(skillType);
            if (skill != null){
                skill.addEXP(crafter, strength, false, PlayerSkillExperienceGainEvent.ExperienceGainReason.SKILL_ACTION);
            }
        }
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Gives the player %.1f additional " + camelCaseSkill + " EXP.", strength));
    }
}
