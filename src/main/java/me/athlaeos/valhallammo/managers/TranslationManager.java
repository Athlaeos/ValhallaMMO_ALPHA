package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.configs.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;

public class TranslationManager {

    private final Map<String, String> translationMap = new HashMap<>();
    // The map key is the key of the word/phrase to translate, its value is the translation.

    private static TranslationManager manager = null;

    public TranslationManager(){
        String language = ConfigManager.getInstance().getConfig("config.yml").get().getString("language");

        YamlConfiguration config = ConfigManager.getInstance().getConfig("languages/" + language + ".yml").get();

        ConfigurationSection section = config.getConfigurationSection("");
        if (section != null){
            for (String key : section.getKeys(false)){
                String value = config.getString(key);
                if (value != null){
                    if (!value.equals("")){
                        translationMap.put(key, value);
                    }
                }
            }
        }
    }

    public Map<String, String> getTranslationMap() {
        return translationMap;
    }

    public static TranslationManager getInstance(){
        if (manager == null) manager = new TranslationManager();
        return manager;
    }

    public String getTranslation(String key){
        return manager.getTranslationMap().get(key);
    }
}
