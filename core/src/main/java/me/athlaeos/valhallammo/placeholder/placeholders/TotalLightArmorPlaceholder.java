package me.athlaeos.valhallammo.placeholder.placeholders;

import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import org.bukkit.entity.Player;

public class TotalLightArmorPlaceholder extends Placeholder {

    public TotalLightArmorPlaceholder(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {

        double lightArmor = Math.max(0, AccumulativeStatManager.getInstance().getEntityStatsIncludingCache("LIGHT_ARMOR", p, 500,true));

        double lightArmorMultiplier = Math.max(0, AccumulativeStatManager.getInstance().getEntityStatsIncludingCache("LIGHT_ARMOR_MULTIPLIER", p, 500,true));
        double armorMultiplierBonus = Math.max(0, AccumulativeStatManager.getInstance().getEntityStatsIncludingCache("ARMOR_MULTIPLIER_BONUS", p, 500,true));

        double totalLightArmor = Math.max(0, (lightArmor * lightArmorMultiplier));

        double totalArmor = Math.max(0, (totalLightArmor * (1 + armorMultiplierBonus)));

        return s.replace(this.placeholder, String.format("%.1f", totalArmor));
    }
}
