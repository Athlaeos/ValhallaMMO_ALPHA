package me.athlaeos.valhallammo.utility;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.ArmorType;
import me.athlaeos.valhallammo.dom.EntityProperties;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.managers.CustomEnchantmentManager;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.managers.PotionEffectManager;
import me.athlaeos.valhallatrinkets.TrinketsManager;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EntityUtils {

    /**
     * returns approximately the fraction of damage an attack should do given an attack cooldown
     * this method is not exactly like vanilla, but it comes pretty close to vanilla
     * the original formula involves attack speed, but this formula is intended to work with Player#getAttackCooldown()
     * @param cooldown the attack cooldown, a float between 0 and 1
     * @return the approximate damage multiplier of the attack
     */
    public static float getDamageMultiplierFromAttackCooldown(float cooldown){
        return Math.min(1F, (((4.525F * cooldown * (4.525F * cooldown + 1)) / 2) * 6.4F + 20F) / 100F);
    }

    /**
     * returns approximately the fraction of damage the enchantments on an attack should do given an attack cooldown
     * this method is not exactly like vanilla, but it comes pretty close to vanilla
     * the original formula involves attack speed, but this formula is intended to work with Player#getAttackCooldown()
     * @param cooldown the attack cooldown, a float between 0 and 1
     * @return the approximate damage multiplier of the enchantments on the attack
     */
    public static float getEnchantmentDamageMultiplierFromAttackCooldown(float cooldown){
        return Math.min(1F, (90F * cooldown + 10F) / 100F);
    }

    public static void addUniqueAttribute(LivingEntity e, String identifier, Attribute type, double amount, AttributeModifier.Operation operation){
        AttributeInstance instance = e.getAttribute(type);
        if (instance != null){
            for (AttributeModifier modifier : instance.getModifiers()){
                if (modifier.getName().equals(identifier)){
                    instance.removeModifier(modifier);
                    break;
                }
            }
            instance.addModifier(
                    new AttributeModifier(identifier, amount, operation)
            );
        }
    }

    /**
     * if the entity is a player, return entity as player. If the entity is a projectile, and the shooter is a player,
     * return shooter as player
     */
    public static Player getPlayerFromEntity(Entity entity){
        Player p = null;
        if (entity instanceof Projectile){
            Projectile projectile = (Projectile) entity;
            if (projectile.getShooter() instanceof Player){
                p = (Player) projectile.getShooter();
            }
        } else if (entity instanceof Player){
            p = (Player) entity;
        }
        return p;
    }

    public static void removeUniqueAttribute(LivingEntity e, String identifier, Attribute type){
        AttributeInstance instance = e.getAttribute(type);
        if (instance != null){
            for (AttributeModifier modifier : instance.getModifiers()){
                if (modifier.getName().equals(identifier)){
                    instance.removeModifier(modifier);
                    break;
                }
            }
        }
    }

    public static ItemStack getHoldingItem(Player p, EquipmentClass equipmentClass){
        ItemStack mainHandItem = p.getInventory().getItemInMainHand();
        if (!Utils.isItemEmptyOrNull(mainHandItem)) {
            if (equipmentClass == null) return mainHandItem;
            EquipmentClass clazz = EquipmentClass.getClass(mainHandItem);
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
        for (ItemStack i : getEntityProperties(entity).getIterable(false)){
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

    public static EntityProperties getEntityProperties(Entity entity){
        return getEntityProperties(entity, false, false, false, false);
    }

    public static EntityProperties getEntityProperties(Entity entity, boolean getEnchantments, boolean getAttributes, boolean countArmorTypes, boolean getPotionEffects){
        EntityProperties equipment = new EntityProperties();
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
            if (countArmorTypes){
                equipment.setHeavyArmorCount(ArmorType.getArmorTypeCount(e, ArmorType.HEAVY));
                equipment.setLightArmorCount(ArmorType.getArmorTypeCount(e, ArmorType.LIGHT));
                equipment.setWeightlessArmorCount(ArmorType.getArmorTypeCount(e, ArmorType.WEIGHTLESS));
            }
            if (getPotionEffects){
                equipment.getActivePotionEffects().putAll(PotionEffectManager.getInstance().getActivePotionEffects(e));
            }
            if (getEnchantments){
                if (!Utils.isItemEmptyOrNull(equipment.getHelmet())) equipment.setHelmetEnchantments(CustomEnchantmentManager.getInstance().getCurrentEnchantments(equipment.getHelmet()));
                if (!Utils.isItemEmptyOrNull(equipment.getChestplate())) equipment.setChestplateEnchantments(CustomEnchantmentManager.getInstance().getCurrentEnchantments(equipment.getChestplate()));
                if (!Utils.isItemEmptyOrNull(equipment.getLeggings())) equipment.setLeggingsEnchantments(CustomEnchantmentManager.getInstance().getCurrentEnchantments(equipment.getLeggings()));
                if (!Utils.isItemEmptyOrNull(equipment.getBoots())) equipment.setBootsEnchantments(CustomEnchantmentManager.getInstance().getCurrentEnchantments(equipment.getBoots()));
                if (
                        !Utils.isItemEmptyOrNull(equipment.getMainHand()) &&
                                !EquipmentClass.isArmor(equipment.getMainHand()) &&
                                EquipmentClass.getClass(equipment.getMainHand()) != EquipmentClass.TRINKET
                ){
                    equipment.setMainHandEnchantments(CustomEnchantmentManager.getInstance().getCurrentEnchantments(equipment.getMainHand()));
                }
                if (
                        !Utils.isItemEmptyOrNull(equipment.getOffHand()) &&
                                !EquipmentClass.isArmor(equipment.getOffHand()) &&
                                EquipmentClass.getClass(equipment.getOffHand()) != EquipmentClass.TRINKET
                ){
                    equipment.setOffHandEnchantments(CustomEnchantmentManager.getInstance().getCurrentEnchantments(equipment.getOffHand()));
                }
            }
            if (getAttributes){
                if (!Utils.isItemEmptyOrNull(equipment.getHelmet())) equipment.setHelmetAttributes(ItemAttributesManager.getInstance().getCurrentStats(equipment.getHelmet()));
                if (!Utils.isItemEmptyOrNull(equipment.getChestplate())) equipment.setChestplateAttributes(ItemAttributesManager.getInstance().getCurrentStats(equipment.getChestplate()));
                if (!Utils.isItemEmptyOrNull(equipment.getLeggings())) equipment.setLeggingsAttributes(ItemAttributesManager.getInstance().getCurrentStats(equipment.getLeggings()));
                if (!Utils.isItemEmptyOrNull(equipment.getBoots())) equipment.setBootsAttributes(ItemAttributesManager.getInstance().getCurrentStats(equipment.getBoots()));
                if (
                        !Utils.isItemEmptyOrNull(equipment.getMainHand()) &&
                                !EquipmentClass.isArmor(equipment.getMainHand()) &&
                                EquipmentClass.getClass(equipment.getMainHand()) != EquipmentClass.TRINKET
                ){
                    equipment.setMainHandAttributes(ItemAttributesManager.getInstance().getCurrentStats(equipment.getMainHand()));
                }
                if (
                        !Utils.isItemEmptyOrNull(equipment.getOffHand()) &&
                                !EquipmentClass.isArmor(equipment.getOffHand()) &&
                                EquipmentClass.getClass(equipment.getOffHand()) != EquipmentClass.TRINKET
                ){
                    equipment.setOffHandAttributes(ItemAttributesManager.getInstance().getCurrentStats(equipment.getOffHand()));
                }
            }
        }
        if (ValhallaMMO.isTrinketsHooked()){
            if (entity instanceof Player){
                Map<Integer, ItemStack> trinkets = TrinketsManager.getInstance().getTrinketInventory((Player) entity);
                equipment.getMiscEquipment().addAll(trinkets.values());
                if (getEnchantments){
                    for (ItemStack i : trinkets.values())
                        equipment.getMiscEquipmentEnchantments().put(i, CustomEnchantmentManager.getInstance().getCurrentEnchantments(i));
                }
                if (getAttributes){
                    for (ItemStack i : trinkets.values())
                        equipment.getMiscEquipmentAttributes().put(i, ItemAttributesManager.getInstance().getCurrentStats(i));
                }
            }
        }
        return equipment;
    }

    public enum EntityClassification{
        ALIVE("ALLAY", "AXOLOTL", "BAT", "BEE", "BLAZE", "CAT", "CAVE_SPIDER", "CHICKEN", "COD", "COW", "CREEPER",
                "DOLPHIN", "DONKEY", "ELDER_GUARDIAN", "ENDER_DRAGON", "ENDERMAN", "ENDERMITE", "EVOKER", "FOX",
                "FROG", "GHAST", "GLOW_SQUID", "GOAT", "GUARDIAN", "HOGLIN", "HORSE", "ILLUSIONER", "IRON_GOLEM",
                "LLAMA", "MAGMA_CUBE", "MULE", "MUSHROOM_COW", "OCELOT", "PANDA", "PARROT", "PIG", "PIGLIN",
                "PIGLIN_BRUTE", "PILLAGER", "PLAYER", "POLAR_BEAR", "PUFFERFISH", "RABBIT", "RAVAGER", "SALMON",
                "SHEEP", "SHULKER", "SILVERFISH", "SLIME", "SNOWMAN", "SPIDER", "SQUID", "STRIDER", "TADPOLE",
                "TRADER_LLAMA", "TROPICAL_FISH", "TURTLE", "VEX", "VILLAGER", "VINDICATOR", "WANDERING_TRADER",
                "WARDEN", "WITCH", "WOLF"), // living entities
        UNALIVE("AREA_EFFECT_CLOUD", "ARMOR_STAND", "ARROW", "BOAT", "CHEST_BOAT", "DRAGON_FIREBALL", "DROPPED_ITEM",
                "EGG", "ENDER_CRYSTAL", "ENDER_PEARL", "ENDER_SIGNAL", "EVOKER_FANGS", "EXPERIENCE_ORB", "FALLING_BLOCK",
                "FIREBALL", "FIREWORK", "FISHING_HOOK", "GLOW_ITEM_FRAME", "ITEM_FRAME", "LEASH_HITCH", "LIGHTNING",
                "LLAMA_SPIT", "MARKER", "MINECART", "MINECART_CHEST", "MINECART_COMMAND", "MINECART_FURNACE",
                "MINECART_HOPPER", "MINECART_MOB_SPAWNER", "MINECART_TNT", "PAINTING", "PRIMED_TNT", "SHULKER_BULLET",
                "SMALL_FIREBALL", "SNOWBALL", "SPECTRAL_ARROW", "SPLASH_POTION", "THROWN_EXP_BOTTLE", "TRIDENT",
                "WITHER_SKULL"), // entities that were never alive (e.g. POTION_EFFECT_CLOUD)
        UNDEAD("DROWNED", "GIANT", "HUSK", "PHANTOM", "SKELETON", "SKELETON_HORSE", "STRAY", "WITHER",
                "WITHER_SKELETON", "ZOGLIN", "ZOMBIE", "ZOMBIE_HORSE", "ZOMBIE_VILLAGER", "ZOMBIFIED_PIGLIN"), // undead entities
        SCULK("WARDEN"), // sculk entities
        ARTHROPOD("BEE", "CAVE_SPIDER", "ENDERMITE", "SILVERFISH", "SPIDER"), // arthropod entities
        HOSTILE("BLAZE", "CAVE_SPIDER", "CREEPER", "DROWNED", "ELDER_GUARDIAN", "ENDER_DRAGON", "ENDERMITE",
                "EVOKER", "GHAST", "GUARDIAN", "HOGLIN", "HUSK", "ILLUSIONER", "MAGMA_CUBE", "PHANTOM", "PIGLIN_BRUTE",
                "PILLAGER", "RAVAGER", "SHULKER", "SILVERFISH", "SKELETON", "SLIME", "SPIDER", "STRAY", "VEX",
                "VINDICATOR", "WARDEN", "WITCH", "WITHER", "WITHER_SKELETON", "ZOGLIN", "ZOMBIE", "ZOMBIE_VILLAGER"), // hostile entities
        NEUTRAL("BEE", "CAVE_SPIDER", "ENDERMAN", "GOAT", "IRON_GOLEM", "LLAMA", "PANDA", "PIGLIN", "POLAR_BEAR",
                "SNOWMAN", "SPIDER", "TRADER_LLAMA", "WOLF", "ZOMBIFIED_PIGLIN"), // neutral entities
        PASSIVE("ALLAY", "AXOLOTL", "BAT", "CAT", "CHICKEN", "COD", "COW", "DOLPHIN", "DONKEY", "FOX", "FROG",
                "GLOW_SQUID", "HORSE", "MULE", "MUSHROOM_COW", "OCELOT", "PARROT", "PIG", "POLAR_BEAR", "PUFFERFISH",
                "RABBIT", "SALMON", "SHEEP", "SKELETON_HORSE", "SQUID", "STRIDER", "TADPOLE", "TROPICAL_FISH",
                "TURTLE", "VILLAGER", "WANDERING_TRADER", "ZOMBIE_HORSE"), // passive entities
        FRIENDLY("ALLAY", "AXOLOTL", "CAT", "DOLPHIN", "DONKEY", "FOX", "HORSE", "LLAMA", "MULE", "PARROT",
                "PIGLIN", "SKELETON_HORSE", "SNOWMAN", "TRADER_LLAMA", "WOLF", "ZOMBIE_HORSE"), // friendly/tameable entities (e.g. ALLAY, WOLF, PARROT)
        VILLAGER("VILLAGER", "WANDERING_TRADER"), // villagers
        ILLAGER("EVOKER", "ILLUSIONER", "PILLAGER", "RAVAGER", "VEX", "VINDICATOR", "WITCH"), // illagers
        ANIMAL("AXOLOTL", "BAT", "BEE", "CAT", "CHICKEN", "COD", "COW", "DOLPHIN", "DONKEY", "FOX", "FROG",
                "GLOW_SQUID", "GOAT", "HORSE", "LLAMA", "MULE", "MUSHROOM_COW", "OCELOT", "PANDA", "PARROT", "PIG",
                "POLAR_BEAR", "PUFFERFISH", "RABBIT", "SALMON", "SHEEP", "SKELETON_HORSE", "SQUID", "STRIDER",
                "TADPOLE", "TRADER_LLAMA", "TROPICAL_FISH", "TURTLE", "WOLF", "ZOMBIE_HORSE"), // passive animals
        OVERWORLD_NATIVE("ALLAY", "AXOLOTL", "BAT", "BEE", "CAT", "CAVE_SPIDER", "CHICKEN", "COD", "COW", "CREEPER",
                "DOLPHIN", "DONKEY", "DROWNED", "ELDER_GUARDIAN", "EVOKER", "FOX", "FROG", "GOAT", "GUARDIAN",
                "HORSE", "HUSK", "IRON_GOLEM", "LLAMA", "MULE", "MUSHROOM_COW", "OCELOT", "PANDA", "PARROT",
                "PHANTOM", "PIG", "PILLAGER", "PUFFERFISH", "RABBIT", "RAVAGER", "SALMON", "SHEEP", "SILVERFISH",
                "SKELETON", "SKELETON_HORSE", "SLIME", "SPIDER", "SQUID", "STRAY", "TADPOLE", "TRADER_LLAMA",
                "TROPICAL_FISH", "TURTLE", "VEX", "VILLAGER", "VINDICATOR", "WANDERING_TRADER", "WARDEN", "WITCH",
                "WOLF", "ZOMBIE", "ZOMBIE_VILLAGER"), // creatures that spawn in the overworld
        NETHER_NATIVE("BLAZE", "GHAST", "HOGLIN", "MAGMA_CUBE", "PIGLIN", "PIGLIN_BRUTE", "SKELETON", "STRIDER",
                "WITHER_SKELETON", "ZOMBIFIED_PIGLIN"), // creatures that spawn in the nether
        END_NATIVE("ENDER_DRAGON", "ENDERMAN", "ENDERMITE", "SHULKER"), // creatures that spawn in the end
        BOSS("ELDER_GUARDIAN", "ENDER_DRAGON", "EVOKER", "WARDEN", "WITHER"), // (mini) bosses
        AQUATIC("AXOLOTL", "COD", "DOLPHIN", "DROWNED", "ELDER_GUARDIAN", "FROG", "GUARDIAN", "PUFFERFISH",
                "SALMON", "SQUID", "TADPOLE", "TROPICAL_FISH"), // waterborn entities
        AIRBORN("ALLAY", "BAT", "BEE", "BLAZE", "ENDER_DRAGON", "GHAST", "PHANTOM", "VEX", "WITHER"), // airborn entities
        PROJECTILE("ARROW", "DRAGON_FIREBALL", "EGG", "ENDER_PEARL", "ENDER_SIGNAL", "FIREBALL", "FIREWORK",
                "LLAMA_SPIT", "SHULKER_BULLET", "SMALL_FIREBALL", "SNOWBALL", "SPECTRAL_ARROW", "SPLASH_POTION",
                "THROWN_EXP_BOTTLE", "TRIDENT", "WITHER_SKULL"), // projectiles
        RIDEABLE("BOAT", "CHEST_BOAT", "DONKEY", "HORSE", "LLAMA", "MINECART", "MULE", "SKELETON_HORSE",
                "STRIDER", "TRADER_LLAMA", "ZOMBIE_HORSE"), // entities that are naturally rideable by player (not through commands/API)
        STRUCTURE("ARMOR_STAND", "ENDER_CRYSTAL", "GLOW_ITEM_FRAME", "ITEM_FRAME", "PAINTING"), // structural entities (e.g. ENDER_CRYSTAL or ARMOR_STAND)
        UNNATURAL("GIANT", "ILLUSIONER", "ZOMBIE_HORSE"), // entities that do not spawn (e.g. GIANT or ILLUSIONER)
        BUILDABLE("IRON_GOLEM", "SNOWMAN", "WITHER"), // entities that can be spawned through structures (e.g. SNOW_GOLEM or IRON_GOLEM)
        EXPLODABLE("CREEPER", "DRAGON_FIREBALL", "ENDER_CRYSTAL", "FIREBALL", "FIREWORK", "MINECART_TNT",
                "PRIMED_TNT"), // entities that are capable of exploding
        OTHER("EVOKER_FANGS", "EXPERIENCE_ORB", "FALLING_BLOCK", "FISHING_HOOK", "LEASH_HITCH", "LIGHTNING",
                "MARKER", "PLAYER", "UNKNOWN"),
        NOT_FOUND; // entities that do not belong to a specific classification
        private final Set<String> types;

        EntityClassification(String... type){
            types = new HashSet<>(Arrays.asList(type));
        }

        public Set<String> getTypes() {
            return types;
        }

        public static Set<EntityClassification> getMatchingClassifications(EntityType type){
            Set<EntityClassification> classifications = new HashSet<>();
            for (EntityClassification classification : values()){
                if (classification.getTypes().contains(type.toString())) classifications.add(classification);
            }
            if (classifications.isEmpty()) classifications.add(NOT_FOUND);
            return classifications;
        }

        public static boolean isMatchingClassification(EntityType type, EntityClassification classification){
            return classification.getTypes().contains(type.toString());
        }
    }
}
