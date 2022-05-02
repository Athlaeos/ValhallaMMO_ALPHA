package me.athlaeos.valhallammo.skills.lightarmor;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.ArmorType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.events.PlayerEnterCombatEvent;
import me.athlaeos.valhallammo.events.PlayerLeaveCombatEvent;
import me.athlaeos.valhallammo.events.PlayerSkillExperienceGainEvent;
import me.athlaeos.valhallammo.listeners.EntityDamagedListener;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.managers.CooldownManager;
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

public class LightArmorSkill extends Skill implements OffensiveSkill, PotionEffectSkill, CombatSkill {
    private final double exp_damage_piece;
    private final double exp_second_piece;

    private final Collection<AdrenalinePotionEffect> adrenalinePotionEffects = new HashSet<>();

    public LightArmorSkill(String type) {
        super(type);

        YamlConfiguration lightArmorConfig = ConfigManager.getInstance().getConfig("skill_light_armor.yml").get();
        YamlConfiguration progressionConfig = ConfigManager.getInstance().getConfig("progression_light_armor.yml").get();

        this.loadCommonConfig(lightArmorConfig, progressionConfig);

        exp_damage_piece = progressionConfig.getDouble("experience.exp_damage_piece");
        exp_second_piece = progressionConfig.getDouble("experience.exp_second_piece");

        for (String potionEffectString : lightArmorConfig.getStringList("adrenaline_effects")){
            String[] args = potionEffectString.split(";");
            if (args.length < 5) {
                ValhallaMMO.getPlugin().getServer().getLogger().warning("Could not register Adrenaline potion effect, not enough arguments: POTIONEFFECT;AMPLIFIERBASE;DURATIONBASE;AMPLIFIERLV;DURATIONLV");
                continue;
            }
            try {
                PotionEffectType potionType = PotionEffectType.getByName(args[0]);
                if (potionType == null) throw new IllegalArgumentException();
                double baseAmplifier = Double.parseDouble(args[1]);
                int baseDuration = Integer.parseInt(args[2]);
                double lvAmplifier = Double.parseDouble(args[3]);
                int lvDuration = Integer.parseInt(args[4]);
                adrenalinePotionEffects.add(new AdrenalinePotionEffect(potionType, baseAmplifier, baseDuration, lvAmplifier, lvDuration));
            } catch (IllegalArgumentException ignored){
                ValhallaMMO.getPlugin().getServer().getLogger().warning("Could not register Adrenaline potion effect " + args[0] + ", either it's not a valid potion effect or its arguments are not numbers!");
            }
        }
    }

    @Override
    public NamespacedKey getKey() {
        return new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_profile_light_armor");
    }

    @Override
    public Profile getCleanProfile() {
        return new LightArmorProfile(null);
    }

    @Override
    public void addEXP(Player p, double amount, boolean silent, PlayerSkillExperienceGainEvent.ExperienceGainReason reason) {
        double finalAmount = amount * ((AccumulativeStatManager.getInstance().getStats("LIGHT_ARMOR_EXP_GAIN", p, true) / 100D));
        super.addEXP(p, finalAmount, silent, reason);
    }

    @Override
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        double originalDamage = event.getDamage();
        Player p = (Player) event.getEntity();
        int lightArmorCount = ArmorType.getArmorTypeCount(p, ArmorType.LIGHT);
        addEXP(p, originalDamage * exp_damage_piece * lightArmorCount, false, PlayerSkillExperienceGainEvent.ExperienceGainReason.SKILL_ACTION);

        boolean isWearingEnoughLightArmor = false;
        Profile profile = ProfileManager.getManager().getProfile(p, "LIGHT_ARMOR");
        if (profile != null){
            if (profile instanceof LightArmorProfile){
                int needed = ((LightArmorProfile) profile).getArmorPiecesForBonusses();
                isWearingEnoughLightArmor = lightArmorCount >= needed;
            }
        }

        if (isWearingEnoughLightArmor){
            if (CooldownManager.getInstance().isCooldownPassed(p.getUniqueId(), "adrenaline_cooldown")){
                int adrenalineCooldown = ((LightArmorProfile) profile).getAdrenalineCooldown();
                int adrenalineLevel = ((LightArmorProfile) profile).getAdrenalineLevel();
                if (adrenalineCooldown >= 0 && adrenalineLevel > 0){
                    AttributeInstance maxHealthAttribute = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                    if (maxHealthAttribute != null){
                        new BukkitRunnable(){
                            @Override
                            public void run() {
                                double damageTaken = EntityDamagedListener.getLastDamageTakenMap().getOrDefault(p.getUniqueId(), event.getFinalDamage());
                                if (p.getHealth() - damageTaken <= maxHealthAttribute.getValue() * ((LightArmorProfile) profile).getAdrenalineThreshold()){
                                    for (AdrenalinePotionEffect effect : adrenalinePotionEffects){
                                        PotionEffect potionEffectToAdd = effect.getPotionEffect(adrenalineLevel);
                                        if (potionEffectToAdd == null) continue;
                                        PotionEffect potionEffectToReplace = p.getPotionEffect(potionEffectToAdd.getType());
                                        if (potionEffectToReplace != null){
                                            if (potionEffectToReplace.getDuration() > potionEffectToAdd.getDuration()
                                                    || potionEffectToReplace.getAmplifier() > potionEffectToAdd.getAmplifier()) continue;
                                            // if either the player's existing potion effect duration or amplifier exceeds
                                            // adrenaline's potion effect, it is not replaced.
                                        }
                                        p.addPotionEffect(potionEffectToAdd);
                                    }
                                    CooldownManager.getInstance().setCooldownIgnoreIfPermission(p, adrenalineCooldown, "adrenaline_cooldown");
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
            Profile profile = ProfileManager.getManager().getProfile(p, "LIGHT_ARMOR");
            if (profile != null){
                if (profile instanceof LightArmorProfile){
                    if (((LightArmorProfile) profile).getImmunePotionEffects().contains(event.getNewEffect().getType().toString())){
                        event.setCancelled(true);
                    }
                }
            }
        }
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
        long timeInCombat = event.getTimeInCombat(System.currentTimeMillis());
        int armorCount = ArmorType.getArmorTypeCount(event.getPlayer(), ArmorType.LIGHT);
        int expRewardTimes = (int) (timeInCombat / 1000D);

        addEXP(event.getPlayer(), expRewardTimes * armorCount * exp_second_piece, false, PlayerSkillExperienceGainEvent.ExperienceGainReason.SKILL_ACTION);
    }

    private static class AdrenalinePotionEffect{
        private final PotionEffectType type;
        private final double baseAmplifier;
        private final int baseDuration;
        private final double lvAmplifier;
        private final int lvDuration;

        public AdrenalinePotionEffect(PotionEffectType type, double baseAmplifier, int baseDuration, double lvAmplifier, int lvDuration){
            this.type = type;
            this.baseAmplifier = baseAmplifier;
            this.baseDuration = baseDuration;
            this.lvAmplifier = lvAmplifier;
            this.lvDuration = lvDuration;
        }

        public PotionEffect getPotionEffect(int level){
            int amplifier = (int) Math.floor(baseAmplifier + (lvAmplifier * (level - 1)));
            int duration = baseDuration + (lvDuration * (level - 1));
            if (amplifier <= 0) return null; // if an amplifier in the config is below 1 it should not apply the effect
            return new PotionEffect(type, duration, amplifier, true, false, true);
        }
    }
}
