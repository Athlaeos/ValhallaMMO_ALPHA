package me.athlaeos.valhallammo.persistence;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    private static DatabaseConnection databaseConnection = null;
    private Connection conn;

    public DatabaseConnection(){
        this.conn = initializeConnection();
    }

    public static DatabaseConnection getDatabaseConnection(){
        if (databaseConnection == null) databaseConnection = new DatabaseConnection();
        return databaseConnection;
    }

    public Connection getConnection() {
        return conn;
    }

    private Connection initializeConnection() {
        YamlConfiguration config = ConfigManager.getInstance().getConfig("config.yml").get();
        String host = config.getString("db_host");
        String database = config.getString("db_database");
        String username = config.getString("db_username");
        String password = config.getString("db_password");
        try {
            if (conn != null && !conn.isClosed()) {
                return conn;
            }

            synchronized (ValhallaMMO.getPlugin()) {
                if (conn != null && !conn.isClosed()) {
                    return conn;
                }
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://" + host+  "/" + database, username, password);
                //":" + this.port +
            }
        } catch (Exception e) {
            ValhallaMMO.getPlugin().getServer().getLogger().warning("Database connection failed, using PersistentDataContainer for profile persistence");
            return null;
        }
        return conn;
    }
}
