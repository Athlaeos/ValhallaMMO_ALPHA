package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.events.PlayerSkillExperienceGainEvent;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.items.attributewrappers.AttributeWrapper;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.skills.smithing.SmithingSkill;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class UpgradeIronModifier extends DynamicItemModifier {
    public UpgradeIronModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_MISC;

        this.bigStepDecrease = 0D;
        this.bigStepIncrease = 0D;
        this.smallStepDecrease = 0D;
        this.smallStepIncrease = 0D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 0;
        this.description = Utils.chat("&7Changes the item's type to &eIron &7and rewards the player Smithing EXP" +
                " for the craft appropriate to the item crafted.");
        this.displayName = Utils.chat("&7&lTransform to Iron");
        this.icon = Material.SMITHING_TABLE;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("0");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        if (!(meta instanceof Damageable)) return null;
        EquipmentClass equipmentClass = EquipmentClass.getClass(outputItem);
        if (equipmentClass != null){
            switch (equipmentClass){
                case CHESTPLATE: outputItem.setType(Material.IRON_CHESTPLATE);
                break;
                case LEGGINGS: outputItem.setType(Material.IRON_LEGGINGS);
                break;
                case HELMET: outputItem.setType(Material.IRON_HELMET);
                break;
                case BOOTS: outputItem.setType(Material.IRON_BOOTS);
                break;
                case SWORD: outputItem.setType(Material.IRON_SWORD);
                break;
                case PICKAXE: outputItem.setType(Material.IRON_PICKAXE);
                break;
                case SHOVEL: outputItem.setType(Material.IRON_SHOVEL);
                break;
                case HOE: outputItem.setType(Material.IRON_HOE);
                break;
                case AXE: outputItem.setType(Material.IRON_AXE);
                break;
            }
            for (AttributeWrapper wrapper : ItemAttributesManager.getInstance().getVanillaStats(outputItem).values()){
                // The item's vanilla stats are updated to their vanilla values, any added custom attributes are left alone
                ItemAttributesManager.getInstance().setDefaultAttributeStrength(outputItem, wrapper.getAttribute(), wrapper.getAmount());
            }
            if (this.use){
                Skill skill = SkillProgressionManager.getInstance().getSkill("SMITHING");
                if (skill != null){
                    if (skill instanceof SmithingSkill){
                        double expReward = ((SmithingSkill) skill).expForCraftedItem(crafter, outputItem);
                        skill.addEXP(crafter, expReward, false, PlayerSkillExperienceGainEvent.ExperienceGainReason.SKILL_ACTION);
                    }
                }
            }
            return outputItem;
        }
        return null;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Changing the item's type to &eIron&7 if possible.");
    }
}
