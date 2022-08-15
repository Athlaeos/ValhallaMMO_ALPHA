package me.athlaeos.valhallammo.items.blockstatevalidations;

import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;

public class CauldronFillsWaterValidation extends CraftValidation {

    public CauldronFillsWaterValidation(){
        this.block = Material.CAULDRON;
        this.icon = Material.WATER_BUCKET;
        this.compatibleMaterials = convertStringsToMaterials("WATER_CAULDRON", "CAULDRON");
        this.name = "result_cauldron_water_gain";
        this.displayName = Utils.chat("&9Cauldron fills with water");
        this.description = Utils.chat("&7When crafting on a cauldron, its water level is increased after usage");
    }

    @Override
    public boolean isCompatible(Material m) {
        return compatibleMaterials.contains(m);
    }

    @Override
    public boolean check(Block block) {
        return true;
    }

    @Override
    public void executeAfter(Block block) {
        if (!this.compatibleMaterials.contains(block.getType())) return;
        if (block.getBlockData() instanceof Levelled){
            Levelled cauldron = (Levelled) block.getBlockData();
            if (cauldron.getLevel() >= cauldron.getMaximumLevel()){
                return;
            }
            cauldron.setLevel(cauldron.getLevel() + 1);
            block.setBlockData(cauldron);
        }
    }
}
