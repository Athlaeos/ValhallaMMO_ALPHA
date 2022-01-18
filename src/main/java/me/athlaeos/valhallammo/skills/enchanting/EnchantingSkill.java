package me.athlaeos.valhallammo.skills.alchemy;

import me.athlaeos.valhallammo.configs.ConfigManager;
import me.athlaeos.valhallammo.dom.Perk;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.managers.PerkRewardsManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.CraftingSkill;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AlchemySkill extends Skill implements CraftingSkill {
//    private final Map<PotionType, Double> baseExperienceValues;
//    private final Map<PotionBrewAction, Double> experienceMultipliers;
//    private double turtleMasterPotionBaseEXP = 0D;
//    private double awkwardPotionBaseEXP = 0D;

    public AlchemySkill() {
//        baseExperienceValues = new HashMap<>();
//        experienceMultipliers = new HashMap<>();
        YamlConfiguration alchemyConfig = ConfigManager.getInstance().getConfig("skill_alchemy.yml").get();
        YamlConfiguration progressionConfig = ConfigManager.getInstance().getConfig("progression_alchemy.yml").get();

        this.type = SkillType.ALCHEMY;
        this.displayName = Utils.chat(alchemyConfig.getString("display_name"));
        this.description = Utils.chat(alchemyConfig.getString("description"));
        try {
            this.icon = Material.valueOf(alchemyConfig.getString("icon"));
        } catch (IllegalArgumentException ignored){
            System.out.println("[ValhallaMMO] invalid icon given for Alchemy skill tree in skill_alchemy.yml, defaulting to BREWING_STAND");
            this.icon = Material.BREWING_STAND;
        }

        this.expCurve = progressionConfig.getString("experience.exp_level_curve");

        this.max_level = progressionConfig.getInt("experience.max_level");

        this.messages = progressionConfig.getStringList("messages");

        this.commands = progressionConfig.getStringList("commands");

        try {
            String coords = alchemyConfig.getString("starting_coordinates");
            if (coords == null) throw new IllegalArgumentException();
            String[] indivCoords = coords.split(",");
            if (indivCoords.length != 2) throw new IllegalArgumentException();
            this.centerX = Integer.parseInt(indivCoords[0]);
            this.centerY = Integer.parseInt(indivCoords[1]);
        } catch (IllegalArgumentException e){
            System.out.println("[ValhallaMMO] invalid coordinates given for alchemy in skill_alchemy.yml, defaulting to 0,0. Coords are to be given in the format \"x,y\" where X and Y are whole numbers");
            this.centerX = 0;
            this.centerY = 0;
        }

        ConfigurationSection startingPerksSection = progressionConfig.getConfigurationSection("starting_perks");
        if (startingPerksSection != null){
            for (String startPerk : startingPerksSection.getKeys(false)){
                Object arg = progressionConfig.get("starting_perks." + startPerk);
                if (arg != null){
                    PerkReward reward = PerkRewardsManager.getInstance().createReward(startPerk, arg);
                    if (reward == null) {
                        continue;
                    }
                    startingPerks.add(reward);
                }
            }
        }

        ConfigurationSection levelingPerksSection = progressionConfig.getConfigurationSection("leveling_perks");
        if (levelingPerksSection != null){
            for (String levelPerk : levelingPerksSection.getKeys(false)){
                Object arg = progressionConfig.get("leveling_perks." + levelPerk);
                if (arg != null){
                    PerkReward reward = PerkRewardsManager.getInstance().createReward(levelPerk, arg);
                    if (reward == null) continue;
                    levelingPerks.add(reward);
                }
            }
        }

        ConfigurationSection perksSection = progressionConfig.getConfigurationSection("perks");
        if (perksSection != null){
            for (String perkName : perksSection.getKeys(false)){
                String displayName = progressionConfig.getString("perks." + perkName + ".name");
                String description = progressionConfig.getString("perks." + perkName + ".description");
                Material perkIcon = Material.BOOK;
                try {
                    String stringIcon = progressionConfig.getString("perks." + perkName + ".icon");
                    if (stringIcon == null) {
                        throw new IllegalArgumentException();
                    }
                    perkIcon = Material.valueOf(stringIcon);
                } catch (IllegalArgumentException ignored){
                    System.out.println("[ValhallaMMO] invalid icon given for perk " + perkName + " in skill_alchemy.yml, defaulting to BOOK");
                }
                int perkX;
                int perkY;
                try {
                    String coords = progressionConfig.getString("perks." + perkName + ".coords");
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
                    System.out.println("[ValhallaMMO] invalid coordinates given for perk " + perkName + " in skill_alchemy.yml, cancelling perk creation. Coords are to be given in the format \"x,y\" where X and Y are whole numbers");
                    e.printStackTrace();
                    continue;
                }
                boolean hidden = progressionConfig.getBoolean("perks." + perkName + ".hidden");
                int cost = progressionConfig.getInt("perks." + perkName + ".cost");
                int required_level = progressionConfig.getInt("perks." + perkName + ".required_lv");

                List<PerkReward> perkRewards = new ArrayList<>();
                ConfigurationSection perkRewardSection = progressionConfig.getConfigurationSection("perks." + perkName + ".perk_rewards");
                if (perkRewardSection != null){
                    for (String rewardString : perkRewardSection.getKeys(false)){
                        Object arg = progressionConfig.get("perks." + perkName + ".perk_rewards." + rewardString);
                        if (arg != null){
                            PerkReward reward = null;
                            try {
                                reward = PerkRewardsManager.getInstance().createReward(rewardString, arg);
                                if (reward != null){
                                    reward = reward.clone();
                                }
                            } catch (CloneNotSupportedException ignored){
                            }
                            if (reward == null) {
                                continue;
                            }
                            perkRewards.add(reward);
                        }
                    }
                }

                List<String> perkMessages = progressionConfig.getStringList("perks." + perkName + ".messages");
                List<String> commands = progressionConfig.getStringList("perks." + perkName + ".commands");
                List<String> requirementSkillOne = progressionConfig.getStringList("perks." + perkName + ".requireperk_one");
                List<String> requirementSkillAll = progressionConfig.getStringList("perks." + perkName + ".requireperk_all");

                Perk newPerk = new Perk(perkName, displayName, description, perkIcon,
                        this.type, perkX, perkY, hidden, cost, required_level, perkRewards,
                        perkMessages, commands, requirementSkillOne, requirementSkillAll);
                perks.add(newPerk);
            }
        }

        ConfigurationSection specialSection = progressionConfig.getConfigurationSection("special_perks");
        if (specialSection != null){
            for (String stringLevel : specialSection.getKeys(false)){
                int level;
                try {
                    level = Integer.parseInt(stringLevel);
                } catch (IllegalArgumentException ignored){
                    System.out.println("[ValhallaMMO] Invalid special level given at special_perks." + stringLevel + ". Cancelled this special level, it should be a whole number!");
                    continue;
                }

                specialLevelingCommands.put(level, progressionConfig.getStringList("special_perks." + stringLevel + ".commands"));
                specialLevelingMessages.put(level, progressionConfig.getStringList("special_perks." + stringLevel + ".messages"));
                List<PerkReward> specialPerkRewards = new ArrayList<>();

                ConfigurationSection perkSection = progressionConfig.getConfigurationSection("special_perks." + stringLevel + ".perk_rewards");
                if (perkSection != null){
                    for (String perkName : perkSection.getKeys(false)){
                        Object arg = progressionConfig.get("special_perks." + stringLevel + ".perk_rewards." + perkName);
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

        this.barTitle = alchemyConfig.getString("levelbar_title", "");
        try {
            this.barColor = BarColor.valueOf(alchemyConfig.getString("levelbar_color", "YELLOW"));
        } catch (IllegalArgumentException ignored){
            this.barColor = BarColor.PURPLE;
        }

        try {
            this.barStyle = BarStyle.valueOf(alchemyConfig.getString("levelbar_style", "SEGMENTED_6"));
        } catch (IllegalArgumentException ignored){
            this.barStyle = BarStyle.SEGMENTED_6;
        }

//        ConfigurationSection potionBaseEXPSection = progressionConfig.getConfigurationSection("experience.exp_gain.potion_base");
//        if (potionBaseEXPSection != null){
//            for (String potionEffect : potionBaseEXPSection.getKeys(false)){
//                try {
//                    PotionType potionBaseType = PotionType.valueOf(potionEffect);
//                    double potionBaseAmount = progressionConfig.getDouble("experience.exp_gain.potion_base." + potionEffect);
//                    baseExperienceValues.put(potionBaseType, potionBaseAmount);
//                } catch (IllegalArgumentException ignored){
//                    System.out.println("[ValhallaMMO] Invalid material class given at experience.exp_gain.potion_base." + potionEffect + ". Skipped this section, review the documentation or ask in my discord what the available options are");
//                }
//            }
//        }
//
//        ConfigurationSection potionBrewActionEXPMultiplierSection = progressionConfig.getConfigurationSection("experience.exp_gain.potion_multiplier");
//        if (potionBrewActionEXPMultiplierSection != null){
//            for (String action : potionBrewActionEXPMultiplierSection.getKeys(false)){
//                try {
//                    PotionBrewAction brewAction = PotionBrewAction.valueOf(action);
//                    double actionMultiplier = progressionConfig.getDouble("experience.exp_gain.potion_multiplier." + action);
//                    experienceMultipliers.put(brewAction, actionMultiplier);
//                } catch (IllegalArgumentException ignored){
//                    System.out.println("[ValhallaMMO] Invalid equipment class given at experience.exp_gain.type_multiplier." + action + ". Skipped this section, review the documentation or ask in my discord what the available options are");
//                }
//            }
//        }
    }

//    public Map<PotionType, Double> getBaseExperienceValues() {
//        return baseExperienceValues;
//    }
//
//    public Map<PotionBrewAction, Double> getExperienceMultipliers() {
//        return experienceMultipliers;
//    }

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
     * Because alchemy exp is awarded through the recipe directly, nothing can be awarded here.
     * Will always return 0
     * @param p -
     * @param item -
     * @return 0
     */
    @Override
    public double expForCraftedItem(Player p, ItemStack item) {
        return 0;
    }

    @Override
    public void addEXP(Player p, double amount) {
        double finalAmount = amount * ((AccumulativeStatManager.getInstance().getStats("ALCHEMY_EXP_GAIN", p, true) / 100D));
        super.addEXP(p, finalAmount);
    }

    //    private final Collection<Material> validTypes = new HashSet<>(Arrays.asList(Material.POTION, Material.SPLASH_POTION, Material.LINGERING_POTION));

    /**
     * Calculates how much EXP a player should earn from brewing a vanilla potion. This method also requires what the
     * potion was before brewing, as different upgrades affect the potion differently and grant different EXP rewards.
     * @param p the brewer
     * @param before the potion before the upgrade
     * @param after the potion after the upgrade
     * @return the amount of EXP the potion should grant
     */
//    public double expForVanillaPotion(Player p, ItemStack before, ItemStack after){
//        if (Utils.isItemEmptyOrNull(before) || Utils.isItemEmptyOrNull(after)) return 0;
//        if (!validTypes.contains(before.getType()) || validTypes.contains(after.getType())) return 0;
//        if (!(before.getItemMeta() instanceof PotionMeta) || !(after.getItemMeta() instanceof PotionMeta)) return 0;
//        // If either of the ItemStacks are null or air, not potions, or dont have potion meta, return 0.
//        PotionBrewAction action = PotionBrewAction.BREW;
//        double baseToUse = 0D;
//        PotionMeta beforeMeta = (PotionMeta) before.getItemMeta();
//        PotionMeta afterMeta = (PotionMeta) after.getItemMeta();
//        if (before.getType() == Material.POTION && after.getType() == Material.SPLASH_POTION){
//            action = PotionBrewAction.SPLASH;
//        } else if (before.getType() == Material.SPLASH_POTION && after.getType() == Material.LINGERING_POTION){
//            action = PotionBrewAction.LINGERING;
//        } else if (!beforeMeta.getBasePotionData().isUpgraded() && afterMeta.getBasePotionData().isUpgraded()){
//            action = PotionBrewAction.AMPLIFY;
//        } else if (!beforeMeta.getBasePotionData().isExtended() && afterMeta.getBasePotionData().isExtended()){
//            action = PotionBrewAction.EXTEND;
//        }
//        if (beforeMeta.getBasePotionData().getType() != afterMeta.getBasePotionData().getType()){
//            // if for some freaky reason the previous potion type isn't the same as the current even if the action was previously
//            // already determined to be anything but BREW, it's corrected here.
//            action = PotionBrewAction.BREW;
//        }
//        if (action == PotionBrewAction.BREW){
//            if (baseExperienceValues.containsKey(afterMeta.getBasePotionData().getType())){
//                baseToUse = baseExperienceValues.get(afterMeta.getBasePotionData().getType());
//            }
//        }
//        if (experienceMultipliers.containsKey(action)){
//            return experienceMultipliers.get(action) * baseToUse;
//        }
//        return 0D;
//    }
}
