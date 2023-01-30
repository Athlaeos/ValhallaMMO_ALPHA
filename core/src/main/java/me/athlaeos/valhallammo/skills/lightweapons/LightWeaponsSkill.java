package me.athlaeos.valhallammo.skills.lightweapons;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.CombatType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.dom.WeaponType;
import me.athlaeos.valhallammo.events.EntityCustomPotionEffectEvent;
import me.athlaeos.valhallammo.events.PlayerSkillExperienceGainEvent;
import me.athlaeos.valhallammo.events.ValhallaEntityCriticalHitEvent;
import me.athlaeos.valhallammo.items.PotionType;
import me.athlaeos.valhallammo.listeners.EntityDamagedListener;
import me.athlaeos.valhallammo.listeners.EntitySpawnListener;
import me.athlaeos.valhallammo.loottables.ChancedEntityLootTable;
import me.athlaeos.valhallammo.loottables.LootManager;
import me.athlaeos.valhallammo.loottables.chance_based_entity_loot.ChancedLightWeaponsLootTable;
import me.athlaeos.valhallammo.managers.*;
import me.athlaeos.valhallammo.skills.InteractSkill;
import me.athlaeos.valhallammo.skills.OffensiveSkill;
import me.athlaeos.valhallammo.skills.PotionEffectSkill;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.utility.EntityUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class LightWeaponsSkill extends Skill implements OffensiveSkill, InteractSkill, PotionEffectSkill {
    private final double exp_per_damage;
    private final Map<EntityType, Double> entitySpecificExpNerfs = new HashMap<>();
    private Sound crit_sound_effect;
    private final boolean crit_particle_effect;
    private final double spawner_spawned_multiplier;
    private final int defaultStunDuration = ConfigManager.getInstance().getConfig("config.yml").get().getInt("default_stun_duration");
    private ChancedLightWeaponsLootTable lootTable = null;

    public LightWeaponsSkill(String type) {
        super(type);
        skillTreeMenuOrderPriority = 7;

        YamlConfiguration lightWeaponsConfig = ConfigManager.getInstance().getConfig("skill_light_weapons.yml").get();
        YamlConfiguration progressionConfig = ConfigManager.getInstance().getConfig("progression_light_weapons.yml").get();

        this.loadCommonConfig(lightWeaponsConfig, progressionConfig);

        ChancedEntityLootTable lightWeapons = LootManager.getInstance().getChancedEntityLootTables().get("light_weapons");
        if (lightWeapons != null){
            if (lightWeapons instanceof ChancedLightWeaponsLootTable){
                this.lootTable = (ChancedLightWeaponsLootTable) lightWeapons;
            }
        }

        exp_per_damage = progressionConfig.getDouble("experience.exp_per_damage");
        spawner_spawned_multiplier = progressionConfig.getDouble("experience.spawner_spawned_multiplier");

        ConfigurationSection entitySection = progressionConfig.getConfigurationSection("experience.exp_enemies_nerfed");
        if (entitySection != null){
            for(String s : entitySection.getKeys(false)){
                try {
                    EntityType entityType = EntityType.valueOf(s);
                    double multiplier = progressionConfig.getDouble("experience.exp_enemies_nerfed." + s);
                    entitySpecificExpNerfs.put(entityType, multiplier);
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("invalid entity type given at progression_light_weapons.yml experience.exp_enemies_nerfed." + s);
                }
            }
        }

        YamlConfiguration config = ConfigManager.getInstance().getConfig("config.yml").get();
        try {
            crit_sound_effect = Sound.valueOf(config.getString("crit_sound_effect"));
        } catch (IllegalArgumentException ignored){
            crit_sound_effect = null;
            ValhallaMMO.getPlugin().getLogger().warning("invalid Sound type given at config.yml for crit_sound_effect");
        }
        crit_particle_effect = config.getBoolean("crit_particle_effect");
    }

    @Override
    public NamespacedKey getKey() {
        return new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_profile_light_weapons");
    }

    @Override
    public Profile getCleanProfile() {
        return new LightWeaponsProfile(null);
    }

    @Override
    public void addEXP(Player p, double amount, boolean silent, PlayerSkillExperienceGainEvent.ExperienceGainReason reason) {
        double finalAmount = amount * ((AccumulativeStatManager.getInstance().getStats("LIGHT_WEAPONS_EXP_GAIN", p, true) / 100D));
        super.addEXP(p, finalAmount, silent, reason);
    }

    private final Map<UUID, ItemStack> lastLightWeaponsUsed = new HashMap<>();

    @Override
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
            ItemStack weapon = event.getPlayer().getInventory().getItemInMainHand();
            // if no weapon or weapon is not light, do nothing
            if (Utils.isItemEmptyOrNull(weapon)) {
                lastLightWeaponsUsed.remove(event.getPlayer().getUniqueId());
            } else {
                lastLightWeaponsUsed.put(event.getPlayer().getUniqueId(), weapon);
            }
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR){
            ItemStack weapon = event.getPlayer().getInventory().getItemInMainHand();
            // if no weapon or weapon is not light, do nothing
            if (Utils.isItemEmptyOrNull(weapon) || WeaponType.getWeaponType(weapon) != WeaponType.LIGHT) {
                return;
            }
            if (event.getPlayer().getAttackCooldown() >= 0.9F){
                if (CooldownManager.getInstance().isCooldownPassed(event.getPlayer().getUniqueId(), "parry_cooldown")){
                    if (!PotionEffectManager.getInstance().isStunned(event.getPlayer())){
                        Profile p = ProfileManager.getManager().getProfile(event.getPlayer(), "LIGHT_WEAPONS");
                        if (p != null){
                            if (p instanceof LightWeaponsProfile){
                                int cooldown = ((LightWeaponsProfile) p).getParryCooldown();
                                int parryVulnerableDuration = ((LightWeaponsProfile) p).getParryVulnerableFrame();
                                int parryTimeFrame = ((LightWeaponsProfile) p).getParryTimeFrame();
                                if (cooldown >= 0){
                                    EntityDamagedListener.parry(event.getPlayer(), parryTimeFrame, parryVulnerableDuration, cooldown);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        Entity damaged = event.getEntity();
        if (!(attacker instanceof LivingEntity) || !(damaged instanceof LivingEntity)) return;
        if (EntityUtils.EntityClassification.isMatchingClassification(damaged.getType(), EntityUtils.EntityClassification.UNALIVE)) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;
        ItemStack weapon = null;
        EntityEquipment equipment = ((LivingEntity) attacker).getEquipment();
        if (equipment != null){
            weapon = equipment.getItemInMainHand();
        }
        // if no weapon or weapon is not light, do nothing
        if (weapon == null || WeaponType.getWeaponType(weapon) != WeaponType.LIGHT) {
            return;
        }

        if (event.isCancelled()) return;
        CoatingEffect onHitEffect = coatingEffects.get(attacker.getUniqueId());
        if (onHitEffect != null){
            if (isSimilarEnough(onHitEffect.effectiveOn, weapon)) {
                if (onHitEffect.onHit((LivingEntity) damaged)){
                    coatingEffects.remove(attacker.getUniqueId());
                }
            }
        }

        double damage = event.getDamage();
        double damageMultiplier = AccumulativeStatManager.getInstance().getStats("LIGHT_WEAPONS_DAMAGE_MULTIPLIER", event.getEntity(), event.getDamager(), true);
        damage *= damageMultiplier;

        double critChance = AccumulativeStatManager.getInstance().getStats("LIGHT_WEAPONS_CRIT_CHANCE", event.getEntity(), event.getDamager(), true);
        boolean crit = Utils.getRandom().nextDouble() < critChance;
        double bleedChance = AccumulativeStatManager.getInstance().getStats("BLEED_CHANCE", event.getEntity(), event.getDamager(), true);
        boolean bleed = Utils.getRandom().nextDouble() < bleedChance;
        double bleedDamage = AccumulativeStatManager.getInstance().getStats("BLEED_DAMAGE", event.getEntity(), event.getDamager(), true);
        int bleedDuration = (int) AccumulativeStatManager.getInstance().getStats("BLEED_DURATION", event.getEntity(), event.getDamager(), true);
        double critDamage = AccumulativeStatManager.getInstance().getStats("LIGHT_WEAPONS_CRIT_DAMAGE", event.getEntity(), event.getDamager(), true);

        int stunDuration = defaultStunDuration;
        boolean stun = Utils.getRandom().nextDouble() < AccumulativeStatManager.getInstance().getStats("HEAVY_WEAPONS_STUN_CHANCE", damaged, attacker, true);

        if (attacker instanceof Player){
            Player player = (Player) attacker;
            Profile p = ProfileManager.getManager().getProfile((Player) attacker, "LIGHT_WEAPONS");
            if (p instanceof LightWeaponsProfile){
                LightWeaponsProfile profile = (LightWeaponsProfile) p;
                if (!crit){
                    crit = profile.isCritOnBleed() && PotionEffectManager.getInstance().isBleeding((LivingEntity) event.getEntity());
                }
                if (!crit){ // if standing still did not cause a crit, crit if shooter is not wearing anything and invisible
                    crit = profile.isCritOnStealth() && EntityUtils.getEntityEquipment(player).getIterable(false).isEmpty() && player.isInvisible();
                }
                if (!crit){
                    crit = profile.isCritOnStun() && PotionEffectManager.getInstance().isStunned((LivingEntity) damaged);
                }
                if (!bleed){
                    bleed = profile.isBleedOnCrit() && crit;
                }

                stunDuration = profile.getStunDuration();
            }
        }

        if (stun) PotionEffectManager.getInstance().stunTarget((LivingEntity) damaged, CombatType.MELEE, stunDuration);

        if (crit){
            ValhallaEntityCriticalHitEvent e = new ValhallaEntityCriticalHitEvent((LivingEntity) attacker, (LivingEntity) damaged, CombatType.MELEE, damage, critDamage);
            ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(e);
            if (!e.isCancelled()){
                if (crit_sound_effect != null) {
                    if (attacker instanceof Player)
                        ((Player) attacker).playSound(event.getEntity().getLocation(), crit_sound_effect, 1F, 1F);
                    if (event.getEntity() instanceof Player)
                        ((Player) event.getEntity()).playSound(event.getEntity().getLocation(), crit_sound_effect, 1F, 1F);
                }
                if (crit_particle_effect) damaged.getWorld().spawnParticle(Particle.FLASH, ((LivingEntity) damaged).getEyeLocation().add(0, -damaged.getHeight()/2, 0), 0);
                damage = e.getDamageBeforeCrit() * e.getCriticalHitDamageMultiplier();
            }
        }
        event.setDamage(damage);
        if (bleed){
            PotionEffectManager.getInstance().bleedEntity((LivingEntity) damaged, (LivingEntity) attacker, bleedDuration, bleedDamage);
        }

        if (attacker instanceof Player){
            double entityExpMultiplier = entitySpecificExpNerfs.getOrDefault(event.getEntity().getType(), 1D);
            addEXP((Player) attacker, event.getDamage() * exp_per_damage * entityExpMultiplier * (EntitySpawnListener.getSpawnReason(event.getEntity()) == CreatureSpawnEvent.SpawnReason.SPAWNER ? spawner_spawned_multiplier : 1), false, PlayerSkillExperienceGainEvent.ExperienceGainReason.SKILL_ACTION);
        }

        //ItemStack finalWeapon = weapon;
        //ValhallaMMO.getPlugin().getServer().getScheduler().runTaskLater(ValhallaMMO.getPlugin(), () -> {
        //
        //}, 1L);
    }

    @Override
    public void onEntityKilled(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null){
            Player killer = event.getEntity().getKiller();
            ItemStack weapon = killer.getInventory().getItemInMainHand();
            if (Utils.isItemEmptyOrNull(weapon) || WeaponType.getWeaponType(weapon) != WeaponType.LIGHT) return;
            if (!EntityUtils.EntityClassification.isMatchingClassification(event.getEntityType(), EntityUtils.EntityClassification.UNALIVE)){
                List<ItemStack> newItems = new ArrayList<>(event.getDrops());

                if (!event.getDrops().isEmpty()){
                    double rareDropMultiplier = AccumulativeStatManager.getInstance().getStats("LIGHT_WEAPONS_RARE_DROP_MULTIPLIER", killer, true);
                    lootTable.onEntityKilled(event.getEntity(), newItems, rareDropMultiplier);
                    event.getDrops().clear();
                    event.getDrops().addAll(newItems);
                }
            }
        }
    }

    @Override
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        // do nothing
    }

    @Override
    public void onAtEntityInteract(PlayerInteractAtEntityEvent event) {
        // do nothing
    }

    private final Map<UUID, CoatingEffect> coatingEffects = new HashMap<>();

    @Override
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (event.getCause() == EntityPotionEffectEvent.Cause.POTION_DRINK && event.getNewEffect() != null){
            if (PotionType.getClass(event.getNewEffect().getType()) == PotionType.DEBUFF && event.getEntity() instanceof Player){
                Player p = (Player) event.getEntity();
                Profile profile = ProfileManager.getManager().getProfile(p, "LIGHT_WEAPONS");
                if (profile != null){
                    if (profile instanceof LightWeaponsProfile){
                        ItemStack weapon = lastLightWeaponsUsed.get(p.getUniqueId());
                        if (!((LightWeaponsProfile) profile).isUnlockedWeaponCoating()) {
                            return;
                        }
                        event.setCancelled(true);
                        if (Utils.isItemEmptyOrNull(weapon) || WeaponType.getWeaponType(weapon) != WeaponType.LIGHT) return;
                        long baseDuration = event.getNewEffect().getDuration();
                        double baseAmplifier = event.getNewEffect().getAmplifier();
                        long effectiveUntil;
                        if (((LightWeaponsProfile) profile).getSelfPotionDurationMultiplier() > 0){
                            effectiveUntil = System.currentTimeMillis() + (long) (baseDuration * ((LightWeaponsProfile) profile).getSelfPotionDurationMultiplier()) * 20;
                        } else {
                            effectiveUntil = (long) ((LightWeaponsProfile) profile).getSelfPotionDurationMultiplier();
                        }
                        double amplifier = (Math.max(1, ((LightWeaponsProfile) profile).getEnemyPotionAmplifierMultiplier() * (baseAmplifier + 1))) - 1;
                        int duration = (int) (baseDuration * ((LightWeaponsProfile) profile).getEnemyPotionDurationMultiplier());
                        coatingEffects.put(event.getEntity().getUniqueId(), new CoatingEffect(event.getNewEffect().getType().getName(), weapon, effectiveUntil, amplifier, duration));
                    }
                }
            }
        }
    }

    @Override
    public void onCustomPotionEffect(EntityCustomPotionEffectEvent event) {
        if (event.getCause() == EntityPotionEffectEvent.Cause.POTION_DRINK && event.getNewEffect() != null){
            if (event.getNewEffect().getType() == PotionType.DEBUFF && event.getEntity() instanceof Player){
                Player p = (Player) event.getEntity();
                Profile profile = ProfileManager.getManager().getProfile(p, "LIGHT_WEAPONS");
                if (profile != null){
                    if (profile instanceof LightWeaponsProfile){
                        ItemStack weapon = lastLightWeaponsUsed.get(p.getUniqueId());
                        if (!((LightWeaponsProfile) profile).isUnlockedWeaponCoating()) {
                            return;
                        }
                        event.setCancelled(true);
                        if (Utils.isItemEmptyOrNull(weapon) || WeaponType.getWeaponType(weapon) != WeaponType.LIGHT) return;
                        long baseDuration = event.getNewEffect().getEffectiveUntil() - System.currentTimeMillis();
                        double baseAmplifier = event.getNewEffect().getAmplifier();
                        long effectiveUntil;
                        if (((LightWeaponsProfile) profile).getSelfPotionDurationMultiplier() > 0){
                            effectiveUntil = System.currentTimeMillis() + (long) (baseDuration * ((LightWeaponsProfile) profile).getSelfPotionDurationMultiplier());
                        } else {
                            effectiveUntil = (long) ((LightWeaponsProfile) profile).getSelfPotionDurationMultiplier();
                        }
                        double amplifier = ((LightWeaponsProfile) profile).getEnemyPotionAmplifierMultiplier() * baseAmplifier;
                        int duration = (int) (baseDuration * ((LightWeaponsProfile) profile).getEnemyPotionDurationMultiplier());
                        coatingEffects.put(event.getEntity().getUniqueId(), new CoatingEffect(event.getNewEffect().getName(), weapon, effectiveUntil, amplifier, duration));
                    }
                }
            }
        }
    }

    @Override
    public void onPotionSplash(PotionSplashEvent event) {
        // do nothing
    }

    @Override
    public void onPotionLingering(LingeringPotionSplashEvent event) {
        // do nothing
    }

    @Override
    public void onLingerApply(AreaEffectCloudApplyEvent event) {
        // do nothing
    }

    private static class CoatingEffect {
        private final String baseType;
        private final ItemStack effectiveOn;
        private long effectiveUntil;
        private PotionEffectType vanillaType;
        private final double amplifier;
        private final int duration;

        public CoatingEffect(String type, ItemStack effectiveOn, long effectiveUntil, double amplifier, int duration) {
            this.baseType = type;
            this.effectiveUntil = effectiveUntil;
            if (effectiveUntil < 0) this.effectiveUntil = (int) effectiveUntil;
            this.effectiveOn = effectiveOn;
            this.vanillaType = PotionEffectType.getByName(type);
            this.amplifier = amplifier;
            this.duration = duration;
        }

        public boolean onHit(LivingEntity e) {
            if (effectiveUntil > 0){
                boolean shouldExpire = effectiveUntil < System.currentTimeMillis();
                if (shouldExpire) return true;
            }
            if (vanillaType != null) {
                int amplifier = (int) Math.floor(this.amplifier);
                if (amplifier < 0) {
                    return true;
                }
                PotionEffect potionEffectToAdd = new PotionEffect(vanillaType, duration, amplifier, true, false, true);
                PotionEffect potionEffectToReplace = e.getPotionEffect(vanillaType);
                if (potionEffectToReplace != null) {
                    if (potionEffectToReplace.getDuration() > potionEffectToAdd.getDuration()
                            || potionEffectToReplace.getAmplifier() > potionEffectToAdd.getAmplifier()) return false;
                    // if either the player's existing potion effect duration or amplifier exceeds
                    // the coating's potion effect, it is not replaced.
                }
                e.addPotionEffect(potionEffectToAdd);
            } else {
                me.athlaeos.valhallammo.dom.PotionEffect customEffect = PotionEffectManager.getInstance().getBasePotionEffect(baseType);
                if (customEffect == null) return true;
                PotionEffectManager.getInstance().addPotionEffect(e, new me.athlaeos.valhallammo.dom.PotionEffect(
                        baseType, duration, amplifier, customEffect.getType(), customEffect.isRemovable()
                ), true, EntityPotionEffectEvent.Cause.ATTACK, EntityPotionEffectEvent.Action.ADDED);
            }
            if (effectiveUntil < 0){
                effectiveUntil++;
                return effectiveUntil >= 0;
            }
            return false;
        }
    }

    private boolean isSimilarEnough(ItemStack i1, ItemStack i2){
        if (i1.getType() != i2.getType()) return false; // item base types don't even match
        ItemMeta meta1 = i1.getItemMeta();
        ItemMeta meta2 = i2.getItemMeta();
        if (meta1 == null || meta2 == null) return false; // either of the items don't have an item meta
        if (meta1.hasDisplayName() != meta2.hasDisplayName()) return false; // one of the two items has a display name while the other doesn't
        if (meta1.hasDisplayName() && meta2.hasDisplayName()){
            if (!meta1.getDisplayName().equals(meta2.getDisplayName())) return false; // the items' display names don't match
        }
        if (SmithingItemTreatmentManager.getInstance().getItemsQuality(i1) != SmithingItemTreatmentManager.getInstance().getItemsQuality(i2)) return false;
        // the items' quality values don't match
        return CustomDurabilityManager.getInstance().getMaxDurability(i1) == CustomDurabilityManager.getInstance().getMaxDurability(i2);
        // the items' max durability values don't match
    }
}
