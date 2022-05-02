package me.athlaeos.valhallammo.utility;

import me.athlaeos.valhallammo.items.EquipmentClass;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public class EntityUtils {

    public static ItemStack getHoldingItem(Player p, EquipmentClass equipmentClass){
        ItemStack mainHandItem = p.getInventory().getItemInMainHand();
        if (!Utils.isItemEmptyOrNull(mainHandItem)) {
            if (equipmentClass == null) return mainHandItem;
            EquipmentClass clazz = EquipmentClass.getClass(mainHandItem.getType());
            if (clazz == equipmentClass){
                return mainHandItem;
            }
        }
        return null;
    }

    public static double applyNaturalDamageMitigations(Entity damagee, double baseDamage, EntityDamageEvent.DamageCause cause){
        if (damagee instanceof LivingEntity){
            double armor = 0;
            AttributeInstance armorInstance = ((LivingEntity) damagee).getAttribute(Attribute.GENERIC_ARMOR);
            if (armorInstance != null){
                armor = armorInstance.getValue();
            }
            double armor_toughness = 0;
            AttributeInstance toughnessInstance = ((LivingEntity) damagee).getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS);
            if (toughnessInstance != null){
                armor_toughness = toughnessInstance.getValue();
            }
            int resistance = 0;
            PotionEffect effect = ((LivingEntity) damagee).getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            if (effect != null){
                resistance = effect.getAmplifier() + 1;
            }
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
            return withoutResistanceReduction / (1 - Math.min(20, Math.max(armor / 5, armor - mitigatedDamage / (2 + armor_toughness / 4))) / 25);
        } else {
            return mitigatedDamage;
        }
    }

    public static boolean isEntityFacing(LivingEntity who, Location at, double cos_angle){
        Vector dir = at.toVector().subtract(who.getEyeLocation().toVector()).normalize();
        double dot = dir.dot(who.getEyeLocation().getDirection());
        return dot >= cos_angle;
    }

    public static int getEPF(LivingEntity entity, EntityDamageEvent.DamageCause cause){
        int epf = 0;
        for (ItemStack i : getEntityEquipment(entity).getIterable(false)){
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

    public static EntityEquipment getEntityEquipment(Entity entity){
        EntityEquipment equipment = new EntityEquipment();
        if (entity == null) return equipment;
        if (!(entity instanceof LivingEntity)) return equipment;
        LivingEntity e = (LivingEntity) entity;
        if (e.getEquipment() != null) {
            equipment.setHelmet(e.getEquipment().getHelmet());
            equipment.setChestplate(e.getEquipment().getChestplate());
            equipment.setLeggings(e.getEquipment().getLeggings());
            equipment.setBoots(e.getEquipment().getBoots());
            equipment.setMainHand(e.getEquipment().getItemInMainHand());
            equipment.setOffHand(e.getEquipment().getItemInOffHand());
        }
        return equipment;
    }

    public static class EntityEquipment{
        private ItemStack helmet = null;
        private ItemStack chestplate = null;
        private ItemStack leggings = null;
        private ItemStack boots = null;
        private ItemStack mainHand = null;
        private ItemStack offHand = null;

        public EntityEquipment(){};

        public EntityEquipment(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack leftHand, ItemStack rightHand){
            this.helmet = helmet;
            this.chestplate = chestplate;
            this.leggings = leggings;
            this.boots = boots;
            this.mainHand = leftHand;
            this.offHand = rightHand;
        }

        public ItemStack getHelmet() {
            return helmet;
        }

        public void setHelmet(ItemStack helmet) {
            this.helmet = helmet;
        }

        public ItemStack getChestplate() {
            return chestplate;
        }

        public void setChestplate(ItemStack chestplate) {
            this.chestplate = chestplate;
        }

        public ItemStack getBoots() {
            return boots;
        }

        public void setBoots(ItemStack boots) {
            this.boots = boots;
        }

        public ItemStack getLeggings() {
            return leggings;
        }

        public void setLeggings(ItemStack leggings) {
            this.leggings = leggings;
        }

        public ItemStack getMainHand() {
            return mainHand;
        }

        public void setMainHand(ItemStack mainHand) {
            this.mainHand = mainHand;
        }

        public ItemStack getOffHand() {
            return offHand;
        }

        public void setOffHand(ItemStack offHand) {
            this.offHand = offHand;
        }

        public Collection<ItemStack> getIterable(boolean includeHands){
            Collection<ItemStack> iterable = new HashSet<>();
            if (!Utils.isItemEmptyOrNull(helmet)) iterable.add(helmet);
            if (!Utils.isItemEmptyOrNull(chestplate)) iterable.add(chestplate);
            if (!Utils.isItemEmptyOrNull(leggings)) iterable.add(leggings);
            if (!Utils.isItemEmptyOrNull(boots)) iterable.add(boots);
            if (includeHands){
                if (!Utils.isItemEmptyOrNull(mainHand)) iterable.add(mainHand);
                if (!Utils.isItemEmptyOrNull(offHand)) iterable.add(offHand);
            }
            return iterable;
        }

        public Collection<ItemStack> getHands(){
            Collection<ItemStack> iterable = new HashSet<>();
            if (!Utils.isItemEmptyOrNull(mainHand)) iterable.add(mainHand);
            if (!Utils.isItemEmptyOrNull(offHand)) iterable.add(offHand);
            return iterable;
        }
    }
}
