package me.athlaeos.valhallammo.placeholder.placeholders;

import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import org.bukkit.entity.Player;

public class TotalGenericArmorPlaceholder extends Placeholder {

    public TotalGenericArmorPlaceholder(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {

        double nonEquipmentArmor = Math.max(0, AccumulativeStatManager.getInstance().getEntityStatsIncludingCache("NON_EQUIPMENT_ARMOR", p, 500,true));

        double armorMultiplierBonus = Math.max(0, AccumulativeStatManager.getInstance().getEntityStatsIncludingCache("ARMOR_MULTIPLIER_BONUS", p, 500,true));

        double totalArmor = Math.max(0, (nonEquipmentArmor * (1 + armorMultiplierBonus)));

        return s.replace(this.placeholder, String.format("%.1f", totalArmor));
    }
}
