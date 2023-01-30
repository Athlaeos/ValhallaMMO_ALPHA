package me.athlaeos.valhallammo.placeholder.placeholders;

import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import org.bukkit.entity.Player;

public class TotalHeavyArmorPlaceholder extends Placeholder {

    public TotalHeavyArmorPlaceholder(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {

        double heavyArmor = Math.max(0, AccumulativeStatManager.getInstance().getEntityStatsIncludingCache("HEAVY_ARMOR", p, 500,true));

        double heavyArmorMultiplier = Math.max(0, AccumulativeStatManager.getInstance().getEntityStatsIncludingCache("HEAVY_ARMOR_MULTIPLIER", p, 500,true));
        double armorMultiplierBonus = Math.max(0, AccumulativeStatManager.getInstance().getEntityStatsIncludingCache("ARMOR_MULTIPLIER_BONUS", p, 500,true));

        double totalHeavyArmor = Math.max(0, (heavyArmor * heavyArmorMultiplier));

        double totalArmor = Math.max(0, (totalHeavyArmor * (1 + armorMultiplierBonus)));

        return s.replace(this.placeholder, String.format("%.1f", totalArmor));
    }
}
