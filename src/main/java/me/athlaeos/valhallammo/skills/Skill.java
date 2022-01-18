package me.athlaeos.valhallammo.dom;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.events.PlayerSkillExperienceGainEvent;
import me.athlaeos.valhallammo.events.PlayerSkillLevelupEvent;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.managers.PlaceholderManager;
import me.athlaeos.valhallammo.utility.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Skill {
    protected SkillType type;
    protected String displayName;
    protected String description;
    protected Material icon;
    protected String expCurve;
    protected int max_level;
    protected List<PerkReward> startingPerks = new ArrayList<>();
    protected List<PerkReward> levelingPerks = new ArrayList<>();
    protected Map<Integer, List<PerkReward>> specialLevelingPerks = new HashMap<>();
    protected Map<Integer, List<String>> specialLevelingMessages = new HashMap<>();
    protected Map<Integer, List<String>> specialLevelingCommands = new HashMap<>();
    protected List<String> messages;
    protected List<String> commands;
    protected List<Perk> perks = new ArrayList<>();
    protected int centerX;
    protected int centerY;

    private final String status_experience_gain;

    public Skill(){
        status_experience_gain = TranslationManager.getInstance().getTranslation("status_experience_gained");
    }

    public Material getIcon() {
        return icon;
    }

    public void registerPerks(){
        for (Perk p : perks){
            SkillProgressionManager.getInstance().registerPerk(p);
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<PerkReward> getStartingPerks() {
        return startingPerks;
    }

    public SkillType getType() {
        return type;
    }

    public String getExpCurve() {
        return expCurve;
    }

    public List<Perk> getPerks() {
        return perks;
    }

    public List<PerkReward> getLevelingPerks() {
        return levelingPerks;
    }

    public List<String> getCommands() {
        return commands;
    }

    public List<String> getMessages() {
        return messages;
    }

    public Map<Integer, List<PerkReward>> getSpecialLevelingPerks() {
        return specialLevelingPerks;
    }

    public String getDescription() {
        return description;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public int getMax_level() {
        return max_level;
    }

    public Map<Integer, List<String>> getSpecialLevelingCommands() {
        return specialLevelingCommands;
    }

    public Map<Integer, List<String>> getSpecialLevelingMessages() {
        return specialLevelingMessages;
    }

    /**
     * Returns a player's current level
     * @param p the player to get their level from
     * @return the integer level the player has in this skill
     */
    public int getCurrentLevel(Player p){
        Profile profile = ProfileUtil.getProfile(p, this.type);
        if (profile != null){
            return profile.getLevel();
        }
        return 0;
    }

    /**
     * Returns the double amount of experience needed to progress to the next level, rounded to 4 decimals
     * @param nextLevel the integer next level to get how much EXP it takes to reach it
     * @return the double amount of experience needed to progress to the next level. If the player has already reached
     * max level, it returns -1.
     */
    public double expForlevel(int nextLevel){
        if (nextLevel < max_level){
            return Utils.round(Utils.eval(expCurve.replace("%level%", "" + nextLevel)), 4);
        } else {
            return -1;
        }
    }

    /**
     * Levels a player up if the levelup conditions are met, if the player has more or equal exp than is required to level
     * up to the next level. It does this asynchronously, as this method contains a while loop and may execute several
     * commands depending on configuration.
     * @param p the player to level up depending on if the conditions are met.
     */
    public void updateLevelUpConditions(Player p){
        Profile profile = ProfileUtil.getProfile(p, this.type);
        if (profile == null) profile = ProfileUtil.newProfile(p, this.type);
        if (profile != null){
            int nextLevel = profile.getLevel() + 1;
            double nextLevelEXP = expForlevel(nextLevel);
            double remainingEXP = profile.getExp();
            while (remainingEXP >= nextLevelEXP){
                if (nextLevel <= max_level){
                    levelPlayerUp(p, profile);
                    nextLevel += 1;
                    nextLevelEXP = expForlevel(nextLevel);
                    remainingEXP -= nextLevelEXP;
                    profile = ProfileUtil.getProfile(p, this.type);
                    if (profile != null){
                        profile.setExp(remainingEXP);
                        ProfileUtil.setProfile(p, profile, type);
                    }
                } else {
                    break;
                }
            }
        }
    }

    /**
     * Adds an amount of EXP to the player's profile, and checks if the player should level up
     * @param p the player to add EXP to
     * @param amount the amount of EXP to add
     */
    public void addEXP(Player p, double amount){
        PlayerSkillExperienceGainEvent event = new PlayerSkillExperienceGainEvent(p, amount, this.type);
        ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()){
            Profile profile = ProfileUtil.getProfile(p, this.type);
            if (profile == null) profile = ProfileUtil.newProfile(p, this.type);
            if (profile != null){
                profile.setExp(profile.getExp() + amount);
                if (!status_experience_gain.equals("")){
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(status_experience_gain
                            .replace("%skill%", this.displayName)
                            .replace("%exp%", ((amount >= 0) ? "+" : "-") + ((amount + "").replace("-", ""))))));
                }
                profile.setLifetimeEXP(profile.getLifetimeEXP() + amount);

                ProfileUtil.setProfile(p, profile, this.type);
                if (profile.getExp() >= expForlevel(profile.getLevel() + 1)){
                    updateLevelUpConditions(p);
                }
            }
        }
    }

    /**
     * Levels a player up once, and applies any rewards within the skill to the player
     * @param p the player to level up
     */
    public void levelPlayerUp(Player p, Profile profile){
        if (profile == null) profile = ProfileUtil.newProfile(p, this.type);
        if (profile != null){
            int levelFrom = profile.getLevel();
            PlayerSkillLevelupEvent event = new PlayerSkillLevelupEvent(p, this.type, levelFrom + 1);
            ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(event);
            profile.setLevel(levelFrom + 1);
            ProfileUtil.setProfile(p, profile, this.type);
            int newLevel = profile.getLevel();
            for (PerkReward reward : levelingPerks){
                reward.execute(p);
            }
            for (String command : commands){
                if (!command.equalsIgnoreCase("")){
                    ValhallaMMO.getPlugin().getServer().dispatchCommand(
                            ValhallaMMO.getPlugin().getServer().getConsoleSender(),
                            command.replace("%player%", p.getName())
                    );
                }
            }
            for (String message : messages){
                p.sendMessage(Utils.chat(message.replace("%level%", "" + newLevel)));
            }
            if (specialLevelingCommands.containsKey(newLevel)){
                for (String command : specialLevelingCommands.get(newLevel)){
                    if (!command.equalsIgnoreCase("")){
                        ValhallaMMO.getPlugin().getServer().dispatchCommand(
                                ValhallaMMO.getPlugin().getServer().getConsoleSender(),
                                command.replace("%player%", p.getName())
                        );
                    }
                }
            }
            if (specialLevelingMessages.containsKey(newLevel)){
                for (String message : specialLevelingMessages.get(newLevel)){
                    p.sendMessage(Utils.chat(PlaceholderManager.parse(message, p)));
                }
            }
            if (specialLevelingPerks.containsKey(newLevel)){
                for (PerkReward reward : specialLevelingPerks.get(newLevel)){
                    reward.execute(p);
                }
            }
        }
    }
}
