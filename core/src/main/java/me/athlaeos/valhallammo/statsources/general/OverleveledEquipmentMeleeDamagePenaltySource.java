package me.athlaeos.valhallammo.statsources.general;

import me.athlaeos.valhallammo.items.OverleveledEquipmentTool;
import me.athlaeos.valhallammo.statsources.EvEAccumulativeStatSource;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class OverleveledEquipmentMeleeDamagePenaltySource extends EvEAccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        return 0;
    }

    @Override
    public double add(Entity entity, Entity offender, boolean use) {
        if (offender instanceof Player){
            EntityEquipment equipment = ((LivingEntity) offender).getEquipment();
            if (equipment == null) return 0;
            ItemStack weapon = equipment.getItemInMainHand();
            if (Utils.isItemEmptyOrNull(weapon)) return 0;
            return OverleveledEquipmentTool.getTool().getPenalty((Player) offender, weapon, "damage");
        }
        return 0;
    }
}
