package me.athlaeos.valhallammo.items.blockstatevalidations;

import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class DefaultValidation extends CraftValidation {

    public DefaultValidation(){
        this.block = Material.BARRIER;
        this.icon = Material.BARRIER;
        this.name = "no_validation";
        this.displayName = Utils.chat("&7No validation");
        this.description = Utils.chat("&7No specific block state required");
    }

    @Override
    public boolean isCompatible(Material m) {
        return true;
    }

    @Override
    public boolean check(Block block) {
        return true;
    }

    @Override
    public void executeAfter(Block block) {
    }
}
