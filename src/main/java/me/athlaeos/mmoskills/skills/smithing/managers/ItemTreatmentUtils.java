package me.athlaeos.mmoskills.skills.smithing.managers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import me.athlaeos.mmoskills.MMOSkills;
import me.athlaeos.mmoskills.configs.ConfigManager;
import me.athlaeos.mmoskills.skills.smithing.materials.MaterialTreatment;
import me.athlaeos.mmoskills.utility.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ItemTreatmentUtils {
    private static NamespacedKey treatmentKey = new NamespacedKey(MMOSkills.getPlugin(), "mmoskills_treatments");
    private static BiMap<MaterialTreatment, String> treatmentTranslations = HashBiMap.create();
    private static boolean hideTreatmentLore;

    public static void loadConfig(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("textconfiguration.yml").get();
        treatmentTranslations.put(MaterialTreatment.TEMPERED, Utils.chat(config.getString("item_treatments.tempered")));
        treatmentTranslations.put(MaterialTreatment.QUENCHED, Utils.chat(config.getString("item_treatments.quenced")));
        treatmentTranslations.put(MaterialTreatment.WORK_HARDENED, Utils.chat(config.getString("item_treatments.work_hardened")));
        treatmentTranslations.put(MaterialTreatment.POLISHED, Utils.chat(config.getString("item_treatments.polished")));
        treatmentTranslations.put(MaterialTreatment.ENGRAVED, Utils.chat(config.getString("item_treatments.engraved")));
        treatmentTranslations.put(MaterialTreatment.LEATHER_BOUND, Utils.chat(config.getString("item_treatments.leather_bound")));
        treatmentTranslations.put(MaterialTreatment.WAX_COATED, Utils.chat(config.getString("item_treatments.wax_coated")));

        hideTreatmentLore = MMOSkills.getPlugin().getConfig().getBoolean("hide_treatments");
    }

    public static void updateItemTreatments(ItemStack item){
        if (item == null) return;
        if (item.getItemMeta() == null) return;
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable)) return;

        List<String> lore = meta.getLore();
        List<String> finalLore = new ArrayList<>();
        Set<MaterialTreatment> allItemsTreatments = getItemTreatments(item);
        if (lore == null) lore = new ArrayList<>();
        int lastTreatmentIndex = -1;
        for (String l : lore){
            if (treatmentTranslations.inverse().containsKey(l)){
                if (hideTreatmentLore) continue;
                lastTreatmentIndex = lore.indexOf(l);
                if (allItemsTreatments.contains(treatmentTranslations.inverse().get(l))){
                    finalLore.add(l);
                    allItemsTreatments.remove(treatmentTranslations.inverse().get(l));
                }
            } else {
                finalLore.add(l);
            }
        }
        for (MaterialTreatment m : allItemsTreatments){
            finalLore.add(lastTreatmentIndex + 1, treatmentTranslations.get(m));
        }
        meta.setLore(finalLore);
        item.setItemMeta(meta);
    }

    public static void addItemTreatment(ItemStack i, MaterialTreatment treatment){
        Set<MaterialTreatment> itemsTreatments = getItemTreatments(i);
        itemsTreatments.add(treatment);
        setItemTreatments(i, itemsTreatments);
    }

    public static boolean hasTreatment(ItemStack i, MaterialTreatment treatment){
        if (i == null) return false;
        ItemMeta meta = i.getItemMeta();
        if (meta == null) return false;
        if (!(meta instanceof Damageable)) return false;
        if (!meta.getPersistentDataContainer().has(treatmentKey, PersistentDataType.STRING)) return false;
        String treatmentString = meta.getPersistentDataContainer().get(treatmentKey, PersistentDataType.STRING);
        return treatmentString.contains(treatment.toString());
    }

    public static void setItemTreatments(ItemStack i, Set<MaterialTreatment> treatments){
        if (i == null) return;
        ItemMeta meta = i.getItemMeta();
        if (meta == null) return;
        if (!(meta instanceof Damageable)) return;
        List<String> stringTreatments = new ArrayList<>();
        for (MaterialTreatment materialTreatment : treatments){
            stringTreatments.add(materialTreatment.toString());
        }
        String treatmentString = String.join(";", stringTreatments);
        meta.getPersistentDataContainer().set(treatmentKey, PersistentDataType.STRING, treatmentString);
        i.setItemMeta(meta);
    }

    public static Set<MaterialTreatment> getItemTreatments(ItemStack i){
        Set<MaterialTreatment> itemsTreatments = new HashSet<>();
        if (i == null) return itemsTreatments;
        ItemMeta meta = i.getItemMeta();
        if (meta == null) return itemsTreatments;
        if (!(meta instanceof Damageable)) return itemsTreatments;
        if (!meta.getPersistentDataContainer().has(treatmentKey, PersistentDataType.STRING)) return itemsTreatments;
        String treatmentString = meta.getPersistentDataContainer().get(treatmentKey, PersistentDataType.STRING);
        String[] treatments = treatmentString.split(";");
        for (String s : treatments){
            try {
                MaterialTreatment treatment = MaterialTreatment.valueOf(s);
                itemsTreatments.add(treatment);
            } catch (IllegalArgumentException ignored){
            }
        }
        return itemsTreatments;
    }
}
