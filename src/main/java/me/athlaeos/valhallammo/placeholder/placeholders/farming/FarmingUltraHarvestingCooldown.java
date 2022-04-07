package me.athlaeos.valhallammo.placeholder.placeholders.farming;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.farming.FarmingProfile;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.entity.Player;

public class FarmingUltraHarvestingCooldown extends Placeholder {
    public FarmingUltraHarvestingCooldown(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getProfile(p, "FARMING");
        if (profile != null){
            if (profile instanceof FarmingProfile){
                int cooldown = ((FarmingProfile) profile).getUltraHarvestingCooldown();
                if (cooldown == -1){
                    return s.replace(this.placeholder, "-");
                }
                return s.replace(this.placeholder, Utils.toTimeStamp(cooldown, 1000));
            }
        }
        return s;
    }
}
