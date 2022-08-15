package me.athlaeos.valhallammo.items;

import me.athlaeos.valhallammo.items.blockstatevalidations.*;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockCraftStateValidationManager {
    private static BlockCraftStateValidationManager manager = null;
    private final Map<String, CraftValidation> allCraftValidations = new HashMap<>();

    public BlockCraftStateValidationManager(){
        allCraftValidations.put("no_validation", new DefaultValidation());

        register(new CauldronConsumesWaterValidation());
        register(new CauldronNeedsWaterValidation());
        register(new CauldronFillsWaterValidation());

        register(new CampfireNeedsLitValidation());
        register(new CampfireNeedsUnlitValidation());

        register(new SoulCampfireNeedsLitValidation());
        register(new SoulCampfireNeedsUnlitValidation());

        register(new BlockNeedsWaterloggedValidation()); // generic
        register(new BlockNeedsNotWaterloggedValidation()); // generic
    }

    /**
     * Checks if the block state matches the given validation's conditions
     * @param b the block to check its state
     * @param validation the validation to use
     * @return true if validation checks out or if validation is null. False if validation does not pass.
     */
    public boolean blockConditionsApply(Block b, CraftValidation validation){
        if (validation == null) {
            return true;
        }
        if (validation.isCompatible(b.getType())){
            return validation.check(b);
        }
//        if (materialCraftValidations.containsKey(b.getType())){
//            if (materialCraftValidations.get(b.getType()).containsKey(validation.getName())){
//                return materialCraftValidations.get(b.getType()).get(validation.getName()).check(b);
//            }
//        } else if (genericCraftValidations.containsKey(validation.getName())){
//            return genericCraftValidations.get(validation.getName()).check(b);
//        }
        return true;
    }

    public CraftValidation getValidation(Material craftStation, String name){
        if (name == null) {
            return allCraftValidations.get("no_validation");
        }
        CraftValidation validation = allCraftValidations.get(name);
        if (validation == null) {
            return allCraftValidations.get("no_validation");
        }
        if (validation.isCompatible(craftStation)) {
            return validation;
        }
        return null;
    }

    public CraftValidation getDefaultValidation(){
        return allCraftValidations.get("no_validation");
    }

    public List<CraftValidation> getValidations(Material m){
        List<CraftValidation> validations = new ArrayList<>();
        for (CraftValidation validation : allCraftValidations.values()){
            if (validation.isCompatible(m)){
                validations.add(validation);
            }
        }
        validations.add(getDefaultValidation());
        return validations;
    }

    /**
     * Registers a validation to be available. If the validation's block type is null, it is added as a default validation
     * instead. The name of the validation should be unique.
     * @param validation the validation to reset.
     */
    public void register(CraftValidation validation){
        allCraftValidations.put(validation.getName(), validation);
    }

    public static BlockCraftStateValidationManager getInstance(){
        if (manager == null) manager = new BlockCraftStateValidationManager();
        return manager;
    }
}
