package me.athlaeos.valhallammo.statsources.heavy_armor;

import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.EntityProperties;
import me.athlaeos.valhallammo.managers.EntityEquipmentCacheManager;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class HeavyArmorDefaultEquipmentPoisonResistanceSource extends AccumulativeStatSource {
    private final double resistancePerPiece;

    public HeavyArmorDefaultEquipmentPoisonResistanceSource(){
        resistancePerPiece = ConfigManager.getInstance().getConfig("config.yml").get().getDouble("heavy_poison_reduction");
    }

    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof LivingEntity){
            EntityProperties equipment = EntityEquipmentCacheManager.getInstance().getAndCacheEquipment((LivingEntity) p);
            int heavyArmorCount = equipment.getHeavyArmorCount();
            return heavyArmorCount * resistancePerPiece;
        }
        return 0;
    }
}
