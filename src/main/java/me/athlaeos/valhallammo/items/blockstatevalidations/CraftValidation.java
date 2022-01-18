package me.athlaeos.valhallammo.materials.blockstatevalidations;

import org.bukkit.Material;
import org.bukkit.block.Block;

public abstract class CraftValidation {
    protected Material block;
    protected String displayName;
    protected String description;
    protected String name;

    public abstract boolean check(Block block);
    public abstract void executeAfter(Block block);

    public String getDescription() {
        return description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getBlock() {
        return block;
    }

    public String getName() {
        return name;
    }
}
