package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
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
    private final Map<String, String> placeholderTranslations;
    private final Map<String, List<String>> placeholderListTranslationMap;
    private final Map<Material, String> localizedMaterialNames;

    private static TranslationManager manager = null;
    private final String language;
    private File config;

    public File getConfig() {
        return config;
    }

    public TranslationManager(){
        translationMap = new HashMap<>();
        listTranslationMap = new HashMap<>();
        skillTranslations = new HashMap<>();
        placeholderTranslations = new HashMap<>();
        placeholderListTranslationMap = new HashMap<>();
        localizedMaterialNames = new HashMap<>();

        String language = ConfigManager.getInstance().getConfig("config.yml").get().getString("language", "en-us");
        this.language = language;
        this.config = new File(ValhallaMMO.getPlugin().getDataFolder(), "languages/" + language + ".yml");

        String targetConfig = "languages/" + language + ".yml";
        if (!this.config.exists()){
            targetConfig = "languages/en-us.yml";
            this.config = new File(ValhallaMMO.getPlugin().getDataFolder(), "languages/en-us.yml");
        }

        YamlConfiguration config = ConfigManager.getInstance().getConfig(targetConfig).get();
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

        ConfigurationSection placeholderSection = config.getConfigurationSection("placeholders");
        if (placeholderSection != null){
            for (String key : placeholderSection.getKeys(false)){
                Object value = config.get("placeholders." + key);
                if (value != null){
                    if (value instanceof List){
                        if (!((List<?>) value).isEmpty()){
                            placeholderListTranslationMap.put(key, (List<String>) value);
                        }
                    } else if (value instanceof String){
                        if (!value.equals("")){
                            placeholderTranslations.put(key, (String) value);
                        }
                    }
                }
            }
        }

        YamlConfiguration materialConfig = ConfigManager.getInstance().getConfig("languages/materials/" + language + ".yml").get();
        for (Material m : Material.values()){
            String translation = materialConfig.getString(m.toString());
            if (translation == null) continue;
            localizedMaterialNames.put(m, translation);
        }

        setSkillTranslation("ACCOUNT", "account");
        setSkillTranslation("ACROBATICS", "acrobatics");
        setSkillTranslation("ALCHEMY", "alchemy");
        setSkillTranslation("ARCHERY", "archery");
        setSkillTranslation("HEAVY_ARMOR", "heavy_armor");
        setSkillTranslation("LIGHT_ARMOR", "light_armor");
        setSkillTranslation("ENCHANTING", "enchanting");
        setSkillTranslation("FARMING", "farming");
        setSkillTranslation("MINING", "mining");
        setSkillTranslation("SMITHING", "smithing");
        setSkillTranslation("TRADING", "trading");
        setSkillTranslation("UNARMED", "unarmed");
        setSkillTranslation("HEAVY_WEAPONS", "heavy_weapons");
        setSkillTranslation("LIGHT_WEAPONS", "light_weapons");
        setSkillTranslation("LANDSCAPING", "landscaping");
    }

    public String getSkillTranslation(String type){
        String translation = skillTranslations.get(type);
        if (translation == null) return "-";
        return translation;
    }

    public Map<Material, String> getLocalizedMaterialNames() {
        return localizedMaterialNames;
    }

    public String translatePlaceholders(String originalString){
        String[] matches = StringUtils.substringsBetween(originalString, "<lang.", ">");
        if (matches == null) {
            return originalString;
        }
        for (String s : matches){
            String replacement = placeholderTranslations.getOrDefault(s, translationMap.getOrDefault(s, ""));
            originalString = originalString.replace("<lang." + s + ">", replacement); //getTranslation(s)
        }
        return originalString;
    }

    public List<String> translateListPlaceholders(List<String> originalList){
        List<String> newList = new ArrayList<>();
        if (originalList == null) return newList;

        for (String l : originalList) {
            String subString = StringUtils.substringBetween(l, "<lang.", ">");
            if (subString == null) {
                // list does not contain placeholder match, string is added normally
                newList.add(translatePlaceholders(Utils.chat(l)));
            } else {
                // list has a line matching the placeholder format, placeholder is replaced with associated value
                List<String> placeholderList = placeholderListTranslationMap.getOrDefault(subString, new ArrayList<>());
                if (placeholderList.isEmpty()){
                    newList.add(translatePlaceholders(Utils.chat(l)));
                } else {
                    for (String s : placeholderList) {
                        // each line in the associated list is once again passed through the translation method
                        newList.add(translatePlaceholders(Utils.chat(s)));
                    }
                }
            }
        }
        return newList;
    }

    public String getLanguage() {
        return language;
    }

    /**
     * Replaces any language placeholders in the display name and lore to their translated versions
     * @param i the item to translate
     */
    public ItemStack translateItemStack(ItemStack i){
        if (Utils.isItemEmptyOrNull(i)) return null;
        boolean translated = false;
        ItemMeta iMeta = i.getItemMeta();
        if (iMeta == null) return null;
        if (iMeta.hasDisplayName()){
            if (iMeta.getDisplayName().contains("<lang.")){
                iMeta.setDisplayName(Utils.chat(translatePlaceholders(iMeta.getDisplayName())));
                translated = true;
            }
        }
        if (iMeta.hasLore() && iMeta.getLore() != null){
            List<String> newLore = new ArrayList<>();
            if (iMeta.getLore().stream().anyMatch(s -> s.contains("<lang."))){
                for (String s : translateListPlaceholders(iMeta.getLore())){
                    newLore.add(Utils.chat(s));
                }
                translated = true;
            } else newLore = iMeta.getLore();

            iMeta.setLore(newLore);
        }
        if (!translated) return i;
        i.setItemMeta(iMeta);
        i = reSetItemText(i);

        return i;
    }

    public ItemStack reSetItemText(ItemStack i){
        if (i == null) return null;
        ItemMeta iMeta = i.getItemMeta();
        if (iMeta == null) return null;
        if (iMeta.hasLore() && iMeta.getLore() != null){
            List<String> newLore = new ArrayList<>();
            for (String s : iMeta.getLore()){
                newLore.add(Utils.chat(s));
            }

            iMeta.setLore(newLore);
        }
        if (iMeta.hasDisplayName()){
            iMeta.setDisplayName(Utils.chat(iMeta.getDisplayName()));
        }
        i.setItemMeta(iMeta);
        return i;
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
        if (translation == null) {
            return "";
        }
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
