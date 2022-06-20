package me.athlaeos.valhallammo.statsources.general;

import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.items.attributewrappers.AttributeWrapper;
import me.athlaeos.valhallammo.listeners.PlayerMovementListener;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.statsources.EvEAccumulativeStatSource;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class VelocityDamageAttributeOnAttackSource extends EvEAccumulativeStatSource {
    private final double velocity_damage_constant;
    public VelocityDamageAttributeOnAttackSource(){
        this.velocity_damage_constant = ConfigManager.getInstance().getConfig("config.yml").get().getDouble("velocity_damage_constant", 0.33);
    }

    @Override
    public double add(Entity p, boolean use) {
        return 0;
    }

    @Override
    public double add(Entity entity, Entity offender, boolean use) {
        double value;
        Vector facingDirection;
        Vector movementDirection;
        if (offender instanceof AbstractArrow){
            ItemStack ammo = ItemUtils.getArrowFromEntity((AbstractArrow) offender);
            if (ammo == null) return 0;
            AttributeWrapper wrapper = ItemAttributesManager.getInstance().getAttributeWrapper(ammo, "CUSTOM_VELOCITY_DAMAGE_BONUS");
            if (wrapper == null) return 0;
            value = wrapper.getAmount();
            facingDirection = offender.getVelocity();
            movementDirection = offender.getVelocity();
        } else if (offender instanceof LivingEntity){
            EntityEquipment equipment = ((LivingEntity) offender).getEquipment();
            if (equipment == null) return 0;
            ItemStack weapon = equipment.getItemInMainHand();
            if (Utils.isItemEmptyOrNull(weapon)) return 0;
            AttributeWrapper wrapper = ItemAttributesManager.getInstance().getAttributeWrapper(weapon, "CUSTOM_VELOCITY_DAMAGE_BONUS");
            if (wrapper == null) return 0;
            value = wrapper.getAmount();
            facingDirection = ((LivingEntity) offender).getEyeLocation().getDirection();
            movementDirection = PlayerMovementListener.getLastMovementVectors().get(offender.getUniqueId());
            if (movementDirection == null) return 0;
        } else return 0;
        if (value > 0){
            double speed = movementDirection.length();
            double multiplier = Math.max(0, facingDirection.clone().normalize().dot(movementDirection.clone().normalize()));
            if (Double.isNaN(speed) || Double.isNaN(multiplier)) {
                return 0;
            }
            double speedDamageMultiplier = speed / velocity_damage_constant;
            return speedDamageMultiplier * multiplier * value;
        }
        return 0;
    }
}
