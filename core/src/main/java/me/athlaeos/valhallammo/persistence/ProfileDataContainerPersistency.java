package me.athlaeos.valhallammo.persistence;

import com.google.gson.Gson;
import me.athlaeos.valhallammo.dom.Persistency;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.utility.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileDataContainerPersistency extends Persistency {
    private final Gson gson = new Gson();
    private final Map<UUID, Map<String, Profile>> profiles = new HashMap<>();

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

    private void persistProfile(Player p, Profile profile, String type){
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
        if (profiles.containsKey(p.getUniqueId())) return;
        Map<String, Profile> profs = profiles.getOrDefault(p.getUniqueId(), new HashMap<>());
        for (Skill s : SkillProgressionManager.getInstance().getAllSkills().values()){
            if (!p.getPersistentDataContainer().has(s.getKey(), PersistentDataType.STRING)){
                setProfile(p, s.getCleanProfile(), s.getType());
            }
            String jsonProfile = p.getPersistentDataContainer().get(s.getKey(), PersistentDataType.STRING);
            profs.put(s.getType(), gson.fromJson(jsonProfile, s.getCleanProfile().getClass()));
        }
        profiles.put(p.getUniqueId(), profs);
        p.sendMessage(Utils.chat(TranslationManager.getInstance().getTranslation("status_profiles_loaded")));
        // profiles are persisted and loaded when required, so it is not necessary to load any profiles into memory
    }

    @Override
    public void savePlayerProfiles() {
        // do nothing
    }

    @Override
    public void savePlayerProfiles(Player p) {
        if (profiles.containsKey(p.getUniqueId())){
            for (String skill : profiles.get(p.getUniqueId()).keySet()){
                Profile profile = profiles.get(p.getUniqueId()).get(skill);
                persistProfile(p, profile, skill);
            }
            profiles.remove(p.getUniqueId());
        }
    }
}
