package me.athlaeos.valhallammo.skills;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.Perk;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.events.PlayerSkillExperienceGainEvent;
import me.athlaeos.valhallammo.events.PlayerSkillLevelupEvent;
import me.athlaeos.valhallammo.managers.*;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.account.AccountSkill;
import me.athlaeos.valhallammo.utility.BossBarUtils;
import me.athlaeos.valhallammo.utility.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public abstract class Skill {
    boolean locked = false;

    protected final String type;
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

    protected BarColor barColor = BarColor.YELLOW;
    protected BarStyle barStyle = BarStyle.SEGMENTED_6;
    protected String barTitle = "";

    private final String status_experience_gain;

    public Skill(String type){
        this.type = type;
        status_experience_gain = TranslationManager.getInstance().getTranslation("status_experience_gained");
    }

    /**
     * Should return the ValhallaMMO NamespacedKey the plugin will use to persist the profile onto the player
     * @return the NamespacedKey the skill's profile will be stored with
     */
    public abstract NamespacedKey getKey();

    /**
     * Should return a clean profile associated with the skill
     * Example: a SMITHING profile should <code>return new SmithingProfile()</code>
     * @return a clean profile associated with the skill
     */
    public abstract Profile getCleanProfile();

    /**
     * Loads the following common elements of the two configs into the skill
     * A skill's properties may also be hard coded or implemented in some other fashion
     * Honestly a weird method, but I found myself reusing this code a lot so I just copy pasted it into this method
     *      [PROPERTY_PATH]                 [TYPE]
     * base config:
     *      display_name                    (STRING)
     *      description                     (STRING)
     *      icon                            (STRING)
     *
     * progression config:
     *      messages                        (STRING LIST)
     *      commands                        (STRING LIST)
     *      starting_coordinates            (STRING) *must be formatted like "x,y", where x and y are integers
     *      experience.exp_level_curve      (STRING) *represents formula used for calculating exp needed to level up, where %level% is placeholder for the player's level
     *      experience.max_level            (INTEGER)
     *      perks                           (PERKS LIST) *has its own formatting
     *
     * @param baseSkillConfig the intended base skill config, containing simple properties
     * @param progressionConfig the intended skill progression config, containing details about the skill's progression
     */
    public void loadCommonConfig(YamlConfiguration baseSkillConfig, YamlConfiguration progressionConfig){
        this.displayName = Utils.chat(baseSkillConfig.getString("display_name"));
        this.description = Utils.chat(baseSkillConfig.getString("description"));
        try {
            this.icon = Material.valueOf(baseSkillConfig.getString("icon"));
        } catch (IllegalArgumentException ignored){
            ValhallaMMO.getPlugin().getLogger().warning("[ValhallaMMO] invalid icon given for the " + Utils.toPascalCase(this.type.toString()) + " skill tree in " + baseSkillConfig.getName() + ".yml, defaulting to BARRIER");
            this.icon = Material.BARRIER;
        }

        this.expCurve = progressionConfig.getString("experience.exp_level_curve");
        this.max_level = progressionConfig.getInt("experience.max_level");
        this.messages = progressionConfig.getStringList("messages");
        this.commands = progressionConfig.getStringList("commands");

        try {
            String coords = progressionConfig.getString("starting_coordinates");
            if (coords == null) throw new IllegalArgumentException();
            String[] indivCoords = coords.split(",");
            if (indivCoords.length != 2) throw new IllegalArgumentException();
            this.centerX = Integer.parseInt(indivCoords[0]);
            this.centerY = Integer.parseInt(indivCoords[1]);
        } catch (IllegalArgumentException e){
            ValhallaMMO.getPlugin().getLogger().warning("[ValhallaMMO] invalid coordinates given for alchemy in skill_alchemy.yml, defaulting to 0,0. Coords are to be given in the format \"x,y\" where X and Y are whole numbers");
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
                    ValhallaMMO.getPlugin().getLogger().warning("[ValhallaMMO] invalid icon given for perk " + perkName + " in " + progressionConfig.getName() + ".yml, defaulting to BOOK");
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
                    ValhallaMMO.getPlugin().getLogger().warning("[ValhallaMMO] invalid coordinates given for perk " + perkName + " in " + progressionConfig.getName() + ".yml, cancelling perk creation. Coords are to be given in the format \"x,y\" where X and Y are whole numbers");
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
                    ValhallaMMO.getPlugin().getLogger().warning("[ValhallaMMO] Invalid special level given at special_perks." + stringLevel + ". Cancelled this special level, it should be a whole number!");
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

        this.barTitle = baseSkillConfig.getString("levelbar_title", "");
        try {
            this.barColor = BarColor.valueOf(baseSkillConfig.getString("levelbar_color", "YELLOW"));
        } catch (IllegalArgumentException ignored){
            this.barColor = BarColor.WHITE;
        }

        try {
            this.barStyle = BarStyle.valueOf(baseSkillConfig.getString("levelbar_style", "SEGMENTED_6"));
        } catch (IllegalArgumentException ignored){
            this.barStyle = BarStyle.SEGMENTED_6;
        }
    }

    public Material getIcon() {
        return icon;
    }

    public void registerPerks(){
        for (Perk p : perks){
            SkillProgressionManager.getInstance().registerPerk(p, this.type);
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<PerkReward> getStartingPerks() {
        return startingPerks;
    }

    public String getType() {
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

    public void setCenterX(int centerX) {
        if (!locked){
            this.centerX = centerX;
        }
    }

    public void setCenterY(int centerY) {
        if (!locked){
            this.centerY = centerY;
        }
    }

    public void lock() {
        this.locked = true;
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
        Profile profile = ProfileManager.getProfile(p, this.type);
        if (profile != null){
            return profile.getLevel();
        }
        return 0;
    }

    /**
     * Returns the double amount of experience needed to progress to this level, rounded to 4 decimals
     * @param nextLevel the integer level to get how much EXP it takes to reach it
     * @return the double amount of experience needed to progress to reach this level. If the level exceeds the
     * max level, it returns -1.
     */
    public double expForlevel(int nextLevel){
        if (nextLevel <= max_level){
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
    public void updateLevelUpConditions(Player p, boolean silent){
        Profile profile = ProfileManager.getProfile(p, this.type);
        if (profile == null) profile = ProfileManager.newProfile(p, this.type);
        if (profile != null){
            int currentLevel = profile.getLevel();
            int nextLevel = profile.getLevel() + 1;
            double nextLevelEXP = expForlevel(nextLevel);
            double remainingEXP = profile.getExp();
            while (remainingEXP >= nextLevelEXP){
                if (nextLevel <= max_level){
                    levelPlayerUp(p, profile, silent);
                    nextLevel += 1;
                    remainingEXP -= nextLevelEXP;
                    nextLevelEXP = expForlevel(nextLevel);
                    profile = ProfileManager.getProfile(p, this.type);
                    if (profile != null){
                        profile.setExp(remainingEXP);
                        ProfileManager.setProfile(p, profile, type);
                    }
                } else {
                    break;
                }
            }
            Profile profile2 = ProfileManager.getProfile(p, this.type);
            if (profile2 != null){
                int newLevel = profile2.getLevel();
                if (newLevel > currentLevel){
                    if (!silent){
                        // this is all to ensure skill levelups only display messages once if levelups occur in quick succession
                        if (CooldownManager.getInstance().isCooldownPassed(p.getUniqueId(), "delay_" + this.type + "_levelup")){
                            CooldownManager.getInstance().setCooldown(p.getUniqueId(), 500, "delay_" + this.type + "_levelup");
                            final String type = this.type;
                            new BukkitRunnable(){
                                @Override
                                public void run() {
                                    Profile profile = ProfileManager.getProfile(p, type);
                                    if (profile != null){
                                        Skill accountSkill = SkillProgressionManager.getInstance().getSkill(type);
                                        if (accountSkill != null){
                                            int level = profile.getLevel();
                                            for (String message : accountSkill.getMessages()){
                                                p.sendMessage(Utils.chat(message.replace("%level%", "" + level)));
                                            }
                                        }
                                    }
                                }
                            }.runTaskLater(ValhallaMMO.getPlugin(), 10L);
                        }
//                            for (String message : messages){
//                                p.sendMessage(Utils.chat(message.replace("%level%", "" + newLevel)));
//                            }
                    }
                }
            }
            showBossBar(p, profile);
        }
    }

    /**
     * Adds an amount of EXP to the player's profile, and checks if the player should level up
     * @param p the player to add EXP to
     * @param amount the amount of EXP to add
     */
    public void addEXP(Player p, double amount, boolean silent){
        if (!this.type.equals("ACCOUNT")){
            amount *= AccumulativeStatManager.getInstance().getStats("GLOBAL_EXP_GAIN", p, true) / 100D;
        }
        PlayerSkillExperienceGainEvent event = new PlayerSkillExperienceGainEvent(p, amount, this);
        ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()){
            if (amount == 0) return;
            Profile profile = ProfileManager.getProfile(p, this.type);
            if (profile == null) profile = ProfileManager.newProfile(p, this.type);
            if (profile != null){
                profile.setExp(profile.getExp() + amount);
                profile.setLifetimeEXP(profile.getLifetimeEXP() + amount);
                if (!silent){
                    if (!status_experience_gain.equals("")){
                        double statusAmount = accumulateEXP(p, amount, this.type);
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(status_experience_gain
                                .replace("%skill%", this.displayName)
                                .replace("%exp%", ((statusAmount >= 0) ? "+" : "") + (String.format("%,.2f", statusAmount).replace("-", ""))))));
                    }
                    showBossBar(p, profile);
                }

                ProfileManager.setProfile(p, profile, this.type);
                if (profile.getExp() >= expForlevel(profile.getLevel() + 1)){
                    updateLevelUpConditions(p, silent);
                }
            }
        }
    }

    private final Map<UUID, EXPStatusStruct> expTracker = new HashMap<>();

    /*
    This method is solely responsible for the prevention of possibly misleading action bar messages when obtaining EXP.
    If you were to get an amount of exp several times quickly in succession, as typically happens with brewing for example,
    this method would make it so you would see the combined total of exp rather than the last instance of exp you were given.
    This exp accumulates on the actionbar as long as:
    - it's been less than 3 seconds since the last EXP instance
    - the types of EXP match to what's given
    - the amount is less than 100,000

    This method has no influence on the experience obtained, it is purely a visual effect.
     */
    private double accumulateEXP(Player p, double amount, String type){
        EXPStatusStruct struct = expTracker.get(p.getUniqueId());
        if (struct == null) {
            struct = new EXPStatusStruct(type, 0, System.currentTimeMillis());
        }
        if (!struct.getType().equals(type) || struct.getTime_since_last() + 3000L <= System.currentTimeMillis() || struct.getExp() > 100000){
            struct = new EXPStatusStruct(type, 0, System.currentTimeMillis());
        }
        struct.setExp(struct.getExp() + amount);
        struct.setTime_since_last(System.currentTimeMillis());
        struct.setType(type);

        expTracker.put(p.getUniqueId(), struct);
        return struct.getExp();
    }

    private static class EXPStatusStruct{
        private String type;
        private Long time_since_last;
        private double exp;

        public EXPStatusStruct(String type, double exp, Long time_since_last){
            this.type = type;
            this.exp = exp;
            this.time_since_last = time_since_last;
        }

        public double getExp() {
            return exp;
        }

        public Long getTime_since_last() {
            return time_since_last;
        }

        public String getType() {
            return type;
        }

        public void setExp(double exp) {
            this.exp = exp;
        }

        public void setTime_since_last(Long time_since_last) {
            this.time_since_last = time_since_last;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    private void showBossBar(Player p, Profile profile){
        if (!barTitle.equals("")){
            double expForNextLevel = expForlevel(profile.getLevel() + 1);

            String bossBarTitle = Utils.chat(barTitle
                    .replace("%skill%", this.displayName)
                    .replace("%exp_current%", String.format("%.2f", profile.getExp()))
                    .replace("%lv_current%", "" + Math.abs(profile.getLevel()))
                    .replace("%lv_next%", (expForNextLevel == -1) ? TranslationManager.getInstance().getTranslation("max_level") : "" + (profile.getLevel() + 1))
                    .replace("%exp_next%", (expForNextLevel == -1) ? TranslationManager.getInstance().getTranslation("max_level") : String.format("%.2f", expForNextLevel)));
            float fraction;
            if (expForNextLevel <= 0) {
                fraction = 1;
            } else {
                fraction = (float) Utils.round(profile.getExp() / expForNextLevel, 4);
            }
            BossBarUtils.showBossBarToPlayer(p, bossBarTitle, Math.min(fraction, 1F), 50, this.type, barColor, barStyle);
        } else {
            System.out.println("bar title empty");
        }
    }

    /**
     * Levels a player up once, and applies any rewards within the skill to the player
     * @param p the player to level up
     */
    public void levelPlayerUp(Player p, Profile profile, boolean silent){
        if (profile == null) profile = ProfileManager.newProfile(p, this.type);
        if (profile != null){
            int levelFrom = profile.getLevel();
            PlayerSkillLevelupEvent event = new PlayerSkillLevelupEvent(p, this, levelFrom + 1);
            ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(event);
            if (!this.getType().equals("ACCOUNT")){
                Skill accountSkill = SkillProgressionManager.getInstance().getSkill("ACCOUNT");
                if (accountSkill != null) {
                    if (accountSkill instanceof AccountSkill){
                        ((AccountSkill) accountSkill).addAccountEXP(p, ((AccountSkill) accountSkill).getExpPerLevelUp());
                    }
                }
            }
            profile.setLevel(levelFrom + 1);
            ProfileManager.setProfile(p, profile, this.type);
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
            if (!silent){
                if (specialLevelingMessages.containsKey(newLevel)){
                    for (String message : specialLevelingMessages.get(newLevel)){
                        p.sendMessage(Utils.chat(PlaceholderManager.parse(message, p)));
                    }
                }
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
            if (specialLevelingPerks.containsKey(newLevel)){
                for (PerkReward reward : specialLevelingPerks.get(newLevel)){
                    reward.execute(p);
                }
            }
        }
    }
}
