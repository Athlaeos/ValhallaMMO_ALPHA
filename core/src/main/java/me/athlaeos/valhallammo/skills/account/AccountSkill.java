package me.athlaeos.valhallammo.skills.account;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.events.PlayerSkillExperienceGainEvent;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.Skill;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class AccountSkill extends Skill {
    private final double expPerLevelUp;

    public AccountSkill(String type) {
        super(type);
        skillTreeMenuOrderPriority = 0;
        YamlConfiguration playerConfig = ConfigManager.getInstance().getConfig("skill_player.yml").get();
        YamlConfiguration playerProgressionConfig = ConfigManager.getInstance().getConfig("progression_player.yml").get();

        this.loadCommonConfig(playerConfig, playerProgressionConfig);

        this.expPerLevelUp = playerProgressionConfig.getDouble("experience.exp_gain");
    }

    @Override
    public NamespacedKey getKey() {
        return new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_profile_account");
    }

    @Override
    public Profile getCleanProfile() {
        return new AccountProfile(null);
    }

    public double getExpPerLevelUp() {
        return expPerLevelUp;
    }

    public void addAccountEXP(Player p, double amount, PlayerSkillExperienceGainEvent.ExperienceGainReason reason){
        Profile profile = ProfileManager.getManager().getProfile(p, "ACCOUNT");
        if (profile == null) profile = ProfileManager.getManager().newProfile(p, "ACCOUNT");
        if (profile != null){
            if (profile instanceof AccountProfile){
                PlayerSkillExperienceGainEvent event = new PlayerSkillExperienceGainEvent(p, amount, this, reason);
                ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()){
                    addEXP(p, event.getAmount(), false, reason);
                }
            }
        }
    }
}
