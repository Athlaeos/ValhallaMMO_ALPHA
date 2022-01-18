package me.athlaeos.valhallammo.materials.blockstatevalidations;

import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;

public class CauldronFillsWaterValidation extends CraftValidation {

    public CauldronFillsWaterValidation(){
        this.block = Material.CAULDRON;
        this.name = "result_cauldron_water_gain";
        this.displayName = Utils.chat("&9Cauldron fills with water");
        this.description = Utils.chat("&7When crafting with a cauldron, its water level is increased after usage");
    }

    @Override
    public boolean check(Block block) {
        return true;
    }

    @Override
    public void executeAfter(Block block) {
        if (block.getType() == Material.CAULDRON){
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
}
