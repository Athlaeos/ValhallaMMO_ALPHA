package me.athlaeos.valhallammo.statsources.general;

import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import me.athlaeos.valhallammo.utility.EntityUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EquipmentFireProtectionEnchantmentFireResistanceSource extends AccumulativeStatSource {
    private final double resistancePerPiece;

    public EquipmentFireProtectionEnchantmentFireResistanceSource(){
        resistancePerPiece = ConfigManager.getInstance().getConfig("config.yml").get().getDouble("fire_protection_effectiveness");
    }

    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof Player){
            int totalLevelCount = 0;
            for (ItemStack i : EntityUtils.getEntityEquipment(p).getIterable(false)){
                totalLevelCount += i.getEnchantmentLevel(Enchantment.PROTECTION_FIRE);
            }
            return totalLevelCount * resistancePerPiece;
        }
        return 0;
    }
}
