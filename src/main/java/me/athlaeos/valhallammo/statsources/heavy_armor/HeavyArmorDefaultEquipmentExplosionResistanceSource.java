package me.athlaeos.valhallammo.statsources.heavy_armor;

import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.ArmorType;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class HeavyArmorDefaultEquipmentExplosionResistanceSource extends AccumulativeStatSource {
    private final double resistancePerPiece;

    public HeavyArmorDefaultEquipmentExplosionResistanceSource(){
        resistancePerPiece = ConfigManager.getInstance().getConfig("config.yml").get().getDouble("heavy_explosion_reduction");
    }

    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof LivingEntity){
            int heavyArmorCount = ArmorType.getArmorTypeCount((LivingEntity) p, ArmorType.HEAVY);
            return heavyArmorCount * resistancePerPiece;
        }
        return 0;
    }
}
