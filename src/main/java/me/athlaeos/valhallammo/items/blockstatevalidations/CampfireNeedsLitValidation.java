package me.athlaeos.valhallammo.items.blockstatevalidations;

import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Lightable;

public class CampfireNeedsLitValidation extends CraftValidation {

    public CampfireNeedsLitValidation(){
        this.block = Material.CAMPFIRE;
        this.icon = Material.CAMPFIRE;
        this.name = "requirement_campfire_lit";
        this.compatibleMaterials = convertStringsToMaterials("CAMPFIRE");
        this.displayName = Utils.chat("&9Campfire needs to be on");
        this.description = Utils.chat("&7When crafting with a campfire, it needs to be on " +
                "to be used.");
    }

    @Override
    public boolean isCompatible(Material m) {
        return compatibleMaterials.contains(m);
    }

    @Override
    public boolean check(Block block) {
        if (block.getBlockData() instanceof Lightable){
            Lightable campfire = (Lightable) block.getBlockData();
            return campfire.isLit();
        }
        return true;
    }

    @Override
    public void executeAfter(Block block) {
    }
}
