package me.athlaeos.valhallammo.statsources.heavy_armor;

import me.athlaeos.valhallammo.dom.ArmorType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.heavyarmor.HeavyArmorProfile;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class HeavyArmorProfileEquipmentKnockbackResistanceSource extends AccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof Player){
            Profile profile = ProfileManager.getManager().getProfile((Player) p, "HEAVY_ARMOR");
            if (profile != null){
                if (profile instanceof HeavyArmorProfile){
                    double resistancePerPiece = ((HeavyArmorProfile) profile).getKnockbackResistance();
                    int heavyArmorCount = ArmorType.getArmorTypeCount((LivingEntity) p, ArmorType.HEAVY);
                    return heavyArmorCount * resistancePerPiece;
                }
            }
        }
        return 0;
    }
}
