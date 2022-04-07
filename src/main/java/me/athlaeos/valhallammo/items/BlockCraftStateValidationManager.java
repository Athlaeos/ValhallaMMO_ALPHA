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
    private final Map<Material, Map<String, CraftValidation>> materialCraftValidations = new HashMap<>();
    private final Map<String, CraftValidation> genericCraftValidations = new HashMap<>();
    private final Map<String, CraftValidation> allCraftValidations = new HashMap<>();

    public BlockCraftStateValidationManager(){
        genericCraftValidations.put("no_validation", new DefaultValidation());

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
        if (validation == null) return true;
        if (validation.getCompatibleMaterials().contains(b.getType())){
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
        if (craftStation != null){
            if (materialCraftValidations.containsKey(craftStation)){
                return materialCraftValidations.get(craftStation).get(name);
            } else {
                return null;
            }
        } else {
            return genericCraftValidations.get(name);
        }
    }

    public CraftValidation getDefaultValidation(){
        return genericCraftValidations.get("no_validation");
    }

    /**
     *
     * @param m
     * @return
     */
    public List<CraftValidation> getValidations(Material m){
        List<CraftValidation> validations = new ArrayList<>();
//        if (materialCraftValidations.containsKey(m)){
//            validations.addAll(materialCraftValidations.get(m).values());
//        }
        for (CraftValidation validation : allCraftValidations.values()){
            if (!genericCraftValidations.containsValue(validation)){ // checks all but generic craft validations
                if (validation.getCompatibleMaterials().contains(m)){
                    validations.add(validation);
                }
            }
        }
        validations.addAll(genericCraftValidations.values());
        return validations;
    }

    /**
     * Registers a validation to be available. If the validation's block type is null, it is added as a default validation
     * instead. The name of the validation should be unique.
     * @param validation the validation to reset.
     */
    public void register(CraftValidation validation){
        allCraftValidations.put(validation.getName(), validation);
        if (validation.getBlock() == null){
            genericCraftValidations.put(validation.getName(), validation);
        }
        Map<String, CraftValidation> validations = new HashMap<>();
        if (materialCraftValidations.containsKey(validation.getBlock())){
            validations = materialCraftValidations.get(validation.getBlock());
        }
        validations.put(validation.getName(), validation);

        materialCraftValidations.put(validation.getBlock(), validations);
    }

    public static BlockCraftStateValidationManager getInstance(){
        if (manager == null) manager = new BlockCraftStateValidationManager();
        return manager;
    }
}
