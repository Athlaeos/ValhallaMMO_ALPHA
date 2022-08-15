package me.athlaeos.valhallammo.statsources.light_armor;

import me.athlaeos.valhallammo.dom.ArmorType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.lightarmor.LightArmorProfile;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class LightArmorProfileEquipmentFireResistanceSource extends AccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof Player){
            Profile profile = ProfileManager.getManager().getProfile((Player) p, "LIGHT_ARMOR");
            if (profile != null){
                if (profile instanceof LightArmorProfile){
                    double resistancePerPiece = ((LightArmorProfile) profile).getFireResistance();
                    int lightArmorCount = ArmorType.getArmorTypeCount((LivingEntity) p, ArmorType.LIGHT);
                    return lightArmorCount * resistancePerPiece;
                }
            }
        }
        return 0;
    }
}
