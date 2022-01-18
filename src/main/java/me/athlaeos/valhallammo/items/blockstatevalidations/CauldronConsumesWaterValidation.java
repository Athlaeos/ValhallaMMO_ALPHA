package me.athlaeos.valhallammo.materials.blockstatevalidations;

import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;

public class CauldronConsumesWaterValidation extends CraftValidation {

    public CauldronConsumesWaterValidation(){
        this.block = Material.CAULDRON;
        this.name = "requirement_cauldron_water_consume";
        this.displayName = Utils.chat("&9Cauldron consumes water");
        this.description = Utils.chat("&7When crafting with a cauldron, it requires at least 1 level of water " +
                "to be used. After crafting, its water level is reduced by 1");
    }

    @Override
    public boolean check(Block block) {
        if (block.getType() == Material.CAULDRON){
            if (block.getBlockData() instanceof Levelled){
                Levelled cauldron = (Levelled) block.getBlockData();
                return cauldron.getLevel() > 0;
            }
        }
        return true;
    }

    @Override
    public void executeAfter(Block block) {
        if (block.getType() == Material.CAULDRON){
            if (block.getBlockData() instanceof Levelled){
                Levelled cauldron = (Levelled) block.getBlockData();
                if (cauldron.getLevel() > 0){
                    cauldron.setLevel(cauldron.getLevel() - 1);
                }
                block.setBlockData(cauldron);
            }
        }
    }
}
