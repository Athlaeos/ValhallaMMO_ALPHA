package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_conditionals;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.InvisibleIfIncompatibleModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.items.OverleveledEquipmentTool;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class SkillLevelRequirementAddModifier extends DynamicItemModifier implements InvisibleIfIncompatibleModifier {
    private final String displaySkill;
    private final Skill skill;

    public SkillLevelRequirementAddModifier(String name, String skill, Material icon) {
        super(name, 0D, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_MISC;

        this.bigStepDecrease = 20D;
        this.bigStepIncrease = 20D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = Integer.MAX_VALUE;

        this.skill = SkillProgressionManager.getInstance().getSkill(skill);
        displaySkill = Utils.toPascalCase(skill);
        if (this.skill == null){
            this.description = Utils.chat("&7&cBroken modifier, notify plugin/add-on author responsible for &e" + skill
                    + "&7 because it doesn't exist. This modifier does not do anything");
            this.displayName = Utils.chat("&cSomeone messed up");
        } else {
            this.description = Utils.chat("&7Requires the user of the item to have " + displaySkill + " at a specific" +
                    " level or better to be able to fully benefit from the item. If the user does not have the required " +
                    "level minimum the item will be significantly weaker the further the player is from the required goal." +
                    " If &e0&7, the requirement is removed instead.");
            this.displayName = Utils.chat("&7&lSkill Level Requirement: " + displaySkill);
        }
        this.icon = icon;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<intended_usage_level>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        if (strength <= 0){
            OverleveledEquipmentTool.getTool().removeSkillRequirement(outputItem, skill.getType());
        } else {
            OverleveledEquipmentTool.getTool().addSkillRequirement(outputItem, skill, (int) strength);
        }
        return outputItem;
    }

    @Override
    public String toString() {
        if (strength == 0){
            return Utils.chat("&7Removes the skill level requirement for &e" + displaySkill + "&7 from the item");
        } else {
            return Utils.chat("&7Requires the user of the item item to be at least level &e" + ((int) strength) +
                    "&7 in &e" + displaySkill + " &7to make full use of the item");
        }
    }
}
