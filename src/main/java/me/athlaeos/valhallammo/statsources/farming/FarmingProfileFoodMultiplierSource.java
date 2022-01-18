package me.athlaeos.valhallammo.statsources.farming;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.skills.farming.FarmingProfile;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class FarmingFoodMultiplierSource extends AccumulativeStatSource {
    private final FoodType type;

    public FarmingFoodMultiplierSource(FoodType type){
        this.type = type;
    }

    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof Player){
            Profile profile = ProfileUtil.getProfile((Player) p, SkillType.FARMING);
            if (profile == null) return 0;
            if (!(profile instanceof FarmingProfile)) return 0;
            FarmingProfile farmingProfile = (FarmingProfile) profile;
            switch (type){
                case VEG: return farmingProfile.getVegetarianHungerMultiplier();
                case MEAT: return farmingProfile.getCarnivorousHungerMultiplier();
                case FISH: return farmingProfile.getPescotarianHungerMultiplier();
                case GARBAGE: return farmingProfile.getGarbageHungerMultiplier();
                case MAGICAL: return farmingProfile.getMagicalHungerMultiplier();
            }
        }
        return 0;
    }

    public enum FoodType {
        MEAT,
        VEG,
        FISH,
        GARBAGE,
        MAGICAL
    }
}
