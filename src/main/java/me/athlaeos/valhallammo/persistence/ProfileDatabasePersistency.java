package me.athlaeos.valhallammo.persistence;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.IProfilePersistency;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.utility.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileDatabasePersistency extends IProfilePersistency {
    private final DatabaseConnection conn;
    private final ProfileDataContainerPersistency altPersistency;

    private final Map<UUID, Map<String, Profile>> profiles = new HashMap<>();

    public ProfileDatabasePersistency(DatabaseConnection conn, ProfileDataContainerPersistency altPersistency){
        this.conn = conn;
        this.altPersistency = altPersistency;
        createDatabaseStructure();
    }

    private void createDatabaseStructure(){
        for (Skill s : SkillProgressionManager.getInstance().getAllSkills().values()){
            try {
                s.getCleanProfile().createProfileTable(conn.getConnection());
            } catch (SQLException e){
                ValhallaMMO.getPlugin().getServer().getLogger().severe("SQLException when trying create a table for skill type " + s.getType() + ". ");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setProfile(Player p, Profile profile, String type) {
        boolean reset = false;
        if (profile == null) {
            profile = getCleanProfile(p, type);
            reset = true;
        }
        if (profile != null){
            if (profile.getKey() != null){
                Skill s = SkillProgressionManager.getInstance().getSkill(type);
                if (s.getKey() != null){
                    if (profile.getKey().equals(s.getKey())){

                        Map<String, Profile> profiles = this.profiles.getOrDefault(p.getUniqueId(), new HashMap<>());
                        profiles.put(type, profile);
                        this.profiles.put(p.getUniqueId(), profiles);

                        if (reset){
                            profile.setDefaultStats(p);
                        }
                    } else {
                        throw new IllegalArgumentException("type NamespacedKey:" + s.getKey().getKey() + " does not match profile NamespacedKey:" + profile.getKey().getKey());
                    }
                }
            }
        }
    }

    @Override
    public Profile getProfile(Player p, String type) {
        if (p == null) return null;
        Skill s = SkillProgressionManager.getInstance().getSkill(type);
        if (s == null) return null;
        Profile profile = this.profiles.getOrDefault(p.getUniqueId(), new HashMap<>()).get(s.getType());
        if (profile == null){
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    Utils.chat(TranslationManager.getInstance().getTranslation("warning_profiles_unloaded"))
            ));
            return null;
        }
        return profile;
    }

    @Override
    public void loadPlayerProfiles(Player p) {
        if (profiles.containsKey(p.getUniqueId())) return; // stats are presumably already loaded in and so they do not
        // need to be loaded in from the database again
        new BukkitRunnable(){
            @Override
            public void run() {
                Map<String, Profile> profs = profiles.getOrDefault(p.getUniqueId(), new HashMap<>());
                for (Skill s : SkillProgressionManager.getInstance().getAllSkills().values()){
                    try {
                        Profile profile = s.getCleanProfile().fetchProfile(p, conn.getConnection());
                        // if no profile was found in the database, default to PersistentDataContainer implementation
                        // anyway
                        if (profile == null) profile = altPersistency.getProfile(p, s.getType());
                        if (profile == null) profile = getCleanProfile(p, s.getType());
                        profs.put(s.getType(), profile);
                    } catch (SQLException e){
                        ValhallaMMO.getPlugin().getServer().getLogger().severe("SQLException when trying to fetch " + p.getName() + "'s profile for skill type " + s.getType() + ". ");
                        e.printStackTrace();
                    }
                }
                profiles.put(p.getUniqueId(), profs);
                p.sendMessage(Utils.chat(TranslationManager.getInstance().getTranslation("status_profiles_loaded")));
            }
        }.runTaskAsynchronously(ValhallaMMO.getPlugin());
    }

    @Override
    public void savePlayerProfiles() {
        for (UUID p : profiles.keySet()){
            for (Profile profile : profiles.get(p).values()){
                try {
                    profile.insertOrUpdateProfile(conn.getConnection());
                } catch (SQLException e){
                    ValhallaMMO.getPlugin().getServer().getLogger().severe("SQLException when trying to save profile for profile type " + profile.getClass().getName() + ". ");
                    e.printStackTrace();
                }
            }
        }
    }
}
