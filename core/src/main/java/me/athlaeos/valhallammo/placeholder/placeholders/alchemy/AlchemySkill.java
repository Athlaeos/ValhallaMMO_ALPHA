package me.athlaeos.valhallammo.placeholder.placeholders.alchemy;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.PotionType;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.alchemy.AlchemyProfile;
import org.bukkit.entity.Player;

public class AlchemySkill extends Placeholder {
    private final PotionType potionType;

    public AlchemySkill(String placeholder, PotionType potionType) {
        super(placeholder);
        this.potionType = potionType;
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "ALCHEMY");
        if (profile != null){
            if (profile instanceof AlchemyProfile){
                int skill;
                if (potionType == null){
                    skill = ((AlchemyProfile) profile).getGeneralBrewingQuality();
                } else {
                    skill = ((AlchemyProfile) profile).getBrewingQuality(potionType);
                }
                return s.replace(this.placeholder, String.format("%,d", skill));
            }
        }
        return s;
    }
}
