package me.athlaeos.valhallammo.items.blockstatevalidations;

import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Lightable;

public class CampfireNeedsUnlitValidation extends CraftValidation {

    public CampfireNeedsUnlitValidation(){
        this.block = Material.CAMPFIRE;
        this.icon = Material.CAMPFIRE;
        this.compatibleMaterials = convertStringsToMaterials("CAMPFIRE");
        this.name = "requirement_campfire_unlit";
        this.displayName = Utils.chat("&9Campfire needs to be off");
        this.description = Utils.chat("&7When crafting with a campfire, it needs to be off " +
                "to be used.");
    }

    @Override
    public boolean check(Block block) {
        if (block.getType() == Material.CAMPFIRE){
            if (block.getState() instanceof Lightable){
                Lightable campfire = (Lightable) block.getState();
                return !campfire.isLit();
            }
        }
        return true;
    }

    @Override
    public void executeAfter(Block block) {
    }
}
