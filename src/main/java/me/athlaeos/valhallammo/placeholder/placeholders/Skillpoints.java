package me.athlaeos.valhallammo.placeholder.placeholders;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.dom.SkillType;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.materials.MaterialClass;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.smithing.dom.SmithingProfile;
import org.bukkit.entity.Player;

public class SmithingSkill extends Placeholder {
    private final MaterialClass materialClass;

    public SmithingSkill(String placeholder, MaterialClass materialClass) {
        super(placeholder);
        this.materialClass = materialClass;
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileUtil.getProfile(p, SkillType.SMITHING);
        if (profile != null){
            if (profile instanceof SmithingProfile){
                int skill;
                if (materialClass == null){
                    skill = ((SmithingProfile) profile).getGeneralCraftingQuality();
                } else {
                    skill = ((SmithingProfile) profile).getCraftingQuality(materialClass);
                }
                return s.replace(this.placeholder, String.format("%,d", skill));
            }
        }
        return s;
    }
}
