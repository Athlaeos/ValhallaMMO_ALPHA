package me.athlaeos.valhallammo.placeholder.placeholders.archery;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.archery.ArcheryProfile;
import org.bukkit.entity.Player;

public class ArcheryCritDamageMultiplier extends Placeholder {
    public ArcheryCritDamageMultiplier(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "ARCHERY");
        if (profile != null){
            if (profile instanceof ArcheryProfile){
                float multiplier = ((ArcheryProfile) profile).getCritDamageMultiplier();
                return s.replace(this.placeholder, String.format("%,.2f", multiplier));
            }
        }
        return s;
    }
}
