package me.athlaeos.valhallammo.perks.smithing;

import me.athlaeos.valhallammo.configs.ConfigManager;
import me.athlaeos.valhallammo.domain.Perk;
import me.athlaeos.valhallammo.domain.Skill;
import me.athlaeos.valhallammo.domain.SkillType;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.perkrewards.PerkRewardsManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

public class SmithingSkill extends Skill {

    public SmithingSkill() {

        YamlConfiguration smithingConfig = ConfigManager.getInstance().getConfig("smithing.yml").get();
        YamlConfiguration smithingProgressionConfig = ConfigManager.getInstance().getConfig("progression_smithing.yml").get();

        this.type = SkillType.SMITHING;
        this.displayName = Utils.chat(smithingConfig.getString("display_name"));
        this.description = Utils.chat(smithingConfig.getString("description"));
        try {
            this.icon = Material.valueOf(smithingConfig.getString("icon"));
        } catch (IllegalArgumentException ignored){
            System.out.println("[ValhallaMMO] invalid icon given for Smithing skill tree in smithing.yml, defaulting to ANVIL");
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
            System.out.println("[ValhallaMMO] invalid coordinates given for smithing in smithing.yml, defaulting to 0,0. Coords are to be given in the format \"x,y\" where X and Y are whole numbers");
            this.centerX = 0;
            this.centerY = 0;
        }

        ConfigurationSection startingPerksSection = smithingProgressionConfig.getConfigurationSection("starting_perks");
        if (startingPerksSection != null){
            for (String startPerk : startingPerksSection.getKeys(false)){
                Object arg = smithingProgressionConfig.get("starting_perks." + startPerk);
                if (arg != null){
                    PerkReward reward = PerkRewardsManager.getInstance().createReward(startPerk, arg);
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
                    levelingPerks.add(reward);
                }
            }
        }

        ConfigurationSection perksSection = smithingProgressionConfig.getConfigurationSection("perks");
        if (perksSection != null){
            for (String perkName : perksSection.getKeys(false)){
                String displayName = smithingProgressionConfig.getString("perks." + perksSection + ".name");
                Material perkIcon = Material.BOOK;
                try {
                    perkIcon = Material.valueOf(smithingProgressionConfig.getString("perks." + perksSection + ".icon"));
                } catch (IllegalArgumentException ignored){
                    System.out.println("[ValhallaMMO] invalid icon given for perk " + perkName + " in smithing.yml, defaulting to BOOK");
                }
                int perkX;
                int perkY;
                try {
                    String coords = smithingProgressionConfig.getString("perks." + perksSection + ".coords");
                    if (coords == null) throw new IllegalArgumentException();
                    String[] indivCoords = coords.split(",");
                    if (indivCoords.length != 2) throw new IllegalArgumentException();
                    perkX = Integer.parseInt(indivCoords[0]);
                    perkY = Integer.parseInt(indivCoords[1]);
                } catch (IllegalArgumentException e){
                    System.out.println("[ValhallaMMO] invalid coordinates given for perk " + perkName + " in smithing.yml, cancelling perk creation. Coords are to be given in the format \"x,y\" where X and Y are whole numbers");
                    e.printStackTrace();
                    continue;
                }
                boolean hidden = smithingProgressionConfig.getBoolean("perks." + perksSection + ".hidden");
                int cost = smithingProgressionConfig.getInt("perks." + perksSection + ".cost");
                int required_level = smithingProgressionConfig.getInt("perks." + perksSection + ".required_lv");

                List<PerkReward> perkRewards = new ArrayList<>();
                ConfigurationSection perkRewardSection = smithingProgressionConfig.getConfigurationSection("start." + perkName + ".perks");
                if (perkRewardSection != null){
                    for (String startPerk : perkRewardSection.getKeys(false)){
                        Object arg = smithingProgressionConfig.get("start." + perkName + ".perk_rewards" + startPerk);
                        if (arg != null){
                            PerkReward reward = PerkRewardsManager.getInstance().createReward(startPerk, arg);
                            perkRewards.add(reward);
                        }
                    }
                }

                List<String> perkMessages = smithingProgressionConfig.getStringList("perks." + perksSection + ".messages");
                List<String> commands = smithingProgressionConfig.getStringList("perks." + perksSection + ".commands");
                List<String> requirementSkillOne = smithingProgressionConfig.getStringList("perks." + perksSection + ".requireskill_one");
                List<String> requirementSkillAll = smithingProgressionConfig.getStringList("perks." + perksSection + ".requireskill_all");

                perks.add(new Perk(perkName, displayName, perkIcon,
                        this.type, perkX, perkY, hidden, cost, required_level, perkRewards,
                        perkMessages, commands, requirementSkillOne, requirementSkillAll));
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
                    
                }

                specialLevelingPerks.put(level, specialPerkRewards);
            }
        }
    }
}
