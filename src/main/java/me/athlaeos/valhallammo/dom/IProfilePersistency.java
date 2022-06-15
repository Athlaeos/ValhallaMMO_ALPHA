package me.athlaeos.valhallammo.dom;

import me.athlaeos.valhallammo.events.PlayerSkillExperienceGainEvent;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.skills.Skill;
import org.bukkit.entity.Player;

public abstract class IProfilePersistency {
    /**
     * Sets a profile of a given type to a player
     * @param p the player to set the profile to
     * @param profile the profile to set to the player. If the profile type doesn't match the profile's type, an
     *                IllegalArgumentException is thrown. Ex: if setting a SmithingProfile, type SMITHING must be used.
     * @param type the profile type you're attempting to set
     */
    public abstract void setProfile(Player p, Profile profile, String type);

    /**
     * If a player has the profile of a given type in their PersistentDataContainer it returns an abstract Profile
     * that can be cast to that profile type.
     * @param p the player to get their profile from
     * @param type the type of profile you're looking for on the player
     * @return the abstract Profile on the player of the given type, or null if they have none.
     */
    public abstract Profile getProfile(Player p, String type);

    /**
     * Returns an instance of Profile from the given SkillType
     * Ex: "SMITHING" returns a SmithingProfile
     * @param owner the player owner of the profile
     * @param type the type of profile you want to generate
     * @return an instance of Profile from the given SkillType
     */
    public final Profile getCleanProfile(Player owner, String type){
        Profile newProfile = null;
        Skill s = SkillProgressionManager.getInstance().getSkill(type);
        try {
            if (s != null) {
                newProfile = s.getCleanProfile().clone();
                newProfile.setOwner(owner.getUniqueId());
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
    public final void resetProfiles(Player p, boolean hardReset){
        setProfile(p, null, "ACCOUNT"); // account profile must be the first to reset
        for (String type : SkillProgressionManager.getInstance().getAllSkills().keySet()){
            if (type.equals("ACCOUNT")) continue;
            double exp = 0;
            if (!hardReset){
                Profile profile = getProfile(p, type);
                exp = profile.getLifetimeEXP();
            }
            setProfile(p, null, type);
            if (!hardReset && exp > 0){
                SkillProgressionManager.getInstance().getSkill(type).addEXP(p, exp, true, PlayerSkillExperienceGainEvent.ExperienceGainReason.COMMAND);
            }
        }
    }

    /**
     * loads a player's profiles into memory if needed
     */
    public abstract void loadPlayerProfiles(Player p);

    /**
     * saves and persists a profile
     */
    public abstract void savePlayerProfiles();
}
