package me.athlaeos.valhallammo.items;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.CooldownManager;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.utility.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class OverleveledEquipmentTool {
    private final NamespacedKey skillRequirementKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_overleveled_skill_requirement");
    private static OverleveledEquipmentTool tool = null;

    private final String penaltyMultiplierFormula;
    private final Map<String, Double> penaltyValues = new HashMap<>();
    private final String tooAdvancedWarning;
    private final int warningDisplayOption;
    private final int overleveledWarningDelay;

    public OverleveledEquipmentTool(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("config.yml").get();
        penaltyMultiplierFormula = config.getString("overleveled_equipment_effectiveness_formula");
        ConfigurationSection valuesSection = config.getConfigurationSection("overleveled_equipment_penalties");
        if (valuesSection != null){
            for (String type : valuesSection.getKeys(false)){
                penaltyValues.put(type, config.getDouble("overleveled_equipment_penalties." + type));
            }
        }
        this.overleveledWarningDelay = config.getInt("overleveled_warning_delay");
        String warningDisplayOption = config.getString("overleveled_warning", "NONE");
        if (warningDisplayOption.equals("ACTION_BAR")) this.warningDisplayOption = 1;
        else if (warningDisplayOption.equals("CHAT")) this.warningDisplayOption = 2;
        else this.warningDisplayOption = 0;

        tooAdvancedWarning = TranslationManager.getInstance().getTranslation("warning_item_too_advanced");
    }

    public static OverleveledEquipmentTool getTool() {
        if (tool == null) tool = new OverleveledEquipmentTool();
        return tool;
    }

    public double getPenalty(Player p, ItemStack i, String type){
        if (Utils.isItemEmptyOrNull(i)) return 0;
        if (i.getItemMeta() == null) return 0;
        if (!i.getItemMeta().getPersistentDataContainer().has(skillRequirementKey, PersistentDataType.STRING)) {
            return 0;
        }
        if (!penaltyValues.containsKey(type)) {
            return 0;
        }
        double penalty = penaltyValues.get(type);
        Collection<SkillRequirement> requirements = getSkillRequirements(i);
        if (requirements.isEmpty()) return 0;
        for (SkillRequirement requirement : requirements){
            if (requirement.levelRequirement <= 0) continue;
            Profile profile = ProfileManager.getManager().getProfile(p, requirement.skill.getType());
            if (profile == null) continue;
            if (profile.getLevel() >= requirement.levelRequirement) {
                continue;
            }
            double fractionLevel = (double) profile.getLevel() / (double) requirement.levelRequirement;
            double formulaResult = Utils.eval(penaltyMultiplierFormula.replace("%fraction_level%", String.format("%.3f", fractionLevel)));
            if (formulaResult > 0 && CooldownManager.getInstance().isCooldownPassed(p.getUniqueId(), "cooldown_warning_overleveled_item_" + i.getType().toString().toLowerCase())){
                String message = tooAdvancedWarning
                        .replace("%item%", Utils.getItemName(i))
                        .replace("%skill%", requirement.skill.getDisplayName())
                        .replace("%level%", "" + requirement.levelRequirement);
                switch (warningDisplayOption){
                    case 0: break;
                    case 1: {
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(message)));
                        CooldownManager.getInstance().setCooldown(p.getUniqueId(), overleveledWarningDelay, "cooldown_warning_overleveled_item_" + i.getType().toString().toLowerCase());
                        break;
                    }
                    case 2: {
                        p.sendMessage(Utils.chat(message));
                        CooldownManager.getInstance().setCooldown(p.getUniqueId(), overleveledWarningDelay, "cooldown_warning_overleveled_item_" + i.getType().toString().toLowerCase());
                        break;
                    }
                }
            }
            return penalty * Math.max(0, Math.min(1, formulaResult));
        }
        return 0;
    }

    /**
     * Sets a skill level requirement for the item. If the item user is below the level requirement for the given
     * skill, debuffs are applied to the player and the player is also warned their item is too advanced for them.
     * @param i the itemstack to apply the skill level requirement to
     * @param forSkill the skill for which the player has to meet the level requirement
     * @param required the level the player needs to be at least to not suffer from penalties
     */
    public void addSkillRequirement(ItemStack i, Skill forSkill, int required){
        Collection<SkillRequirement> existingRequirements = getSkillRequirements(i);
        existingRequirements.add(new SkillRequirement(forSkill, required));
        setSkillRequirements(i, existingRequirements);
    }

    public void removeSkillRequirements(ItemStack i){
        setSkillRequirements(i, new HashSet<>());
    }

    public void removeSkillRequirement(ItemStack i, String skill){
        Collection<SkillRequirement> existingRequirements = getSkillRequirements(i);
        existingRequirements.removeIf(skillRequirement -> skillRequirement.skill.getType().equals(skill));
        setSkillRequirements(i, existingRequirements);
    }

    public void setSkillRequirements(ItemStack i, Collection<SkillRequirement> requirements){
        ItemMeta iMeta = i.getItemMeta();
        if (iMeta == null) return;
        if (requirements.isEmpty()) {
            iMeta.getPersistentDataContainer().remove(skillRequirementKey);
        } else {
            StringBuilder requirementsString = new StringBuilder();
            boolean first = true;
            for (SkillRequirement requirement : requirements){
                if (first){
                    requirementsString.append(requirement.skill.getType()).append(":").append(requirement.levelRequirement);
                    first = false;
                } else {
                    requirementsString.append(";").append(requirement.skill.getType()).append(":").append(requirement.levelRequirement);
                }
            }
            iMeta.getPersistentDataContainer().set(skillRequirementKey, PersistentDataType.STRING, requirementsString.toString());
        }
        i.setItemMeta(iMeta);
    }

    public Collection<SkillRequirement> getSkillRequirements(ItemStack i){
        ItemMeta iMeta = i.getItemMeta();
        Collection<SkillRequirement> requirements = new HashSet<>();
        if (iMeta == null) return requirements;
        if (iMeta.getPersistentDataContainer().has(skillRequirementKey, PersistentDataType.STRING)){
            String storedValue = iMeta.getPersistentDataContainer().get(skillRequirementKey, PersistentDataType.STRING);
            if (storedValue == null) return requirements;
            String[] stringRequirements = storedValue.split(";");
            for (String requirement : stringRequirements){
                String[] args = requirement.split(":");
                if (args.length >= 2){
                    try {
                        int level = Integer.parseInt(args[1]);
                        Skill skill = SkillProgressionManager.getInstance().getSkill(args[0]);
                        if (skill == null) continue;
                        requirements.add(new SkillRequirement(skill, level));
                    } catch (IllegalArgumentException ignored){
                    }
                }
            }
        }
        return requirements;
    }

    private static class SkillRequirement{
        private final Skill skill;
        private final int levelRequirement;

        public SkillRequirement(Skill skill, int levelRequirement){
            this.skill = skill;
            this.levelRequirement = levelRequirement;
        }
    }
}
