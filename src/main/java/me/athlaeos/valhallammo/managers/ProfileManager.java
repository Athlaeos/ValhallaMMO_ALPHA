package me.athlaeos.valhallammo.managers;

import com.google.gson.Gson;
import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.skills.Skill;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class ProfileManager {
    /**
     * Sets a profile of a given type to a player
     * @param p the player to set the profile to
     * @param profile the profile to set to the player. If the profile type doesn't match the profile's type, an
     *                IllegalArgumentException is thrown. Ex: if setting a SmithingProfile, type SMITHING must be used.
     * @param type the profile type you're attempting to set
     */
    public static void setProfile(Player p, Profile profile, String type){
        boolean reset = false;
        if (profile == null) {
            profile = newProfile(p, type);
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

    /**
     * If a player has the profile of a given type in their PersistentDataContainer it returns an abstract Profile
     * that can be cast to that profile type.
     * @param p the player to get their profile from
     * @param type the type of profile you're looking for on the player
     * @return the abstract Profile on the player of the given type, or null if they have none.
     */
    public static Profile getProfile(Player p, String type){
        if (p == null) return null;
        Skill s = SkillProgressionManager.getInstance().getSkill(type);
        if (!p.getPersistentDataContainer().has(s.getKey(), PersistentDataType.STRING)){
            setProfile(p, null, type);
        }
        String jsonProfile = p.getPersistentDataContainer().get(s.getKey(), PersistentDataType.STRING);
        if (jsonProfile == null) {
            ValhallaMMO.getPlugin().getLogger().severe("[ValhallaMMO] Profile is still null after creation, this should never occur. Notify plugin author");
            return null;
        }
        return new Gson().fromJson(jsonProfile, s.getCleanProfile().getClass());
    }

    /**
     * Returns an instance of Profile from the given SkillType
     * Ex: "SMITHING" returns a SmithingProfile
     * @param p the player owner of the profile
     * @param type the type of profile you want to generate
     * @return an instance of Profile from the given SkillType
     */
    public static Profile newProfile(Player p, String type){
        Profile newProfile = null;
        Skill s = SkillProgressionManager.getInstance().getSkill(type);
        try {
            if (s != null) {
                newProfile = s.getCleanProfile().clone();
                newProfile.setOwner(p.getUniqueId());
            }
        } catch (CloneNotSupportedException ignored){
        }
        return newProfile;
    }

    /**
     * resets a player's profiles
     * if hardReset is true, all profiles are reset to default stats
     * if hardReset is false, all profiles are reset to default stats and the player is then re-awarded their previous
     * EXP. Players leveling up with this EXP will do so silently.
     * Note: the ACCOUNT profile will not be re-awarded the same EXP since it gets its exp from leveling other skills.
     * @param p the player to reset their profiles
     * @param hardReset whether it should completely reset the player's profiles or not
     */
    public static void resetProfiles(Player p, boolean hardReset){
        ProfileManager.setProfile(p, null, "ACCOUNT"); // account profile must be the first to reset
        for (String type : SkillProgressionManager.getInstance().getAllSkills().keySet()){
            if (type.equals("ACCOUNT")) continue;
            double exp = 0;
            if (!hardReset){
                Profile profile = ProfileManager.getProfile(p, type);
                exp = profile.getLifetimeEXP();
            }
            ProfileManager.setProfile(p, null, type);
            if (!hardReset && exp > 0){
                SkillProgressionManager.getInstance().getSkill(type).addEXP(p, exp, true);
            }
        }
    }
}
