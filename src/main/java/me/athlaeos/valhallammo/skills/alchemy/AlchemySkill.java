package me.athlaeos.valhallammo.skills.smithing;

import me.athlaeos.valhallammo.configs.ConfigManager;
import me.athlaeos.valhallammo.dom.Perk;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.materials.EquipmentClass;
import me.athlaeos.valhallammo.materials.MaterialClass;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.perkrewards.PerkRewardsManager;
import me.athlaeos.valhallammo.skills.CraftingSkill;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmithingSkill extends Skill implements CraftingSkill {
    private final Map<MaterialClass, Double> baseExperienceValues;
    private final Map<EquipmentClass, Double> experienceMultipliers;

    public SmithingSkill() {

        baseExperienceValues = new HashMap<>();
        experienceMultipliers = new HashMap<>();

        YamlConfiguration smithingConfig = ConfigManager.getInstance().getConfig("skill_smithing.yml").get();
        YamlConfiguration smithingProgressionConfig = ConfigManager.getInstance().getConfig("progression_smithing.yml").get();

        this.type = SkillType.SMITHING;
        this.displayName = Utils.chat(smithingConfig.getString("display_name"));
        this.description = Utils.chat(smithingConfig.getString("description"));
        try {
            this.icon = Material.valueOf(smithingConfig.getString("icon"));
        } catch (IllegalArgumentException ignored){
            System.out.println("[ValhallaMMO] invalid icon given for Smithing skill tree in skill_smithing.yml, defaulting to ANVIL");
            this.icon = Material.ANVIL;
        }

        this.expCurve = smithingProgressionConfig.getString("experience.exp_level_curve");

        this.max_level = smithingProgressionConfig.getInt("experience.max_level");

        this.messages = smithingProgressionConfig.getStringList("messages");

        this.commands = smithingProgressionConfig.getStringList("commands");

        try {
            String coords = smithingConfig.getString("starting_coordinates");
            if (coords == null) throw new IllegalArgumentException();
            String[] indivCoords = coords.split(",");
            if (indivCoords.length != 2) throw new IllegalArgumentException();
            this.centerX = Integer.parseInt(indivCoords[0]);
            this.centerY = Integer.parseInt(indivCoords[1]);
        } catch (IllegalArgumentException e){
            System.out.println("[ValhallaMMO] invalid coordinates given for smithing in skill_smithing.yml, defaulting to 0,0. Coords are to be given in the format \"x,y\" where X and Y are whole numbers");
            this.centerX = 0;
            this.centerY = 0;
        }

        ConfigurationSection startingPerksSection = smithingProgressionConfig.getConfigurationSection("starting_perks");
        if (startingPerksSection != null){
            for (String startPerk : startingPerksSection.getKeys(false)){
                Object arg = smithingProgressionConfig.get("starting_perks." + startPerk);
                if (arg != null){
                    PerkReward reward = PerkRewardsManager.getInstance().createReward(startPerk, arg);
                    if (reward == null) {
                        continue;
                    }
                    startingPerks.add(reward);
                }
            }
        }

        ConfigurationSection levelingPerksSection = smithingProgressionConfig.getConfigurationSection("leveling_perks");
        if (levelingPerksSection != null){
            for (String levelPerk : levelingPerksSection.getKeys(false)){
                Object arg = smithingProgressionConfig.get("leveling_perks." + levelPerk);
                if (arg != null){
                    PerkReward reward = PerkRewardsManager.getInstance().createReward(levelPerk, arg);
                    if (reward == null) continue;
                    levelingPerks.add(reward);
                }
            }
        }

        ConfigurationSection perksSection = smithingProgressionConfig.getConfigurationSection("perks");
        if (perksSection != null){
            for (String perkName : perksSection.getKeys(false)){
                String displayName = smithingProgressionConfig.getString("perks." + perkName + ".name");
                String description = smithingProgressionConfig.getString("perks." + perkName + ".description");
                Material perkIcon = Material.BOOK;
                try {
                    String stringIcon = smithingProgressionConfig.getString("perks." + perkName + ".icon");
                    if (stringIcon == null) {
                        throw new IllegalArgumentException();
                    }
                    perkIcon = Material.valueOf(stringIcon);
                } catch (IllegalArgumentException ignored){
                    System.out.println("[ValhallaMMO] invalid icon given for perk " + perkName + " in skill_smithing.yml, defaulting to BOOK");
                }
                int perkX;
                int perkY;
                try {
                    String coords = smithingProgressionConfig.getString("perks." + perkName + ".coords");
                    if (coords == null) {
                        throw new IllegalArgumentException();
                    }
                    String[] indivCoords = coords.split(",");
                    if (indivCoords.length != 2) {
                        throw new IllegalArgumentException();
                    }
                    perkX = Integer.parseInt(indivCoords[0]);
                    perkY = Integer.parseInt(indivCoords[1]);
                } catch (IllegalArgumentException e){
                    System.out.println("[ValhallaMMO] invalid coordinates given for perk " + perkName + " in skill_smithing.yml, cancelling perk creation. Coords are to be given in the format \"x,y\" where X and Y are whole numbers");
                    e.printStackTrace();
                    continue;
                }
                boolean hidden = smithingProgressionConfig.getBoolean("perks." + perkName + ".hidden");
                int cost = smithingProgressionConfig.getInt("perks." + perkName + ".cost");
                int required_level = smithingProgressionConfig.getInt("perks." + perkName + ".required_lv");

                List<PerkReward> perkRewards = new ArrayList<>();
                ConfigurationSection perkRewardSection = smithingProgressionConfig.getConfigurationSection("perks." + perkName + ".perk_rewards");
                if (perkRewardSection != null){
                    for (String rewardString : perkRewardSection.getKeys(false)){
                        Object arg = smithingProgressionConfig.get("perks." + perkName + ".perk_rewards." + rewardString);
                        if (arg != null){
                            PerkReward reward = null;
                            try {
                                reward = PerkRewardsManager.getInstance().createReward(rewardString, arg).clone();
                            } catch (CloneNotSupportedException ignored){
                            }
                            if (reward == null) {
                                continue;
                            }
                            perkRewards.add(reward);
                        }
                    }
                }

                List<String> perkMessages = smithingProgressionConfig.getStringList("perks." + perkName + ".messages");
                List<String> commands = smithingProgressionConfig.getStringList("perks." + perkName + ".commands");
                List<String> requirementSkillOne = smithingProgressionConfig.getStringList("perks." + perkName + ".requireperk_one");
                List<String> requirementSkillAll = smithingProgressionConfig.getStringList("perks." + perkName + ".requireperk_all");

                Perk newPerk = new Perk(perkName, displayName, description, perkIcon,
                        this.type, perkX, perkY, hidden, cost, required_level, perkRewards,
                        perkMessages, commands, requirementSkillOne, requirementSkillAll);
                perks.add(newPerk);
            }
        }

        ConfigurationSection specialSection = smithingProgressionConfig.getConfigurationSection("special_perks");
        if (specialSection != null){
            for (String stringLevel : specialSection.getKeys(false)){
                int level;
                try {
                    level = Integer.parseInt(stringLevel);
                } catch (IllegalArgumentException ignored){
                    System.out.println("[ValhallaMMO] Invalid special level given at special_perks." + stringLevel + ". Cancelled this special level, it should be a whole number!");
                    continue;
                }

                specialLevelingCommands.put(level, smithingProgressionConfig.getStringList("special_perks." + stringLevel + ".commands"));
                specialLevelingMessages.put(level, smithingProgressionConfig.getStringList("special_perks." + stringLevel + ".messages"));
                List<PerkReward> specialPerkRewards = new ArrayList<>();

                ConfigurationSection perkSection = smithingProgressionConfig.getConfigurationSection("special_perks." + stringLevel + ".perk_rewards");
                if (perkSection != null){
                    for (String perkName : perkSection.getKeys(false)){
                        Object arg = smithingProgressionConfig.get("special_perks." + stringLevel + ".perk_rewards." + perkName);
                        if (arg != null){
                            PerkReward reward = PerkRewardsManager.getInstance().createReward(perkName, arg);
                            if (reward == null) continue;
                            specialPerkRewards.add(reward);
                        }
                    }
                }

                specialLevelingPerks.put(level, specialPerkRewards);
            }
        }

        ConfigurationSection materialBaseEXPSection = smithingProgressionConfig.getConfigurationSection("experience.exp_gain.material_base");
        if (materialBaseEXPSection != null){
            for (String material : materialBaseEXPSection.getKeys(false)){
                try {
                    MaterialClass materialClass = MaterialClass.valueOf(material);
                    double materialBase = smithingProgressionConfig.getDouble("experience.exp_gain.material_base." + material);
                    baseExperienceValues.put(materialClass, materialBase);
                } catch (IllegalArgumentException ignored){
                    System.out.println("[ValhallaMMO] Invalid material class given at experience.exp_gain.material_base." + material + ". Skipped this section, review the documentation or ask in my discord what the available options are");
                }
            }
        }

        ConfigurationSection toolEXPMultiplierSection = smithingProgressionConfig.getConfigurationSection("experience.exp_gain.type_multiplier");
        if (toolEXPMultiplierSection != null){
            for (String toolType : toolEXPMultiplierSection.getKeys(false)){
                try {
                    EquipmentClass toolClass = EquipmentClass.valueOf(toolType);
                    double typeMult = smithingProgressionConfig.getDouble("experience.exp_gain.type_multiplier." + toolType);
                    experienceMultipliers.put(toolClass, typeMult);
                } catch (IllegalArgumentException ignored){
                    System.out.println("[ValhallaMMO] Invalid equipment class given at experience.exp_gain.type_multiplier." + toolType + ". Skipped this section, review the documentation or ask in my discord what the available options are");
                }
            }
        }
    }

    public Map<MaterialClass, Double> getBaseExperienceValues() {
        return baseExperienceValues;
    }

    public Map<EquipmentClass, Double> getExperienceMultipliers() {
        return experienceMultipliers;
    }

//    public void addSmithingEXP(Player p, double amount, MaterialClass materialClass){
//        Profile profile = ProfileUtil.getProfile(p, SkillType.SMITHING);
//        if (profile == null) profile = ProfileUtil.newProfile(p, SkillType.SMITHING);
//        if (profile != null){
//            if (profile instanceof SmithingProfile){
//                SmithingProfile smithingProfile = (SmithingProfile) profile;
//                double expMultiplier = (smithingProfile.getCraftingEXPMultiplier(materialClass)) / 100D;
//                double finalEXP = amount * expMultiplier * (smithingProfile.getGeneralCraftingExpMultiplier() / 100D);
//                if (finalEXP < 0) finalEXP = 0;
//            }
//        }
//    }

    /**
     * Calculates how much EXP a player should earn from crafting an item, first it calculates the base value of
     * the item crafted, then it applies whatever EXP multiplier stats the player has.
     * Returns the total amount of exp a player should earn from crafting this item
     * @param p the crafter
     * @param item the item crafted
     * @return the amount of EXP the crafter should earn
     */
    @Override
    public double expForCraftedItem(Player p, ItemStack item) {
        Profile profile = ProfileUtil.getProfile(p, SkillType.SMITHING);
        if (profile == null) profile = ProfileUtil.newProfile(p, SkillType.SMITHING);
        MaterialClass materialClass = MaterialClass.getMatchingClass(item.getType());
        EquipmentClass equipmentClass = EquipmentClass.getClass(item.getType());
        if (materialClass != null && equipmentClass != null){
            double materialClassBase = 0;
            if (getBaseExperienceValues().get(materialClass) != null) materialClassBase = getBaseExperienceValues().get(materialClass);
            double typeMultiplier = 0;
            if (getExperienceMultipliers().get(equipmentClass) != null) typeMultiplier = getExperienceMultipliers().get(equipmentClass);
            double baseEXP = materialClassBase * typeMultiplier;

            if (profile != null){
                if (profile instanceof SmithingProfile){
                    SmithingProfile smithingProfile = (SmithingProfile) profile;
                    double expMultiplier = (smithingProfile.getCraftingEXPMultiplier(materialClass)) / 100D;
                    double finalEXP = baseEXP * expMultiplier * (smithingProfile.getGeneralCraftingExpMultiplier() / 100D);
                    if (finalEXP < 0) finalEXP = 0;

                    return finalEXP;
                }
            }
        }
        return 0;
    }
}
