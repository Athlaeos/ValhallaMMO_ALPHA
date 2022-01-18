package me.athlaeos.valhallammo.statsources.general;

import me.athlaeos.valhallammo.items.enchantmentwrappers.EnchantmentWrapper;
import me.athlaeos.valhallammo.managers.CustomEnchantmentManager;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import me.athlaeos.valhallammo.utility.EntityUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class EnchantmentDamageTakenSource extends AccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof LivingEntity){
            double totalQuality = 0D;
            Collection<ItemStack> equipment = EntityUtils.getEntityEquipment(p, true);
            for (ItemStack i : equipment){
                EnchantmentWrapper enchantment = CustomEnchantmentManager.getInstance().getCustomEnchant(i, "DAMAGE_TAKEN");
                if (enchantment != null){
                    totalQuality += enchantment.getAmplifier();
                }
            }
            return totalQuality;
        }

        return 0;
    }
}
