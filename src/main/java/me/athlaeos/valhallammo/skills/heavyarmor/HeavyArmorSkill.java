package me.athlaeos.valhallammo.skills.heavyarmor;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.ArmorType;
import me.athlaeos.valhallammo.dom.CombatType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.events.*;
import me.athlaeos.valhallammo.listeners.EntityDamagedListener;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.managers.CooldownManager;
import me.athlaeos.valhallammo.managers.PotionEffectManager;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.CombatSkill;
import me.athlaeos.valhallammo.skills.OffensiveSkill;
import me.athlaeos.valhallammo.skills.PotionEffectSkill;
import me.athlaeos.valhallammo.skills.Skill;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashSet;

public class HeavyArmorSkill extends Skill implements OffensiveSkill, PotionEffectSkill, CombatSkill {
    private final double exp_damage_piece;
    private final double exp_second_piece;

    private final Collection<RagePotionEffect> ragePotionEffects = new HashSet<>();

    public HeavyArmorSkill(String type) {
        super(type);
        skillTreeMenuOrderPriority = 11;

        YamlConfiguration heavyArmorConfig = ConfigManager.getInstance().getConfig("skill_heavy_armor.yml").get();
        YamlConfiguration progressionConfig = ConfigManager.getInstance().getConfig("progression_heavy_armor.yml").get();

        this.loadCommonConfig(heavyArmorConfig, progressionConfig);

        exp_damage_piece = progressionConfig.getDouble("experience.exp_damage_piece");
        exp_second_piece = progressionConfig.getDouble("experience.exp_second_piece");

        for (String potionEffectString : heavyArmorConfig.getStringList("rage_effects")){
            String[] args = potionEffectString.split(";");
            if (args.length < 5) {
                ValhallaMMO.getPlugin().getServer().getLogger().warning("Could not register Rage potion effect, not enough arguments: POTIONEFFECT;AMPLIFIERBASE;DURATIONBASE;AMPLIFIERLV;DURATIONLV");
                continue;
            }
            try {
                String effect = args[0];
                PotionEffectType vanillaEffect = PotionEffectType.getByName(effect);
                if (vanillaEffect == null && !effect.equals("STUN")){
                    me.athlaeos.valhallammo.dom.PotionEffect customEffect = PotionEffectManager.getInstance().getBasePotionEffect(effect);
                    if (customEffect == null){
                        ValhallaMMO.getPlugin().getServer().getLogger().warning("Could not register Rage potion effect " + effect + ", either it's not a valid potion effect or its arguments are not numbers!");
                        continue;
                    }
                }
                double baseAmplifier = Double.parseDouble(args[1]);
                int baseDuration = Integer.parseInt(args[2]);
                double lvAmplifier = Double.parseDouble(args[3]);
                int lvDuration = Integer.parseInt(args[4]);
                ragePotionEffects.add(new RagePotionEffect(effect, baseAmplifier, baseDuration, lvAmplifier, lvDuration));
            } catch (IllegalArgumentException ignored){
                ValhallaMMO.getPlugin().getServer().getLogger().warning("Could not register Rage potion effect " + args[0] + ", either it's not a valid potion effect or its arguments are not numbers!");
            }
        }
    }

    @Override
    public NamespacedKey getKey() {
        return new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_profile_heavy_armor");
    }

    @Override
    public Profile getCleanProfile() {
        return new HeavyArmorProfile(null);
    }

    @Override
    public void addEXP(Player p, double amount, boolean silent, PlayerSkillExperienceGainEvent.ExperienceGainReason reason) {
        double finalAmount = amount * ((AccumulativeStatManager.getInstance().getStats("HEAVY_ARMOR_EXP_GAIN", p, true) / 100D));
        super.addEXP(p, finalAmount, silent, reason);
    }

    @Override
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getFinalDamage() == 0) return;
        double originalDamage = event.getDamage();
        Player p = (Player) event.getEntity();
        int heavyArmorCount = ArmorType.getArmorTypeCount(p, ArmorType.HEAVY);
        addEXP(p, originalDamage * exp_damage_piece * heavyArmorCount, false, PlayerSkillExperienceGainEvent.ExperienceGainReason.SKILL_ACTION);

        boolean isWearingEnoughHeavyArmor = false;
        Profile profile = ProfileManager.getManager().getProfile(p, "HEAVY_ARMOR");
        if (profile != null){
            if (profile instanceof HeavyArmorProfile){
                int needed = ((HeavyArmorProfile) profile).getArmorPiecesForBonusses();
                isWearingEnoughHeavyArmor = heavyArmorCount >= needed;
            }
        }

        if (isWearingEnoughHeavyArmor){
            if (CooldownManager.getInstance().isCooldownPassed(p.getUniqueId(), "rage_cooldown")){
                int rageCooldown = ((HeavyArmorProfile) profile).getRageCooldown();
                int rageLevel = ((HeavyArmorProfile) profile).getRageLevel();
                if (rageCooldown >= 0 && rageLevel > 0){
                    AttributeInstance maxHealthAttribute = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                    if (maxHealthAttribute != null){
                        new BukkitRunnable(){
                            @Override
                            public void run() {
                                double damageTaken = EntityDamagedListener.getLastDamageTakenMap().getOrDefault(p.getUniqueId(), event.getFinalDamage());
                                if (p.getHealth() - damageTaken <= maxHealthAttribute.getValue() * ((HeavyArmorProfile) profile).getRageThreshold()){
                                    for (RagePotionEffect effect : ragePotionEffects){
                                        effect.applyPotionEffect(p, rageLevel);
                                    }
                                    CooldownManager.getInstance().setCooldownIgnoreIfPermission(p, rageCooldown, "rage_cooldown");
                                }
                            }
                        }.runTaskLater(ValhallaMMO.getPlugin(), 1L);
                    }
                }
            }
        }
    }

    @Override
    public void onEntityKilled(EntityDeathEvent event) {
        // do nothing
    }

    @Override
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (event.getNewEffect() == null) return;
        if (event.getEntity() instanceof Player){
            Player p = (Player) event.getEntity();
            Profile profile = ProfileManager.getManager().getProfile(p, "HEAVY_ARMOR");
            if (profile != null){
                if (profile instanceof HeavyArmorProfile){
                    if (((HeavyArmorProfile) profile).getImmunePotionEffects().contains(event.getNewEffect().getType().toString())){
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @Override
    public void onCustomPotionEffect(EntityCustomPotionEffectEvent event) {

    }

    @Override
    public void onPotionSplash(PotionSplashEvent event) {

    }

    @Override
    public void onPotionLingering(LingeringPotionSplashEvent event) {

    }

    @Override
    public void onLingerApply(AreaEffectCloudApplyEvent event) {

    }

    @Override
    public void onCombatEnter(PlayerEnterCombatEvent event) {

    }

    @Override
    public void onCombatLeave(PlayerLeaveCombatEvent event) {
        long timeInCombat = event.getTimeInCombat();
        int armorCount = ArmorType.getArmorTypeCount(event.getPlayer(), ArmorType.HEAVY);
        int expRewardTimes = (int) (timeInCombat / 1000D);

        addEXP(event.getPlayer(), expRewardTimes * armorCount * exp_second_piece, false, PlayerSkillExperienceGainEvent.ExperienceGainReason.SKILL_ACTION);
    }

    @Override
    public void onEntityStun(ValhallaEntityStunEvent event) {

    }

    @Override
    public void onPlayerCriticalStrike(ValhallaEntityCriticalHitEvent event) {

    }

    private static class RagePotionEffect{
        private final String baseType;
        private PotionEffectType vanillaType = null;
        private final double baseAmplifier;
        private final int baseDuration;
        private final double lvAmplifier;
        private final int lvDuration;

        public RagePotionEffect(String type, double baseAmplifier, int baseDuration, double lvAmplifier, int lvDuration){
            this.baseType = type;
            this.vanillaType = PotionEffectType.getByName(type);
            this.baseAmplifier = baseAmplifier;
            this.baseDuration = baseDuration;
            this.lvAmplifier = lvAmplifier;
            this.lvDuration = lvDuration;
        }

        public void applyPotionEffect(Player p, int level){
            int duration = baseDuration + (lvDuration * (level - 1));
            if (baseType.equals("STUN")) {
                PotionEffectManager.getInstance().stunTarget(p, CombatType.MELEE, baseDuration);
                return;
            }
            if (vanillaType != null){
                int amplifier = (int) Math.floor(baseAmplifier + (lvAmplifier * (level - 1)));
                if (amplifier <= 0) return;
                PotionEffect potionEffectToAdd = new PotionEffect(vanillaType, duration, amplifier, true, false, true);
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
                        baseType, duration, baseAmplifier + (lvAmplifier * (level - 1)), customEffect.getType(), customEffect.isRemovable()
                ), true, EntityPotionEffectEvent.Cause.PLUGIN, EntityPotionEffectEvent.Action.ADDED);
            }
        }
    }
}
