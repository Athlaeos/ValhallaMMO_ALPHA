package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.dom.EffectAdditionMode;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class GlobalEffectManager {
    private static GlobalEffectManager manager = null;

    private final Map<String, Pair<Long, Double>> activeGlobalEffects = new HashMap<>();
    private final Collection<String> validEffects = new HashSet<>();

    public GlobalEffectManager(){
        addValidEffect("buff_exp_gain_all");
        addValidEffect("buff_exp_gain_smithing");
        addValidEffect("buff_exp_gain_enchanting");
        addValidEffect("buff_exp_gain_alchemy");
        addValidEffect("buff_exp_gain_farming");
        addValidEffect("buff_exp_gain_mining");
        addValidEffect("buff_exp_gain_landscaping");
    }

    /**
     * Adds a global effect with a given duration and amplifier
     * The chosen mode determines how the effect is added, if it already exists
     * ADDITIVE_DURATION stacks the given duration on top of the current duration. If an effect is already active with
     * 1 hour remaining, adding a new effect with this mode with a duration of 3 hours results in a final duration of 4 hours.
     * The amplifier is overwritten by the new
     * ADDITIVE_AMPLIFIER does the same thing, except with the amplifier. An existing amplifier of 50 would be amplified to 150
     * if a new effect with an amplifier of 100 was added with this mode. The duration is overwritten by the new
     * ADDITIVE_BOTH does both of the two modes. Neither of the two are overwritten
     * OVERWRITE does not add the effects, instead it completely overwrites them.
     * @param effect the effect to add
     * @param duration the duration of the effect in milliseconds
     * @param amplifier the amplifier of the effect
     * @param mode the mode the effect will be added in
     */
    public void addEffect(String effect, long duration, double amplifier, EffectAdditionMode mode){
        double existingAmplifier = getAmplifier(effect);
        long existingDuration = getDuration(effect);
        switch (mode){
            case ADDITIVE_BOTH: {
                existingAmplifier += amplifier;
                existingDuration += duration;
                break;
            }
            case ADDITIVE_DURATION: {
                existingDuration += duration;
                existingAmplifier = amplifier;
                break;
            }
            case ADDITIVE_AMPLIFIER: {
                existingAmplifier += amplifier;
                existingDuration = duration;
                break;
            }
            case OVERWRITE: {
                existingAmplifier = amplifier;
                existingDuration = duration;
                break;
            }
        }
        existingDuration += System.currentTimeMillis();
        activeGlobalEffects.put(effect, new Pair<>(existingDuration, existingAmplifier));
    }

    /**
     * Returns true if the given effect is active (duration left > 0)
     * @param buff the name of the effect
     * @return true if active, false if not
     */
    public boolean isActive(String buff){
        if (activeGlobalEffects.containsKey(buff)){
            return activeGlobalEffects.get(buff).getKey() > System.currentTimeMillis();
        }
        return false;
    }

    /**
     * Returns the remaining duration of the given effect
     * If not active, duration is 0
     * @param buff the name of the effect
     * @return the remaining duration (in milliseconds) of the effect
     */
    public long getDuration(String buff){
        if (activeGlobalEffects.containsKey(buff)){
            return Math.max(0, activeGlobalEffects.get(buff).getKey() - System.currentTimeMillis());
        }
        return 0;
    }

    /**
     * Returns the amplifier of the given effect
     * If not active, amplifier is 0
     * @param buff the name of the effect
     * @return the amplifier of the effect
     */
    public double getAmplifier(String buff){
        if (activeGlobalEffects.containsKey(buff)){
            return Math.max(0, activeGlobalEffects.get(buff).getValue());
        }
        return 0;
    }

    public static GlobalEffectManager getInstance(){
        if (manager == null) manager = new GlobalEffectManager();
        return manager;
    }

    public Collection<String> getValidEffects() {
        return validEffects;
    }

    public void addValidEffect(String effect){
        validEffects.add(effect);
    }

    public void removeValidEffect(String effect){
        validEffects.remove(effect);
    }

    public Map<String, Pair<Long, Double>> getActiveGlobalEffects() {
        return activeGlobalEffects;
    }

    private static class Pair<K, V>{
        private K key;
        private V value;
        public Pair(K key, V value){
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }
}
