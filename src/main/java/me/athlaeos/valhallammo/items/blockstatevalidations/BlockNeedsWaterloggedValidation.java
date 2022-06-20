package me.athlaeos.valhallammo.items.blockstatevalidations;

import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;

public class BlockNeedsWaterloggedValidation extends CraftValidation{

    public BlockNeedsWaterloggedValidation(){
        this.block = null;
        this.icon = Material.WATER_BUCKET;
        this.name = "requirement_waterlogged";
        this.displayName = Utils.chat("&9Block requires waterlogging");
        this.description = Utils.chat("&7When crafting, the block needs to be waterlogged to be used");
    }

    @Override
    public boolean isCompatible(Material m) {
        return m.createBlockData() instanceof Waterlogged;
    }

    @Override
    public boolean check(Block block) {
        if (block.getBlockData() instanceof Waterlogged){
            Waterlogged logged = (Waterlogged) block.getBlockData();
            return logged.isWaterlogged();
        }
        return true;
    }

    @Override
    public void executeAfter(Block block) {
    }
}
