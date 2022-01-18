package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats.DynamicItemModifier;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.skills.CraftingSkill;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SkillEXPModifier extends DynamicItemModifier {
    private final SkillType skillType;
    private final String camelCaseSkill;
    public SkillEXPModifier(String name, double strength, ModifierPriority priority, SkillType skillType) {
        super(name, strength, priority);

        this.name = name;
        this.skillType = skillType;

        this.bigStepDecrease = 10D;
        this.bigStepIncrease = 10D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 1000D;
        camelCaseSkill = skillType.toString().toLowerCase().replaceFirst(
                "" + Character.toUpperCase(skillType.toString().charAt(0)),
                "" + skillType.toString().charAt(0));

        this.description = Utils.chat("&7Grants the player a % of the item type's EXP rewards as additional " + camelCaseSkill + " EXP");
        this.displayName = Utils.chat("&7&lGive player " + camelCaseSkill + " EXP");
        this.icon = Material.EXPERIENCE_BOTTLE;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        if (crafter == null) return null;

        if (this.use){
            Skill skill = SkillProgressionManager.getInstance().getSkill(skillType);
            if (skill != null){
                if (skill instanceof CraftingSkill){
                    double expReward = (strength / 100) * ((CraftingSkill) skill).expForCraftedItem(crafter, outputItem);
                    skill.addEXP(crafter, expReward);
                }
            }
        }
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Gives the player %.1f%% of the item's usual EXP reward as additional " + camelCaseSkill + " EXP.", strength));
    }
}
