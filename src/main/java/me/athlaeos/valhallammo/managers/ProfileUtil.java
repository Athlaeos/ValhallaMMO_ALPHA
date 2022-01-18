package me.athlaeos.valhallammo.managers;

import com.google.gson.Gson;
import me.athlaeos.valhallammo.domain.Profile;
import me.athlaeos.valhallammo.domain.ProfileType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class ProfileManager {
    public static void setProfile(Player p, Profile profile, ProfileType type){
        if (profile == null) return;
        if (profile.getKey().equals(type.getProfileKey())){
            // The appropriate account type is being serialized to the player
            String jsonProfile = new Gson().toJson(profile);
            p.getPersistentDataContainer().set(type.getProfileKey(), PersistentDataType.STRING, jsonProfile);
        }
    }

    public static Profile getAccountProfile(Player p, ProfileType type){
        if (!p.getPersistentDataContainer().has(type.getProfileKey(), PersistentDataType.STRING)){
            return null;
        }
        String jsonProfile = p.getPersistentDataContainer().get(type.getProfileKey(), PersistentDataType.STRING);
        return new Gson().fromJson(jsonProfile, Profile.class);
    }
}
