package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.Scaling;
import me.athlaeos.valhallammo.events.PlayerEnterCombatEvent;
import me.athlaeos.valhallammo.events.PlayerLeaveCombatEvent;
import me.athlaeos.valhallammo.items.attributewrappers.AttributeWrapper;
import me.athlaeos.valhallammo.loottables.ChancedEntityLootTable;
import me.athlaeos.valhallammo.loottables.LootManager;
import me.athlaeos.valhallammo.loottables.chance_based_entity_loot.GlobalChancedEntityLootTable;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.managers.CustomArrowManager;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.skills.CombatSkill;
import me.athlaeos.valhallammo.skills.OffensiveSkill;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.utility.EntityUtils;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.EntityEffect;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

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

    private static final Map<UUID, UUID> lastDamagedByMap = new HashMap<>();
    private static final Map<UUID, Double> lastDamageTakenMap = new HashMap<>();

    public EntityDamagedListener(){
        listener = this;
        YamlConfiguration config = ConfigManager.getInstance().getConfig("config.yml").get();
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
        double originalDamage = e.getDamage();
        if (physicalDamageCauses.contains(e.getCause())){
            double lightArmor = Math.max(0, AccumulativeStatManager.getInstance().getStats("LIGHT_ARMOR", e.getEntity(), true));
            double heavyArmor = Math.max(0, AccumulativeStatManager.getInstance().getStats("HEAVY_ARMOR", e.getEntity(), true));
            double lightArmorMultiplier = Math.max(0, AccumulativeStatManager.getInstance().getStats("LIGHT_ARMOR_MULTIPLIER", e.getEntity(), true));
            double heavyArmorMultiplier = Math.max(0, AccumulativeStatManager.getInstance().getStats("HEAVY_ARMOR_MULTIPLIER", e.getEntity(), true));
            double nonEquipmentArmor = Math.max(0, AccumulativeStatManager.getInstance().getStats("NON_EQUIPMENT_ARMOR", e.getEntity(), true));

            double totalArmor = (lightArmor * lightArmorMultiplier) + (heavyArmor * heavyArmorMultiplier) + nonEquipmentArmor;
            double toughness = Math.max(0, AccumulativeStatManager.getInstance().getStats("TOUGHNESS", e.getEntity(), true));

            double scalingResult = Utils.eval(physicalDamageScaling.getScaling()
                    .replace("%damage%", "" + Utils.round(originalDamage, 3))
                    .replace("%armor%", "" + Utils.round(totalArmor, 3))
                    .replace("%toughness%", "" + Utils.round(toughness, 3)));
            if (physicalDamageScalingSetMode){
                double minimumDamage = originalDamage * physicalDamageReductionCap;
                return Math.max(minimumDamage, scalingResult);
            } else {
                return originalDamage * Math.max(physicalDamageReductionCap, scalingResult);
            }
        } else {
            return originalDamage;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleNewDamageFormula(EntityDamageEvent e){
        if (e.isCancelled()) return;
        if (e.getEntity() instanceof LivingEntity){
            if (((LivingEntity) e.getEntity()).getNoDamageTicks() > 0) return;
            double customDamage = Math.max(0, getCustomDamage(e));
            ((LivingEntity) e.getEntity()).setHealth(Math.max(0, ((LivingEntity) e.getEntity()).getHealth() - customDamage));
            e.setDamage(0.0001); // a little bit of damage is apparently required to be able to damage equipment
            ((LivingEntity) e.getEntity()).setNoDamageTicks(10);
            lastDamageTakenMap.put(e.getEntity().getUniqueId(), customDamage);
        }
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamaged(EntityDamageEvent e){
        if (e.isCancelled()) return;
        if (e.getEntity() instanceof LivingEntity){
            switch (e.getCause().toString()){
                case "FIRE": case "LAVA": case "MELTING": case "FIRE_TICK": case "HOT_FLOOR": case "DRYOUT": {
                    double resistance = AccumulativeStatManager.getInstance().getStats("FIRE_RESISTANCE", e.getEntity(), true);
                    double damage = e.getDamage();
                    e.setDamage(calcDamageWithResistance(damage, resistance));
                    break;
                }
                case "MAGIC": case "THORNS": case "LIGHTNING": case "DRAGON_BREATH": case "FREEZE": {
                    double resistance = AccumulativeStatManager.getInstance().getStats("MAGIC_RESISTANCE", e.getEntity(), true);
                    double damage = e.getDamage();
                    e.setDamage(calcDamageWithResistance(damage, resistance));
                    break;
                }
                case "PROJECTILE": {
                    double resistance = AccumulativeStatManager.getInstance().getStats("PROJECTILE_RESISTANCE", e.getEntity(), true);
                    double damage = e.getDamage();
                    e.setDamage(calcDamageWithResistance(damage, resistance));
                    break;
                }
                case "ENTITY_EXPLOSION": case "BLOCK_EXPLOSION": {
                    double resistance = AccumulativeStatManager.getInstance().getStats("EXPLOSION_RESISTANCE", e.getEntity(), true);
                    double damage = e.getDamage();
                    e.setDamage(calcDamageWithResistance(damage, resistance));
                    break;
                }
                case "POISON": case "WITHER": {
                    double resistance = AccumulativeStatManager.getInstance().getStats("POISON_RESISTANCE", e.getEntity(), true);
                    double damage = e.getDamage();
                    e.setDamage(calcDamageWithResistance(damage, resistance));
                    break;
                }
                case "FALL": case "FLY_INTO_WALL": {
                    double resistance = AccumulativeStatManager.getInstance().getStats("FALLING_RESISTANCE", e.getEntity(), true);
                    double damage = e.getDamage();
                    e.setDamage(calcDamageWithResistance(damage, resistance));
                    break;
                }
                case "CONTACT": case "ENTITY_ATTACK": case "ENTITY_SWEEP_ATTACK":{
                    double resistance = AccumulativeStatManager.getInstance().getStats("MELEE_RESISTANCE", e.getEntity(), true);
                    double damage = e.getDamage();
                    e.setDamage(calcDamageWithResistance(damage, resistance));
                    break;
                }
            }
            if (e.getCause() != EntityDamageEvent.DamageCause.VOID){
                double resistance = AccumulativeStatManager.getInstance().getStats("DAMAGE_RESISTANCE", e.getEntity(), true);
                double damage = e.getDamage();
                e.setDamage(calcDamageWithResistance(damage, resistance));
            }
            if (e.getDamage() < 0) e.setDamage(0);
        }
    }

    private double calcDamageWithResistance(double damage, double resistance){
        return Math.max(0, damage * (1 - resistance));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent e){
        if (!e.isCancelled()) {
            if (e.getEntity() instanceof LivingEntity){
                double knockbackResistance = AccumulativeStatManager.getInstance().getStats("KNOCKBACK_RESISTANCE", e.getEntity(), true);

                AttributeInstance instance = ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
                if (instance != null){
                    for (AttributeModifier modifier : instance.getModifiers()){
                        if (modifier.getName().equals("valhalla_knockback_resistance_modifier")){
                            instance.removeModifier(modifier);
                            break;
                        }
                    }
                    if (knockbackResistance > 0){
                        instance.addModifier(
                                new AttributeModifier("valhalla_knockback_resistance_modifier", knockbackResistance, AttributeModifier.Operation.ADD_NUMBER)
                        );
                        new BukkitRunnable(){
                            @Override
                            public void run() {
                                AttributeInstance instance = ((LivingEntity) e.getEntity()).getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
                                if (instance != null){
                                    for (AttributeModifier modifier : instance.getModifiers()){
                                        if (modifier.getName().equals("valhalla_knockback_resistance_modifier")){
                                            instance.removeModifier(modifier);
                                            break;
                                        }
                                    }
                                }
                            }
                        }.runTaskLater(ValhallaMMO.getPlugin(), 1L);
                    }
                }

                boolean facing = EntityUtils.isEntityFacing((LivingEntity) e.getEntity(), e.getDamager().getLocation(), cos_facing_angle);
                if (!(!facing && prevent_dodge_not_facing_attacker)){
                    double dodgeChance = AccumulativeStatManager.getInstance().getStats("DODGE_CHANCE", e.getEntity(), e.getDamager(), true);
                    if (Utils.getRandom().nextDouble() < dodgeChance){
                        e.setCancelled(true);
                        return;
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

                lastDamagedByMap.put(e.getEntity().getUniqueId(), damager.getUniqueId());

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
                    multiplier += AccumulativeStatManager.getInstance().getStats("FARMING_DAMAGE_ANIMAL_MULTIPLIER", damager, true) - 1;
                }
                if (e.getDamager() instanceof AbstractArrow && !(e.getDamager() instanceof Trident)){
                    multiplier += AccumulativeStatManager.getInstance().getStats("ARCHERY_DAMAGE", damager, true);
                }
                multiplier += AccumulativeStatManager.getInstance().getStats("DAMAGE_DEALT", damager, true);

                e.setDamage((e.getDamage() * Math.max(0, multiplier)));

                for (Skill skill : SkillProgressionManager.getInstance().getAllSkills().values()){
                    if (skill != null){
                        if (skill instanceof OffensiveSkill){
                            ((OffensiveSkill) skill).onEntityDamage(e);
                        }
                    }
                }

                if (e.getDamager() instanceof AbstractArrow && !(e.getDamager() instanceof Trident)){
                    if (((AbstractArrow) e.getDamager()).getShooter() instanceof LivingEntity){
                        ItemStack ammo = ItemUtils.getArrowFromEntity((AbstractArrow) e.getDamager());
                        if (ammo == null) return;
                        CustomArrowManager.getInstance().executeOnDamage(ammo, e);
                    }
                }

                if (e.getEntity() instanceof Player){
                    combatAction((Player) e.getEntity());
                } else if (damager instanceof Player){
                    combatAction((Player) damager);
                }
                if (reflect_damage_type != null){
                    if (e.getCause() != reflect_damage_type){ // reflect damage should not be able to reflect again
                        if (damager instanceof LivingEntity){
                            double reflectChance = AccumulativeStatManager.getInstance().getStats("REFLECT_CHANCE", e.getEntity(), true);
                            if (Utils.getRandom().nextDouble() < reflectChance){
                                double reflectFraction = AccumulativeStatManager.getInstance().getStats("REFLECT_FRACTION", e.getEntity(), true);
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDeath(EntityDeathEvent e){
        Player killer = e.getEntity().getKiller();
        if (killer == null) return;

        for (Skill skill : SkillProgressionManager.getInstance().getAllSkills().values()){
            if (skill != null){
                if (skill instanceof OffensiveSkill){
                    ((OffensiveSkill) skill).onEntityKilled(e);
                }
            }
        }
        if (lootTable != null){
            List<ItemStack> newItems = new ArrayList<>(e.getDrops());
            if (!e.getDrops().isEmpty()){
                lootTable.onEntityKilled(e.getEntity(), newItems, 1);
            }
            e.getDrops().clear();
            e.getDrops().addAll(newItems);
        }
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

    public static Map<UUID, Double> getLastDamageTakenMap() {
        return lastDamageTakenMap;
    }

    public static Map<UUID, UUID> getLastDamagedByMap() {
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
                if (timeLastCombatAction + 10000 < System.currentTimeMillis()){
                    // player should no longer be considered in combat
                    PlayerLeaveCombatEvent event = new PlayerLeaveCombatEvent(who, timeLastStartedCombat);
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
}
