package me.athlaeos.mmoskills.utility;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityUtils {

    public static double applyNaturalDamageMitigations(Entity damagee, double baseDamage, EntityDamageEvent.DamageCause cause){
        if (damagee instanceof LivingEntity){
            double armor = ((LivingEntity) damagee).getAttribute(Attribute.GENERIC_ARMOR).getValue();
            double armor_toughness = ((LivingEntity) damagee).getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue();
            int resistance = (((LivingEntity) damagee).hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE))
                    ? ((LivingEntity) damagee).getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE).getAmplifier() + 1 : 0;
            int epf = getEPF((LivingEntity) damagee, cause);

            double withArmorReduction = baseDamage * (1 - Math.min(20, Math.max(armor / 5, armor - baseDamage / (2 + armor_toughness / 4))) / 25);
            double withResistanceReduction = withArmorReduction * (1 - (resistance * 0.2));
            return withResistanceReduction * (1 - (Math.min(epf, 20) / 25D));
        } else {
            return baseDamage;
        }
    }

    public static double removeNaturalDamageMitigations(Entity damagee, double mitigatedDamage, EntityDamageEvent.DamageCause cause){
        if (damagee instanceof LivingEntity){
            double armor = ((LivingEntity) damagee).getAttribute(Attribute.GENERIC_ARMOR).getValue();
            double armor_toughness = ((LivingEntity) damagee).getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue();
            int resistance = (((LivingEntity) damagee).hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE))
                    ? ((LivingEntity) damagee).getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE).getAmplifier() + 1 : 0;
            int epf = getEPF((LivingEntity) damagee, cause);

            double withoutEnchantmentReduction = mitigatedDamage / (1 - (Math.min(epf, 20) / 25D));
            double withoutResistanceReduction = withoutEnchantmentReduction / (1 - (resistance * 0.2));
            double withoutArmorReduction = withoutResistanceReduction / (1 - Math.min(20, Math.max(armor / 5, armor - mitigatedDamage / (2 + armor_toughness / 4))) / 25);
            return withoutArmorReduction;
        } else {
            return mitigatedDamage;
        }
    }

    public static int getEPF(LivingEntity entity, EntityDamageEvent.DamageCause cause){
        int epf = 0;
        for (ItemStack i : getEntityEquipment(entity, false)){
            if (cause == EntityDamageEvent.DamageCause.PROJECTILE){
                epf += 2 * i.getEnchantmentLevel(Enchantment.PROTECTION_PROJECTILE);
            }
            if (Arrays.asList(
                    EntityDamageEvent.DamageCause.FIRE,
                    EntityDamageEvent.DamageCause.LAVA,
                    EntityDamageEvent.DamageCause.FIRE_TICK,
                    EntityDamageEvent.DamageCause.HOT_FLOOR,
                    EntityDamageEvent.DamageCause.MELTING).contains(cause)){
                epf += 2 * i.getEnchantmentLevel(Enchantment.PROTECTION_FIRE);
            }
            if (Arrays.asList(
                    EntityDamageEvent.DamageCause.ENTITY_EXPLOSION,
                    EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
            ).contains(cause)){
                epf += 2 * i.getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS);
            }
            if (Arrays.asList(
                    EntityDamageEvent.DamageCause.FALL,
                    EntityDamageEvent.DamageCause.FLY_INTO_WALL
            ).contains(cause)){
                epf += 3 * i.getEnchantmentLevel(Enchantment.PROTECTION_FALL);
            }
            epf += i.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
        }
        return epf;
    }

    public static List<ItemStack> getEntityEquipment(Entity entity, boolean getHands){
        List<ItemStack> equipment = new ArrayList<>();
        if (entity == null) return new ArrayList<>();
        if (!(entity instanceof LivingEntity)) return equipment;
        LivingEntity e = (LivingEntity) entity;
        if (e.getEquipment() != null) {
            if (e.getEquipment().getHelmet() != null){ equipment.add(e.getEquipment().getHelmet()); }
            if (e.getEquipment().getChestplate() != null){ equipment.add(e.getEquipment().getChestplate()); }
            if (e.getEquipment().getLeggings() != null){ equipment.add(e.getEquipment().getLeggings()); }
            if (e.getEquipment().getBoots() != null){ equipment.add(e.getEquipment().getBoots()); }
            if (getHands){
                if (e.getEquipment().getItemInMainHand().getType() != Material.AIR){ equipment.add(e.getEquipment().getItemInMainHand()); }
                if (e.getEquipment().getItemInOffHand().getType() != Material.AIR){ equipment.add(e.getEquipment().getItemInOffHand()); }
            }
        }
        return equipment;
    }
}
