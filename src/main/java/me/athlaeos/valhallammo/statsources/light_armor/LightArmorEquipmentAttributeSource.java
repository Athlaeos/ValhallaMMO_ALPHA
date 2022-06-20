package me.athlaeos.valhallammo.statsources.light_armor;

import me.athlaeos.valhallammo.dom.ArmorType;
import me.athlaeos.valhallammo.items.OverleveledEquipmentTool;
import me.athlaeos.valhallammo.items.attributewrappers.AttributeWrapper;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import me.athlaeos.valhallammo.utility.EntityUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LightArmorEquipmentAttributeSource extends AccumulativeStatSource {
    private final String wrapper;

    /**
     * Collects the combined strength of the given attribute off a player's equipment, including hands
     * @param attributeWrapper the attribute to get the combined stats from
     */
    public LightArmorEquipmentAttributeSource(String attributeWrapper){
        this.wrapper = attributeWrapper;
    }

    @Override
    public double add(Entity p, boolean use) {
        double combinedStrength = 0D;
        for (ItemStack i : EntityUtils.getEntityEquipment(p).getIterable(false)){
            if (i.getItemMeta() == null) continue;
            if (ArmorType.getArmorType(i) == ArmorType.LIGHT){
                AttributeWrapper attributeWrapper = ItemAttributesManager.getInstance().getAnyAttributeWrapper(i, wrapper);
                if (attributeWrapper != null) {
                    double value = attributeWrapper.getAmount();
                    if (p instanceof Player){
                        double penalty = OverleveledEquipmentTool.getTool().getPenalty((Player) p, i, "armor");
                        value *= (1 + penalty);
                    }
                    combinedStrength += value;
                }
            }
        }
        return combinedStrength;
    }
}
