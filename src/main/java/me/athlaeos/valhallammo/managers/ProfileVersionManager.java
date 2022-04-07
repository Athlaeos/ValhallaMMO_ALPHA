package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class ProfileVersionManager {
    private static ProfileVersionManager manager = null;
    private final NamespacedKey profileVersionKey = new NamespacedKey(ValhallaMMO.getPlugin(), "profile_version");
    private final int currentVersion;

    public ProfileVersionManager(){
        currentVersion = ConfigManager.getInstance().getConfig("config.yml").get().getInt("profile_version");
    }

    public void reload(){
        manager = null;
        getInstance();
    }

    public static ProfileVersionManager getInstance(){
        if (manager == null) manager = new ProfileVersionManager();
        return manager;
    }

    public int getProfileVersion(Player p){
        if (!p.getPersistentDataContainer().has(profileVersionKey, PersistentDataType.INTEGER)){
            setProfileVersion(p, currentVersion);
        }
        return p.getPersistentDataContainer().get(profileVersionKey, PersistentDataType.INTEGER);
    }

    public void setProfileVersion(Player p, int v){
        p.getPersistentDataContainer().set(profileVersionKey, PersistentDataType.INTEGER, v);
    }

    public void checkForReset(Player p){
        int version = getProfileVersion(p);
        if (version != currentVersion){
            ProfileManager.resetProfiles(p, true);
        }
    }
}
