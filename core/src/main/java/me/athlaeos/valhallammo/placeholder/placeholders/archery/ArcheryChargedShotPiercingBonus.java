package me.athlaeos.valhallammo.placeholder.placeholders.archery;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.archery.ArcheryProfile;
import org.bukkit.entity.Player;

public class ArcheryChargedShotPiercingBonus extends Placeholder {
    public ArcheryChargedShotPiercingBonus(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "ARCHERY");
        if (profile != null){
            if (profile instanceof ArcheryProfile){
                int charges = ((ArcheryProfile) profile).getChargedShotPiercingBonus();
                return s.replace(this.placeholder, "" + Math.max(0, charges));
            }
        }
        return s;
    }
}
