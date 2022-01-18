package me.athlaeos.valhallammo.statsources.farming;

import me.athlaeos.valhallammo.configutils.ConfigManager;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class FarmingLOTSFishingRewardTierSource extends AccumulativeStatSource {
    private double fishingLOTSPotency;
    private double fishing_luck_potency;
    public FarmingLOTSFishingRewardTierSource(){
        YamlConfiguration farmingConfig = ConfigManager.getInstance().getConfig("skill_farming.yml").get();
        fishingLOTSPotency = farmingConfig.getDouble("fishing_luck_of_the_sea_potency");
        fishing_luck_potency = farmingConfig.getDouble("fishing_luck_potency");
    }

    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof LivingEntity){
            LivingEntity e = (LivingEntity) p;
            if (e.getEquipment() != null) {
                ItemStack rod;
                if (!Utils.isItemEmptyOrNull(e.getEquipment().getItemInMainHand())){
                    rod = e.getEquipment().getItemInMainHand();
                } else {
                    rod = e.getEquipment().getItemInOffHand();
                }
                if (!Utils.isItemEmptyOrNull(rod)){
                    int level = rod.getEnchantmentLevel(Enchantment.LUCK);
                    return level * fishingLOTSPotency;
                }
            }
        }
        return 0;
    }
}
