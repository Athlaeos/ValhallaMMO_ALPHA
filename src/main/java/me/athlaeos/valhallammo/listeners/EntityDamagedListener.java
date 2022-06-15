package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.CombatType;
import me.athlaeos.valhallammo.dom.Scaling;
import me.athlaeos.valhallammo.dom.WeaponType;
import me.athlaeos.valhallammo.events.PlayerEnterCombatEvent;
import me.athlaeos.valhallammo.events.PlayerLeaveCombatEvent;
import me.athlaeos.valhallammo.items.attributewrappers.AttributeWrapper;
import me.athlaeos.valhallammo.loottables.ChancedEntityLootTable;
import me.athlaeos.valhallammo.loottables.LootManager;
import me.athlaeos.valhallammo.loottables.chance_based_entity_loot.GlobalChancedEntityLootTable;
import me.athlaeos.valhallammo.managers.*;
import me.athlaeos.valhallammo.skills.CombatSkill;
import me.athlaeos.valhallammo.skills.OffensiveSkill;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.utility.EntityUtils;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.EntityEffect;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.*;

public class EntityDamagedListener implements Listener {
    private double tridentThrown;
    private double tridentThrownLoyal;
    private GlobalChancedEntityLootTable lootTable = null;
    private static EntityDamagedListener listener;
    private static Scaling physicalDamageScaling;
    private static double physicalDamageReductionCap;
    private static boolean physicalDamageScalingSetMode;
    private final double cos_facing_angle;
    private final boolean prevent_dodge_not_facing_attacker;
    private EntityDamageEvent.DamageCause reflect_damage_type;
    private static final Collection<EntityDamageEvent.DamageCause> physicalDamageCauses = new HashSet<>();
    private static Sound parrySound = Sound.BLOCK_IRON_TRAPDOOR_OPEN;
    private static Sound parrySuccessSound = Sound.ENTITY_ITEM_BREAK;
    private static Sound parryFailedSound = Sound.ITEM_SHIELD_BREAK;
    private static int combat_time_frame;
    private static boolean combat_mobs_only;

    private final Collection<PotionEffectHolder> parryEnemyDebuffs = new HashSet<>();
    private final Collection<PotionEffectHolder> parryFailedDebuffs = new HashSet<>();
    private final boolean parry_sparks;

    private static final Map<UUID, Entity> lastDamagedByMap = new HashMap<>();
    private static final Map<UUID, Double> lastDamageTakenMap = new HashMap<>();

    private final List<String> bleeding_death_messages_player;
    private final List<String> bleeding_death_messages;

    public EntityDamagedListener(){
        listener = this;
        bleeding_death_messages_player = TranslationManager.getInstance().getList("bleeding_death_messages_player");
        bleeding_death_messages = TranslationManager.getInstance().getList("bleeding_death_messages");

        YamlConfiguration config = ConfigManager.getInstance().getConfig("config.yml").get();
        try {
            parrySound = Sound.valueOf(config.getString("parry_sound"));
        } catch (IllegalArgumentException ignored){
            ValhallaMMO.getPlugin().getServer().getLogger().warning("Invalid parry sound type given");
        }
        try {
            parryFailedSound = Sound.valueOf(config.getString("parry_failed_sound"));
        } catch (IllegalArgumentException ignored){
            ValhallaMMO.getPlugin().getServer().getLogger().warning("Invalid parry fail sound type given");
        }
        try {
            parrySuccessSound = Sound.valueOf(config.getString("parry_success_sound"));
        } catch (IllegalArgumentException ignored){
            ValhallaMMO.getPlugin().getServer().getLogger().warning("Invalid parry success sound type given");
        }
        parry_sparks = config.getBoolean("parry_sparks");
        combat_time_frame = config.getInt("combat_time_frame");
        combat_mobs_only = config.getBoolean("combat_mobs_only");

        tridentThrown = config.getDouble("trident_damage_ranged");
        tridentThrownLoyal = config.getDouble("trident_damage_ranged_loyalty");
        ChancedEntityLootTable table = LootManager.getInstance().getChancedEntityLootTables().get("global_entities");
        if (table instanceof GlobalChancedEntityLootTable){
            lootTable = (GlobalChancedEntityLootTable) table;
        }

        String scaling = config.getString("damage_formula_physical", "");
        physicalDamageScalingSetMode = config.getString("damage_formula_mode", "").equalsIgnoreCase("set");
        physicalDamageReductionCap = config.getDouble("damage_reduction_cap");
        physicalDamageScaling = new Scaling(scaling, null, 0, 0, false, false);
        double facing_angle = config.getDouble("facing_angle");
        cos_facing_angle = Math.cos(facing_angle);
        prevent_dodge_not_facing_attacker = config.getBoolean("prevent_dodge_not_facing_attacker");
        try {
            reflect_damage_type = EntityDamageEvent.DamageCause.valueOf(config.getString("reflect_damage_type"));
        } catch (IllegalArgumentException ignored){
            ValhallaMMO.getPlugin().getServer().getLogger().warning("Invalid reflect damage type given");
            reflect_damage_type = null;
        }
        for (String s : config.getStringList("armor_effective_types")){
            try {
                physicalDamageCauses.add(EntityDamageEvent.DamageCause.valueOf(s));
            } catch (IllegalArgumentException ignored){
                ValhallaMMO.getPlugin().getServer().getLogger().warning("Could not register " + s + " as armor effective damage causes");
            }
        }

        ConfigurationSection parryEnemyDebuffs = config.getConfigurationSection("parry_enemy_debuffs");
        if (parryEnemyDebuffs != null){
            for (String effect : parryEnemyDebuffs.getKeys(false)){
                try {
                    PotionEffectType vanillaEffect = PotionEffectType.getByName(effect);
                    if (vanillaEffect == null && !effect.equals("STUN")){
                        me.athlaeos.valhallammo.dom.PotionEffect customEffect = PotionEffectManager.getInstance().getBasePotionEffect(effect);
                        if (customEffect == null){
                            ValhallaMMO.getPlugin().getServer().getLogger().warning("Could not register Parry Enemy Debuff potion effect " + effect + ", either it's not a valid potion effect or its argument is not a number!");
                            continue;
                        }
                    }
                    this.parryEnemyDebuffs.add(new PotionEffectHolder(effect, config.getInt("parry_enemy_debuffs." + effect)));
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getServer().getLogger().warning("Could not register Parry Enemy Debuff potion effect " + effect + ", either it's not a valid potion effect or its argument is not a number!");
                }
            }
        }

        ConfigurationSection parryFailedDebuffs = config.getConfigurationSection("parry_failed_debuffs");
        if (parryFailedDebuffs != null){
            for (String effect : parryFailedDebuffs.getKeys(false)){
                try {
                    PotionEffectType vanillaEffect = PotionEffectType.getByName(effect);
                    if (vanillaEffect == null && !effect.equals("STUN")){
                        me.athlaeos.valhallammo.dom.PotionEffect customEffect = PotionEffectManager.getInstance().getBasePotionEffect(effect);
                        if (customEffect == null){
                            ValhallaMMO.getPlugin().getServer().getLogger().warning("Could not register Parry Failed Debuff potion effect " + effect + ", either it's not a valid potion effect or its argument is not a number!");
                            continue;
                        }
                    }
                    this.parryFailedDebuffs.add(new PotionEffectHolder(effect, config.getInt("parry_failed_debuffs." + effect)));
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getServer().getLogger().warning("Could not register Parry Failed Debuff potion effect " + effect + ", either it's not a valid potion effect or its argument is not a number!");
                }
            }
        }
    }

    public static EntityDamagedListener getListener() {
        return listener;
    }

    public void reload(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("config.yml").get();
        tridentThrown = config.getDouble("trident_damage_ranged");
        tridentThrownLoyal = config.getDouble("trident_damage_ranged_loyalty");

        String scaling = config.getString("damage_formula_physical", "");
        physicalDamageScalingSetMode = config.getString("damage_formula_mode", "").equalsIgnoreCase("set");
        physicalDamageReductionCap = config.getDouble("damage_reduction_cap");
        physicalDamageScaling = new Scaling(scaling, null, 0, 0, false, false);
    }

    public static double getCustomDamage(EntityDamageEvent e){
        Entity lastDamagedBy = null;
        if (Arrays.asList("ENTITY_ATTACK", "ENTITY_SWEEP_ATTACK", "PROJECTILE", "ENTITY_EXPLOSION").contains(e.getCause().toString())){
            lastDamagedBy = lastDamagedByMap.get(e.getEntity().getUniqueId());
        } else {
            lastDamagedByMap.remove(e.getEntity().getUniqueId());
        }
        if (e.getEntity() instanceof LivingEntity){
            double resistance = 0;
            switch (e.getCause().toString()){
                case "FIRE": case "LAVA": case "MELTING": case "FIRE_TICK": case "HOT_FLOOR": case "DRYOUT": {
                    resistance = AccumulativeStatManager.getInstance().getStats("FIRE_RESISTANCE", e.getEntity(), lastDamagedBy, true);
                    break;
                }
                case "MAGIC": case "THORNS": case "LIGHTNING": case "DRAGON_BREATH": case "FREEZE": {
                    resistance = AccumulativeStatManager.getInstance().getStats("MAGIC_RESISTANCE", e.getEntity(), lastDamagedBy, true);
                    break;
                }
                case "PROJECTILE": {
                    resistance = AccumulativeStatManager.getInstance().getStats("PROJECTILE_RESISTANCE", e.getEntity(), lastDamagedBy, true);
                    break;
                }
                case "ENTITY_EXPLOSION": case "BLOCK_EXPLOSION": {
                    resistance = AccumulativeStatManager.getInstance().getStats("EXPLOSION_RESISTANCE", e.getEntity(), lastDamagedBy, true);
                    break;
                }
                case "POISON": case "WITHER": {
                    resistance = AccumulativeStatManager.getInstance().getStats("POISON_RESISTANCE", e.getEntity(), lastDamagedBy, true);
                    break;
                }
                case "FALL": case "FLY_INTO_WALL": {
                    resistance = AccumulativeStatManager.getInstance().getStats("FALLING_RESISTANCE", e.getEntity(), lastDamagedBy, true);
                    break;
                }
                case "CONTACT": case "ENTITY_ATTACK": case "ENTITY_SWEEP_ATTACK":{
                    resistance = AccumulativeStatManager.getInstance().getStats("MELEE_RESISTANCE", e.getEntity(), lastDamagedBy, true);
                    break;
                }
            }
            double damage = e.getDamage();
            e.setDamage(Math.max(0, damage * (1 - resistance)));

            if (e.getCause() != EntityDamageEvent.DamageCause.VOID){
                double addedResistance = AccumulativeStatManager.getInstance().getStats("DAMAGE_RESISTANCE", e.getEntity(), lastDamagedBy, true);
                e.setDamage(Math.max(0, e.getDamage() * (1 - addedResistance)));
            }
            if (e.getDamage() < 0) e.setDamage(0);
        } else return e.getDamage();

        double damageWithResistances = e.getDamage();
        if (physicalDamageCauses.contains(e.getCause())){
            double lightArmor = Math.max(0, AccumulativeStatManager.getInstance().getStats("LIGHT_ARMOR", e.getEntity(), lastDamagedBy,true));
            double lightArmorFlatPenetration = AccumulativeStatManager.getInstance().getStats("LIGHT_ARMOR_FLAT_IGNORED", e.getEntity(), lastDamagedBy, true);
            double heavyArmor = Math.max(0, AccumulativeStatManager.getInstance().getStats("HEAVY_ARMOR", e.getEntity(), lastDamagedBy, true));
            double heavyArmorFlatPenetration = AccumulativeStatManager.getInstance().getStats("HEAVY_ARMOR_FLAT_IGNORED", e.getEntity(), lastDamagedBy, true);
            double nonEquipmentArmor = Math.max(0, AccumulativeStatManager.getInstance().getStats("NON_EQUIPMENT_ARMOR", e.getEntity(), lastDamagedBy, true));
            double armorFlatPenetration = AccumulativeStatManager.getInstance().getStats("ARMOR_FLAT_IGNORED", e.getEntity(), lastDamagedBy, true);

            double lightArmorMultiplier = Math.max(0, AccumulativeStatManager.getInstance().getStats("LIGHT_ARMOR_MULTIPLIER", e.getEntity(), lastDamagedBy, true));
            double lightArmorFractionPenetration = AccumulativeStatManager.getInstance().getStats("LIGHT_ARMOR_FRACTION_IGNORED", e.getEntity(), lastDamagedBy, true);
            double heavyArmorMultiplier = Math.max(0, AccumulativeStatManager.getInstance().getStats("HEAVY_ARMOR_MULTIPLIER", e.getEntity(), lastDamagedBy, true));
            double heavyArmorFractionPenetration = AccumulativeStatManager.getInstance().getStats("HEAVY_ARMOR_FRACTION_IGNORED", e.getEntity(), lastDamagedBy, true);
            double armorMultiplierBonus = Math.max(0, AccumulativeStatManager.getInstance().getStats("ARMOR_MULTIPLIER_BONUS", e.getEntity(), lastDamagedBy, true));
            double armorFractionPenetration = AccumulativeStatManager.getInstance().getStats("ARMOR_FRACTION_IGNORED", e.getEntity(), lastDamagedBy, true);

            double totalLightArmor = Math.max(0, (lightArmor * lightArmorMultiplier) * (1 - lightArmorFractionPenetration) - lightArmorFlatPenetration);
            double totalHeavyArmor = Math.max(0, (heavyArmor * heavyArmorMultiplier) * (1 - heavyArmorFractionPenetration) - heavyArmorFlatPenetration);

            double totalArmor = Math.max(0, ((totalLightArmor + totalHeavyArmor + nonEquipmentArmor) * (1 + armorMultiplierBonus)) * (1 - armorFractionPenetration) - armorFlatPenetration);
            double toughness = Math.max(0, AccumulativeStatManager.getInstance().getStats("TOUGHNESS", e.getEntity(), lastDamagedBy, true));

            double scalingResult = Utils.eval(physicalDamageScaling.getScaling()
                    .replace("%damage%", "" + Utils.round(damageWithResistances, 3))
                    .replace("%armor%", "" + Utils.round(totalArmor, 3))
                    .replace("%toughness%", "" + Utils.round(toughness, 3)));

            if (physicalDamageScalingSetMode){
                double minimumDamage = damageWithResistances * physicalDamageReductionCap;
                return Math.max(minimumDamage, scalingResult);
            } else {
                return damageWithResistances * Math.max(physicalDamageReductionCap, scalingResult);
            }
        } else {
            return damageWithResistances;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleNewDamageFormula(EntityDamageEvent e){
        if (e.getCause() == EntityDamageEvent.DamageCause.CUSTOM || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK){
            if (PotionEffectManager.getInstance().getBleedTicks().contains(e.getEntity().getUniqueId())){
                // can only occur if the entity died of bleeding, shouldn't do anything else then
                return;
            }
        }
        if (e.isCancelled() || e.getFinalDamage() == 0) {
            return;
        }
        if (e.getEntity() instanceof LivingEntity){
            if (((LivingEntity) e.getEntity()).getNoDamageTicks() > 0) {
                return;
            }
            double customDamage = Math.max(0, getCustomDamage(e));
            if (((LivingEntity) e.getEntity()).getHealth() - customDamage + 0.0001 > 0){
                Entity lastDamagedBy = null;
                double fractionIFrameBonus = 0;
                if (Arrays.asList("THORNS", "ENTITY_ATTACK", "ENTITY_SWEEP_ATTACK", "PROJECTILE", "ENTITY_EXPLOSION").contains(e.getCause().toString())){
                    lastDamagedBy = lastDamagedByMap.get(e.getEntity().getUniqueId());
                    fractionIFrameBonus = AccumulativeStatManager.getInstance().getStats("IMMUNITY_FRAME_MULTIPLIER", e.getEntity(), lastDamagedBy, true);
                }

                e.setDamage(0.0001); // a little bit of damage is apparently required to be able to damage equipment
                ((LivingEntity) e.getEntity()).setHealth(Math.max(0, ((LivingEntity) e.getEntity()).getHealth() - customDamage + 0.0001));
                int iFrames = 10 + (int) AccumulativeStatManager.getInstance().getStats("IMMUNITY_FRAME_BONUS", e.getEntity(), lastDamagedBy, true);
                iFrames = Math.max(0, (int) (iFrames * (1 + fractionIFrameBonus)));
                final int finalIFrames = iFrames;
                ValhallaMMO.getPlugin().getServer().getScheduler().runTaskLater(ValhallaMMO.getPlugin(),
                        () -> ((LivingEntity) e.getEntity()).setNoDamageTicks(finalIFrames), 1);
                PotionEffectManager.getInstance().getBleedTicks().remove(e.getEntity().getUniqueId());
            } else {
                if (e.getEntity() instanceof Player){
                    ItemDamageListener.getExcludeEquipmentDamage().add(e.getEntity().getUniqueId());
                }
                e.getEntity().setLastDamageCause(e);
                ((LivingEntity) e.getEntity()).setHealth(0.0001);
//                e.setCancelled(true);
//                e.getEntity().setLastDamageCause(null);
                if (PotionEffectManager.getInstance().getBleedTicks().contains(e.getEntity().getUniqueId())){
                    e.getEntity().setLastDamageCause(e);
                    LivingEntity cause = PotionEffectManager.getInstance().getBleedingCause((LivingEntity) e.getEntity());
                    if (cause != null){
                        ((LivingEntity) e.getEntity()).damage(e.getDamage(), cause);
                    } else {
                        ((LivingEntity) e.getEntity()).damage(e.getDamage());
                    }
                    e.setCancelled(true);
                }
            }
            lastDamageTakenMap.put(e.getEntity().getUniqueId(), customDamage);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onIncreasedRangeAttack(PlayerInteractEvent e){
        if (e.useItemInHand() == Event.Result.DENY) return;
        if (e.getAction() == Action.LEFT_CLICK_AIR && e.getHand() == EquipmentSlot.HAND){
            AttributeInstance damageInstance = e.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
            if (damageInstance != null){
                double reach = AccumulativeStatManager.getInstance().getStats("ATTACK_REACH_BONUS", e.getPlayer(), true);
                if (reach <= 0) return;
                RayTraceResult rayTrace = e.getPlayer().getWorld().rayTrace(e.getPlayer().getEyeLocation(),
                        e.getPlayer().getEyeLocation().getDirection(), 2.9 + reach, FluidCollisionMode.NEVER, true, 0.1, (entity) -> !entity.equals(e.getPlayer()));
                if (rayTrace != null){
                    Entity hitEntity = rayTrace.getHitEntity();
                    if (hitEntity != null){
                        if (hitEntity instanceof LivingEntity){
                            double baseDamage = damageInstance.getValue();
                            if (e.getPlayer().getFallDistance() > 0) {
                                baseDamage *= 1.5;
                                e.getPlayer().spawnParticle(Particle.CRIT, hitEntity.getLocation().add(0, 0.75, 0),
                                        15);
                                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT,
                                        1, 1);
                            }
                            double cooldownMultiplier = EntityUtils.getDamageMultiplierFromAttackCooldown(e.getPlayer().getAttackCooldown());
                            baseDamage *= cooldownMultiplier;

                            ItemStack weapon = e.getPlayer().getInventory().getItemInMainHand();
                            double enchantmentDamage = 0;
                            if (!Utils.isItemEmptyOrNull(weapon)) {
                                ItemMeta weaponMeta = weapon.getItemMeta();
                                if (weaponMeta != null){
                                    if (weaponMeta.getAttributeModifiers(Attribute.GENERIC_ATTACK_DAMAGE) == null) {
                                        baseDamage--;
                                    }
                                }
                                int sharpnessLevel = weapon.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
                                if (sharpnessLevel > 0){
                                    enchantmentDamage += 1 + (0.5 * (sharpnessLevel - 1));
                                }
                                if (EntityUtils.EntityClassification.isMatchingClassification(hitEntity.getType(), EntityUtils.EntityClassification.UNDEAD)){
                                    enchantmentDamage += (weapon.getEnchantmentLevel(Enchantment.DAMAGE_UNDEAD) * 2.5);
                                }
                                if (EntityUtils.EntityClassification.isMatchingClassification(hitEntity.getType(), EntityUtils.EntityClassification.ARTHROPOD)){
                                    enchantmentDamage += (weapon.getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS) * 2.5);
                                }
                                enchantmentDamage *= EntityUtils.getEnchantmentDamageMultiplierFromAttackCooldown(e.getPlayer().getAttackCooldown());
                            }
                            ((LivingEntity) rayTrace.getHitEntity()).damage(baseDamage + enchantmentDamage, e.getPlayer());
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent e){
        double reach = AccumulativeStatManager.getInstance().getStats("ATTACK_REACH_BONUS", e.getDamager(), true);
        if (reach < 0 && e.getDamager() instanceof LivingEntity){
            RayTraceResult rayTrace = e.getDamager().getWorld().rayTrace(((LivingEntity) e.getDamager()).getEyeLocation(),
                    ((LivingEntity) e.getDamager()).getEyeLocation().getDirection(), 2.9 + reach, FluidCollisionMode.NEVER, true, 0.1, (entity) -> !entity.equals(e.getDamager()));
            if (rayTrace == null || rayTrace.getHitEntity() == null) {
                e.setCancelled(true);
                return;
            }
        }

        if (e.getDamager() instanceof LivingEntity && e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
            EntityEquipment equipment = ((LivingEntity) e.getDamager()).getEquipment();
            if (equipment != null){
                ItemStack mainHandItem = equipment.getItemInMainHand();
                if (!Utils.isItemEmptyOrNull(mainHandItem) && WeaponType.getWeaponType(mainHandItem) == WeaponType.HEAVY) {
                    e.setCancelled(true);
                    // makes sure swords re-typed as heavy weaponry can't sweep attack
                    return;
                }
            }
        }
        lastDamagedByMap.put(e.getEntity().getUniqueId(), e.getDamager());
        if (e.getCause() == EntityDamageEvent.DamageCause.CUSTOM || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK){
            if (PotionEffectManager.getInstance().getBleedTicks().contains(e.getEntity().getUniqueId())){
                // can only occur if the entity died of bleeding, shouldn't do anything else then
                return;
            }
        }
        if (!e.isCancelled() && e.getFinalDamage() > 0) {
            if (!(e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
                    || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK || e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE
                    || e.getCause() == EntityDamageEvent.DamageCause.THORNS)) {
                return;
            }
            if (e.getDamager() instanceof LivingEntity && e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK){
                double multiplier = 1 + AccumulativeStatManager.getInstance().getStats("MELEE_DAMAGE_DEALT", e.getEntity(), e.getDamager(), true);
                e.setDamage(e.getDamage() * multiplier);
            }
            if (e.getEntity() instanceof LivingEntity){
                double knockbackResistance = AccumulativeStatManager.getInstance().getStats("KNOCKBACK_RESISTANCE", e.getEntity(), e.getDamager(), true);
                double knockbackBonus = AccumulativeStatManager.getInstance().getStats("KNOCKBACK_BONUS", e.getEntity(), e.getDamager(),true);
                if (knockbackBonus < 0) knockbackResistance += -knockbackBonus;
                EntityUtils.addUniqueAttribute((LivingEntity) e.getEntity(), "valhalla_knockback_resistance_modifier", Attribute.GENERIC_KNOCKBACK_RESISTANCE, knockbackResistance, AttributeModifier.Operation.ADD_NUMBER);
                ValhallaMMO.getPlugin().getServer().getScheduler().runTaskLater(ValhallaMMO.getPlugin(),
                        () -> {
                            EntityUtils.removeUniqueAttribute((LivingEntity) e.getEntity(), "valhalla_knockback_resistance_modifier", Attribute.GENERIC_KNOCKBACK_RESISTANCE);
                            if (e.getDamager() instanceof LivingEntity && knockbackBonus > 0){
                                // Gets a vector of length 1 in the direction the player holding this item is looking
                                Vector lookingDirection = ((LivingEntity) e.getDamager()).getEyeLocation().getDirection().normalize();

                                //lookingDirection = lookingDirection.multiply(knockbackBonus);
                                lookingDirection.setX(lookingDirection.getX() * knockbackBonus);
                                lookingDirection.setY(0);
                                lookingDirection.setZ(lookingDirection.getZ() * knockbackBonus);

                                e.getEntity().setVelocity(e.getEntity().getVelocity().add(lookingDirection));
                            }
                        },
                        1L);

                boolean facing = EntityUtils.isEntityFacing((LivingEntity) e.getEntity(), e.getDamager().getLocation(), cos_facing_angle);
                if (!(!facing && prevent_dodge_not_facing_attacker)){
                    double dodgeChance = AccumulativeStatManager.getInstance().getStats("DODGE_CHANCE", e.getEntity(), e.getDamager(), true);
                    if (Utils.getRandom().nextDouble() < dodgeChance){
                        e.setCancelled(true);
                        return;
                    }
                }

                if (facing){
                    if (e.getDamager() instanceof LivingEntity && !CooldownManager.getInstance().isCooldownPassed(e.getEntity().getUniqueId(), "parry_effectiveness_frame")){
                        // parry successful
                        CooldownManager.getInstance().setCooldown(e.getEntity().getUniqueId(), 0, "parry_vulnerability_frame");
                        CooldownManager.getInstance().setCooldown(e.getEntity().getUniqueId(), 0, "parry_effectiveness_frame");
                        int parryDebuffDuration = (int) AccumulativeStatManager.getInstance().getStats("PARRY_ENEMY_DEBUFF_DURATION", e.getEntity(), e.getDamager(), true);
                        double damageReduction = AccumulativeStatManager.getInstance().getStats("PARRY_DAMAGE_REDUCTION", e.getEntity(), e.getDamager(), true);
                        for (PotionEffectHolder effect : parryEnemyDebuffs){
                            effect.applyPotionEffect((LivingEntity) e.getDamager(), parryDebuffDuration);
                        }
                        if (parry_sparks){
                            if (e.getEntity().getLocation().getWorld() != null){
                                e.getEntity().getLocation().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, e.getEntity().getLocation(), 10);
                            }
                        }
                        e.setDamage(e.getDamage() * (1 - damageReduction));
                        e.getEntity().getWorld().playSound(e.getEntity().getLocation(), parrySuccessSound, 1F, 1F);
                    } else if (!CooldownManager.getInstance().isCooldownPassed(e.getEntity().getUniqueId(), "parry_vulnerability_frame")){
                        // parry failed
                        CooldownManager.getInstance().setCooldown(e.getEntity().getUniqueId(), 0, "parry_vulnerability_frame");
                        int parryDebuffDuration = (int) AccumulativeStatManager.getInstance().getStats("PARRY_DEBUFF_DURATION", e.getEntity(), e.getDamager(), true);
                        for (PotionEffectHolder effect : parryFailedDebuffs){
                            effect.applyPotionEffect((LivingEntity) e.getDamager(), parryDebuffDuration);
                        }
                        e.getEntity().getWorld().playSound(e.getEntity().getLocation(), parryFailedSound, 1F, 1F);
                    }
                }

                Entity damager = e.getDamager();
                if (damager instanceof Projectile){
                    ProjectileSource shooter = ((Projectile) damager).getShooter();
                    if (shooter != null){
                        if (shooter instanceof Entity){
                            damager = (Entity) shooter;
                        }
                    }
                }

                if (e.getDamager() instanceof Trident) {
                    Trident t = (Trident) e.getDamager();
                    AttributeWrapper wrapper = ItemAttributesManager.getInstance().getAttributeWrapper(t.getItem(), "GENERIC_ATTACK_DAMAGE");
                    if (wrapper != null) {
                        if (t.getItem().getEnchantmentLevel(Enchantment.LOYALTY) == 0) {
                            e.setDamage(e.getDamage() * tridentThrown);
                        } else {
                            e.setDamage(e.getDamage() * tridentThrownLoyal);
                        }
                    }
                }

                double multiplier = 1F;
                if (damager instanceof Player && e.getEntity() instanceof Animals){
                    multiplier += AccumulativeStatManager.getInstance().getStats("FARMING_DAMAGE_ANIMAL_MULTIPLIER", e.getEntity(), damager, true) - 1;
                }
                if (e.getDamager() instanceof AbstractArrow && !(e.getDamager() instanceof Trident)){
                    multiplier += AccumulativeStatManager.getInstance().getStats("ARCHERY_DAMAGE", e.getEntity(), damager, true);
                }
                multiplier += AccumulativeStatManager.getInstance().getStats("DAMAGE_DEALT", e.getEntity(), damager, true);

                e.setDamage((e.getDamage() * Math.max(0, multiplier)));

                for (Skill skill : SkillProgressionManager.getInstance().getAllSkills().values()){
                    if (skill != null){
                        if (skill instanceof OffensiveSkill){
                            ((OffensiveSkill) skill).onEntityDamage(e);
                        }
                    }
                }

//                if (damager instanceof LivingEntity && !PotionEffectManager.getInstance().isBleeding((LivingEntity) e.getEntity())){
//                    double bleedChance = AccumulativeStatManager.getInstance().getStats("BLEED_CHANCE", e.getEntity(), e.getDamager(), true);
//                    boolean bleed = Utils.getRandom().nextDouble() < bleedChance;
//                    if (bleed){
//                        double bleedDamage = AccumulativeStatManager.getInstance().getStats("BLEED_DAMAGE", e.getEntity(), e.getDamager(), true);
//                        int bleedDuration = (int) AccumulativeStatManager.getInstance().getStats("BLEED_DURATION", e.getEntity(), e.getDamager(), true);
//                        PotionEffectManager.getInstance().bleedEntity((LivingEntity) e.getEntity(), (LivingEntity) damager, bleedDuration, bleedDamage);
//                    }
//                }

                if (e.getDamager() instanceof AbstractArrow && !(e.getDamager() instanceof Trident)){
                    if (((AbstractArrow) e.getDamager()).getShooter() instanceof LivingEntity){
                        ItemStack ammo = ItemUtils.getArrowFromEntity((AbstractArrow) e.getDamager());
                        if (ammo == null) return;
                        CustomArrowManager.getInstance().executeOnDamage(ammo, e);
                    }
                }

                if (e.getEntity() instanceof Player){
                    combatAction((Player) e.getEntity());
                } else if (damager instanceof Player && (!combat_mobs_only || e.getEntity() instanceof Monster)){
                    combatAction((Player) damager);
                }
                if (reflect_damage_type != null){
                    if (e.getCause() != reflect_damage_type){ // reflect damage should not be able to reflect again
                        if (damager instanceof LivingEntity){
                            double reflectChance = AccumulativeStatManager.getInstance().getStats("REFLECT_CHANCE", e.getEntity(), e.getDamager(), true);
                            if (Utils.getRandom().nextDouble() < reflectChance){
                                double reflectFraction = AccumulativeStatManager.getInstance().getStats("REFLECT_FRACTION", e.getEntity(), e.getDamager(), true);
                                double reflectDamage = e.getDamage() * reflectFraction;
                                if (reflectDamage > 0){
                                    EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(
                                            e.getEntity(), damager, reflect_damage_type, reflectDamage
                                    );
                                    ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(event);
                                    damager.playEffect(EntityEffect.THORNS_HURT);
                                    // calling the event alone should inflict the damage, because it causes the
                                    // damage with the custom damage system
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent e){
        if (PotionEffectManager.getInstance().getBleedTicks().contains(e.getEntity().getUniqueId())){
            PotionEffectManager.getInstance().getBleedTicks().remove(e.getEntity().getUniqueId());
            // Player most likely died from bleeding damage
            LivingEntity cause = PotionEffectManager.getInstance().getBleedingCause(e.getEntity());
            if (cause != null){
                if (bleeding_death_messages_player.isEmpty()) return;
                String message = bleeding_death_messages_player.get(Utils.getRandom().nextInt(bleeding_death_messages_player.size()));
                e.setDeathMessage(Utils.chat(message
                        .replace("%player%", e.getEntity().getName())
                        .replace("%killer%", cause.getName())));
            } else {
                if (bleeding_death_messages.isEmpty()) return;
                String message = bleeding_death_messages.get(Utils.getRandom().nextInt(bleeding_death_messages_player.size()));
                e.setDeathMessage(Utils.chat(message
                        .replace("%player%", e.getEntity().getName())));
            }
            PotionEffectManager.getInstance().removeBleeding(e.getEntity());
        }
    }

    @EventHandler(priority =EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent e){
        if (e.getEntity().getType() != EntityType.PLAYER){
            PotionEffectManager.getInstance().removeBleeding(e.getEntity());
        }
        Player killer = e.getEntity().getKiller();
        if (killer == null) return;
        List<ItemStack> newItems = new ArrayList<>();
        double dropMultiplier = 1 + AccumulativeStatManager.getInstance().getStats("ENTITY_DROP_MULTIPLIER_BONUS", e.getEntity(), killer, true);

        ItemUtils.multiplyItemStacks(e.getDrops(), newItems, dropMultiplier, false);

        for (Skill skill : SkillProgressionManager.getInstance().getAllSkills().values()){
            if (skill != null){
                if (skill instanceof OffensiveSkill){
                    ((OffensiveSkill) skill).onEntityKilled(e);
                }
            }
            newItems = new ArrayList<>(e.getDrops());
        }
        if (lootTable != null){
            if (!e.getDrops().isEmpty()){
                lootTable.onEntityKilled(e.getEntity(), newItems, 1);
            }
        }
        e.getDrops().clear();
        e.getDrops().addAll(newItems);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCombatEnter(PlayerEnterCombatEvent e){
        if (e.isCancelled()) return;
        for (Skill skill : SkillProgressionManager.getInstance().getAllSkills().values()){
            if (skill != null){
                if (skill instanceof CombatSkill){
                    ((CombatSkill) skill).onCombatEnter(e);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCombatEnter(PlayerLeaveCombatEvent e){
        if (e.isCancelled()) return;
        for (Skill skill : SkillProgressionManager.getInstance().getAllSkills().values()){
            if (skill != null){
                if (skill instanceof CombatSkill){
                    ((CombatSkill) skill).onCombatLeave(e);
                }
            }
        }
    }

    public static void parry(Player p, int activeFor, int vulnerableFor, int cooldown){
        CooldownManager.getInstance().setCooldown(p.getUniqueId(), activeFor, "parry_effectiveness_frame");
        CooldownManager.getInstance().setCooldown(p.getUniqueId(), vulnerableFor, "parry_vulnerability_frame");
        CooldownManager.getInstance().setCooldown(p.getUniqueId(), cooldown, "parry_cooldown");
        p.getWorld().playSound(p.getLocation(), parrySound, 1F, 1F);
    }

    public static Map<UUID, Double> getLastDamageTakenMap() {
        return lastDamageTakenMap;
    }

    public static Map<UUID, Entity> getLastDamagedByMap() {
        return lastDamagedByMap;
    }

    public static Map<UUID, CombatLog> playersInCombat = new HashMap<>();

    /**
     * This method should be called when a player experiences an event that would cause them to be in combat
     * If it's been more than 10 seconds since their last combat action their combat timer is reset
     * This method calls a PlayerEnterCombatEvent if the player was previously not in combat
     */
    public static void combatAction(Player who){
        playersInCombat.putIfAbsent(who.getUniqueId(), new CombatLog(who));
        playersInCombat.get(who.getUniqueId()).combatAction();
    }

    /**
     * This method should be called regularly to check if a player was previously in combat, but not any more.
     * If a player's isInCombat tag is false, nothing happens
     * If a player's isInCombat tag is true, and it's been more than 10 seconds since the player's last combat action,
     * a PlayerLeaveCombatEvent is called and their combat timer is updated to how long they were in combat for.
     * If a player's isInCombat tag is true, but it's been less than 10 seconds since the player's last combat action,
     * nothing happens.
     * @param who the player to update their status
     */
    public static void updateCombatStatus(Player who){
        CombatLog log = playersInCombat.get(who.getUniqueId());
        if (log != null) {
            log.checkPlayerLeftCombat();
        }
    }

    /**
     * returns the time the player with this log was in combat for, this number resets to 0 when the player re-enters
     * combat. to get the accurate time the player was in combat for, listen to a PlayerLeaveCombatEvent
     * @return the time in milliseconds the player was in combat for
     */
    public static long timePlayerInCombat(Player who){
        CombatLog log = playersInCombat.get(who.getUniqueId());
        if (log == null) return 0;
        return log.getTimeInCombat();
    }

    private static class CombatLog{
        private final Player who;
        private long timeLastStartedCombat = 0;
        private long timeLastCombatAction = 0;
        private boolean isInCombat = false;
        private long timeInCombat = 0;

        public CombatLog(Player who){
            this.who = who;
        }

        public void combatAction(){
            if (timeLastCombatAction + 10000 < System.currentTimeMillis()) {
                PlayerEnterCombatEvent event = new PlayerEnterCombatEvent(who);
                ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()){
                    timeLastStartedCombat = event.getWhen();
                    timeLastCombatAction = event.getWhen();
                    timeInCombat = 0;
                    isInCombat = true;
                }
            } else {
                timeInCombat += (System.currentTimeMillis() - timeLastCombatAction);
                timeLastCombatAction = System.currentTimeMillis();
            }
        }

        public void checkPlayerLeftCombat(){
            if (isInCombat){ // player was previously in combat
                if (timeLastCombatAction + combat_time_frame < System.currentTimeMillis()){
                    // player should no longer be considered in combat
                    PlayerLeaveCombatEvent event = new PlayerLeaveCombatEvent(who, timeLastStartedCombat, timeLastCombatAction + combat_time_frame);
                    ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        isInCombat = false;
                        timeInCombat = event.getTimeInCombat(timeLastStartedCombat);
                    }
                }
            }
        }

        /**
         * @return the current state of the log's isInCombat property
         */
        public boolean isInCombat() {
            return isInCombat;
        }

        public long getTimeInCombat(){
            return timeInCombat;
        }

        public long getTimeLastCombatAction() {
            return timeLastCombatAction;
        }

        public long getTimeLastStartedCombat() {
            return timeLastStartedCombat;
        }
    }

    private static class PotionEffectHolder{
        private final String baseType;
        private PotionEffectType vanillaType = null;
        private final double amplifier;

        public PotionEffectHolder(String type, double baseAmplifier){
            this.baseType = type;
            this.vanillaType = PotionEffectType.getByName(type);
            this.amplifier = baseAmplifier;
        }

        public void applyPotionEffect(LivingEntity p, int durationms){
            if (baseType.equals("STUN")) {
                PotionEffectManager.getInstance().stunTarget(p, CombatType.MELEE, durationms);
                return;
            }
            if (vanillaType != null){
                int amplifier = (int) Math.floor(this.amplifier);
                if (amplifier <= 0) return;
                PotionEffect potionEffectToAdd = new PotionEffect(vanillaType, (int) (durationms / 50D), amplifier, true, false, true);
                PotionEffect potionEffectToReplace = p.getPotionEffect(vanillaType);
                if (potionEffectToReplace != null){
                    if (potionEffectToReplace.getDuration() > potionEffectToAdd.getDuration()
                            || potionEffectToReplace.getAmplifier() > potionEffectToAdd.getAmplifier()) return;
                    // if either the player's existing potion effect duration or amplifier exceeds
                    // adrenaline's potion effect, it is not replaced.
                }
                p.addPotionEffect(potionEffectToAdd);
            } else {
                me.athlaeos.valhallammo.dom.PotionEffect customEffect = PotionEffectManager.getInstance().getBasePotionEffect(baseType);
                if (customEffect == null) return;
                PotionEffectManager.getInstance().addPotionEffect(p, new me.athlaeos.valhallammo.dom.PotionEffect(
                        baseType, durationms, amplifier, customEffect.getType(), customEffect.isRemovable()
                ), true, EntityPotionEffectEvent.Cause.PLUGIN, EntityPotionEffectEvent.Action.ADDED);
            }
        }
    }
}
