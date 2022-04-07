package me.athlaeos.valhallammo.skills.archery;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.attributewrappers.AttributeWrapper;
import me.athlaeos.valhallammo.items.attributewrappers.CustomArrowAccuracyWrapper;
import me.athlaeos.valhallammo.items.attributewrappers.CustomArrowDamageWrapper;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.managers.PotionEffectManager;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.InteractSkill;
import me.athlaeos.valhallammo.skills.OffensiveSkill;
import me.athlaeos.valhallammo.skills.ProjectileSkill;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.utility.EntityUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    private final NamespacedKey arrowCustomModelDataKey = new NamespacedKey(ValhallaMMO.getPlugin(), "arrow_damage");

    public ArcherySkill(String type) {
        super(type);

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
    public void addEXP(Player p, double amount, boolean silent) {
        double finalAmount = amount * ((AccumulativeStatManager.getInstance().getStats("ARCHERY_EXP_GAIN_GENERAL", p, true) / 100D));
        super.addEXP(p, finalAmount, silent);
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {

    }

    private final Map<UUID, ItemStack> bowsUsedInShooting = new HashMap<>();

    private boolean isFacing(LivingEntity who, Location at){
        Vector dir = at.toVector().subtract(who.getEyeLocation().toVector()).normalize();
        double dot = dir.dot(who.getEyeLocation().getDirection());
        return dot >= cos_facing_angle;
    }

    @Override
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        if (!(event.getEntity() instanceof LivingEntity)) return;
        if (attacker instanceof AbstractArrow){
            AbstractArrow arrow = (AbstractArrow) event.getDamager();
            if (arrow.getShooter() instanceof Player){
                Player shooter = (Player) arrow.getShooter();
                ItemStack bow = bowsUsedInShooting.get(shooter.getUniqueId()); // bow will always either be a bow or crossbow

                int distance = (int) shooter.getLocation().distance(event.getEntity().getLocation());
                // exp & damage
                int damageDistanceBonus = (int) (Math.min(damage_distance_limit, distance) / 10D);
                int expDistanceBonus = (int) (Math.min(exp_distance_limit, distance) / 10D);
                if (bow != null){
                    Profile p = ProfileManager.getProfile(shooter, "ARCHERY");
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
                    double damageDistanceBaseMultiplier = AccumulativeStatManager.getInstance().getStats("ARCHERY_DISTANCE_DAMAGE_MULTIPLIER_BASE", shooter, true);
                    double damageDistanceMultiplier = AccumulativeStatManager.getInstance().getStats("ARCHERY_DISTANCE_DAMAGE_MULTIPLIER", shooter, true);
                    double finalDamageDistanceMultiplier = damageDistanceBaseMultiplier + (damageDistanceBonus * damageDistanceMultiplier);
                    damage *= finalDamageDistanceMultiplier;
                    double critDamage = AccumulativeStatManager.getInstance().getStats("ARCHERY_CRIT_DAMAGE_MULTIPLIER", shooter, true);
                    double critChance;

                    if (bow.getType() == Material.BOW){
                        expToGive = ((distance_exp_multiplier_base * bow_exp_base) + (bow_exp_base * distance_exp_multiplier_bonus * expDistanceBonus)) * (AccumulativeStatManager.getInstance().getStats("ARCHERY_EXP_GAIN_BOW", shooter, true) / 100D);
                        damageMultiplier = AccumulativeStatManager.getInstance().getStats("ARCHERY_BOW_DAMAGE_MULTIPLIER", shooter, true);
                        critChance = AccumulativeStatManager.getInstance().getStats("ARCHERY_BOW_CRIT_CHANCE", shooter, true);
                    } else {
                        expToGive = ((distance_exp_multiplier_base * crossbow_exp_base) + (crossbow_exp_base * expDistanceBonus)) * (AccumulativeStatManager.getInstance().getStats("ARCHERY_EXP_GAIN_CROSSBOW", shooter, true) / 100D);
                        damageMultiplier = AccumulativeStatManager.getInstance().getStats("ARCHERY_CROSSBOW_DAMAGE_MULTIPLIER", shooter, true);
                        critChance = AccumulativeStatManager.getInstance().getStats("ARCHERY_CROSSBOW_CRIT_CHANCE", shooter, true);
                    }

                    damage *= damageMultiplier;

                    if (hasInfinity) {
                        expToGive *= infinity_exp_multiplier;
                        damage *= AccumulativeStatManager.getInstance().getStats("ARCHERY_INFINITY_DAMAGE_MULTIPLIER", shooter, true);
                    }

                    boolean facing = isFacing((LivingEntity) event.getEntity(), shooter.getLocation());
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
                        damage *= critDamage;
                        stun = profile.isStunoncrit();
                    }

                    if (!stun){
                        if (Utils.getRandom().nextDouble() < AccumulativeStatManager.getInstance().getStats("ARCHERY_STUN_CHANCE", shooter, true)){
                            stun = true;
                        }
                    }

                    int duration = (int) AccumulativeStatManager.getInstance().getStats("ARCHERY_STUN_DURATION", shooter, true);
                    if (stun) PotionEffectManager.getInstance().stunTarget((LivingEntity) event.getEntity(), duration);

                    addEXP(shooter, expToGive, false);
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

                // add custom model data as data value to arrow projectile so it can be re-applied when picking it up
                ItemMeta arrowMeta = event.getConsumable().getItemMeta();
                if (arrowMeta != null && arrowMeta.hasCustomModelData()){
                    arrow.getPersistentDataContainer().set(arrowCustomModelDataKey, PersistentDataType.INTEGER, arrowMeta.getCustomModelData());
                }

                AttributeWrapper damageWrapper = ItemAttributesManager.getInstance().getAttributeWrapper(event.getConsumable(), "CUSTOM_ARROW_DAMAGE");
                if (damageWrapper != null){
                    event.setConsumeItem(true);
                    arrow.setDamage(Math.max(0, damageWrapper.getAmount()));
                    arrow.setMetadata("is_custom", new FixedMetadataValue(ValhallaMMO.getPlugin(), true));
                }
                AttributeWrapper accuracyWrapper = ItemAttributesManager.getInstance().getAttributeWrapper(event.getConsumable(), "CUSTOM_ARROW_ACCURACY");
                if (accuracyWrapper != null){
                    event.setConsumeItem(true);
                    inaccuracy += accuracyWrapper.getAmount();
                    arrow.setMetadata("valhallammo_accuracy", new FixedMetadataValue(ValhallaMMO.getPlugin(), accuracyWrapper.getAmount()));
                }

                // save ammo mechanics
                if (event.shouldConsumeItem()){
                    double saveChance = AccumulativeStatManager.getInstance().getStats("ARCHERY_AMMO_SAVE_CHANCE", event.getEntity(), true);
                    if (Utils.getRandom().nextDouble() < saveChance) {
                        event.setConsumeItem(false);
                        arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
                        shooter.updateInventory();
                    }
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
        Item i = event.getItem();
        double damage = event.getArrow().getDamage();
        ItemStack item = i.getItemStack();
        if (event.getArrow().getPersistentDataContainer().has(arrowCustomModelDataKey, PersistentDataType.INTEGER)){
            int value = event.getArrow().getPersistentDataContainer().get(arrowCustomModelDataKey, PersistentDataType.INTEGER);
            ItemMeta meta = item.getItemMeta();
            if (meta != null){
                meta.setCustomModelData(value);
                item.setItemMeta(meta);
            }
        }
        if (event.getArrow().hasMetadata("is_custom")){
            ItemAttributesManager.getInstance().addDefaultStat(item, new CustomArrowDamageWrapper(
                    damage,
                    AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlot.HAND
            ));
        }
        if (event.getArrow().hasMetadata("valhallammo_accuracy")){
            List<MetadataValue> metaData = event.getArrow().getMetadata("valhallammo_accuracy");
            if (!metaData.isEmpty()){
                try {
                    ItemAttributesManager.getInstance().addDefaultStat(item, new CustomArrowAccuracyWrapper(
                            metaData.get(0).asDouble(),
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlot.HAND
                    ));
                } catch (Exception ignored){
                    ValhallaMMO.getPlugin().getServer().getLogger().severe("Another plugin is using metadata key 'valhallammo_accuracy' and not using the proper data type");
                }
            }
        }
        i.setItemStack(item);
    }

    @Override
    public void onEntityKilled(EntityDeathEvent event) {

    }

    @Override
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        // do nothing
    }

    @Override
    public void onAtEntityInteract(PlayerInteractAtEntityEvent event) {
        // do nothing
    }
}
