package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class ProfileVersionManager {
    private static ProfileVersionManager manager = null;
    private final NamespacedKey profileOldVersionKey = new NamespacedKey(ValhallaMMO.getPlugin(), "profile_version");
    private final NamespacedKey profileMainVersionKey = new NamespacedKey(ValhallaMMO.getPlugin(), "profile_main_version");
    private final NamespacedKey profileSubVersionKey = new NamespacedKey(ValhallaMMO.getPlugin(), "profile_sub_version");
    private final int oldVersion;
    private final int currentMainVersion;
    private final int currentSubVersion;

    public ProfileVersionManager(){
        oldVersion = ConfigManager.getInstance().getConfig("config.yml").get().getInt("profile_version");
        currentMainVersion = ConfigManager.getInstance().getConfig("config.yml").get().getInt("profile_main_version");
        currentSubVersion = ConfigManager.getInstance().getConfig("config.yml").get().getInt("profile_sub_version");
    }

    public void reload(){
        manager = null;
        getInstance();
    }

    public static ProfileVersionManager getInstance(){
        if (manager == null) manager = new ProfileVersionManager();
        return manager;
    }

    public int getOldProfileVersion(Player p){
        return p.getPersistentDataContainer().getOrDefault(profileOldVersionKey, PersistentDataType.INTEGER, -1);
    }

    public int getProfileMainVersion(Player p){
        if (!p.getPersistentDataContainer().has(profileMainVersionKey, PersistentDataType.INTEGER)){
            setProfileMainVersion(p, currentMainVersion);
        }
        return p.getPersistentDataContainer().getOrDefault(profileMainVersionKey, PersistentDataType.INTEGER, currentMainVersion);
    }

    public int getProfileSubVersion(Player p){
        if (!p.getPersistentDataContainer().has(profileSubVersionKey, PersistentDataType.INTEGER)){
            setProfileSubVersion(p, currentMainVersion);
        }
        return p.getPersistentDataContainer().getOrDefault(profileSubVersionKey, PersistentDataType.INTEGER, currentSubVersion);
    }

    public void setProfileMainVersion(Player p, int v){
        p.getPersistentDataContainer().set(profileMainVersionKey, PersistentDataType.INTEGER, v);
    }

    public void setProfileSubVersion(Player p, int v){
        p.getPersistentDataContainer().set(profileSubVersionKey, PersistentDataType.INTEGER, v);
    }

    public void checkForReset(Player p){
        int oldVersion = getOldProfileVersion(p);
        if (oldVersion >= 0 && oldVersion != this.oldVersion) {
            // player still has old version, and profile version are not the same. soft reset
            ProfileManager.getManager().resetProfiles(p, false);
            p.getPersistentDataContainer().remove(profileOldVersionKey);
            return;
        }
        int mainVersion = getProfileMainVersion(p);
        if (mainVersion != currentMainVersion){
            // if main versions are not the same, hard reset
            ProfileManager.getManager().resetProfiles(p, true);
            setProfileMainVersion(p, currentMainVersion);
            return;
        }
        int subVersion = getProfileSubVersion(p);
        if (subVersion != currentSubVersion){
            // if sub versions are not the same, soft reset
            ProfileManager.getManager().resetProfiles(p, false);
            setProfileSubVersion(p, currentSubVersion);
        }
    }
}
