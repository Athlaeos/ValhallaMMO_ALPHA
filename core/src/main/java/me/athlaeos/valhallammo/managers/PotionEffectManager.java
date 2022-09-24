package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.CombatType;
import me.athlaeos.valhallammo.dom.PotionEffect;
import me.athlaeos.valhallammo.events.EntityCustomPotionEffectEvent;
import me.athlaeos.valhallammo.events.ValhallaEntityStunEvent;
import me.athlaeos.valhallammo.items.PotionType;
import me.athlaeos.valhallammo.items.potioneffectwrappers.PotionEffectWrapper;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.function.Predicate;

public class PotionEffectManager {
    private static PotionEffectManager manager = null;

    private final NamespacedKey potionEffectKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_potion_effects");
    private final Map<String, PotionEffect> registeredPotionEffects = new HashMap<>();

    private final Map<PotionEffectType, Integer> stunPotionEffects;
    private final int stun_immunity_frame;

    public PotionEffectManager(){
        registerPotionEffect(new PotionEffect("MASTERPIECE_SMITHING", 0, 0, PotionType.BUFF, false));
        registerPotionEffect(new PotionEffect("MASTERPIECE_ENCHANTING", 0, 0, PotionType.BUFF, false));
        registerPotionEffect(new PotionEffect("MASTERPIECE_ALCHEMY", 0, 0, PotionType.BUFF, false));
        registerPotionEffect(new PotionEffect("FORTIFY_ENCHANTING", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("FORTIFY_ANVIL_COMBINING", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("FORTIFY_SMITHING", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("ALCHEMY_BREW_SPEED", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("ALCHEMY_INGREDIENT_SAVE", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("ALCHEMY_POTION_VELOCITY", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("ALCHEMY_POTION_SAVE", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("ARCHERY_ACCURACY", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("ARCHERY_DAMAGE", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("ARCHERY_AMMO_SAVE", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("UNARMED_DAMAGE", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("WEAPONS_DAMAGE", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("MINING_EXTRA_DROPS", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("MINING_RARE_DROPS", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("FARMING_EXTRA_DROPS", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("FARMING_RARE_DROPS", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("FARMING_FISHING_TIER", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("WOODCUTTING_EXTRA_DROPS", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("WOODCUTTING_RARE_DROPS", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("FORTIFY_ACROBATICS", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("ENTITY_EXTRA_DROPS", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("INCREASE_EXP", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("INCREASE_VANILLA_EXP", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("MILK", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("CHOCOLATE_MILK", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("ARMOR_FLAT_BONUS", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("LIGHT_ARMOR_FLAT_BONUS", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("HEAVY_ARMOR_FLAT_BONUS", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("ARMOR_FRACTION_BONUS", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("LIGHT_ARMOR_FRACTION_BONUS", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("HEAVY_ARMOR_FRACTION_BONUS", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("CUSTOM_DAMAGE_RESISTANCE", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("EXPLOSION_RESISTANCE", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("CUSTOM_FIRE_RESISTANCE", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("MAGIC_RESISTANCE", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("POISON_RESISTANCE", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("PROJECTILE_RESISTANCE", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("MELEE_RESISTANCE", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("FALLING_RESISTANCE", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("KNOCKBACK_RESISTANCE", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("BLEED_RESISTANCE", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("CRAFTING_TIME_REDUCTION", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("HUNGER_SAVE_CHANCE", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("DODGE_CHANCE", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("KNOCKBACK_BONUS", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("COOLDOWN_REDUCTION", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("IMMUNITY_FRAME_BONUS", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("IMMUNITY_FRAME_MULTIPLIER", 0, 0, PotionType.BUFF));

        registerPotionEffect(new PotionEffect("HEALING_BONUS", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("REFLECT_CHANCE", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("REFLECT_FRACTION", 0, 0, PotionType.BUFF));


        registerPotionEffect(new PotionEffect("POISON_ANTI_HEAL", 0, 0, PotionType.DEBUFF));
        registerPotionEffect(new PotionEffect("POISON_VULNERABLE", 0, 0, PotionType.DEBUFF));
        registerPotionEffect(new PotionEffect("FRACTION_ARMOR_REDUCTION", 0, 0, PotionType.DEBUFF));
        registerPotionEffect(new PotionEffect("FLAT_ARMOR_REDUCTION", 0, 0, PotionType.DEBUFF));


        stunPotionEffects = new HashMap<>();
        YamlConfiguration config = ConfigManager.getInstance().getConfig("config.yml").get();
        ConfigurationSection stunEffectSection = config.getConfigurationSection("stun_effects");
        stun_immunity_frame = config.getInt("stun_immunity_frame");
        if (stunEffectSection != null){
            for (String effect : stunEffectSection.getKeys(false)){
                PotionEffectType potionType = PotionEffectType.getByName(effect);
                if (potionType != null){
                    int intensity = config.getInt("stun_effects." + effect);
                    stunPotionEffects.put(potionType, intensity);
                }
            }
        }

        startBleedingRunnable();
    }

    /**
     * Stuns a target lasting the given duration, if a target already has potion effects where the amplifier is stronger
     * these effects are skipped
     * @param entity the entity to stun
     * @param duration the duration (in game-ticks) to stun the entity
     */
    public void stunTarget(LivingEntity entity, CombatType cause, int duration){
        double durationMultiplier = 1 - AccumulativeStatManager.getInstance().getStats("STUN_RESISTANCE", entity, true);
        ValhallaEntityStunEvent e = new ValhallaEntityStunEvent(entity, cause, durationMultiplier);
        e.setCancelled(!CooldownManager.getInstance().isCooldownPassed(entity.getUniqueId(), "stun_immunity_frame"));
        ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(e);
        if (!e.isCancelled()){
            duration = (int) (duration * e.getDurationMultiplier());
            for (PotionEffectType type : stunPotionEffects.keySet()){
                int intensity = stunPotionEffects.get(type);
                org.bukkit.potion.PotionEffect existingEffect = entity.getPotionEffect(type);
                if (existingEffect != null) {
                    if (existingEffect.getAmplifier() > intensity) continue;
                }
                entity.addPotionEffect(new org.bukkit.potion.PotionEffect(type, (int) (duration / 50D), intensity, true, false));
            }
            CooldownManager.getInstance().setCooldown(entity.getUniqueId(), stun_immunity_frame, "stun_immunity_frame");
        }
    }

    public boolean isStunned(LivingEntity entity){
        if (stunPotionEffects.isEmpty()) return false;
        for (PotionEffectType type : stunPotionEffects.keySet()){
            int intensity = stunPotionEffects.get(type);
            org.bukkit.potion.PotionEffect existingEffect = entity.getPotionEffect(type);
            if (existingEffect == null) return false;
            if (existingEffect.getAmplifier() != intensity) return false;
        }
        return true;
    }

    public void reload(){
        manager = null;
        getInstance();
    }

    /**
     * Gets all active(where effectiveUntil is greater than System.currentTimeMillis()) potion effects on the player.
     * Potion effects where the effectiveUntil is -1 are considered infinite and are added as well.
     * @param p the player to retrieve all potion effects from
     * @return a map of potion effects, a HashMap where the key is the effect's name and the value is the PotionEffect
     */
    public Map<String, PotionEffect> getActivePotionEffects(Entity p){
        Map<String, PotionEffect> potionEffects = new HashMap<>();
        if (p.getPersistentDataContainer().has(potionEffectKey, PersistentDataType.STRING)){
            String potionString = p.getPersistentDataContainer().get(potionEffectKey, PersistentDataType.STRING);
            assert potionString != null;
            String[] potionEffectStrings = potionString.split(";");
            for (String potionEffectString : potionEffectStrings){
                String[] args = potionEffectString.split(":");
                if (args.length == 3){
                    try {
                        String name = args[0];
                        PotionEffect basePotionEffect = registeredPotionEffects.get(name);
                        if (basePotionEffect == null) {
                            continue;
                        }
                        long effectiveUntil = Long.parseLong(args[1]);
                        double amplifier = Double.parseDouble(args[2]);
                        if (effectiveUntil != -1){
                            if (effectiveUntil < System.currentTimeMillis()) {
                                continue;
                            }
                        }
                        potionEffects.put(name, new PotionEffect(name, effectiveUntil, amplifier, basePotionEffect.getType()));
                    } catch (IllegalArgumentException ignored){
                    }
                }
            }
        }
        return potionEffects;
    }

    public void setActivePotionEffects(LivingEntity p, Collection<PotionEffect> effects){
        for (PotionEffect eff : new HashMap<>(getActivePotionEffects(p)).values()){
            eff.setEffectiveUntil(0);
            addPotionEffect(p, eff, true, EntityPotionEffectEvent.Cause.PLUGIN, EntityPotionEffectEvent.Action.ADDED);
        }
        if (effects == null) return;
        for (PotionEffect eff : effects){
            addPotionEffect(p, eff, true, EntityPotionEffectEvent.Cause.PLUGIN, EntityPotionEffectEvent.Action.ADDED);
        }
    }

    public void removePotionEffects(LivingEntity p, EntityPotionEffectEvent.Cause cause, Predicate<PotionEffect> filter){
        for (PotionEffect eff : new HashMap<>(getActivePotionEffects(p)).values()){
            if (filter.test(eff)){
                EntityCustomPotionEffectEvent event = new EntityCustomPotionEffectEvent(p, eff, null, cause, EntityPotionEffectEvent.Action.REMOVED, true);
                ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()){
                    eff.setEffectiveUntil(0);
                    addPotionEffect(p, eff, true, cause, EntityPotionEffectEvent.Action.REMOVED);
                }
            }
        }
    }

    public void removePotionEffects(LivingEntity p){
        for (PotionEffect eff : new HashMap<>(getActivePotionEffects(p)).values()){
            eff.setEffectiveUntil(0);
            addPotionEffect(p, eff, true, EntityPotionEffectEvent.Cause.MILK, EntityPotionEffectEvent.Action.REMOVED);
        }
    }

    /**
     * Sets a given set of PotionEffects to the player
     * @param p the player to set potion effects to
     * @param potionEffects the potion effects to set to the player
     */
    private void setPotionEffects(Entity p, Set<PotionEffect> potionEffects){
        Set<String> potionEffectStrings = new HashSet<>();
        for (PotionEffect effect : potionEffects){
            if (effect.getEffectiveUntil() != -1){
                if (effect.getEffectiveUntil() <= System.currentTimeMillis()) continue;
            }
            potionEffectStrings.add(
                    String.format("%s:%d:%.6f", effect.getName(), effect.getEffectiveUntil(), effect.getAmplifier())
            );
        }
        if (potionEffectStrings.size() == 0){
            p.getPersistentDataContainer().remove(potionEffectKey);
        } else {
            p.getPersistentDataContainer().set(potionEffectKey, PersistentDataType.STRING,
                    String.join(";", potionEffectStrings)
            );
        }
    }

    /**
     * Adds one potion effect to the player's active potion effects. If the duration of the effect is less than 0, it
     * is removed instead.
     * @param p the player to add a potion effect to
     * @param force if true, the potion effect will be applied regardless if it's weaker or not. If false, if the player
     *              already has the potion effect but with a stronger amplifier, it is not added
     * @param effect the effect to add to the player
     */
    public void addPotionEffect(LivingEntity p, PotionEffect effect, boolean force, EntityPotionEffectEvent.Cause cause, EntityPotionEffectEvent.Action action){
        if (effect == null) return;
        if (!registeredPotionEffects.containsKey(effect.getName())) {
            ValhallaMMO.getPlugin().getLogger().warning("Attempting to apply potion effect " + effect.getName() + ", but it was not registered");
            return;
        }
        Map<String, PotionEffect> currentPotionEffects = getActivePotionEffects(p);
        EntityCustomPotionEffectEvent event = new EntityCustomPotionEffectEvent(p, currentPotionEffects.get(effect.getName()), effect, cause, action, force);
        ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()){
            if (!event.isOverride()){
                if (currentPotionEffects.containsKey(event.getNewEffect().getName())){
                    if (currentPotionEffects.get(event.getNewEffect().getName()).getAmplifier() > event.getNewEffect().getAmplifier()) {
                        return;
                    }
                }
            }
            if (event.getNewEffect().getEffectiveUntil() != -1){
                if (event.getNewEffect().getEffectiveUntil() <= 0){
                    currentPotionEffects.remove(event.getNewEffect().getName());
                } else {
                    currentPotionEffects.put(event.getNewEffect().getName(), event.getNewEffect());
                }
            } else {
                currentPotionEffects.put(event.getNewEffect().getName(), event.getNewEffect());
            }
            setPotionEffects(p, new HashSet<>(currentPotionEffects.values()));
        }
    }

    /**
     * Retrieves the remaining duration (in milliseconds) of the potion effect with the given name.
     * This duration is the effect's effectiveUntil - System.currentTimeMillis()
     * @param p the player to retrieve the potion effect's duration from
     * @param name the name of the effect
     * @return the remaining duration of the effect, 0 if it does not exist/is expired, or -1 if the effect is infinite.
     */
    public long getRemainingPotionEffectDuration(Player p, String name){
        PotionEffect effect = getActivePotionEffects(p).get(name);
        if (effect != null) {
            if (effect.getEffectiveUntil() == -1) return -1;
            if (effect.getEffectiveUntil() - System.currentTimeMillis() > 0){
                return effect.getEffectiveUntil() - System.currentTimeMillis();
            }
        }
        return 0;
    }

    /**
     * Retrieves the active potion effect if present on the player of the given name, or null if it's not present
     * @param p the player to retrieve the active potion effect from
     * @param name the name of the potion effect
     * @return the PotionEffect if active on the player, or null if expired/doesn't exist
     */
    public PotionEffect getPotionEffect(Entity p, String name){
        PotionEffect effect = getActivePotionEffects(p).get(name);
        if (effect != null){
            if (effect.getEffectiveUntil() != -1){
                if (effect.getEffectiveUntil() < System.currentTimeMillis()) {
                    return null;
                }
            }
        }
        return effect;
    }

    public void registerPotionEffect(PotionEffect potionEffect){
        registeredPotionEffects.put(potionEffect.getName(), potionEffect);
    }

    public PotionEffect getBasePotionEffect(String name){
        return registeredPotionEffects.get(name);
    }

    public static PotionEffectManager getInstance(){
        if (manager == null) manager = new PotionEffectManager();
        return manager;
    }

    public Map<String, PotionEffect> getRegisteredPotionEffects() {
        return registeredPotionEffects;
    }

    public static void renamePotion(ItemStack i, boolean override){
        if (i == null) return;
        if (i.getItemMeta() instanceof PotionMeta){
            PotionMeta meta = (PotionMeta) i.getItemMeta();
            List<PotionEffectWrapper> potionEffects = new ArrayList<>(PotionAttributesManager.getInstance().getCurrentStats(i));
            boolean isTransmutationPotion = !TransmutationManager.getInstance().getStoredTransmutations(i).isEmpty();
            if (potionEffects.size() == 0 && !isTransmutationPotion) return;

            String formatToUse;
            if (!isTransmutationPotion){
                if (!meta.hasDisplayName() || override){
                    switch (i.getType()){
                        case SPLASH_POTION:{
                            formatToUse = TranslationManager.getInstance().getTranslation("potion_splash_format");
                            break;
                        }
                        case LINGERING_POTION:{
                            formatToUse = TranslationManager.getInstance().getTranslation("potion_lingering_format");
                            break;
                        }
                        case TIPPED_ARROW:{
                            formatToUse = TranslationManager.getInstance().getTranslation("tipped_arrow_format");
                            break;
                        }
                        default:{
                            formatToUse = TranslationManager.getInstance().getTranslation("potion_base_format");
                        }
                    }
                    String translation = TranslationManager.getInstance()
                            .getTranslation("effect_" + potionEffects.get(0).getPotionEffect().toLowerCase());
                    meta.setDisplayName(Utils.chat(
                            formatToUse.replace("%effect%", "" + translation)
                    ));
                }

            } else {
                formatToUse = TranslationManager.getInstance().getTranslation("transmutation_potion");
                meta.setDisplayName(Utils.chat(
                        formatToUse.replace("%effect%", "" + formatToUse)
                ));
            }

            i.setItemMeta(meta);
        }
    }

    private final Map<UUID, BleedingInstance> bleedingEntities = new HashMap<>();
    private void startBleedingRunnable(){
        int delay = ConfigManager.getInstance().getConfig("config.yml").get().getInt("bleed_delay", 20);
        new BukkitRunnable(){
            @Override
            public void run() {
                for (BleedingInstance instance : new HashSet<>(bleedingEntities.values())){
                    if (!instance.getBleedingEntity().isValid() || instance.getBleedingUntil() < System.currentTimeMillis()) {
                        bleedingEntities.remove(instance.getBleedingEntity().getUniqueId());
                    } else {
                        EntityDamageEvent event;
                        if (instance.getBleedingCausedBy() == null){
                            event = new EntityDamageEvent(instance.getBleedingEntity(), EntityDamageEvent.DamageCause.STARVATION,
                                    instance.getBleedingDamage());
                        } else {
                            event = new EntityDamageByEntityEvent(instance.getBleedingCausedBy(),
                                    instance.getBleedingEntity(), EntityDamageEvent.DamageCause.STARVATION, instance.getBleedingDamage());
                        }
                        bleedTick.add(instance.getBleedingEntity().getUniqueId());
                        instance.getBleedingEntity().setLastDamageCause(event);
                        ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(event);
                        int particleCount = (int) (3 * Math.min(10, event.getDamage()));
                        instance.getBleedingEntity().getWorld().spawnParticle(Particle.BLOCK_DUST, instance.getBleedingEntity().getLocation().add(0, 1, 0),
                                particleCount, 0.4, 0.4, 0.4, Material.REDSTONE_BLOCK.createBlockData());
                        instance.getBleedingEntity().playEffect(EntityEffect.HURT);
                    }
                }
            }
        }.runTaskTimer(ValhallaMMO.getPlugin(), 0L, delay);
    }

    private final Collection<UUID> bleedTick = new HashSet<>();

    public Collection<UUID> getBleedTicks() {
        return bleedTick;
    }

    public LivingEntity getBleedingCause(LivingEntity bleeder){
        BleedingInstance instance = bleedingEntities.get(bleeder.getUniqueId());
        if (instance == null) return null;
        return instance.getBleedingCausedBy();
    }

    public void bleedEntity(LivingEntity bleeder, LivingEntity causedBy, int duration, double damage){
        if (damage <= 0) return;
        BleedingInstance instance = bleedingEntities.get(bleeder.getUniqueId());
        if (instance != null){
            if (instance.getBleedingDamage() > damage) return;
        }
        damage *= (1 - AccumulativeStatManager.getInstance().getStats("BLEED_RESISTANCE", bleeder, causedBy, true));
        bleedingEntities.put(bleeder.getUniqueId(), new BleedingInstance(bleeder, causedBy, duration, Math.max(0, damage)));
    }

    public void removeBleeding(LivingEntity bleeder){
        bleedingEntities.remove(bleeder.getUniqueId());
    }

    public boolean isBleeding(LivingEntity bleeder){
        BleedingInstance instance = bleedingEntities.get(bleeder.getUniqueId());
        if (instance == null) return false;
        return instance.getBleedingUntil() >= System.currentTimeMillis();
    }

    private static class BleedingInstance{
        private final LivingEntity entityBleeding;
        private final LivingEntity bleedingCausedBy;
        private final long bleedingUntil;
        private final double bleedingDamage;

        public BleedingInstance(LivingEntity entityBleeding, LivingEntity bleedingCausedBy, long duration, double bleedingDamage){
            this.entityBleeding = entityBleeding;
            this.bleedingCausedBy = bleedingCausedBy;
            this.bleedingUntil = System.currentTimeMillis() + duration;
            this.bleedingDamage = bleedingDamage;
        }

        public LivingEntity getBleedingEntity() {
            return entityBleeding;
        }

        public LivingEntity getBleedingCausedBy() {
            return bleedingCausedBy;
        }

        public long getBleedingUntil() {
            return bleedingUntil;
        }

        public double getBleedingDamage() {
            return bleedingDamage;
        }
    }
}
