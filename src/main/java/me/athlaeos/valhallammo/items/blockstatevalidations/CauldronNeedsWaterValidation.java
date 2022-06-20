package me.athlaeos.valhallammo.items.blockstatevalidations;

import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;

public class CauldronNeedsWaterValidation extends CraftValidation {

    public CauldronNeedsWaterValidation(){
        this.block = Material.CAULDRON;
        this.icon = Material.WATER_BUCKET;
        this.compatibleMaterials = convertStringsToMaterials("WATER_CAULDRON", "CAULDRON"/*, "LAVA_CAULDRON", "POWDERED_SNOW_CAULDRON"*/);
        this.name = "requirement_cauldron_water";
        this.displayName = Utils.chat("&9Cauldron needs water");
        this.description = Utils.chat("&7When crafting with a cauldron, it requires at least 1 level of water " +
                "to be used. Its water level is not reduced after usage");
    }

    @Override
    public boolean isCompatible(Material m) {
        return compatibleMaterials.contains(m);
    }

    @Override
    public boolean check(Block block) {
        if (block.getBlockData() instanceof Levelled){
            Levelled cauldron = (Levelled) block.getBlockData();
            return cauldron.getLevel() > 0;
        }
        return false;
    }

    @Override
    public void executeAfter(Block block) {
    }
}
