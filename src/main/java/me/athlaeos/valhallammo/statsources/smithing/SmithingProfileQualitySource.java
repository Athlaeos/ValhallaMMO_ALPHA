package me.athlaeos.valhallammo.statsources.smithing;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.items.MaterialClass;
import me.athlaeos.valhallammo.skills.smithing.SmithingProfile;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SmithingProfileQualitySource extends AccumulativeStatSource {
    private final MaterialClass materialClass;

    public SmithingProfileQualitySource(MaterialClass m){
        this.materialClass = m;
    }

    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof Player){
            Profile profile = ProfileManager.getProfile((Player) p, "SMITHING");
            if (profile == null) return 0;
            if (!(profile instanceof SmithingProfile)) return 0;
            SmithingProfile smithingProfile = (SmithingProfile) profile;
            if (materialClass == null){
                return smithingProfile.getGeneralCraftingQuality();
            } else {
                return smithingProfile.getCraftingQuality(materialClass);
            }
        }
        return 0;
    }
}
