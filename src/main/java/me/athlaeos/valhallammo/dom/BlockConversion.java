package me.athlaeos.valhallammo.dom;

import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.Map;

public class BlockConversion {
    private final Material from;
    private final Material to;
    private final String name;
    private final Map<Material, BlockConversionData> compatibleItems;
    private final Sound sound;

    public BlockConversion(String name, Map<Material, BlockConversionData> compatibleItems, Material from, Material to, Sound sound){
        this.name = name;
        this.from = from;
        this.to = to;
        this.compatibleItems = compatibleItems;
        this.sound = sound;
    }

    public String getName() {
        return name;
    }

    public Material getFrom() {
        return from;
    }

    public Material getTo() {
        return to;
    }

    public Map<Material, BlockConversionData> getCompatibleItems() {
        return compatibleItems;
    }

    public Sound getSound() {
        return sound;
    }

    public static class BlockConversionData{
        private final int customModelData;
        private final boolean isConsumed;

        public BlockConversionData(int customModelData, boolean isConsumed){
            this.customModelData = customModelData;
            this.isConsumed = isConsumed;
        }

        public int getCustomModelData() {
            return customModelData;
        }

        public boolean isConsumed() {
            return isConsumed;
        }
    }
}
