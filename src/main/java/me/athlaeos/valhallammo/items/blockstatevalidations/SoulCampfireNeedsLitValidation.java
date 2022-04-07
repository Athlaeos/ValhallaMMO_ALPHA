package me.athlaeos.valhallammo.items.blockstatevalidations;

import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Lightable;

public class SoulCampfireNeedsLitValidation extends CraftValidation {

    public SoulCampfireNeedsLitValidation(){
        this.block = Material.SOUL_CAMPFIRE;
        this.icon = Material.SOUL_CAMPFIRE;
        this.compatibleMaterials = convertStringsToMaterials("SOUL_CAMPFIRE");
        this.name = "requirement_soul_campfire_lit";
        this.displayName = Utils.chat("&9Campfire needs to be on");
        this.description = Utils.chat("&7When crafting with a campfire, it needs to be on " +
                "to be used.");
    }

    @Override
    public boolean check(Block block) {
        if (block.getType() == Material.SOUL_CAMPFIRE){
            if (block.getState() instanceof Lightable){
                Lightable campfire = (Lightable) block.getState();
                return campfire.isLit();
            }
        }
        return true;
    }

    @Override
    public void executeAfter(Block block) {
    }
}
