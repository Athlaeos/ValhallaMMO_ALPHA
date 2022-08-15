package me.athlaeos.valhallammo.placeholder.placeholders.archery;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.archery.ArcheryProfile;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.entity.Player;

public class ArcheryChargedShotCooldown extends Placeholder {
    public ArcheryChargedShotCooldown(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "ARCHERY");
        if (profile != null){
            if (profile instanceof ArcheryProfile){
                int cooldown = ((ArcheryProfile) profile).getChargedShotCooldown();
                if (cooldown == -1){
                    return s.replace(this.placeholder, "-");
                }
                return s.replace(this.placeholder, Utils.toTimeStamp(cooldown, 1000));
            }
        }
        return s;
    }
}
