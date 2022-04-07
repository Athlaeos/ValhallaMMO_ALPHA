package me.athlaeos.valhallammo.skills.smithing;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.items.MaterialClass;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.skills.Skill;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class SmithingSkill extends Skill {
    private final Map<MaterialClass, Double> baseExperienceValues;
    private final Map<EquipmentClass, Double> experienceMultipliers;

    public SmithingSkill(String type) {
        super(type);

        baseExperienceValues = new HashMap<>();
        experienceMultipliers = new HashMap<>();

        YamlConfiguration smithingConfig = ConfigManager.getInstance().getConfig("skill_smithing.yml").get();
        YamlConfiguration smithingProgressionConfig = ConfigManager.getInstance().getConfig("progression_smithing.yml").get();

        this.loadCommonConfig(smithingConfig, smithingProgressionConfig);

        ConfigurationSection materialBaseEXPSection = smithingProgressionConfig.getConfigurationSection("experience.exp_gain.material_base");
        if (materialBaseEXPSection != null){
            for (String material : materialBaseEXPSection.getKeys(false)){
                try {
                    MaterialClass materialClass = MaterialClass.valueOf(material);
                    double materialBase = smithingProgressionConfig.getDouble("experience.exp_gain.material_base." + material);
                    baseExperienceValues.put(materialClass, materialBase);
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("[ValhallaMMO] Invalid material class given at experience.exp_gain.material_base." + material + ". Skipped this section, review the documentation or ask in my discord what the available options are");
                }
            }
        }

        ConfigurationSection toolEXPMultiplierSection = smithingProgressionConfig.getConfigurationSection("experience.exp_gain.type_multiplier");
        if (toolEXPMultiplierSection != null){
            for (String toolType : toolEXPMultiplierSection.getKeys(false)){
                try {
                    EquipmentClass toolClass = EquipmentClass.valueOf(toolType);
                    double typeMult = smithingProgressionConfig.getDouble("experience.exp_gain.type_multiplier." + toolType);
                    experienceMultipliers.put(toolClass, typeMult);
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("[ValhallaMMO] Invalid equipment class given at experience.exp_gain.type_multiplier." + toolType + ". Skipped this section, review the documentation or ask in my discord what the available options are");
                }
            }
        }
    }

    @Override
    public NamespacedKey getKey() {
        return new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_profile_smithing");
    }

    @Override
    public Profile getCleanProfile() {
        return new SmithingProfile(null);
    }

    public Map<MaterialClass, Double> getBaseExperienceValues() {
        return baseExperienceValues;
    }

    public Map<EquipmentClass, Double> getExperienceMultipliers() {
        return experienceMultipliers;
    }

    /**
     * Calculates how much EXP a player should earn from crafting an item, first it calculates the base value of
     * the item crafted, then it applies whatever EXP multiplier stats the player has.
     * Returns the total amount of exp a player should earn from crafting this item
     * @param p the crafter
     * @param item the item crafted
     * @return the amount of EXP the crafter should earn
     */
    public double expForCraftedItem(Player p, ItemStack item) {
        MaterialClass materialClass = MaterialClass.getMatchingClass(item.getType());
        EquipmentClass equipmentClass = EquipmentClass.getClass(item.getType());
        if (materialClass != null && equipmentClass != null){
            double materialClassBase = 0;
            if (getBaseExperienceValues().get(materialClass) != null) materialClassBase = getBaseExperienceValues().get(materialClass);
            double typeMultiplier = 0;
            if (getExperienceMultipliers().get(equipmentClass) != null) typeMultiplier = getExperienceMultipliers().get(equipmentClass);
            double baseEXP = materialClassBase * typeMultiplier;

            double expMultiplier = (AccumulativeStatManager.getInstance().getStats("SMITHING_EXP_GAIN_" + materialClass, p, true)) / 100D;
            double finalEXP = baseEXP * expMultiplier * (AccumulativeStatManager.getInstance().getStats("SMITHING_EXP_GAIN_GENERAL", p, true) / 100D);
            if (finalEXP < 0) finalEXP = 0;

            return finalEXP;
        }
        return 0;
    }
}
