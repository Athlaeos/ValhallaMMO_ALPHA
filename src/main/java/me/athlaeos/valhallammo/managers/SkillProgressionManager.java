package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.Perk;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.skills.account.AccountProfile;
import me.athlaeos.valhallammo.skills.account.AccountSkill;
import me.athlaeos.valhallammo.skills.alchemy.AlchemySkill;
import me.athlaeos.valhallammo.skills.archery.ArcherySkill;
import me.athlaeos.valhallammo.skills.enchanting.EnchantingSkill;
import me.athlaeos.valhallammo.skills.farming.FarmingSkill;
import me.athlaeos.valhallammo.skills.heavyarmor.HeavyArmorSkill;
import me.athlaeos.valhallammo.skills.landscaping.LandscapingSkill;
import me.athlaeos.valhallammo.skills.lightarmor.LightArmorSkill;
import me.athlaeos.valhallammo.skills.mining.MiningSkill;
import me.athlaeos.valhallammo.skills.smithing.SmithingSkill;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SkillProgressionManager {
    private static SkillProgressionManager manager = null;
    private final Map<String, Skill> allSkills;
    private final Map<String, Map<String, Perk>> allPerks;

    public SkillProgressionManager(){
        allSkills = new HashMap<>();
        allPerks = new HashMap<>();
        YamlConfiguration config = ConfigManager.getInstance().getConfig("config.yml").get();
        if (config.getBoolean("enabled_skills.alchemy")) registerSkill(new AlchemySkill("ALCHEMY"));
        if (config.getBoolean("enabled_skills.smithing")) registerSkill(new SmithingSkill("SMITHING"));
        if (config.getBoolean("enabled_skills.player")) registerSkill(new AccountSkill("ACCOUNT"));
        if (config.getBoolean("enabled_skills.enchanting")) registerSkill(new EnchantingSkill("ENCHANTING"));
        if (config.getBoolean("enabled_skills.farming")) registerSkill(new FarmingSkill("FARMING"));
        if (config.getBoolean("enabled_skills.mining")) registerSkill(new MiningSkill("MINING"));
        if (config.getBoolean("enabled_skills.landscaping")) registerSkill(new LandscapingSkill("LANDSCAPING"));
        if (config.getBoolean("enabled_skills.archery")) registerSkill(new ArcherySkill("ARCHERY"));
        if (config.getBoolean("enabled_skills.armor_light")) registerSkill(new LightArmorSkill("LIGHT_ARMOR"));
        if (config.getBoolean("enabled_skills.armor_heavy")) registerSkill(new HeavyArmorSkill("HEAVY_ARMOR"));


    }

    public void registerSkill(Skill skill){
        allSkills.put(skill.getType(), skill);
    }

    public void reload(){
        manager = null;
        getInstance();
    }

    public void registerPerks(){
        for (Skill s : allSkills.values()){
            s.registerPerks();
        }
    }

    public Skill getSkill(String skill){
        return allSkills.get(skill);
    }

    public void registerPerk(Perk p, String skillType){
        Map<String, Perk> existingPerks = this.allPerks.get(skillType);
        if (existingPerks == null) existingPerks = new HashMap<>();
        existingPerks.put(p.getName(), p);
        this.allPerks.put(skillType, existingPerks);
    }

    public static SkillProgressionManager getInstance(){
        if (manager == null) {
            manager = new SkillProgressionManager();
        }
        return manager;
    }

    public Perk getPerk(String name, String type){
        if (allPerks.containsKey(type)){
            return allPerks.get(type).get(name);
        }
        return null;
    }

    public Map<String, Perk> getAllPerks(){
        Map<String, Perk> perks = new HashMap<>();
        for (String skill : allPerks.keySet()){
            perks.putAll(allPerks.get(skill));
        }
        return perks;
    }

    public Map<String, Skill> getAllSkills() {
        return allSkills;
    }

    public void unlockPerk(Player player, Perk perk){
        Profile account = ProfileManager.getManager().getProfile(player, "ACCOUNT");
        if (account != null){
            if (account instanceof AccountProfile){
                Set<String> perks = ((AccountProfile) account).getUnlockedPerks();
                if (perks.contains(perk.getName())) return;
                perks.add(perk.getName());
                ((AccountProfile) account).setUnlockedPerks(perks);
                ProfileManager.getManager().setProfile(player, account, "ACCOUNT");
                perk.execute(player);
            }
        }
    }
}
