package me.athlaeos.valhallammo.statsources.light_armor;

import me.athlaeos.valhallammo.dom.ArmorType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.lightarmor.LightArmorProfile;
import me.athlaeos.valhallammo.statsources.EvEAccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class LightArmorProfileFullArmorDodgeChanceBonusEvESource extends EvEAccumulativeStatSource {
    @Override
    public double add(Entity p, Entity e, boolean use) {
        if (p instanceof Player){
            Profile profile = ProfileManager.getManager().getProfile((Player) p, "LIGHT_ARMOR");
            if (profile != null){
                if (profile instanceof LightArmorProfile){
                    int count = ArmorType.getArmorTypeCount((Player) p, ArmorType.LIGHT);
                    if (count >= ((LightArmorProfile) profile).getArmorPiecesForBonusses()){
                        return ((LightArmorProfile) profile).getFullArmorDodgeChance();
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public double add(Entity p, boolean use) {
        return 0;
    }
}
