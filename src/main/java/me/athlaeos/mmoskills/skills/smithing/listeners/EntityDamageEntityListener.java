package me.athlaeos.mmoskills.skills.smithing.listeners;

import me.athlaeos.mmoskills.skills.smithing.managers.ItemDamageManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class EntityDamageEntityListener implements Listener {

    @EventHandler (priority=EventPriority.LOWEST)
    public void onDurabilityChange(EntityDamageByEntityEvent e){
        boolean isCritical = false;
        boolean isRanged = e.getDamager() instanceof Projectile;
        // this mess returns the projectile shooter if there is one and is an entity(otherwise null), or the damager if it was a melee attack.
        Entity trueDamager = (isRanged) ? ((((Projectile) e.getDamager()).getShooter() instanceof Entity) ? (Entity) ((Projectile) e.getDamager()).getShooter() : null) : e.getDamager();

        if (trueDamager instanceof Player && !isRanged) isCritical = isCritical((Player) e.getDamager());

        if (trueDamager instanceof LivingEntity){
            LivingEntity entity = (LivingEntity) trueDamager;
            ItemStack entityWeapon = entity.getEquipment().getItemInMainHand();
            double damage = e.getDamage();
            double extraDamage = ItemDamageManager.getInstance().getDamage(entityWeapon);
            damage += ((isCritical) ? extraDamage * 1.5 : extraDamage);
            e.setDamage(damage);
        }
    }

    private boolean isCritical(Player p) {
        return (p.getVelocity().getY() + 0.0785) <= 0;
    }
}
