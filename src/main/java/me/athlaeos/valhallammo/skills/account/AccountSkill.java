package me.athlaeos.valhallammo.skills;

import me.athlaeos.valhallammo.configs.ConfigManager;
import me.athlaeos.valhallammo.dom.*;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.perkrewards.PerkRewardsManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AccountSkill extends Skill {
    private final double expPerLevelUp;

    public AccountSkill() {
        YamlConfiguration playerConfig = ConfigManager.getInstance().getConfig("skill_player.yml").get();
        YamlConfiguration playerProgressionConfig = ConfigManager.getInstance().getConfig("progression_player.yml").get();

        this.type = SkillType.ACCOUNT;
        this.displayName = Utils.chat(playerConfig.getString("display_name"));
        this.description = Utils.chat(playerConfig.getString("description"));
        try {
            this.icon = Material.valueOf(playerConfig.getString("icon"));
        } catch (IllegalArgumentException ignored){
            System.out.println("[ValhallaMMO] invalid icon given for Player skill tree in skill_player.yml, defaulting to ARMOR_STAND");
            this.icon = Material.ARMOR_STAND;
        }

        this.expCurve = playerProgressionConfig.getString("experience.exp_level_curve");

        this.max_level = playerProgressionConfig.getInt("experience.max_level");

        this.messages = playerProgressionConfig.getStringList("messages");

        this.commands = playerProgressionConfig.getStringList("commands");

        try {
            String coords = playerConfig.getString("starting_coordinates");
            if (coords == null) throw new IllegalArgumentException();
            String[] indivCoords = coords.split(",");
            if (indivCoords.length != 2) throw new IllegalArgumentException();
            this.centerX = Integer.parseInt(indivCoords[0]);
            this.centerY = Integer.parseInt(indivCoords[1]);
        } catch (IllegalArgumentException e){
            System.out.println("[ValhallaMMO] invalid coordinates given for player in skill_player.yml, defaulting to 0,0. Coords are to be given in the format \"x,y\" where X and Y are whole numbers");
            this.centerX = 0;
            this.centerY = 0;
        }

        ConfigurationSection startingPerksSection = playerProgressionConfig.getConfigurationSection("starting_perks");
        if (startingPerksSection != null){
            for (String startPerk : startingPerksSection.getKeys(false)){
                Object arg = playerProgressionConfig.get("starting_perks." + startPerk);
                if (arg != null){
                    PerkReward reward = PerkRewardsManager.getInstance().createReward(startPerk, arg);
                    if (reward == null) {
                        continue;
                    }
                    startingPerks.add(reward);
                }
            }
        }

        ConfigurationSection levelingPerksSection = playerProgressionConfig.getConfigurationSection("leveling_perks");
        if (levelingPerksSection != null){
            for (String levelPerk : levelingPerksSection.getKeys(false)){
                Object arg = playerProgressionConfig.get("leveling_perks." + levelPerk);
                if (arg != null){
                    PerkReward reward = PerkRewardsManager.getInstance().createReward(levelPerk, arg);
                    if (reward == null) continue;
                    levelingPerks.add(reward);
                }
            }
        }

        ConfigurationSection perksSection = playerProgressionConfig.getConfigurationSection("perks");
        if (perksSection != null){
            for (String perkName : perksSection.getKeys(false)){
                String displayName = playerProgressionConfig.getString("perks." + perkName + ".name");
                String description = playerProgressionConfig.getString("perks." + perkName + ".description");
                Material perkIcon = Material.BOOK;
                try {
                    String stringIcon = playerProgressionConfig.getString("perks." + perkName + ".icon");
                    if (stringIcon == null) {
                        throw new IllegalArgumentException();
                    }
                    perkIcon = Material.valueOf(stringIcon);
                } catch (IllegalArgumentException ignored){
                    System.out.println("[ValhallaMMO] invalid icon given for perk " + perkName + " in skill_player.yml, defaulting to BOOK");
                }
                int perkX;
                int perkY;
                try {
                    String coords = playerProgressionConfig.getString("perks." + perkName + ".coords");
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
                    System.out.println("[ValhallaMMO] invalid coordinates given for perk " + perkName + " in skill_player.yml, cancelling perk creation. Coords are to be given in the format \"x,y\" where X and Y are whole numbers");
                    e.printStackTrace();
                    continue;
                }
                boolean hidden = playerProgressionConfig.getBoolean("perks." + perkName + ".hidden");
                int cost = playerProgressionConfig.getInt("perks." + perkName + ".cost");
                int required_level = playerProgressionConfig.getInt("perks." + perkName + ".required_lv");

                List<PerkReward> perkRewards = new ArrayList<>();
                ConfigurationSection perkRewardSection = playerProgressionConfig.getConfigurationSection("start." + perkName + ".perks");
                if (perkRewardSection != null){
                    for (String startPerk : perkRewardSection.getKeys(false)){
                        Object arg = playerProgressionConfig.get("start." + perkName + ".perk_rewards." + startPerk);
                        if (arg != null){
                            PerkReward reward = PerkRewardsManager.getInstance().createReward(startPerk, arg);
                            if (reward == null) continue;
                            perkRewards.add(reward);
                        }
                    }
                }

                List<String> perkMessages = playerProgressionConfig.getStringList("perks." + perkName + ".messages");
                List<String> commands = playerProgressionConfig.getStringList("perks." + perkName + ".commands");
                List<String> requirementSkillOne = playerProgressionConfig.getStringList("perks." + perkName + ".requireskill_one");
                List<String> requirementSkillAll = playerProgressionConfig.getStringList("perks." + perkName + ".requireskill_all");

                Perk newPerk = new Perk(perkName, displayName, description, perkIcon,
                        this.type, perkX, perkY, hidden, cost, required_level, perkRewards,
                        perkMessages, commands, requirementSkillOne, requirementSkillAll);
                perks.add(newPerk);
            }
        }

        ConfigurationSection specialSection = playerProgressionConfig.getConfigurationSection("special_perks");
        if (specialSection != null){
            for (String stringLevel : specialSection.getKeys(false)){
                int level;
                try {
                    level = Integer.parseInt(stringLevel);
                } catch (IllegalArgumentException ignored){
                    System.out.println("[ValhallaMMO] Invalid special level given at special_perks." + stringLevel + ". Cancelled this special level, it should be a whole number!");
                    continue;
                }

                specialLevelingCommands.put(level, playerProgressionConfig.getStringList("special_perks." + stringLevel + ".commands"));
                specialLevelingMessages.put(level, playerProgressionConfig.getStringList("special_perks." + stringLevel + ".messages"));
                List<PerkReward> specialPerkRewards = new ArrayList<>();

                ConfigurationSection perkSection = playerProgressionConfig.getConfigurationSection("special_perks." + stringLevel + ".perk_rewards");
                if (perkSection != null){
                    for (String perkName : perkSection.getKeys(false)){
                        Object arg = playerProgressionConfig.get("special_perks." + stringLevel + ".perk_rewards." + perkName);
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

        this.expPerLevelUp = playerProgressionConfig.getDouble("experience.exp_gain");
    }

    public double getExpPerLevelUp() {
        return expPerLevelUp;
    }

    public void addAccountEXP(Player p, double amount){
        Profile profile = ProfileUtil.getProfile(p, SkillType.ACCOUNT);
        if (profile == null) profile = ProfileUtil.newProfile(p, SkillType.ACCOUNT);
        if (profile != null){
            if (profile instanceof AccountProfile){
                addEXP(p, amount);
            }
        }
    }
}
