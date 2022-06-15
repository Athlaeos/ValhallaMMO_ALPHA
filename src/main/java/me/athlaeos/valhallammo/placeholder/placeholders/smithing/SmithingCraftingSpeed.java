package me.athlaeos.valhallammo.placeholder.placeholders.smithing;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.smithing.SmithingProfile;
import org.bukkit.entity.Player;

public class SmithingCraftingSpeed extends Placeholder {
    public SmithingCraftingSpeed(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "SMITHING");
        if (profile != null){
            if (profile instanceof SmithingProfile){
                float multiplier = ((SmithingProfile) profile).getCraftingTimeReduction();
                double percentage = (1 + multiplier) * 100D;
                return s.replace(this.placeholder, String.format("%,.1f", percentage));
            }
        }
        return s;
    }
}
