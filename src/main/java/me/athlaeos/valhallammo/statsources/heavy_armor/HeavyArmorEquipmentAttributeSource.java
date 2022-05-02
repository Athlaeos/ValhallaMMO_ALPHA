package me.athlaeos.valhallammo.statsources.heavy_armor;

import me.athlaeos.valhallammo.dom.ArmorType;
import me.athlaeos.valhallammo.items.attributewrappers.AttributeWrapper;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import me.athlaeos.valhallammo.utility.EntityUtils;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class HeavyArmorEquipmentAttributeSource extends AccumulativeStatSource {
    private final String wrapper;

    /**
     * Collects the combined strength of the given attribute off a player's equipment, including hands
     * @param attributeWrapper the attribute to get the combined stats from
     */
    public HeavyArmorEquipmentAttributeSource(String attributeWrapper){
        this.wrapper = attributeWrapper;
    }

    @Override
    public double add(Entity p, boolean use) {
        double combinedStrength = 0D;
        for (ItemStack i : EntityUtils.getEntityEquipment(p).getIterable(false)){
            if (i.getItemMeta() == null) continue;
            if (ArmorType.getArmorType(i) == ArmorType.HEAVY){
                AttributeWrapper attributeWrapper = ItemAttributesManager.getInstance().getAnyAttributeWrapper(i, wrapper);
                if (attributeWrapper != null) combinedStrength += attributeWrapper.getAmount();
            }
        }
        return combinedStrength;
    }
}
