package me.athlaeos.valhallammo.statsources.general;

import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.items.attributewrappers.AttributeWrapper;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import me.athlaeos.valhallammo.utility.EntityUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class EquipmentAttributeSource extends AccumulativeStatSource {
    private final String wrapper;
    private boolean ignoreOffHand = false;

    /**
     * Collects the combined strength of the given attribute off a player's equipment, including hands
     * @param attributeWrapper the attribute to get the combined stats from
     */
    public EquipmentAttributeSource(String attributeWrapper){
        this.wrapper = attributeWrapper;
    }

    public EquipmentAttributeSource(String attributeWrapper, boolean ignoreOffHand){
        this.wrapper = attributeWrapper;
        this.ignoreOffHand = ignoreOffHand;
    }

    @Override
    public double add(Entity p, boolean use) {
        double combinedStrength = 0D;
        EntityUtils.EntityEquipment equipment = EntityUtils.getEntityEquipment(p);
        for (ItemStack i : equipment.getIterable(false)){
            if (i.getItemMeta() == null) continue;
            AttributeWrapper attributeWrapper = ItemAttributesManager.getInstance().getAnyAttributeWrapper(i, wrapper);
            if (attributeWrapper != null){
                combinedStrength += attributeWrapper.getAmount();
            }
        }
        if (ignoreOffHand){
            ItemStack i = equipment.getMainHand();
            if (!Utils.isItemEmptyOrNull(i)){
                if (i.getItemMeta() != null){
                    if (EquipmentClass.getClass(i) != EquipmentClass.TRINKET && !EquipmentClass.isArmor(i)) {
                        AttributeWrapper attributeWrapper = ItemAttributesManager.getInstance().getAnyAttributeWrapper(i, wrapper);
                        if (attributeWrapper != null){
                            combinedStrength += attributeWrapper.getAmount();
                        }
                    }
                }
            }
        } else {
            for (ItemStack i : equipment.getHands()){
                if (i.getItemMeta() == null) continue;
                if (EquipmentClass.getClass(i) == EquipmentClass.TRINKET || EquipmentClass.isArmor(i)) continue;
                AttributeWrapper attributeWrapper = ItemAttributesManager.getInstance().getAnyAttributeWrapper(i, wrapper);
                if (attributeWrapper != null){
                    combinedStrength += attributeWrapper.getAmount();
                }
            }
        }
        return combinedStrength;
    }
}
