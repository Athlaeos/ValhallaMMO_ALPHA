package me.athlaeos.valhallammo.placeholder.placeholders;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.items.MaterialClass;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.smithing.SmithingProfile;
import org.bukkit.entity.Player;

public class SmithingEXPMultipliers extends Placeholder {
    private final MaterialClass materialClass;

    public SmithingEXPMultipliers(String placeholder, MaterialClass materialClass) {
        super(placeholder);
        this.materialClass = materialClass;
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileUtil.getProfile(p, SkillType.SMITHING);
        if (profile != null){
            if (profile instanceof SmithingProfile){
                double multiplier;
                if (materialClass == null){
                    multiplier = ((SmithingProfile) profile).getGeneralCraftingExpMultiplier();
                } else {
                    multiplier = ((SmithingProfile) profile).getCraftingEXPMultiplier(materialClass);
                }
                return s.replace(this.placeholder, String.format("%,.2f", multiplier));
            }
        }
        return s;
    }
}
