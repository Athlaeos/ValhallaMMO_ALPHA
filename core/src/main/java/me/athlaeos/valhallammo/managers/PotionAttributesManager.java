package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.ArtificialGlow;
import me.athlaeos.valhallammo.items.potioneffectwrappers.CustomPotionEffectWrapper;
import me.athlaeos.valhallammo.items.potioneffectwrappers.PotionEffectWrapper;
import me.athlaeos.valhallammo.items.potioneffectwrappers.VanillaPotionEffectWrapper;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.*;
import java.util.stream.Collectors;

public class PotionAttributesManager {
    private static PotionAttributesManager manager = null;
    //    private final Map<String, Collection<PotionEffectWrapper>> defaultVanillaPotionStats;
    private final Map<String, PotionEffectWrapper> registeredPotionEffects;
    // key used to save all default vanilla and custom attributes to the item
    private final NamespacedKey defaultPotionEffectsKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_default_potion_effects");
    // key used to save all current custom attributes to the item
    private final NamespacedKey customPotionEffectsKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_custom_potion_effects");

    public void reload() {
        manager = null;
        getInstance();
    }

    public PotionAttributesManager() {
//        defaultVanillaPotionStats = new HashMap<>();
        registeredPotionEffects = new HashMap<>();

        registerPotionEffect(new VanillaPotionEffectWrapper("HEAL", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("BAD_OMEN", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("HARM", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("SATURATION", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("ABSORPTION", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("BLINDNESS", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("CONDUIT_POWER", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("CONFUSION", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("DAMAGE_RESISTANCE", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("FAST_DIGGING", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("FIRE_RESISTANCE", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("GLOWING", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("HEALTH_BOOST", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("HERO_OF_THE_VILLAGE", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("HUNGER", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("INCREASE_DAMAGE", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("INVISIBILITY", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("JUMP", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("LEVITATION", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("LUCK", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("NIGHT_VISION", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("POISON", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("REGENERATION", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("SLOW", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("SLOW_DIGGING", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("SLOW_FALLING", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("SPEED", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("UNLUCK", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("WATER_BREATHING", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("WEAKNESS", 0, 0));
        registerPotionEffect(new VanillaPotionEffectWrapper("WITHER", 0, 0));

        registerPotionEffect(new CustomPotionEffectWrapper("MASTERPIECE_SMITHING", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_masterpiece_smithing"), "+%.1f", false));
        registerPotionEffect(new CustomPotionEffectWrapper("MASTERPIECE_ENCHANTING", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_masterpiece_enchanting"), "+%.1f", false));
        registerPotionEffect(new CustomPotionEffectWrapper("FORTIFY_ENCHANTING", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_fortify_enchanting"), "+%.1f", false));
        registerPotionEffect(new CustomPotionEffectWrapper("FORTIFY_SMITHING", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_fortify_smithing"), "+%.1f", false));
        registerPotionEffect(new CustomPotionEffectWrapper("MINING_EXTRA_DROPS", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_mining_extra_drops"), "+%.1f%%", false));
        registerPotionEffect(new CustomPotionEffectWrapper("MINING_RARE_DROPS", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_mining_rare_drops"), "+%.1f%%", false));
        registerPotionEffect(new CustomPotionEffectWrapper("FARMING_EXTRA_DROPS", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_farming_extra_drops"), "+%.1f%%", false));
        registerPotionEffect(new CustomPotionEffectWrapper("FARMING_RARE_DROPS", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_farming_rare_drops"), "+%.1f%%", false));
        registerPotionEffect(new CustomPotionEffectWrapper("FARMING_FISHING_TIER", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_farming_fishing_tier"), "+%.1f", false));
        registerPotionEffect(new CustomPotionEffectWrapper("WOODCUTTING_EXTRA_DROPS", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_woodcutting_extra_drops"), "+%.1f%%", false));
        registerPotionEffect(new CustomPotionEffectWrapper("WOODCUTTING_RARE_DROPS", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_woodcutting_rare_drops"), "+%.1f%%", false));
        registerPotionEffect(new CustomPotionEffectWrapper("FORTIFY_ACROBATICS", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_fortify_acrobatics"), "+%.1f", false));
        registerPotionEffect(new CustomPotionEffectWrapper("ENTITY_EXTRA_DROPS", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_entity_extra_drops"), "+%.1f%%", false));
        registerPotionEffect(new CustomPotionEffectWrapper("ALCHEMY_BREW_SPEED", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_alchemy_brew_speed"), "+%.1f%%", false));
        registerPotionEffect(new CustomPotionEffectWrapper("ALCHEMY_INGREDIENT_SAVE", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_alchemy_ingredient_save"), "+%.1f%%", false));
        registerPotionEffect(new CustomPotionEffectWrapper("ALCHEMY_POTION_SAVE", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_alchemy_potion_save"), "+%.1f%%", false));
        registerPotionEffect(new CustomPotionEffectWrapper("ALCHEMY_POTION_VELOCITY", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_alchemy_potion_velocity"), "+%.1f%%", false));
        registerPotionEffect(new CustomPotionEffectWrapper("ARCHERY_ACCURACY", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_archery_accuracy"), "+%.1f", false));
        registerPotionEffect(new CustomPotionEffectWrapper("ARCHERY_DAMAGE", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_archery_damage"), "+%.1f%%", false, 100));
        registerPotionEffect(new CustomPotionEffectWrapper("ARCHERY_AMMO_SAVE", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_archery_ammo_save"), "+%.1f%%", false, 100));
        registerPotionEffect(new CustomPotionEffectWrapper("UNARMED_DAMAGE", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_unarmed_damage"), "+%.1f%%", false));
        registerPotionEffect(new CustomPotionEffectWrapper("WEAPONS_DAMAGE", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_weapons_damage"), "+%.1f%%", false));
        registerPotionEffect(new CustomPotionEffectWrapper("INCREASE_EXP", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_increase_exp"), "+%.1f%%", false));
        registerPotionEffect(new CustomPotionEffectWrapper("POISON_ANTI_HEAL", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_poison_anti_heal"), "+%.1f%%", false));
        registerPotionEffect(new CustomPotionEffectWrapper("POISON_VULNERABLE", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_poison_vulnerable"), "+%.1f%%", false));
        registerPotionEffect(new CustomPotionEffectWrapper("MILK", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_milk"), "", true));
        registerPotionEffect(new CustomPotionEffectWrapper("CHOCOLATE_MILK", 0, 0, TranslationManager.getInstance().getTranslation("potion_effect_chocolate_milk"), "", true));

        registerPotionEffect(new CustomPotionEffectWrapper("ARMOR_FLAT_BONUS", 0, 0, TranslationManager.getInstance().getTranslation("effect_armor_flat_bonus"), "+%.1f", false));
        registerPotionEffect(new CustomPotionEffectWrapper("LIGHT_ARMOR_FLAT_BONUS", 0, 0, TranslationManager.getInstance().getTranslation("effect_light_armor_flat_bonus"), "+%.1f", false));
        registerPotionEffect(new CustomPotionEffectWrapper("HEAVY_ARMOR_FLAT_BONUS", 0, 0, TranslationManager.getInstance().getTranslation("effect_heavy_armor_flat_bonus"), "+%.1f", false));
        registerPotionEffect(new CustomPotionEffectWrapper("ARMOR_FRACTION_BONUS", 0, 0, TranslationManager.getInstance().getTranslation("effect_armor_fraction_bonus"), "+%.1f%%", false, 100));
        registerPotionEffect(new CustomPotionEffectWrapper("LIGHT_ARMOR_FRACTION_BONUS", 0, 0, TranslationManager.getInstance().getTranslation("effect_light_armor_fraction_bonus"), "+%.1f%%", false, 100));
        registerPotionEffect(new CustomPotionEffectWrapper("HEAVY_ARMOR_FRACTION_BONUS", 0, 0, TranslationManager.getInstance().getTranslation("effect_heavy_armor_fraction_bonus"), "+%.1f%%", false, 100));
        registerPotionEffect(new CustomPotionEffectWrapper("CUSTOM_DAMAGE_RESISTANCE", 0, 0, TranslationManager.getInstance().getTranslation("effect_custom_damage_resistance"), "+%.1f%%", false, 100));
        registerPotionEffect(new CustomPotionEffectWrapper("EXPLOSION_RESISTANCE", 0, 0, TranslationManager.getInstance().getTranslation("effect_explosion_resistance"), "+%.1f%%", false, 100));
        registerPotionEffect(new CustomPotionEffectWrapper("CUSTOM_FIRE_RESISTANCE", 0, 0, TranslationManager.getInstance().getTranslation("effect_custom_fire_resistance"), "+%.1f%%", false, 100));
        registerPotionEffect(new CustomPotionEffectWrapper("MAGIC_RESISTANCE", 0, 0, TranslationManager.getInstance().getTranslation("effect_magic_resistance"), "+%.1f%%", false, 100));
        registerPotionEffect(new CustomPotionEffectWrapper("POISON_RESISTANCE", 0, 0, TranslationManager.getInstance().getTranslation("effect_poison_resistance"), "+%.1f%%", false, 100));
        registerPotionEffect(new CustomPotionEffectWrapper("PROJECTILE_RESISTANCE", 0, 0, TranslationManager.getInstance().getTranslation("effect_projectile_resistance"), "+%.1f%%", false, 100));
        registerPotionEffect(new CustomPotionEffectWrapper("MELEE_RESISTANCE", 0, 0, TranslationManager.getInstance().getTranslation("effect_melee_resistance"), "+%.1f%%", false, 100));
        registerPotionEffect(new CustomPotionEffectWrapper("FALLING_RESISTANCE", 0, 0, TranslationManager.getInstance().getTranslation("effect_falling_resistance"), "+%.1f%%", false, 100));
        registerPotionEffect(new CustomPotionEffectWrapper("KNOCKBACK_RESISTANCE", 0, 0, TranslationManager.getInstance().getTranslation("effect_knockback_resistance"), "+%.1f%%", false, 100));
        registerPotionEffect(new CustomPotionEffectWrapper("BLEED_RESISTANCE", 0, 0, TranslationManager.getInstance().getTranslation("effect_bleed_resistance"), "+%.1f%%", false, 100));
        registerPotionEffect(new CustomPotionEffectWrapper("CRAFTING_TIME_REDUCTION", 0, 0, TranslationManager.getInstance().getTranslation("effect_crafting_time_reduction"), "+%.1f%%", false, 100));
        registerPotionEffect(new CustomPotionEffectWrapper("HUNGER_SAVE_CHANCE", 0, 0, TranslationManager.getInstance().getTranslation("effect_hunger_save_chance"), "+%.1f%%", false, 100));
        registerPotionEffect(new CustomPotionEffectWrapper("DODGE_CHANCE", 0, 0, TranslationManager.getInstance().getTranslation("effect_dodge_chance"), "+%.1f%%", false, 100));
        registerPotionEffect(new CustomPotionEffectWrapper("KNOCKBACK_BONUS", 0, 0, TranslationManager.getInstance().getTranslation("effect_knockback_bonus"), "+%.1f%%", false, 100));
        registerPotionEffect(new CustomPotionEffectWrapper("COOLDOWN_REDUCTION", 0, 0, TranslationManager.getInstance().getTranslation("effect_cooldown_reduction"), "+%.1f%%", false, 100));
        registerPotionEffect(new CustomPotionEffectWrapper("IMMUNITY_FRAME_BONUS", 0, 0, TranslationManager.getInstance().getTranslation("effect_immunity_frame_bonus"), "+%.0f", false));
        registerPotionEffect(new CustomPotionEffectWrapper("IMMUNITY_FRAME_MULTIPLIER", 0, 0, TranslationManager.getInstance().getTranslation("effect_immunity_frame_multiplier"), "+%.1f%%", false, 100));
        registerPotionEffect(new CustomPotionEffectWrapper("HEALING_BONUS", 0, 0, TranslationManager.getInstance().getTranslation("effect_healing_bonus"), "+%.1f%%", false, 100));
        registerPotionEffect(new CustomPotionEffectWrapper("REFLECT_CHANCE", 0, 0, TranslationManager.getInstance().getTranslation("effect_reflect_chance"), "+%.1f%%", false, 100));
        registerPotionEffect(new CustomPotionEffectWrapper("REFLECT_FRACTION", 0, 0, TranslationManager.getInstance().getTranslation("effect_reflect_fraction"), "+%.1f%%", false, 100));
        registerPotionEffect(new CustomPotionEffectWrapper("FRACTION_ARMOR_REDUCTION", 0, 0, TranslationManager.getInstance().getTranslation("effect_fraction_armor_reduction"), "+%.1f%%", false, 100));
        registerPotionEffect(new CustomPotionEffectWrapper("FLAT_ARMOR_REDUCTION", 0, 0, TranslationManager.getInstance().getTranslation("effect_flat_armor_reduction"), "+%.1f", false));
    }

    public void registerPotionEffect(PotionEffectWrapper wrapper) {
        registeredPotionEffects.put(wrapper.getPotionEffect(), wrapper);
    }

    /**
     * Writes all the given default attributes to the item's metadata, its current stats are also updated
     *
     * @param i          the item to define its default attributes on
     * @param attributes the attributes to define the item's default attributes with
     */
    public void setDefaultPotionEffects(ItemStack i, Collection<PotionEffectWrapper> attributes) {
        if (i == null) return;
        assert i.getItemMeta() != null;
        ItemMeta meta = i.getItemMeta();
        if (attributes.isEmpty()) {
            meta.getPersistentDataContainer().remove(defaultPotionEffectsKey);
            meta.setAttributeModifiers(null);
        } else {
            Collection<String> stringAttributes = new HashSet<>();
            for (PotionEffectWrapper wrapper : attributes) {
                stringAttributes.add(wrapper.getPotionEffect() + ":" + wrapper.getAmplifier() + ":" + wrapper.getDuration());
            }
            String defaultAttributes = String.join(";", stringAttributes);
            meta.getPersistentDataContainer().set(defaultPotionEffectsKey, PersistentDataType.STRING, defaultAttributes);
        }
        i.setItemMeta(meta);
    }

    /**
     * Returns all the attribute stats the item has by default, this does not imply vanilla stats as they can be removed.
     * This will be a map where its key is the attribute name and its value is the AttributeWrapper associated with it.
     *
     * @param i the item to get its default attribute stats from
     * @return the default attributes of the item
     */
    public Collection<PotionEffectWrapper> getDefaultPotionEffects(ItemStack i) {
        if (i == null) return null;
        Collection<PotionEffectWrapper> attributes = new HashSet<>();
        assert i.getItemMeta() != null;
        ItemMeta meta = i.getItemMeta();
        if (meta.getPersistentDataContainer().has(defaultPotionEffectsKey, PersistentDataType.STRING)) {
            String potionEffectString = meta.getPersistentDataContainer().get(defaultPotionEffectsKey, PersistentDataType.STRING);
            if (potionEffectString != null) {
                String[] defaultPotionEffects = potionEffectString.split(";");
                for (String a : defaultPotionEffects) {
                    String[] attributeProperties = a.split(":");
                    if (attributeProperties.length >= 3) {
                        String potionEffect = attributeProperties[0];
                        String amplifier = attributeProperties[1];
                        String duration = attributeProperties[2];
                        try {
                            if (registeredPotionEffects.containsKey(potionEffect)) {
                                PotionEffectWrapper wrapper = registeredPotionEffects.get(potionEffect).clone();
                                wrapper.setAmplifier(Double.parseDouble(amplifier));
                                wrapper.setDuration(Integer.parseInt(duration));

                                attributes.add(wrapper);
                            } else {
                                ValhallaMMO.getPlugin().getLogger().warning("Attempting to grab potion effect " + potionEffect + " but it was not registered.");
                            }
                        } catch (IllegalArgumentException | CloneNotSupportedException e) {
                            ValhallaMMO.getPlugin().getLogger().warning("Malformed potion metadata on item " + i.getType() + ", attempted to parse double value " + amplifier + " and duration " + duration + ", but they could not be parsed.");
                        }
                    } else {
                        ValhallaMMO.getPlugin().getLogger().warning("Malformed potion metadata on item " + i.getType() + ", notify plugin author. Expected property length 3 or more, but it was less.");
                    }
                }
            } //else {
            //attributes.addAll(getVanillaStats(i));
            //}
        } //else {
        //attributes.addAll(getVanillaStats(i));
        //}
        return attributes;
    }

    /**
     * Returns all the vanilla and custom potion effect stats the item has. This will be a map where its key is the potion effect
     * name and its value is the PotionEffectWrapper associated with it.
     *
     * @param i the item to get its stats from
     * @return the potion effect stats of the item
     */
    public Collection<PotionEffectWrapper> getCurrentStats(ItemStack i) {
        if (i == null) return null;
        Collection<PotionEffectWrapper> attributes = new HashSet<>();
        ItemMeta meta = i.getItemMeta();
        if (meta == null) return attributes;
        if (meta.getPersistentDataContainer().has(customPotionEffectsKey, PersistentDataType.STRING)) {
            String customPotionsString = meta.getPersistentDataContainer().get(customPotionEffectsKey, PersistentDataType.STRING);
            if (customPotionsString != null) {
                String[] customEffects = customPotionsString.split(";");
                for (String a : customEffects) {
                    String[] attributeProperties = a.split(":");
                    if (attributeProperties.length >= 3) {
                        String attribute = attributeProperties[0];
                        String amplifier = attributeProperties[1];
                        String duration = attributeProperties[2];
                        try {
                            if (registeredPotionEffects.containsKey(attribute)) {
                                PotionEffectWrapper wrapper = registeredPotionEffects.get(attribute).clone();
                                wrapper.setAmplifier(Double.parseDouble(amplifier));
                                wrapper.setDuration(Integer.parseInt(duration));
                                attributes.add(wrapper);
                            } else {
                                ValhallaMMO.getPlugin().getLogger().warning("Attempting to grab potion effect " + attribute + " but it was not registered.");
                            }
                        } catch (IllegalArgumentException | CloneNotSupportedException e) {
                            ValhallaMMO.getPlugin().getLogger().warning("Malformed potion metadata on item " + i.getType() + ", attempted to parse double value " + amplifier + " and int " + duration + ", but they could not be parsed.");
                        }
                    } else {
                        ValhallaMMO.getPlugin().getLogger().warning("Malformed potion metadata on item " + i.getType() + ", notify plugin author. Expected property length 3 or more, but it was less.");
                    }
                }
            }
        }
//        Multimap<Attribute, AttributeModifier> attributeMap = meta.getAttributeModifiers();
//        if (attributeMap != null){
//            for (Attribute a : attributeMap.keySet()){
//                if (registeredAttributes.containsKey("" + a)){
//                    try {
//                        for (AttributeModifier m : attributeMap.get(a)){
//                            AttributeWrapper attribute = registeredAttributes.get("" + a).clone();
//                            attribute.setAmount(m.getAmount());
//                            attribute.setOperation(m.getOperation());
//                            attribute.setSlot(m.getSlot());
//                            attributes.put(attribute.getAttribute(), attribute);
//                        }
//                    } catch (CloneNotSupportedException ignored){
//                    }
//                }
//            }
//        } else {
//            attributes.putAll(getVanillaStats(i));
//        }
        return attributes;
    }

    /**
     * Returns all stats the item has in vanilla minecraft. For example, a diamond chestplate has 8 armor and 2 toughness
     * This will be a map where its key is the attribute name and its value is the AttributeWrapper associated with it.
     * @param i the item to get its vanilla stats from
     * @return the stats the item has in vanilla minecraft, or an empty map if it has none.
     */
//    public Collection<PotionEffectWrapper> getVanillaStats(ItemStack i){
//        Collection<PotionEffectWrapper> potionEffects = new HashSet<>();
//        if (i == null) return potionEffects;
//        if (!(i.getItemMeta() instanceof PotionMeta)) return potionEffects;
//        PotionMeta meta = (PotionMeta) i.getItemMeta();
//        String key = meta.getBasePotionData().getType() + ";";
//        if (meta.getBasePotionData().isExtended()){
//            key += "_EXT";
//        } else if (meta.getBasePotionData().isUpgraded()){
//            key += "_UPG";
//        } else {
//            key += "_BASE";
//        }
//        if (defaultVanillaPotionStats.containsKey(key)){
//            potionEffects = defaultVanillaPotionStats.get(key);
//        }
//        return potionEffects;
//    }

    /**
     * Sets the given attributes to the item, but only if item has the attribute type in its default attributes.
     *
     * @param i          the item to set its attributes to
     * @param attributes the attributes to set to the item
     */
    public void setStats(ItemStack i, Collection<PotionEffectWrapper> attributes) {
        if (i == null) return;
        if (!(i.getItemMeta() instanceof PotionMeta)) return;
        PotionMeta meta = (PotionMeta) i.getItemMeta();
        if (attributes != null) {
            if (!attributes.isEmpty()) {
                meta.clearCustomEffects();
                for (PotionEffectWrapper registeredAttributeWrapper : registeredPotionEffects.values()) {
                    registeredAttributeWrapper.onRemove(meta);
                }

                Collection<PotionEffectWrapper> customPotionEffects = new HashSet<>();
                for (PotionEffectWrapper potionEffect : new HashSet<>(attributes)) {
                    if (getDefaultPotionEffects(i).stream().noneMatch(potionEffectWrapper -> potionEffectWrapper.getPotionEffect().equals(potionEffect.getPotionEffect()))) {
                        continue;
                    }
                    try {
                        PotionEffectType vanillaAttribute = PotionEffectType.getByName(potionEffect.getPotionEffect());
                        if (vanillaAttribute == null) throw new IllegalArgumentException();
                        int amplifier = (int) Math.floor(potionEffect.getAmplifier());
                        int duration = potionEffect.getDuration();
                        if (i.getType() == Material.LINGERING_POTION) {
                            duration *= 4;
                        }
                        if (i.getType() == Material.TIPPED_ARROW) duration *= 8;
                        //vanilla potion effects being applied
                        meta.addCustomEffect(new PotionEffect(vanillaAttribute, duration, amplifier), true);
                        customPotionEffects.add(potionEffect);
                    } catch (IllegalArgumentException ignored) {
                        //custom potion effects being applied
                        if (!(potionEffect.getPotionEffect().equals("MILK") || potionEffect.getPotionEffect().equals("CHOCOLATE_MILK"))) {
                            meta.addEnchant(new ArtificialGlow(), 0, true);
                        }
                        customPotionEffects.add(potionEffect);
                    }
                }
                if (!customPotionEffects.isEmpty()) {
                    List<String> customAttributeStringComponents = new ArrayList<>();
                    for (PotionEffectWrapper wrapper : customPotionEffects) {
                        customAttributeStringComponents.add(wrapper.getPotionEffect() + ":" + wrapper.getAmplifier() + ":" + wrapper.getDuration());
                        wrapper.onApply(meta);
                    }
                    meta.getPersistentDataContainer().set(customPotionEffectsKey, PersistentDataType.STRING, String.join(";", customAttributeStringComponents));
                } else {
                    meta.getPersistentDataContainer().remove(customPotionEffectsKey);
                }
                i.setItemMeta(meta);
                return;
            }
        }
        meta.setBasePotionData(new PotionData(PotionType.UNCRAFTABLE, false, false));
        meta.clearCustomEffects();
        for (PotionEffectWrapper registeredAttributeWrapper : registeredPotionEffects.values()) {
            registeredAttributeWrapper.onRemove(meta);
        }
        meta.getPersistentDataContainer().remove(customPotionEffectsKey);
        i.setItemMeta(meta);
        //remove attributes
    }

    /**
     * Stores all the item's vanilla stats to the item's PersistentDataContainer. If the item has no vanilla attributes,
     * nothing happens. Example: Items like a diamond chestplate will be assigned 8 armor and 2 toughness
     * The attribute modifiers will also be applied to the item
     *
     * @param i the item to set vanilla attributes to.
     */
//    public void applyVanillaStats(ItemStack i){
//        if (i == null) return;
//        Collection<PotionEffectWrapper> vanillaStats = new HashSet<>(getVanillaStats(i));
//        if (vanillaStats.size() > 0){
//            ItemMeta itemMeta = i.getItemMeta();
//            if (itemMeta == null) return;
//            setDefaultPotionEffects(i, vanillaStats);
//        }
//    }
    public PotionEffectWrapper getPotionEffectWrapper(ItemStack i, String attribute) {
        if (i == null) return null;
        List<PotionEffectWrapper> wrappers = getCurrentStats(i).stream().filter(potionEffectWrapper -> potionEffectWrapper.getPotionEffect().equals(attribute)).collect(Collectors.toList());
        if (wrappers.size() > 0) {
            return wrappers.get(0);
        }
        return null;
    }

    public PotionEffectWrapper getDefaultPotionEffect(ItemStack i, String attribute) {
        if (i == null) return null;
        List<PotionEffectWrapper> wrappers = getDefaultPotionEffects(i).stream().filter(potionEffectWrapper -> potionEffectWrapper.getPotionEffect().equals(attribute)).collect(Collectors.toList());
        if (wrappers.size() > 0) {
            return wrappers.get(0);
        }
        return null;
    }

    /**
     * Adds a new attribute to the item's default stats
     *
     * @param i                   the item to add the attribute to
     * @param potionEffectWrapper the attribute to add to the item
     */
    public void addDefaultStat(ItemStack i, PotionEffectWrapper potionEffectWrapper) {
        if (i == null) return;
        Collection<PotionEffectWrapper> defaultStats = getDefaultPotionEffects(i);
        if (defaultStats.stream().anyMatch(potionEffect -> potionEffect.getPotionEffect().equals(potionEffectWrapper.getPotionEffect()))) {
            defaultStats.removeIf(potionEffectWrapper1 -> potionEffectWrapper1.getPotionEffect().equals(potionEffectWrapper.getPotionEffect()));
        }
        defaultStats.add(potionEffectWrapper);

        setDefaultPotionEffects(i, defaultStats);
        setPotionEffectStrength(i, potionEffectWrapper.getPotionEffect(), potionEffectWrapper.getAmplifier(), potionEffectWrapper.getDuration());
    }

    public void removeDefaultStat(ItemStack i, String potionEffect) {
        if (i == null) return;
        Collection<PotionEffectWrapper> defaultStats = getDefaultPotionEffects(i);
        defaultStats.removeIf(potionEffectWrapper1 -> potionEffectWrapper1.getPotionEffect().equals(potionEffect));
        setDefaultPotionEffects(i, defaultStats);
    }

    /**
     * Sets an attribute's strength to an item only if the type of the item has this attribute by default.
     * This does not edit default attributes, it only changes the value of the applied attributes.
     *
     * @param i         the item to add the attribute to
     * @param attribute the attribute to add to the item
     * @param amplifier the value to give to the item
     */
    public void setPotionEffectStrength(ItemStack i, String attribute, double amplifier, int duration) {
        if (i == null) return;
        Collection<PotionEffectWrapper> currentStats = new HashSet<>(getCurrentStats(i));
        Collection<PotionEffectWrapper> defaultStats = new HashSet<>(getDefaultPotionEffects(i));
        List<PotionEffectWrapper> wrappers = defaultStats.stream().filter(potionEffectWrapper -> potionEffectWrapper.getPotionEffect().equals(attribute)).collect(Collectors.toList());
        if (wrappers.size() > 0) {
            try {
                PotionEffectWrapper currentPotionEffect = wrappers.get(0).clone();
                amplifier = Utils.round(amplifier, 4);
                currentPotionEffect.setAmplifier(amplifier);
                currentPotionEffect.setDuration(duration);
                currentStats.removeIf(potionEffectWrapper -> potionEffectWrapper.getPotionEffect().equals(currentPotionEffect.getPotionEffect()));
                currentStats.add(currentPotionEffect);
                setStats(i, currentStats);
            } catch (CloneNotSupportedException ignored) {
                ValhallaMMO.getPlugin().getLogger().warning("Attempted to clone potion effect wrapper, but this failed");
            }
        }
    }

    // The following 5 methods are only there to help define minecraft's vanilla attribute stats for tools and armor better
//    private void registerVanillaDefaultPotionEffect(PotionType type, boolean extended, boolean upgraded, String potionEffect, int duration, double amplifier){
//        Collection<PotionEffectWrapper> modifiers = new HashSet<>();
//        String key = type + ((extended) ? "_EXT" : ((upgraded) ? "_UPG" : "_BASE"));
//        if (defaultVanillaPotionStats.containsKey(key)){
//            modifiers = defaultVanillaPotionStats.get(key);
//        }
//        PotionEffectWrapper wrapperToAdd = new VanillaPotionEffectWrapper(potionEffect, amplifier, duration);
//        modifiers.add(wrapperToAdd);
//        defaultVanillaPotionStats.put(key, modifiers);
//    }


    public NamespacedKey getCustomPotionEffectsKey() {
        return customPotionEffectsKey;
    }

    public Map<String, PotionEffectWrapper> getRegisteredPotionEffects() {
        return registeredPotionEffects;
    }

    public static PotionAttributesManager getInstance() {
        if (manager == null) manager = new PotionAttributesManager();
        return manager;
    }
}
