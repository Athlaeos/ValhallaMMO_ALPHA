package me.athlaeos.valhallammo.placeholder.placeholders.alchemy;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.alchemy.AlchemyProfile;
import org.bukkit.entity.Player;

public class AlchemyIngredientSaveChance extends Placeholder {
    public AlchemyIngredientSaveChance(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "ALCHEMY");
        if (profile != null){
            if (profile instanceof AlchemyProfile){
                float chance = ((AlchemyProfile) profile).getBrewingIngredientSaveChance();
                if (chance >= 1) return s.replace(this.placeholder, "100");
                double percentage = chance * 100;
                return s.replace(this.placeholder, String.format("%,.1f", percentage));
            }
        }
        return s;
    }
}
