package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.dom.IProfilePersistency;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.persistence.DatabaseConnection;
import me.athlaeos.valhallammo.persistence.ProfileDataContainerPersistency;
import me.athlaeos.valhallammo.persistence.ProfileDatabasePersistency;
import org.bukkit.entity.Player;

public class ProfileManager {
    private static ProfileManager manager = null;
    private IProfilePersistency persistency = null;

    public void setupPersistency(){
        DatabaseConnection conn = DatabaseConnection.getDatabaseConnection();
        ProfileDataContainerPersistency PDCPersistency = new ProfileDataContainerPersistency();
        if (conn.getConnection() != null){
            persistency = new ProfileDatabasePersistency(conn, PDCPersistency);
        } else {
            persistency = PDCPersistency;
        }
    }

    public void setProfile(Player p, Profile profile, String type){
        persistency.setProfile(p, profile, type);
    }

    public Profile getProfile(Player p, String type){
        return persistency.getProfile(p, type);
    }

    public Profile newProfile(Player p, String type){
        return persistency.getCleanProfile(p, type);
    }

    public void resetProfiles(Player p, boolean hardReset){
        persistency.resetProfiles(p, hardReset);
    }

    public void loadPlayerProfiles(Player p){
        persistency.loadPlayerProfiles(p);
    }

    public void savePlayerProfiles() { persistency.savePlayerProfiles(); }

    public static ProfileManager getManager() {
        if (manager == null) {
            manager = new ProfileManager();
            manager.setupPersistency();
        }
        return manager;
    }
}
