package me.athlaeos.valhallammo.placeholder.placeholders.archery;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.archery.ArcheryProfile;
import org.bukkit.entity.Player;

public class ArcheryChargedShotKnockbackBonus extends Placeholder {
    public ArcheryChargedShotKnockbackBonus(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "ARCHERY");
        if (profile != null){
            if (profile instanceof ArcheryProfile){
                int knockbackBonus = ((ArcheryProfile) profile).getChargedShotKnockbackBonus();
                return s.replace(this.placeholder, String.format("%,d", knockbackBonus));
            }
        }
        return s;
    }
}
