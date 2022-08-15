package me.athlaeos.valhallammo.statsources.general;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.items.attributewrappers.AttributeWrapper;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import me.athlaeos.valhallatrinkets.TrinketsManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TrinketAttributeSource extends AccumulativeStatSource {
    private final String wrapper;

    /**
     * Collects the combined strength of the given attribute off a player's equipment, including hands
     * @param attributeWrapper the attribute to get the combined stats from
     */
    public TrinketAttributeSource(String attributeWrapper){
        this.wrapper = attributeWrapper;
    }

    @Override
    public double add(Entity p, boolean use) {
        if (!ValhallaMMO.isTrinketsHooked()) return 0;
        double combinedStrength = 0D;
        if (p instanceof Player){
            for (ItemStack i : TrinketsManager.getInstance().getTrinketInventory((Player) p).values()){
                if (i.getItemMeta() == null) continue;
                AttributeWrapper attributeWrapper = ItemAttributesManager.getInstance().getAnyAttributeWrapper(i, wrapper);
                if (attributeWrapper != null) {
                    combinedStrength += attributeWrapper.getAmount();
                }
            }
        }
        return combinedStrength;
    }
}
