package me.athlaeos.valhallammo.items;

import me.athlaeos.valhallammo.items.blockstatevalidations.*;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CraftValidationManager {
    private static CraftValidationManager manager = null;
    private final Map<Material, Map<String, CraftValidation>> materialCraftValidations = new HashMap<>();
    private final Map<String, CraftValidation> defaultCraftValidations = new HashMap<>();

    public CraftValidationManager(){
        defaultCraftValidations.put("no_validation", new DefaultValidation());

        register(new CauldronConsumesWaterValidation());
        register(new CauldronNeedsWaterValidation());
        register(new CauldronFillsWaterValidation());

        register(new CampfireNeedsLitValidation());
        register(new CampfireNeedsUnlitValidation());

        register(new SoulCampfireNeedsLitValidation());
        register(new SoulCampfireNeedsUnlitValidation());

        register(new BlockNeedsWaterloggedValidation());
        register(new BlockNeedsNotWaterloggedValidation());
    }

    /**
     * Checks if the block state matches the given validation's conditions
     * @param b the block to check its state
     * @param validation the validation to use
     * @return true if validation checks out or if validation is null. False if validation does not check out.
     */
    public boolean blockConditionsApply(Block b, CraftValidation validation){
        if (validation == null) return true;
        if (materialCraftValidations.containsKey(b.getType())){
            if (materialCraftValidations.get(b.getType()).containsKey(validation.getName())){
                return materialCraftValidations.get(b.getType()).get(validation.getName()).check(b);
            }
        } else if (defaultCraftValidations.containsKey(validation.getName())){
            return defaultCraftValidations.get(validation.getName()).check(b);
        }
        return true;
    }

    public CraftValidation getValidation(Material craftStation, String name){
        if (craftStation != null){
            if (materialCraftValidations.containsKey(craftStation)){
                return materialCraftValidations.get(craftStation).get(name);
            } else {
                return null;
            }
        } else {
            return defaultCraftValidations.get(name);
        }
    }

    public CraftValidation getDefaultValidation(){
        return defaultCraftValidations.get("no_validation");
    }

    public List<CraftValidation> getValidations(Material m){
        List<CraftValidation> validations = new ArrayList<>();
        if (materialCraftValidations.containsKey(m)) validations.addAll(materialCraftValidations.get(m).values());
        validations.addAll(defaultCraftValidations.values());
        return validations;
    }

    /**
     * Registers a validation to be available. If the validation's block type is null, it is added as a default validation
     * instead. The name of the validation should be unique.
     * @param validation the validation to reset.
     */
    public void register(CraftValidation validation){
        if (validation.getBlock() == null){
            defaultCraftValidations.put(validation.getName(), validation);
        }
        Map<String, CraftValidation> validations = new HashMap<>();
        if (materialCraftValidations.containsKey(validation.getBlock())){
            validations = materialCraftValidations.get(validation.getBlock());
        }
        validations.put(validation.getName(), validation);

        materialCraftValidations.put(validation.getBlock(), validations);
    }

    public static CraftValidationManager getInstance(){
        if (manager == null) manager = new CraftValidationManager();
        return manager;
    }
}
