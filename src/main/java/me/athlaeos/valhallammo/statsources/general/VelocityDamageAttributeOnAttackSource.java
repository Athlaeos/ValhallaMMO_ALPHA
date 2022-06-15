package me.athlaeos.valhallammo.statsources.general;

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

    @Override
    public double add(Entity p, boolean use) {
        return 0;
    }

    @Override
    public double add(Entity entity, Entity offender, boolean use) {
        double value = 0;
        Vector direction;
        if (offender.getVelocity().length() < 0.001) return 0;
        if (offender instanceof AbstractArrow){
            ItemStack ammo = ItemUtils.getArrowFromEntity((AbstractArrow) offender);
            if (ammo == null) return 0;
            AttributeWrapper wrapper = ItemAttributesManager.getInstance().getAttributeWrapper(ammo, "CUSTOM_VELOCITY_DAMAGE_BONUS");
            if (wrapper == null) return 0;
            value = wrapper.getAmount();
            direction = offender.getVelocity();
        } else if (offender instanceof LivingEntity){
            EntityEquipment equipment = ((LivingEntity) offender).getEquipment();
            if (equipment == null) return 0;
            ItemStack weapon = equipment.getItemInMainHand();
            if (Utils.isItemEmptyOrNull(weapon)) return 0;
            AttributeWrapper wrapper = ItemAttributesManager.getInstance().getAttributeWrapper(weapon, "CUSTOM_VELOCITY_DAMAGE_BONUS");
            if (wrapper == null) return 0;
            value = wrapper.getAmount();
            direction = ((LivingEntity) offender).getEyeLocation().getDirection();
        } else return 0;
        if (value > 0){
            double multiplier = Math.max(0, direction.dot(offender.getVelocity()));
            System.out.println(multiplier > 0 ? "Player is moving towards hit target, damage increased x" +multiplier : "Player is moving away from target, damage stays the same");
            Vector movementDirection = PlayerMovementListener.getLastMovementVectors().get(offender.getUniqueId());
            if (movementDirection == null) return 0;
            double speed = movementDirection.length();
            System.out.println("velocity: " + speed);
            System.out.printf("Speed: %.3f, multiplier: %.3f, attribute: %.2f = %.3f%n", speed, multiplier, value, speed * multiplier * value);
            return speed * multiplier * value;
        }
        return 0;
    }
}
