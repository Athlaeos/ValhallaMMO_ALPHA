package me.athlaeos.valhallammo.perkrewards.enchanting;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.EnchantmentType;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.enchanting.EnchantingProfile;
import me.athlaeos.valhallammo.skills.enchanting.EnchantingSkill;
import org.bukkit.entity.Player;

public class EnchantingSkillSetReward extends PerkReward {
    private int points = 0;
    private final EnchantmentType enchantmentType;

    /**
     * Constructor for EnchantingSkillSetReward, which sets the player's skill points (of a specific EnchantmentType if desired)
     * to the player's profile when execute() runs. If enchantmentType is null, the player's general enchanting skill is set
     * instead.
     * @param name the name of the reward. Must be unique to others rewards, or it will override them.
     *             This is also the name used to define the rewards in the configs.
     * @param argument the amount of points to set to the player's crafting skill. Must be Integer or Double.
     *                 If Double, it's cast to int.
     * @param enchantmentType the EnchantmentType to add to the player's skill. If null, general enchanting skill is added on
     *                     instead.
     */
    public EnchantingSkillSetReward(String name, Object argument, EnchantmentType enchantmentType) {
        super(name, argument);
        this.enchantmentType = enchantmentType;
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "ENCHANTING");
        if (profile == null) return;
        if (profile instanceof EnchantingProfile){
            EnchantingProfile enchantingProfile = (EnchantingProfile) profile;
            enchantingProfile.setEnchantingSkill(enchantmentType, points);
            ProfileManager.getManager().setProfile(player, enchantingProfile, "ENCHANTING");
            EnchantingSkill.resetPlayerEnchantmentCache(player);
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Integer){
                points = (Integer) argument;
            }
            if (argument instanceof Double){
                double temp = (Double) argument;
                points = (int) temp;
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.INTEGER;
    }
}
