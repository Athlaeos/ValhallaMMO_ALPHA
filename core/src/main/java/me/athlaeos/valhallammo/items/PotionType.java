package me.athlaeos.valhallammo.items;

import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collection;

public enum PotionType {
    BUFF(Arrays.asList(PotionEffectType.ABSORPTION, PotionEffectType.CONDUIT_POWER, PotionEffectType.DAMAGE_RESISTANCE,
            PotionEffectType.DOLPHINS_GRACE, PotionEffectType.FAST_DIGGING, PotionEffectType.FIRE_RESISTANCE,
            PotionEffectType.HEAL, PotionEffectType.HEALTH_BOOST, PotionEffectType.HERO_OF_THE_VILLAGE,
            PotionEffectType.INCREASE_DAMAGE, PotionEffectType.INVISIBILITY, PotionEffectType.JUMP,
            PotionEffectType.LUCK, PotionEffectType.NIGHT_VISION, PotionEffectType.REGENERATION,
            PotionEffectType.SATURATION, PotionEffectType.SPEED, PotionEffectType.WATER_BREATHING)),
    DEBUFF(Arrays.asList(PotionEffectType.WITHER, PotionEffectType.UNLUCK, PotionEffectType.WEAKNESS,
            PotionEffectType.SLOW_DIGGING, PotionEffectType.SLOW, PotionEffectType.HUNGER, PotionEffectType.HARM,
            PotionEffectType.CONFUSION, PotionEffectType.BLINDNESS, PotionEffectType.BAD_OMEN,
            PotionEffectType.POISON)),
    NEUTRAL(Arrays.asList(PotionEffectType.SLOW_FALLING, PotionEffectType.LEVITATION, PotionEffectType.GLOWING));

    private final Collection<PotionEffectType> vanillaEffects;
    PotionType(Collection<PotionEffectType> vanillaEffects){
        this.vanillaEffects = vanillaEffects;
    }

    public Collection<PotionEffectType> getVanillaEffects() {
        return vanillaEffects;
    }

    public static PotionType getClass(PotionEffectType m){
        for (PotionType potionType : PotionType.values()){
            if (potionType.getVanillaEffects().contains(m)){
                return potionType;
            }
        }
        return null;
    }
}
