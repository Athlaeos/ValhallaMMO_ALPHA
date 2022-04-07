package me.athlaeos.valhallammo.dom;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.account.AccountProfile;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class Perk {
    private final String name;
    private final String displayName;
    private final String description;
    private final Material icon;
    private final String skill;
    private final int x;
    private final int y;
    private final boolean hidden;
    private final int cost;
    private final int requirement_level;
    private final List<PerkReward> rewards;
    private final List<String> messages;
    private final List<String> commands;
    private final List<String> requirement_perk_one;
    private final List<String> requirement_perk_all;

    public Perk(String name, String displayName, String description, Material icon, String skill, int x, int y, boolean hidden, int cost, int requirement_level, List<PerkReward> rewards, List<String> messages, List<String> commands, List<String> requirement_perk_one, List<String> requirement_perk_all) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
        this.skill = skill;
        this.x = x;
        this.y = y;
        this.hidden = hidden;
        this.cost = cost;
        this.requirement_level = requirement_level;
        this.rewards = rewards;
        this.messages = messages;
        this.commands = commands;
        this.requirement_perk_one = requirement_perk_one;
        this.requirement_perk_all = requirement_perk_all;
    }

    public boolean canUnlock(Player p){
        Profile profile = ProfileManager.getProfile(p, "ACCOUNT");
        if (profile != null){
            if (profile instanceof AccountProfile){
                if (((AccountProfile) profile).getUnlockedPerks().contains(this.name)) {
                    return false;
                }
                return metLevelRequirement(p) && metAllPerkRequirement((AccountProfile) profile) && metSinglePerkRequirement((AccountProfile) profile);
            }
        }
        return false;
    }

    public boolean hasUnlocked(Player p){
        Profile profile = ProfileManager.getProfile(p, "ACCOUNT");
        if (profile != null){
            if (profile instanceof AccountProfile){
                return ((AccountProfile) profile).getUnlockedPerks().contains(this.name);
            }
        }
        return false;
    }

    private boolean metLevelRequirement(Player p){
        int currentLevel = 0;
        Profile profile = ProfileManager.getProfile(p, skill);
        if (profile != null){
            currentLevel = profile.getLevel();
        }
        return currentLevel >= requirement_level;
    }

    private boolean metSinglePerkRequirement(AccountProfile p){
        if (p != null){
            if (requirement_perk_one != null){
                if (requirement_perk_one.size() > 0){
                    boolean hasOneOf = false;
                    for (String require : requirement_perk_one){
                        hasOneOf = p.getUnlockedPerks().contains(require);
                        if (hasOneOf){
                            break;
                        }
                    }
                    return hasOneOf;
                }
            }
            return true;
        }
        return false;
    }

    private boolean metAllPerkRequirement(AccountProfile p){
        if (p != null){
            if (requirement_perk_all != null){
                if (requirement_perk_all.size() > 0){
                    return p.getUnlockedPerks().containsAll(requirement_perk_all);
                }
            }
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getIcon() {
        return icon;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isHidden() {
        return hidden;
    }

    public int getCost() {
        return cost;
    }

    public int getRequirement_level() {
        return requirement_level;
    }

    public List<PerkReward> getRewards() {
        return rewards;
    }

    public List<String> getMessages() {
        return messages;
    }

    public List<String> getCommands() {
        return commands;
    }

    public List<String> getRequirement_perk_one() {
        return requirement_perk_one;
    }

    public List<String> getRequirement_perk_all() {
        return requirement_perk_all;
    }

    public String getDescription() {
        return description;
    }

    public void execute(Player p){
        for (String message : messages){
            p.sendMessage(Utils.chat(message));
        }
        for (String command : commands){
            ValhallaMMO.getPlugin().getServer().dispatchCommand(ValhallaMMO.getPlugin().getServer().getConsoleSender(), command.replace("%player%", p.getName()));
        }
        for (PerkReward reward : rewards){
            reward.execute(p);
        }
    }
}
