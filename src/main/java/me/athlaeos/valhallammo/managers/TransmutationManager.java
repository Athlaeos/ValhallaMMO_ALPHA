package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.Transmutation;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TransmutationManager {
    private static TransmutationManager manager = null;
    private final Map<String, Transmutation> transmutations = new HashMap<>();
    private final Map<Material, Transmutation> transmutationMaterialMap = new HashMap<>();
    private final NamespacedKey storedTransmutationsKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_transmutations");
    private final int radius;
    private final boolean flash;
    private Sound sound;

    public TransmutationManager(){
        YamlConfiguration transmutationConfig = ConfigManager.getInstance().getConfig("alchemy_transmutations.yml").get();
        this.radius = ConfigManager.getInstance().getConfig("skill_alchemy.yml").get().getInt("radius_transmutation_liquid", 2);
        this.flash = ConfigManager.getInstance().getConfig("skill_alchemy.yml").get().getBoolean("transmutation_liquid_flash", true);
        try {
            String s = ConfigManager.getInstance().getConfig("alchemy.yml").get().getString("transmutation_sound");
            if (s == null) {
                sound = null;
            } else {
                this.sound = Sound.valueOf(s);
            }
        } catch (IllegalArgumentException ignored){
        }

        ConfigurationSection section = transmutationConfig.getConfigurationSection("transmutations");
        if (section != null){
            for (String name : section.getKeys(false)){
                String fs = transmutationConfig.getString("transmutations." + name + ".from");
                String ts = transmutationConfig.getString("transmutations." + name + ".to");
                try {
                    Material from = Material.valueOf(fs);
                    Material to = Material.valueOf(ts);

                    registerTransmutation(new Transmutation(name, from, to));
                } catch (IllegalArgumentException no){
                    ValhallaMMO.getPlugin().getLogger().warning("[ValhallaMMO] Could not register transmutation " + name + ", invalid material used. " + fs + "/" + ts);
                }
            }
        }
    }

    public void reload(){
        manager = null;
        getInstance();
    }

    public Map<Material, Transmutation> getStoredTransmutations(ItemStack i){
        Map<Material, Transmutation> transmutations = new HashMap<>();
        if (Utils.isItemEmptyOrNull(i)) return transmutations;
        ItemMeta meta = i.getItemMeta();
        if (meta == null) return transmutations;
        if (meta.getPersistentDataContainer().has(storedTransmutationsKey, PersistentDataType.STRING)){
            String storedTransmutationString = meta.getPersistentDataContainer().get(storedTransmutationsKey, PersistentDataType.STRING);
            if (storedTransmutationString == null) return transmutations;
            String[] storedTransmutations = storedTransmutationString.split(";");
            for (String transmutation : storedTransmutations){
                Transmutation t = getTransmutation(transmutation);
                if (t == null) continue;
                transmutations.put(t.getFrom(), t);
            }
        }
        return transmutations;
    }

    public void setStoredTransmutations(ItemStack i, Collection<String> transmutations){
        if (Utils.isItemEmptyOrNull(i)) return;
        ItemMeta meta = i.getItemMeta();
        assert meta != null;
        if (transmutations != null){
            if (!transmutations.isEmpty()){
                String storedTransmutationString = String.join(";", transmutations);
                meta.getPersistentDataContainer().set(storedTransmutationsKey, PersistentDataType.STRING, storedTransmutationString);
                i.setItemMeta(meta);
                return;
            }
        }

        meta.getPersistentDataContainer().remove(storedTransmutationsKey);
        i.setItemMeta(meta);
    }

    public void registerTransmutation(Transmutation t){
        if (transmutationMaterialMap.containsKey(t.getFrom())) return;
        this.transmutations.put(t.getName(), t);
        this.transmutationMaterialMap.put(t.getFrom(), t);
    }

    public static TransmutationManager getInstance(){
        if (manager == null) manager = new TransmutationManager();
        return manager;
    }

    public int getRadius() {
        return radius;
    }

    public boolean isFlash() {
        return flash;
    }

    public Sound getSound() {
        return sound;
    }

    public Transmutation getTransmutation(String name){
        return transmutations.get(name);
    }

    public Transmutation getTransmutation(Material m){
        return transmutationMaterialMap.get(m);
    }

    public Map<String, Transmutation> getTransmutations() {
        return transmutations;
    }
}
