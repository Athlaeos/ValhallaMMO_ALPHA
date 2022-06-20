package me.athlaeos.valhallammo.statsources.general;

import me.athlaeos.valhallammo.items.OverleveledEquipmentTool;
import me.athlaeos.valhallammo.skills.archery.ArcherySkill;
import me.athlaeos.valhallammo.statsources.EvEAccumulativeStatSource;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

public class OverleveledEquipmentArcheryDamagePenaltySource extends EvEAccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        return 0;
    }

    @Override
    public double add(Entity entity, Entity offender, boolean use) {
        if (offender instanceof AbstractArrow){
            ProjectileSource source = ((AbstractArrow) offender).getShooter();
            if (source != null){
                if (source instanceof Player){
                    Player shooter = (Player) source;
                    ItemStack bow = ArcherySkill.getBowsUsedInShooting().get(shooter.getUniqueId());
                    if (Utils.isItemEmptyOrNull(bow)) return 0;
                    return OverleveledEquipmentTool.getTool().getPenalty(shooter, bow, "damage");
                }
            }
        }
        return 0;
    }
}
