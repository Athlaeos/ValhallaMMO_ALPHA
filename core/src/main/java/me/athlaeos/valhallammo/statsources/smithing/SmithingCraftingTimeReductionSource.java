package me.athlaeos.valhallammo.statsources.smithing;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.smithing.SmithingProfile;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SmithingCraftingTimeReductionSource extends AccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof Player){
            Profile profile = ProfileManager.getManager().getProfile((Player) p, "SMITHING");
            if (profile != null){
                if (profile instanceof SmithingProfile){
                    return ((SmithingProfile) profile).getCraftingTimeReduction();
                }
            }
        }
        return 0;
    }
}
