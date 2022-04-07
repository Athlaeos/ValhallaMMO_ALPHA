package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.config.ConfigManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;

public class MaterialCosmeticManager {
    private static MaterialCosmeticManager manager = null;

    private final Map<Material, Sound> craftFinishSounds;
    private final Map<Material, Sound> craftWorkSounds;
    private Sound defaultCraftFinishSound = null;
    private Sound defaultCraftWorKSound = null;

    public MaterialCosmeticManager(){
        craftFinishSounds = new HashMap<>();
        craftWorkSounds = new HashMap<>();
        YamlConfiguration config = ConfigManager.getInstance().getConfig("sounds.yml").get();
        ConfigurationSection finishSection = config.getConfigurationSection("craft_finish");
        if (finishSection != null){
            for (String station : finishSection.getKeys(false)){
                Material block = null;
                Sound sound = null;
                try {
                    sound = Sound.valueOf(config.getString("craft_finish." + station));
                } catch (IllegalArgumentException ignored){
                    if (config.getString("craft_finish." + station) != null) {
                        continue;
                    }
                }
                try {
                    block = Material.valueOf(station);
                } catch (IllegalArgumentException ignored){
                    if (station.equalsIgnoreCase("default")){
                        defaultCraftFinishSound = sound;
                    } else {
                        continue;
                    }
                }
                if (block != null){
                    craftFinishSounds.put(block, sound);
                }
            }
        }
        ConfigurationSection workSection = config.getConfigurationSection("craft_work");
        if (workSection != null){
            for (String station : workSection.getKeys(false)){
                Material block = null;
                Sound sound = null;
                try {
                    sound = Sound.valueOf(config.getString("craft_work." + station));
                } catch (IllegalArgumentException ignored){
                    if (config.getString("craft_work." + station) != null) {
                        continue;
                    }
                }
                try {
                    block = Material.valueOf(station);
                } catch (IllegalArgumentException ignored){
                    if (station.equalsIgnoreCase("default")){
                        defaultCraftWorKSound = sound;
                    } else {
                        continue;
                    }
                }
                if (block != null){
                    craftWorkSounds.put(block, sound);
                }
            }
        }
    }

    public static MaterialCosmeticManager getInstance(){
        if (manager == null) manager = new MaterialCosmeticManager();
        return manager;
    }

    public Sound getCraftFinishSounds(Material m) {
        if (craftFinishSounds.get(m) == null){
            return defaultCraftFinishSound;
        } else {
            return craftFinishSounds.get(m);
        }
    }

    public Sound getCraftWorkSound(Material m) {
        if (craftWorkSounds.get(m) == null){
            return defaultCraftWorKSound;
        } else {
            return craftWorkSounds.get(m);
        }
    }

    public void reload(){
        manager = null;
        getInstance();
    }
}
