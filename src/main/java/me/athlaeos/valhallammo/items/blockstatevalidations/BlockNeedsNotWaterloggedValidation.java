package me.athlaeos.valhallammo.materials.blockstatevalidations;

import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;

public class BlockNeedsNotWaterloggedValidation extends CraftValidation{

    public BlockNeedsNotWaterloggedValidation(){
        this.block = null;
        this.name = "requirement_not_waterlogged";
        this.displayName = Utils.chat("&9Block can't be waterlogged");
        this.description = Utils.chat("&7When crafting, the block needs to not be waterlogged to be used");
    }

    @Override
    public boolean check(Block block) {
        if (block.getBlockData() instanceof Waterlogged){
            Waterlogged logged = (Waterlogged) block.getBlockData();
            return !logged.isWaterlogged();
        }
        return true;
    }

    @Override
    public void executeAfter(Block block) {
    }
}
