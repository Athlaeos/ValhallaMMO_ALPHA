package me.athlaeos.valhallammo.config;

import me.athlaeos.valhallammo.ValhallaMMO;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public class Config {
    private String name;
    private File file;
    private YamlConfiguration config;

    public Config(String name) {
        this.name = name;
    }

    public Config save() {
        if ((this.config == null) || (this.file == null))
            return this;
        try {
            if (config.getConfigurationSection("").getKeys(true).size() != 0)
                config.save(this.file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    public YamlConfiguration get() {
        if (this.config == null)
            reload();

        return this.config;
    }

    public Config saveDefaultConfig() {
        file = new File(ValhallaMMO.getPlugin().getDataFolder(), this.name);

        ValhallaMMO.getPlugin().saveResource(this.name, false);

        return this;
    }

    public Config reload() {
        if (file == null)
            this.file = new File(ValhallaMMO.getPlugin().getDataFolder(), this.name);

        this.config = YamlConfiguration.loadConfiguration(file);

        Reader defConfigStream;
        try {
            defConfigStream = new InputStreamReader(ValhallaMMO.getPlugin().getResource(this.name), "UTF8");

            if (defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                this.config.setDefaults(defConfig);
            }
        } catch (UnsupportedEncodingException | NullPointerException ignored) {
        }
        return this;
    }

    public Config copyDefaults(boolean force) {
        get().options().copyDefaults(force);
        return this;
    }

    public Config set(String key, Object value) {
        get().set(key, value);
        return this;
    }

    public Object get(String key) {
        return get().get(key);
    }
}