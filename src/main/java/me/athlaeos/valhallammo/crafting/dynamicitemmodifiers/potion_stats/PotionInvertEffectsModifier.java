package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.potion_stats;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.items.potioneffectwrappers.PotionEffectWrapper;
import me.athlaeos.valhallammo.managers.PotionAttributesManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PotionInvertEffectsModifier extends DynamicItemModifier {
    private static Map<String, InvertedEffect> invertedEffects = null;

    public PotionInvertEffectsModifier(String name, double duration_ticks, ModifierPriority priority) {
        super(name, duration_ticks, priority);

        this.name = name;
        this.category = ModifierCategory.POTION_STATS;

        this.bigStepDecrease = 0;
        this.bigStepIncrease = 0;
        this.smallStepDecrease = 0;
        this.smallStepIncrease = 0;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 0;

        this.description = Utils.chat("&7Inverts the potion's potion effects for as much as possible. If no potion effect could be" +
                " inverted, the recipe is cancelled.");
        this.displayName = Utils.chat("&7&lInvert Potion Effects");

        if (invertedEffects == null){
            invertedEffects = new HashMap<>();
            YamlConfiguration config = ConfigManager.getInstance().getConfig("skill_alchemy.yml").get();
            ConfigurationSection section = config.getConfigurationSection("effects_inverted");
            if (section != null){
                for (String effect : section.getKeys(false)){
                    String invert = config.getString("effects_inverted." + effect + ".inverted_effect");
                    if (invert == null) continue;
                    String color = config.getString("effects_inverted." + effect + ".color");
                    int duration = config.getInt("effects_inverted." + effect + ".duration");
                    double amplifier = config.getDouble("effects_inverted." + effect + ".amplifier");
                    if (color == null){
                        invertedEffects.put(effect, new InvertedEffect(invert, duration, amplifier));
                    } else {
                        invertedEffects.put(effect, new InvertedEffect(invert, color, duration, amplifier));
                    }
                }
            }
        }

        this.icon = Material.FERMENTED_SPIDER_EYE;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        if (!(outputItem.getItemMeta() instanceof PotionMeta)) return null;

        Collection<PotionEffectWrapper> defaultWrappers = PotionAttributesManager.getInstance().getDefaultPotionEffects(outputItem);
        Collection<PotionEffectWrapper> currentWrappers = PotionAttributesManager.getInstance().getCurrentStats(outputItem);

        Collection<PotionEffectWrapper> bDefaultWrappers = new HashSet<>(defaultWrappers);
        Collection<PotionEffectWrapper> bCurrentWrappers = new HashSet<>(currentWrappers);

        boolean changedDefaults = false;
        for (PotionEffectWrapper w : defaultWrappers){
            InvertedEffect invert = invertedEffects.get(w.getPotionEffect());
            if (invert == null) {
                continue;
            }
            bDefaultWrappers.removeIf(potionEffectWrapper -> potionEffectWrapper.getPotionEffect().equals(w.getPotionEffect()));
            try {
                PotionEffectWrapper wrapper = w.clone();
                wrapper.setPotionEffect(invert.getInvertedEffect());
                wrapper.setDuration(invert.getDuration());
                wrapper.setAmplifier(invert.getAmplifier());

                if (invert.getColor() != null){
                    PotionMeta meta = (PotionMeta) outputItem.getItemMeta();
                    if (meta == null) return null;

                    java.awt.Color c = Utils.hexToRgb(invert.getColor());
                    meta.setColor(Color.fromRGB(c.getRed(), c.getBlue(), c.getGreen()));
                    outputItem.setItemMeta(meta);
                }

                bDefaultWrappers.add(wrapper);
                changedDefaults = true;
            } catch (CloneNotSupportedException ignored) {
                ValhallaMMO.getPlugin().getLogger().warning("[ValhallaMMO] Could not clone potion effect wrapper in PotionInvertEffectsModifier");
                return null;
            }

            PotionAttributesManager.getInstance().setDefaultPotionEffects(outputItem, bDefaultWrappers);
        }

        boolean changedCurrents = false;
        for (PotionEffectWrapper w : currentWrappers){
            InvertedEffect invert = invertedEffects.get(w.getPotionEffect());
            if (invert == null) continue;
            bCurrentWrappers.removeIf(potionEffectWrapper -> potionEffectWrapper.getPotionEffect().equals(w.getPotionEffect()));
            try {
                PotionEffectWrapper wrapper = w.clone();
                wrapper.setPotionEffect(invert.getInvertedEffect());
//                wrapper.setDuration(invert.getDuration());
//                wrapper.setAmplifier(invert.getAmplifier());
//
//                if (invert.getColor() != null){
//                    PotionMeta meta = (PotionMeta) outputItem.getItemMeta();
//                    if (meta == null) return null;
//
//                    java.awt.Color c = Utils.hexToRgb(invert.getColor());
//                    meta.setColor(Color.fromRGB(c.getRed(), c.getBlue(), c.getGreen()));
//                    outputItem.setItemMeta(meta);
//                }

                bCurrentWrappers.add(wrapper);
                changedCurrents = true;
            } catch (CloneNotSupportedException ignored) {
                ValhallaMMO.getPlugin().getLogger().warning("[ValhallaMMO] Could not clone potion effect wrapper in PotionInvertEffectsModifier");
                return null;
            }

            PotionAttributesManager.getInstance().setStats(outputItem, bCurrentWrappers);
        }

        if (!(changedCurrents || changedDefaults)) return null;

        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Inverts a potion's potion effects.");
    }

    private class InvertedEffect{
        private final String invertedEffect;
        private String color = null;
        private final int duration;
        private final double amplifier;

        public InvertedEffect(String invertedEffect, String color, int duration, double amplifier){
            this.invertedEffect = invertedEffect;
            this.color = color;
            this.duration = duration;
            this.amplifier = amplifier;
        }

        public InvertedEffect(String invertedEffect, int duration, double amplifier){
            this.invertedEffect = invertedEffect;
            this.duration = duration;
            this.amplifier = amplifier;
        }

        public double getAmplifier() {
            return amplifier;
        }

        public int getDuration() {
            return duration;
        }

        public String getColor() {
            return color;
        }

        public String getInvertedEffect() {
            return invertedEffect;
        }
    }
}
