package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.PotionEffect;
import me.athlaeos.valhallammo.items.PotionType;
import me.athlaeos.valhallammo.items.potioneffectwrappers.PotionEffectWrapper;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.function.Predicate;

public class PotionEffectManager {
    private static PotionEffectManager manager = null;

    private final NamespacedKey potionEffectKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_potion_effects");
    private final Map<String, PotionEffect> registeredPotionEffects = new HashMap<>();

    public PotionEffectManager(){
        registerPotionEffect(new PotionEffect("MASTERPIECE_SMITHING", 0, 0, PotionType.BUFF, false));
        registerPotionEffect(new PotionEffect("MASTERPIECE_ENCHANTING", 0, 0, PotionType.BUFF, false));
        registerPotionEffect(new PotionEffect("MASTERPIECE_ALCHEMY", 0, 0, PotionType.BUFF, false));
        registerPotionEffect(new PotionEffect("FORTIFY_ENCHANTING", 0, 0, PotionType.BUFF));
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
        registerPotionEffect(new PotionEffect("FORTIFY_TRADING", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("INCREASE_EXP", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("INCREASE_VANILLA_EXP", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("MILK", 0, 0, PotionType.BUFF));
        registerPotionEffect(new PotionEffect("CHOCOLATE_MILK", 0, 0, PotionType.BUFF));

        registerPotionEffect(new PotionEffect("POISON_ANTI_HEAL", 0, 0, PotionType.DEBUFF));
        registerPotionEffect(new PotionEffect("POISON_VULNERABLE", 0, 0, PotionType.DEBUFF));
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

    public void setActivePotionEffects(Entity p, Collection<PotionEffect> effects){
        for (PotionEffect eff : new HashMap<>(getActivePotionEffects(p)).values()){
            eff.setEffectiveUntil(0);
            addPotionEffect(p, eff, true);
        }
        if (effects == null) return;
        for (PotionEffect eff : effects){
            addPotionEffect(p, eff, true);
        }
    }

    public void removePotionEffects(Entity p, Predicate<PotionEffect> filter){
        for (PotionEffect eff : new HashMap<>(getActivePotionEffects(p)).values()){
            if (filter.test(eff)){
                eff.setEffectiveUntil(0);
                addPotionEffect(p, eff, true);
            }
        }
    }

    public void removePotionEffects(Entity p){
        for (PotionEffect eff : new HashMap<>(getActivePotionEffects(p)).values()){
            eff.setEffectiveUntil(0);
            addPotionEffect(p, eff, true);
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
    public void addPotionEffect(Entity p, PotionEffect effect, boolean force){
        if (effect == null) return;
        if (!registeredPotionEffects.containsKey(effect.getName())) {
            ValhallaMMO.getPlugin().getLogger().warning("[ValhallaMMO] Attempting to apply potion effect " + effect.getName() + ", but it was not registered");
            return;
        }
        Map<String, PotionEffect> currentPotionEffects = getActivePotionEffects(p);
        if (!force){
            if (currentPotionEffects.containsKey(effect.getName())){
                if (currentPotionEffects.get(effect.getName()).getAmplifier() > effect.getAmplifier()) {
                    return;
                }
            }
        }
        if (effect.getEffectiveUntil() != -1){
            if (effect.getEffectiveUntil() <= 0){
                currentPotionEffects.remove(effect.getName());
            } else {
                currentPotionEffects.put(effect.getName(), effect);
            }
        } else {
            currentPotionEffects.put(effect.getName(), effect);
        }
        setPotionEffects(p, new HashSet<>(currentPotionEffects.values()));
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
}
