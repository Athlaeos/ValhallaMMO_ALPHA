package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.config.ConfigManager;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranslationManager {

    private final Map<String, String> translationMap;
    // The map key is the key of the word/phrase to translate, its value is the translation.
    private final Map<String, List<String>> listTranslationMap;
    // The map key is the key of the word/phrase to translate, its value is a list of translations.
    private final Map<String, String> skillTranslations;

    private static TranslationManager manager = null;

    public TranslationManager(){
        translationMap = new HashMap<>();
        listTranslationMap = new HashMap<>();
        skillTranslations = new HashMap<>();

        String language = ConfigManager.getInstance().getConfig("config.yml").get().getString("language");

        YamlConfiguration config = ConfigManager.getInstance().getConfig("languages/" + language + ".yml").get();

        ConfigurationSection section = config.getConfigurationSection("");
        if (section != null){
            for (String key : section.getKeys(false)){
                Object value = config.get(key);
                if (value != null){
                    if (value instanceof List){
                        if (!((List<?>) value).isEmpty()){
                            listTranslationMap.put(key, (List<String>) value);
                        }
                    } else if (value instanceof String){
                        if (!value.equals("")){
                            translationMap.put(key, (String) value);
                        }
                    }
                }
            }
        }

        setSkillTranslation("ACCOUNT", "account");
        setSkillTranslation("ACROBATICS", "acrobatics");
        setSkillTranslation("ALCHEMY", "alchemy");
        setSkillTranslation("ARCHERY", "archery");
        setSkillTranslation("ARMOR_HEAVY", "heavy_armor");
        setSkillTranslation("ARMOR_LIGHT", "light_armor");
        setSkillTranslation("ENCHANTING", "enchanting");
        setSkillTranslation("FARMING", "farming");
        setSkillTranslation("MINING", "mining");
        setSkillTranslation("SMITHING", "smithing");
        setSkillTranslation("TRADING", "trading");
        setSkillTranslation("UNARMED", "unarmed");
        setSkillTranslation("WEAPONS_HEAVY", "heavy_weapons");
        setSkillTranslation("WEAPONS_LIGHT", "light_weapons");
        setSkillTranslation("LANDSCAPING", "landscaping");
    }

    public String getSkillTranslation(String type){
        String translation = skillTranslations.get(type);
        if (translation == null) return "-";
        return translation;
    }

    public Map<String, String> getSkillTranslations() {
        return skillTranslations;
    }

    private void setSkillTranslation(String skill, String path){
        String translation = getTranslation(path);
        if (translation != null){
            skillTranslations.put(skill, translation);
        }
    }

    public Map<String, String> getTranslationMap() {
        return translationMap;
    }

    public Map<String, List<String>> getListTranslationMap() {
        return listTranslationMap;
    }

    public static TranslationManager getInstance(){
        if (manager == null) manager = new TranslationManager();
        return manager;
    }

    public void reload(){
        String language = ConfigManager.getInstance().getConfig("config.yml").get().getString("language");
        ConfigManager.getInstance().getConfig("languages/" + language + ".yml").reload();
        manager = null;
        getInstance();
    }

    public String getTranslation(String key){
        String translation = translationMap.get(key);
        if (translation == null) return "";
        return StringEscapeUtils.unescapeJava(translation);
    }

    public List<String> getList(String key){
        List<String> list = new ArrayList<>();
        if (!manager.getListTranslationMap().containsKey(key)) return null;
        for (String s : manager.getListTranslationMap().get(key)){
            list.add(StringEscapeUtils.unescapeJava(s));
        }
        return list;
    }
}
