package me.athlaeos.valhallammo.placeholder.placeholders;

import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import org.bukkit.entity.Player;

public class TotalArmorPlaceholder extends Placeholder {

    public TotalArmorPlaceholder(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {

        double lightArmor = Math.max(0, AccumulativeStatManager.getInstance().getEntityStatsIncludingCache("LIGHT_ARMOR", p, 500,true));
        double heavyArmor = Math.max(0, AccumulativeStatManager.getInstance().getEntityStatsIncludingCache("HEAVY_ARMOR", p, 500,true));
        double nonEquipmentArmor = Math.max(0, AccumulativeStatManager.getInstance().getEntityStatsIncludingCache("NON_EQUIPMENT_ARMOR", p, 500,true));

        double lightArmorMultiplier = Math.max(0, AccumulativeStatManager.getInstance().getEntityStatsIncludingCache("LIGHT_ARMOR_MULTIPLIER", p, 500,true));
        double heavyArmorMultiplier = Math.max(0, AccumulativeStatManager.getInstance().getEntityStatsIncludingCache("HEAVY_ARMOR_MULTIPLIER", p, 500,true));
        double armorMultiplierBonus = Math.max(0, AccumulativeStatManager.getInstance().getEntityStatsIncludingCache("ARMOR_MULTIPLIER_BONUS", p, 500,true));

        double totalLightArmor = Math.max(0, (lightArmor * lightArmorMultiplier));
        double totalHeavyArmor = Math.max(0, (heavyArmor * heavyArmorMultiplier));

        double totalArmor = Math.max(0, ((totalLightArmor + totalHeavyArmor + nonEquipmentArmor) * (1 + armorMultiplierBonus)));

        return s.replace(this.placeholder, String.format("%.1f", totalArmor));
    }
}
