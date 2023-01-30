package me.athlaeos.valhallammo.persistence;

import com.google.gson.Gson;
import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.Persistency;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.skills.Skill;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class ProfileDataContainerPersistency extends Persistency {
    private final Gson gson = new Gson();

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
                        String jsonProfile = new Gson().toJson(profile);
                        p.getPersistentDataContainer().set(s.getKey(), PersistentDataType.STRING, jsonProfile);
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
        if (!p.getPersistentDataContainer().has(s.getKey(), PersistentDataType.STRING)){
            setProfile(p, null, type);
        }
        String jsonProfile = p.getPersistentDataContainer().get(s.getKey(), PersistentDataType.STRING);
        if (jsonProfile == null) {
            ValhallaMMO.getPlugin().getLogger().severe("Profile is still null after creation, this should never occur. Notify plugin author");
            return null;
        }
        return gson.fromJson(jsonProfile, s.getCleanProfile().getClass());
    }

    @Override
    public void loadPlayerProfiles(Player p) {
        // profiles are persisted and loaded when required, so it is not necessary to load any profiles into memory
    }

    @Override
    public void savePlayerProfiles() {
        // do nothing
    }

    @Override
    public void savePlayerProfiles(Player p) {
        // do nothing
    }
}
