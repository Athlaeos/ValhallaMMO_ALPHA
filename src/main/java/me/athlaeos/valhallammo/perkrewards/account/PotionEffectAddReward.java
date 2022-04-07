package me.athlaeos.valhallammo.perkrewards.account;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.PotionEffect;
import me.athlaeos.valhallammo.managers.PotionEffectManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import org.bukkit.entity.Player;

import java.util.*;

public class PotionEffectAddReward extends PerkReward {
    private final Map<String, PotionEffect> potionEffects = new HashMap<>();
    /**
     * Constructor for PotionEffectAddReward, which adds a number of skill points (of a specific MaterialClass if desired)
     * to the player's profile when execute() runs. If materialClass is null, the amount is added to the player's general
     * crafting skill instead.
     * @param name the name of the reward. Must be unique to others rewards, or it will override them.
     *             This is also the name used to define the rewards in the configs.
     * @param argument the amount of points to add to the player. Must be Integer or Double. If Double, it's cast to int.
     */
    public PotionEffectAddReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        for (PotionEffect e : potionEffects.values()){
            PotionEffectManager.getInstance().addPotionEffect(player, e, true);
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        List<String> potionEffectArgs = new ArrayList<>();
        if (argument != null){
            if (argument instanceof Collection){
                potionEffectArgs.addAll((Collection<String>) argument);
            }
        }

       if (potionEffectArgs.size() > 0){
           if (potionEffectArgs.size() % 3 == 0){
               for (int c = 0; c < potionEffectArgs.size() / 3; c++){
                   try {
                       String potionEffectName = potionEffectArgs.get((c * 3));
                       long potionEffectDuration = Long.parseLong(potionEffectArgs.get(1 + (c * 3)));
                       double potionEffectAmplifier = Double.parseDouble(potionEffectArgs.get(2 + (c * 3)));
                       PotionEffect baseEffect = PotionEffectManager.getInstance().getBasePotionEffect(potionEffectName);
                       if (baseEffect == null) throw new IllegalArgumentException();
                       potionEffects.put(potionEffectName, new PotionEffect(potionEffectName, potionEffectDuration, potionEffectAmplifier, baseEffect.getType()));
                   } catch (IllegalArgumentException e){
                       ValhallaMMO.getPlugin().getLogger().warning("[ValhallaMMO] Invalid arguments given for reward potion_effect_add in progression_smithing.yml" +
                               " where name = " + potionEffectArgs.get(0) + ", duration = " + potionEffectArgs.get(1) + ", and amplifier is " + potionEffectArgs.get(2) + "\n" +
                               "A string list is to be given with a number of arguments divisible by 3, each set of 3 representing a potion effect." +
                               "\nEvery first argument must be the string name of the potion effect." +
                               "\nEvery second argument must be the integer duration of the potion effect, enter -1 to make effect infinite." +
                               "\nEvery third argument must be the integer amplifier of the potion effect. The impact of this effect differs per potion effect." +
                               "\nExample:" +
                               "\nperk_rewards:" +
                               "\n  potion_effect_add:" +
                               "\n    - 'smithing_buff_quality_singleuse'" +
                               "\n    - '-1'" +
                               "\n    - '50'\n");
                   }
               }
           } else {
               ValhallaMMO.getPlugin().getLogger().warning("[ValhallaMMO] Invalid arguments given for reward potion_effect_add in progression_smithing.yml" +
                       "\nA string list is to be given with a number of arguments divisible by 3, each set of 3 representing a potion effect." +
                       "\nEvery first argument must be the string name of the potion effect." +
                       "\nEvery second argument must be the integer duration of the potion effect, enter -1 to make effect infinite." +
                       "\nEvery third argument must be the integer amplifier of the potion effect. The impact of this effect differs per potion effect." +
                       "\nExample:" +
                       "\nperk_rewards:" +
                       "\n  potion_effect_add:" +
                       "\n    - 'smithing_buff_quality_singleuse'" +
                       "\n    - '-1'" +
                       "\n    - '50'\n");
           }
       }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.STRING_LIST;
    }
}
