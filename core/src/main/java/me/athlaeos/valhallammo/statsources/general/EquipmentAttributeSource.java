package me.athlaeos.valhallammo.statsources.general;

import me.athlaeos.valhallammo.dom.EntityProperties;
import me.athlaeos.valhallammo.items.attributewrappers.AttributeWrapper;
import me.athlaeos.valhallammo.managers.EntityEquipmentCacheManager;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Map;

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
        if (p instanceof LivingEntity){
            EntityProperties equipment = EntityEquipmentCacheManager.getInstance().getAndCacheEquipment((LivingEntity) p);
            combinedStrength += getValue(equipment.getHelmetAttributes());
            combinedStrength += getValue(equipment.getChestplateAttributes());
            combinedStrength += getValue(equipment.getLeggingsAttributes());
            combinedStrength += getValue(equipment.getBootsAttributes());
//        for (ItemStack i : equipment.getIterable(false)){
//            if (i.getItemMeta() == null) continue;
//            AttributeWrapper attributeWrapper = ItemAttributesManager.getInstance().getAnyAttributeWrapper(i, wrapper);
//            if (attributeWrapper != null){
//                combinedStrength += attributeWrapper.getAmount();
//            }
//        }

            combinedStrength += getValue(equipment.getMainHandAttributes());
            if (!ignoreOffHand){
                combinedStrength += getValue(equipment.getOffHandAttributes());
//            ItemStack i = equipment.getMainHand();
//            if (!Utils.isItemEmptyOrNull(i)){
//                if (i.getItemMeta() != null){
//                    if (EquipmentClass.getClass(i) != EquipmentClass.TRINKET && !EquipmentClass.isArmor(i)) {
//                        AttributeWrapper attributeWrapper = ItemAttributesManager.getInstance().getAnyAttributeWrapper(i, wrapper);
//                        if (attributeWrapper != null){
//                            combinedStrength += attributeWrapper.getAmount();
//                        }
//                    }
//                }
//            }
            } //else {
//            for (ItemStack i : equipment.getHands()){
//                if (i.getItemMeta() == null) continue;
//                if (EquipmentClass.getClass(i) == EquipmentClass.TRINKET || EquipmentClass.isArmor(i)) continue;
//                AttributeWrapper attributeWrapper = ItemAttributesManager.getInstance().getAnyAttributeWrapper(i, wrapper);
//                if (attributeWrapper != null){
//                    combinedStrength += attributeWrapper.getAmount();
//                }
//            }
//        }
        }
        return combinedStrength;
    }

    private double getValue(Map<String, AttributeWrapper> wrappers){
        AttributeWrapper attributeWrapper = wrappers.get(wrapper);
        if (attributeWrapper != null) return attributeWrapper.getAmount();
        return 0;
    }
}
