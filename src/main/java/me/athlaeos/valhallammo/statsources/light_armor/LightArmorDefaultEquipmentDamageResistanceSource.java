package me.athlaeos.valhallammo.statsources.light_armor;

import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.ArmorType;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class LightArmorDefaultEquipmentDamageResistanceSource extends AccumulativeStatSource {
    private final double resistancePerPiece;

    public LightArmorDefaultEquipmentDamageResistanceSource(){
        resistancePerPiece = ConfigManager.getInstance().getConfig("config.yml").get().getDouble("light_damage_reduction");
    }

    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof LivingEntity){
            int lightArmorCount = ArmorType.getArmorTypeCount((LivingEntity) p, ArmorType.LIGHT);
            return lightArmorCount * resistancePerPiece;
        }
        return 0;
    }
}
