package me.athlaeos.valhallammo.skills.archery;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats.AttributeAddArrowInfinityExploitableModifier;
import me.athlaeos.valhallammo.dom.CombatType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.events.PlayerSkillExperienceGainEvent;
import me.athlaeos.valhallammo.events.ValhallaEntityCriticalHitEvent;
import me.athlaeos.valhallammo.items.attributewrappers.AttributeWrapper;
import me.athlaeos.valhallammo.managers.*;
import me.athlaeos.valhallammo.skills.InteractSkill;
import me.athlaeos.valhallammo.skills.OffensiveSkill;
import me.athlaeos.valhallammo.skills.ProjectileSkill;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.utility.EntityUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class ArcherySkill extends Skill implements OffensiveSkill, InteractSkill, ProjectileSkill {
    private final double bow_exp_base;
    private final double crossbow_exp_base;
    private final double distance_exp_multiplier_bonus;
    private final double distance_exp_multiplier_base;
    private final int exp_distance_limit;
    private final int damage_distance_limit;
    private final double infinity_exp_multiplier;
    private final double facing_angle;
    private final double cos_facing_angle;
    private final boolean prevent_crits_facing_shooter;
    private final double diminishing_returns_limit;
    private final double diminishing_returns_multiplier;
    private Sound charged_shot_sound;
    private static boolean special_arrow_trails = true;
    private static double special_arrow_trail_duration = 3;

    public double getFacing_angle() {
        return facing_angle;
    }

    public ArcherySkill(String type) {
        super(type);
        skillTreeMenuOrderPriority = 9;

        YamlConfiguration archeryConfig = ConfigManager.getInstance().getConfig("skill_archery.yml").get();
        YamlConfiguration progressionConfig = ConfigManager.getInstance().getConfig("progression_archery.yml").get();

        this.loadCommonConfig(archeryConfig, progressionConfig);

        bow_exp_base = progressionConfig.getDouble("experience.bow_exp_base");
        crossbow_exp_base = progressionConfig.getDouble("experience.crossbow_exp_base");
        distance_exp_multiplier_bonus = progressionConfig.getDouble("experience.distance_exp_multiplier");
        distance_exp_multiplier_base = progressionConfig.getDouble("experience.distance_exp_multiplier_base");
        exp_distance_limit = progressionConfig.getInt("experience.distance_limit");
        damage_distance_limit = archeryConfig.getInt("distance_limit");
        infinity_exp_multiplier = progressionConfig.getDouble("experience.infinity_multiplier");
        facing_angle = archeryConfig.getDouble("facing_angle");
        cos_facing_angle = Math.cos(facing_angle);
        prevent_crits_facing_shooter = archeryConfig.getBoolean("prevent_crits_facing_shooter");
        diminishing_returns_limit = archeryConfig.getDouble("diminishing_returns_limit");
        diminishing_returns_multiplier = archeryConfig.getDouble("diminishing_returns_multiplier");
        special_arrow_trails = archeryConfig.getBoolean("special_arrow_trails");
        special_arrow_trail_duration = archeryConfig.getDouble("special_arrow_trail_duration");

        try {
            charged_shot_sound = Sound.valueOf(archeryConfig.getString("charged_shot_sound"));
        } catch (IllegalArgumentException ignored){
            ValhallaMMO.getPlugin().getServer().getLogger().warning("Invalid sound name used for charged_shot_sound in skill_archery.yml. Defaulted to ENTITY_BLAZE_SHOOT");
            charged_shot_sound = Sound.ENTITY_BLAZE_SHOOT;
        }
    }

    @Override
    public NamespacedKey getKey() {
        return new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_profile_archery");
    }

    @Override
    public Profile getCleanProfile() {
        return new ArcheryProfile(null);
    }

    @Override
    public void addEXP(Player p, double amount, boolean silent, PlayerSkillExperienceGainEvent.ExperienceGainReason reason) {
        double finalAmount = amount * ((AccumulativeStatManager.getInstance().getStats("ARCHERY_EXP_GAIN_GENERAL", p, true) / 100D));
        super.addEXP(p, finalAmount, silent, reason);
    }

    private final Collection<UUID> chargedShotUsers = new HashSet<>();

    @Override
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR){
            if (!chargedShotUsers.contains(event.getPlayer().getUniqueId())){
                ItemStack mainHandItem = event.getPlayer().getInventory().getItemInMainHand();
                if (!Utils.isItemEmptyOrNull(mainHandItem)){
                    if (mainHandItem.getType() == Material.BOW || mainHandItem.getType() == Material.CROSSBOW){
                        if (CooldownManager.getInstance().isCooldownPassed(event.getPlayer().getUniqueId(), "cooldown_charged_shot")){
                            int cooldown = (int) AccumulativeStatManager.getInstance().getStats("ARCHERY_CHARGED_SHOT_COOLDOWN", event.getPlayer(), true);
                            chargedShotUsers.add(event.getPlayer().getUniqueId());
                            CooldownManager.getInstance().setCooldownIgnoreIfPermission(event.getPlayer(), cooldown, "cooldown_charged_shot");
                            event.getPlayer().playSound(event.getPlayer().getLocation(), charged_shot_sound, 0.6F, 1F);
                        }
                    }
                }
            }
        }
    }

    private final Map<UUID, ItemStack> bowsUsedInShooting = new HashMap<>();

    @Override
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        if (!(event.getEntity() instanceof LivingEntity)) return;
        if (attacker instanceof AbstractArrow && !(attacker instanceof Trident)){
            AbstractArrow arrow = (AbstractArrow) event.getDamager();
            if (arrow.getShooter() instanceof Player){
                Player shooter = (Player) arrow.getShooter();
                ItemStack bow = bowsUsedInShooting.get(shooter.getUniqueId()); // bow will always either be a bow or crossbow

                int distance = (int) shooter.getLocation().distance(event.getEntity().getLocation());
                // exp & damage
                int damageDistanceBonus = (int) (Math.min(damage_distance_limit, distance) / 10D);
                int expDistanceBonus = (int) (Math.min(exp_distance_limit, distance) / 10D);
                if (bow != null){
                    Profile p = ProfileManager.getManager().getProfile(shooter, "ARCHERY");
                    ArcheryProfile profile = null;
                    if (p instanceof ArcheryProfile){
                        profile = (ArcheryProfile) p;
                    }
                    if (profile == null) return;

                    boolean hasInfinity = bow.getEnchantmentLevel(Enchantment.ARROW_INFINITE) > 0;
                    double expToGive;
                    double damageMultiplier;
                    double damage = event.getDamage();
                    boolean crit = false;
                    boolean stun = false;

                    // distance multiplier
                    double damageDistanceBaseMultiplier = AccumulativeStatManager.getInstance().getStats("ARCHERY_DISTANCE_DAMAGE_MULTIPLIER_BASE", event.getEntity(), event.getDamager(), true);
                    double damageDistanceMultiplier = AccumulativeStatManager.getInstance().getStats("ARCHERY_DISTANCE_DAMAGE_MULTIPLIER", event.getEntity(), event.getDamager(), true);
                    double finalDamageDistanceMultiplier = damageDistanceBaseMultiplier + (damageDistanceBonus * damageDistanceMultiplier);
                    damage *= finalDamageDistanceMultiplier;
                    double critDamage = AccumulativeStatManager.getInstance().getStats("ARCHERY_CRIT_DAMAGE_MULTIPLIER", event.getEntity(), event.getDamager(), true);
                    double critChance;

                    if (bow.getType() == Material.BOW){
                        expToGive = ((distance_exp_multiplier_base * bow_exp_base) + (bow_exp_base * distance_exp_multiplier_bonus * expDistanceBonus)) * (AccumulativeStatManager.getInstance().getStats("ARCHERY_EXP_GAIN_BOW", shooter, true) / 100D);
                        damageMultiplier = AccumulativeStatManager.getInstance().getStats("ARCHERY_BOW_DAMAGE_MULTIPLIER", event.getEntity(), event.getDamager(), true);
                        critChance = AccumulativeStatManager.getInstance().getStats("ARCHERY_BOW_CRIT_CHANCE", event.getEntity(), event.getDamager(), true);
                    } else {
                        expToGive = ((distance_exp_multiplier_base * crossbow_exp_base) + (crossbow_exp_base * expDistanceBonus)) * (AccumulativeStatManager.getInstance().getStats("ARCHERY_EXP_GAIN_CROSSBOW", shooter, true) / 100D);
                        damageMultiplier = AccumulativeStatManager.getInstance().getStats("ARCHERY_CROSSBOW_DAMAGE_MULTIPLIER", event.getEntity(), event.getDamager(), true);
                        critChance = AccumulativeStatManager.getInstance().getStats("ARCHERY_CROSSBOW_CRIT_CHANCE", event.getEntity(), event.getDamager(), true);
                    }

                    damage *= damageMultiplier;

                    if (hasInfinity) {
                        expToGive *= infinity_exp_multiplier;
                        damage *= AccumulativeStatManager.getInstance().getStats("ARCHERY_INFINITY_DAMAGE_MULTIPLIER", event.getEntity(), event.getDamager(), true);
                    }

                    boolean facing = EntityUtils.isEntityFacing((LivingEntity) event.getEntity(), shooter.getLocation(), cos_facing_angle);
                    boolean can_crit = !(prevent_crits_facing_shooter && facing);
                    if (can_crit){ // if the target is not facing the shooter, and facing the shooter does not prevent crits
                        if (!facing){ // if the target is not facing the shooter, crit if target is facing away if enabled
                            crit = profile.isCritOnFacingAway();
                        }
                        if (!crit){ // if facing away did not cause a crit, crit with RNG
                            crit = Utils.getRandom().nextDouble() < critChance;
                        }
                        if (!crit){ // if RNG did not cause a crit, crit if target is standing still if enabled
                            if (profile.isCritOnStandingStill()) {
                                crit = event.getEntity().getVelocity().getX() == 0 && event.getEntity().getVelocity().getZ() == 0;
                            }
                        }
                        if (!crit){ // if standing still did not cause a crit, crit if shooter is not wearing anything and invisible
                            if (profile.isCritOnStealth()){
                                crit = EntityUtils.getEntityEquipment(shooter).getIterable(false).isEmpty() && shooter.isInvisible();
                            }
                        }
                    }

                    if (crit){
                        ValhallaEntityCriticalHitEvent e = new ValhallaEntityCriticalHitEvent(shooter, (LivingEntity) event.getEntity(), CombatType.RANGED, damage, critDamage);
                        ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(e);
                        if (!e.isCancelled()){
                            damage = e.getDamageBeforeCrit() * e.getCriticalHitDamageMultiplier();
                            stun = profile.isStunoncrit();
                        }
                    }

                    if (!stun){
                        if (Utils.getRandom().nextDouble() < AccumulativeStatManager.getInstance().getStats("ARCHERY_STUN_CHANCE", event.getEntity(), event.getDamager(), true)){
                            stun = true;
                        }
                    }

                    int duration = (int) AccumulativeStatManager.getInstance().getStats("ARCHERY_STUN_DURATION", event.getEntity(), event.getDamager(), true);
                    if (stun) PotionEffectManager.getInstance().stunTarget((LivingEntity) event.getEntity(), CombatType.RANGED, duration);

                    addEXP(shooter, expToGive, false, PlayerSkillExperienceGainEvent.ExperienceGainReason.SKILL_ACTION);

                    // apply diminishing returns to damage
                    if (damage > diminishing_returns_limit){
                        double excessDamage = damage - diminishing_returns_limit;
                        damage = diminishing_returns_limit + (excessDamage * diminishing_returns_multiplier);
                    }

                    event.setDamage(damage);
                    bowsUsedInShooting.remove(shooter.getUniqueId());
                }
            }
        }
    }

    @Override
    public void onProjectileLaunch(ProjectileLaunchEvent event) {

    }

    @Override
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getProjectile() instanceof Trident) return;
        if (event.getConsumable() == null) return;
        if (event.getEntity() instanceof Player){
            Player shooter = (Player) event.getEntity();
            if (event.getBow() != null){
                if (event.getBow().getType() == Material.BOW || event.getBow().getType() == Material.CROSSBOW){
                    bowsUsedInShooting.put(event.getEntity().getUniqueId(), event.getBow());
                }
            }

            double inaccuracy = AccumulativeStatManager.getInstance().getStats("ARCHERY_INACCURACY", event.getEntity(), true);

            if (event.getProjectile() instanceof AbstractArrow){
                AbstractArrow arrow = (AbstractArrow) event.getProjectile();
                arrow.setCritical(false); // the plugin has its own crit system, so vanilla crits are disabled

                // custom attributes
                boolean isInfinityCompatible = AttributeAddArrowInfinityExploitableModifier.isInfinityCompatible(event.getConsumable());
                boolean hasInfinity = event.getBow().getEnchantments().containsKey(Enchantment.ARROW_INFINITE);
                boolean shouldSave = isInfinityCompatible && hasInfinity;
                if (!isInfinityCompatible && hasInfinity){
                    arrow.setPickupStatus(AbstractArrow.PickupStatus.ALLOWED); // if the arrow is not infinity compatible but still shot with an infinity bow, it should be pickuppable
                    event.setConsumeItem(true);
                }
                AttributeWrapper damageWrapper = ItemAttributesManager.getInstance().getAttributeWrapper(event.getConsumable(), "CUSTOM_ARROW_DAMAGE");
                if (damageWrapper != null){
                    event.setConsumeItem(!shouldSave);
                    arrow.setDamage(Math.max(0, damageWrapper.getAmount()));
                    // the Power enchantment usually increases arrow damage, but since base damage numbers are changed
                    // the damage values power provides need to be re-added. This goes according to 1 + 0.5/(level - 1)
                    int powerLevel = event.getBow().getEnchantmentLevel(Enchantment.ARROW_DAMAGE);
                    if (powerLevel > 0) arrow.setDamage(arrow.getDamage() + (1 + (0.5 * (powerLevel - 1))));
                }
                AttributeWrapper accuracyWrapper = ItemAttributesManager.getInstance().getAttributeWrapper(event.getConsumable(), "CUSTOM_ARROW_ACCURACY");
                if (accuracyWrapper != null){
                    event.setConsumeItem(!shouldSave);
                    inaccuracy += accuracyWrapper.getAmount();
                }
                AttributeWrapper piercingWrapper = ItemAttributesManager.getInstance().getAttributeWrapper(event.getConsumable(), "CUSTOM_ARROW_PIERCING");
                if (piercingWrapper != null){
                    event.setConsumeItem(!shouldSave);
                    arrow.setPierceLevel(arrow.getPierceLevel() + (int) piercingWrapper.getAmount());
                }

                // save ammo mechanics
                if (event.shouldConsumeItem()){
                    double saveChance = AccumulativeStatManager.getInstance().getStats("ARCHERY_AMMO_SAVE_CHANCE", event.getEntity(), true);
                    AttributeWrapper saveChanceWrapper = ItemAttributesManager.getInstance().getAttributeWrapper(event.getConsumable(), "CUSTOM_ARROW_SAVE_CHANCE");
                    if (saveChanceWrapper != null){
                        saveChance += saveChanceWrapper.getAmount();
                    }
                    if (Utils.getRandom().nextDouble() < saveChance) {
                        event.setConsumeItem(false);
                        arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
                    }
                }
                shooter.updateInventory();

                if (chargedShotUsers.contains(shooter.getUniqueId())){
                    double chargedDamageMultiplier = AccumulativeStatManager.getInstance().getStats("ARCHERY_CHARGED_SHOT_DAMAGE_MULTIPLIER", shooter, true);
                    int chargedKnockbackBonus = (int) AccumulativeStatManager.getInstance().getStats("ARCHERY_CHARGED_SHOT_KNOCKBACK_BONUS", shooter, true);
                    arrow.setKnockbackStrength(arrow.getKnockbackStrength() + chargedKnockbackBonus);
                    arrow.setDamage(arrow.getDamage() * chargedDamageMultiplier);
                    chargedShotUsers.remove(shooter.getUniqueId());
                    trailProjectile(arrow, Particle.CRIT_MAGIC);
                }

                // inaccuracy mechanics
                // first equal arrow direction to player facing direction
                Vector aV = arrow.getVelocity().clone();
                Vector sV = shooter.getEyeLocation().getDirection().clone();
                double strength = aV.length(); // record initial speed of the arrow
                aV = aV.normalize(); // reduce vector lengths to 1
                sV = sV.normalize();
                aV.setX(sV.getX()); // set direction of arrow equal to direction of shooter looking
                aV.setY(sV.getY());
                aV.setZ(sV.getZ());
                aV.multiply(strength); // restore the initial speed to the arrow

                inaccuracy = Math.max(0, inaccuracy);
                aV.setX(aV.getX() + Utils.getRandom().nextGaussian() * 0.0075 * inaccuracy);
                aV.setY(aV.getY() + Utils.getRandom().nextGaussian() * 0.0075 * inaccuracy);
                aV.setY(aV.getY() + Utils.getRandom().nextGaussian() * 0.0075 * inaccuracy);

                arrow.setVelocity(aV);
            }
        }
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent event) {

    }

    @Override
    public void onArrowPickup(PlayerPickupArrowEvent event) {
        // do nothing
    }

    @Override
    public void onEntityKilled(EntityDeathEvent event) {
        // do nothing
    }

    @Override
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        // do nothing
    }

    @Override
    public void onAtEntityInteract(PlayerInteractAtEntityEvent event) {
        // do nothing
    }

    /**
     * Creates a particle trail behind the arrow while flying
     * @param projectile the arrow to give a trail to
     */
    public static void trailProjectile(Projectile projectile, Particle particle){
        if (special_arrow_trails){
            new BukkitRunnable(){
                final World w = projectile.getWorld();
                final int duration = (int) (special_arrow_trail_duration * 20D);
                int count = 0;
                @Override
                public void run() {
                    if (count >= duration) {
                        cancel();
                        return;
                    }
                    if (projectile != null && projectile.isValid()){
                        w.spawnParticle(particle, projectile.getLocation(), 0);
                    } else {
                        cancel();
                    }
                    count++;
                }
            }.runTaskTimer(ValhallaMMO.getPlugin(), 1L, 1L);
        }
    }
}
