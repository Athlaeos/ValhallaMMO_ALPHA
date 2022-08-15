package me.athlaeos.valhallammo.items.blockstatevalidations;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Collection;
import java.util.HashSet;

public abstract class CraftValidation {
    protected Material block;
    protected Material icon;
    protected String displayName;
    protected String description;
    protected String name;
    protected Collection<Material> compatibleMaterials = new HashSet<>();

    public abstract boolean isCompatible(Material m);

    public abstract boolean check(Block block);
    public abstract void executeAfter(Block block);

    /**
     * Description of what the validation does, displayed in its icon's lore in the recipe creation menus
     * @return the description of the validation
     */
    public String getDescription() {
        return description;
    }

    /**
     * The display name of the validation's icon in the recipe creation menus
     * @return the display name of the validation
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * All compatible block types for this validation, if a validation should work with both normal cauldrons and water-
     * filled cauldrons both should be in this collection, with a base cauldron as the block (getBlock())
     * @return a collection of all materials the validation should work with
     */
    public Collection<Material> getCompatibleMaterials() {
        return compatibleMaterials;
    }

    /**
     * The base crafting station block type the validation is intended for, can be null if validation is generic
     * @return the block type the validation is intended for, or null if generic
     */
    public Material getBlock() {
        return block;
    }

    /**
     * The system name used in identifying the validation
     * @return the system name
     */
    public String getName() {
        return name;
    }

    /**
     * Icon to be displayed in the recipe creation menus
     * @return the icon
     */
    public Material getIcon() {
        return icon;
    }

    public Collection<Material> convertStringsToMaterials(String... materials){
        Collection<Material> allMaterials = new HashSet<>();
        for (String s : materials){
            try {
                allMaterials.add(Material.valueOf(s));
            } catch (IllegalArgumentException ignored){
            }
        }
        return allMaterials;
    }
}
