package me.athlaeos.valhallammo.placeholder.placeholders.enchanting;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.EnchantmentType;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.enchanting.EnchantingProfile;
import org.bukkit.entity.Player;

public class EnchantingSkill extends Placeholder {
    private final EnchantmentType enchantmentType;

    public EnchantingSkill(String placeholder, EnchantmentType enchantmentType) {
        super(placeholder);
        this.enchantmentType = enchantmentType;
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "ENCHANTING");
        if (profile != null){
            if (profile instanceof EnchantingProfile){
                int skill;
                skill = ((EnchantingProfile) profile).getEnchantingSkill(enchantmentType);
                return s.replace(this.placeholder, String.format("%,d", skill));
            }
        }
        return s;
    }
}
