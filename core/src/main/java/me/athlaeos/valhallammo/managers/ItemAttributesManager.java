package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.items.attributewrappers.*;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ItemAttributesManager {
    private static ItemAttributesManager manager = null;
    private final Map<Material, Map<String, AttributeWrapper>> defaultVanillaAttributes;
    private final Map<String, AttributeWrapper> registeredAttributes;
    // key used to save all default vanilla and custom attributes to the item
    private final NamespacedKey defaultAttributeKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_default_attributes");
    // key used to save all current custom attributes to the item
    private final NamespacedKey customAttributeKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_custom_attributes");

    public ItemAttributesManager(){
        defaultVanillaAttributes = new HashMap<>();
        registeredAttributes = new HashMap<>();

        registerAttribute(new CustomDrawStrengthWrapper(0D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        registerAttribute(new CustomArrowDamageWrapper(0D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        registerAttribute(new CustomArrowSaveChanceWrapper(0D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        registerAttribute(new CustomArrowSpeedWrapper(0D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        registerAttribute(new CustomArrowAccuracyWrapper(0D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        registerAttribute(new CustomArrowPiercingWrapper(0D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        registerAttribute(new CustomMaxDurabilityWrapper(0D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        registerAttribute(new CustomKnockbackWrapper(0D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        registerAttribute(new CustomStunChanceWrapper(0D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        registerAttribute(new CustomBleedChanceWrapper(0D, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlot.HAND));
        registerAttribute(new CustomBleedDamageWrapper(0D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        registerAttribute(new CustomBleedDurationWrapper(0D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        registerAttribute(new CustomCritChanceWrapper(0D, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlot.HAND));
        registerAttribute(new CustomCritDamageWrapper(0D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        registerAttribute(new CustomFlatArmorPenetrationWrapper(0D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        registerAttribute(new CustomFlatLightArmorPenetrationWrapper(0D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        registerAttribute(new CustomFlatHeavyArmorPenetrationWrapper(0D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        registerAttribute(new CustomFractionLightArmorPiercingWrapper(0D, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlot.HAND));
        registerAttribute(new CustomFractionHeavyArmorPiercingWrapper(0D, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlot.HAND));
        registerAttribute(new CustomFractionArmorPiercingWrapper(0D, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlot.HAND));
        registerAttribute(new CustomHeavyArmorDamageWrapper(0D, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlot.HAND));
        registerAttribute(new CustomLightArmorDamageWrapper(0D, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlot.HAND));
        registerAttribute(new CustomImmunityFrameBonusWrapper(0D, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlot.HAND));
        registerAttribute(new CustomImmunityFrameReductionWrapper(0D, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlot.HAND));
        registerAttribute(new CustomFlatImmunityFrameBonusWrapper(0D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        registerAttribute(new CustomMeleeDamageWrapper(0D, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlot.HAND));
        registerAttribute(new CustomWeaponReachWrapper(0D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        registerAttribute(new CustomVelocityDamageBonusWrapper(0D, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlot.HAND));
        registerAttribute(new CustomDismountChanceWrapper(0D, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlot.HAND));

        registerAttribute(new VanillaArmorToughnessWrapper(0D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        registerAttribute(new VanillaArmorWrapper(0D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        registerAttribute(new VanillaAttackDamageWrapper(0D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        registerAttribute(new VanillaAttackSpeedWrapper(0D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        registerAttribute(new VanillaKnockbackResistanceWrapper(0D, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlot.HAND));
        registerAttribute(new VanillaMaxHealthWrapper(0D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        registerAttribute(new VanillaMovementSpeedWrapper(0D, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlot.HAND));

        registerAttribute(new CustomDamageResistanceWrapper(0D, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlot.HAND));
        registerAttribute(new CustomExplosionResistanceWrapper(0D, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlot.HAND));
        registerAttribute(new CustomFallingResistanceWrapper(0D, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlot.HAND));
        registerAttribute(new CustomFireResistanceWrapper(0D, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlot.HAND));
        registerAttribute(new CustomMagicResistanceWrapper(0D, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlot.HAND));
        registerAttribute(new CustomPoisonResistanceWrapper(0D, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlot.HAND));
        registerAttribute(new CustomProjectileResistanceWrapper(0D, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlot.HAND));
        registerAttribute(new CustomMeleeResistanceWrapper(0D, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlot.HAND));

        weaponDamage(Material.WOODEN_SWORD, 4D);
        weaponSpeed(Material.WOODEN_SWORD, 1.6);
        weaponDamage(Material.WOODEN_PICKAXE, 2D);
        weaponSpeed(Material.WOODEN_PICKAXE, 1.2);
        weaponDamage(Material.WOODEN_SHOVEL, 2.5);
        weaponSpeed(Material.WOODEN_SHOVEL, 1D);
        weaponDamage(Material.WOODEN_AXE, 7D);
        weaponSpeed(Material.WOODEN_AXE, 0.8);
        weaponDamage(Material.WOODEN_HOE, 1D);
        weaponSpeed(Material.WOODEN_HOE, 1D);

        weaponDamage(Material.STONE_SWORD, 5D);
        weaponSpeed(Material.STONE_SWORD, 1.6);
        weaponDamage(Material.STONE_PICKAXE, 3D);
        weaponSpeed(Material.STONE_PICKAXE, 1.2);
        weaponDamage(Material.STONE_SHOVEL, 3.5);
        weaponSpeed(Material.STONE_SHOVEL, 1D);
        weaponDamage(Material.STONE_AXE, 9D);
        weaponSpeed(Material.STONE_AXE, 0.8);
        weaponDamage(Material.STONE_HOE, 1D);
        weaponSpeed(Material.STONE_HOE, 2D);

        weaponDamage(Material.GOLDEN_SWORD, 4D);
        weaponSpeed(Material.GOLDEN_SWORD, 1.6);
        weaponDamage(Material.GOLDEN_PICKAXE, 2D);
        weaponSpeed(Material.GOLDEN_PICKAXE, 1.2);
        weaponDamage(Material.GOLDEN_SHOVEL, 2.5);
        weaponSpeed(Material.GOLDEN_SHOVEL, 1D);
        weaponDamage(Material.GOLDEN_AXE, 7D);
        weaponSpeed(Material.GOLDEN_AXE, 1D);
        weaponDamage(Material.GOLDEN_HOE, 1D);
        weaponSpeed(Material.GOLDEN_HOE, 1D);

        weaponDamage(Material.IRON_SWORD, 6D);
        weaponSpeed(Material.IRON_SWORD, 1.6);
        weaponDamage(Material.IRON_PICKAXE, 4D);
        weaponSpeed(Material.IRON_PICKAXE, 1.2);
        weaponDamage(Material.IRON_SHOVEL, 4.5);
        weaponSpeed(Material.IRON_SHOVEL, 1D);
        weaponDamage(Material.IRON_AXE, 9D);
        weaponSpeed(Material.IRON_AXE, 0.9);
        weaponDamage(Material.IRON_HOE, 1D);
        weaponSpeed(Material.IRON_HOE, 3D);

        weaponDamage(Material.DIAMOND_SWORD, 7D);
        weaponSpeed(Material.DIAMOND_SWORD, 1.6);
        weaponDamage(Material.DIAMOND_PICKAXE, 5D);
        weaponSpeed(Material.DIAMOND_PICKAXE, 1.2);
        weaponDamage(Material.DIAMOND_SHOVEL, 5.5);
        weaponSpeed(Material.DIAMOND_SHOVEL, 1D);
        weaponDamage(Material.DIAMOND_AXE, 9D);
        weaponSpeed(Material.DIAMOND_AXE, 1D);
        weaponDamage(Material.DIAMOND_HOE, 1D);
        weaponSpeed(Material.DIAMOND_HOE, 4D);

        weaponDamage(Material.NETHERITE_SWORD, 8D);
        weaponSpeed(Material.NETHERITE_SWORD, 1.6);
        weaponDamage(Material.NETHERITE_PICKAXE, 6D);
        weaponSpeed(Material.NETHERITE_PICKAXE, 1.2);
        weaponDamage(Material.NETHERITE_SHOVEL, 6.5);
        weaponSpeed(Material.NETHERITE_SHOVEL, 1D);
        weaponDamage(Material.NETHERITE_AXE, 10D);
        weaponSpeed(Material.NETHERITE_AXE, 1D);
        weaponDamage(Material.NETHERITE_HOE, 1D);
        weaponSpeed(Material.NETHERITE_HOE, 4D);

        armorRating(Material.LEATHER_HELMET, 1D);
        armorRating(Material.LEATHER_CHESTPLATE, 3D);
        armorRating(Material.LEATHER_LEGGINGS, 2D);
        armorRating(Material.LEATHER_BOOTS, 1D);

        armorRating(Material.CHAINMAIL_HELMET, 2D);
        armorRating(Material.CHAINMAIL_CHESTPLATE, 5D);
        armorRating(Material.CHAINMAIL_LEGGINGS, 4D);
        armorRating(Material.CHAINMAIL_BOOTS, 1D);

        armorRating(Material.GOLDEN_HELMET, 2D);
        armorRating(Material.GOLDEN_CHESTPLATE, 5D);
        armorRating(Material.GOLDEN_LEGGINGS, 3D);
        armorRating(Material.GOLDEN_BOOTS, 1D);

        armorRating(Material.IRON_HELMET, 2D);
        armorRating(Material.IRON_CHESTPLATE, 6D);
        armorRating(Material.IRON_LEGGINGS, 5D);
        armorRating(Material.IRON_BOOTS, 2D);

        armorRating(Material.DIAMOND_HELMET, 3D);
        armorToughnessRating(Material.DIAMOND_HELMET, 2D);
        armorRating(Material.DIAMOND_CHESTPLATE, 8D);
        armorToughnessRating(Material.DIAMOND_CHESTPLATE, 2D);
        armorRating(Material.DIAMOND_LEGGINGS, 6D);
        armorToughnessRating(Material.DIAMOND_LEGGINGS, 2D);
        armorRating(Material.DIAMOND_BOOTS, 3D);
        armorToughnessRating(Material.DIAMOND_BOOTS, 2D);

        armorRating(Material.NETHERITE_HELMET, 3D);
        armorToughnessRating(Material.NETHERITE_HELMET, 3D);
        armorKnockbackResistanceRating(Material.NETHERITE_HELMET, 0.01D);
        armorRating(Material.NETHERITE_CHESTPLATE, 8D);
        armorToughnessRating(Material.NETHERITE_CHESTPLATE, 3D);
        armorKnockbackResistanceRating(Material.NETHERITE_CHESTPLATE, 0.01D);
        armorRating(Material.NETHERITE_LEGGINGS, 6D);
        armorToughnessRating(Material.NETHERITE_LEGGINGS, 3D);
        armorKnockbackResistanceRating(Material.NETHERITE_LEGGINGS, 0.01D);
        armorRating(Material.NETHERITE_BOOTS, 3D);
        armorToughnessRating(Material.NETHERITE_BOOTS, 3D);
        armorKnockbackResistanceRating(Material.NETHERITE_BOOTS, 0.01D);

        armorRating(Material.TURTLE_HELMET, 2D);

        weaponDamage(Material.TRIDENT, 9);
        weaponSpeed(Material.TRIDENT, 1.1);

        bowStrength(Material.BOW, 1);
        bowStrength(Material.CROSSBOW, 1);
    }

    public void registerAttribute(AttributeWrapper attribute){
        registeredAttributes.put(attribute.getAttribute(), attribute);
    }

    public AttributeWrapper getWrapperClone(String wrapper){
        AttributeWrapper baseWrapper = registeredAttributes.get(wrapper);
        if (baseWrapper != null) {
            try {
                return baseWrapper.clone();
            } catch (CloneNotSupportedException ignored) {
            }
        }
        return null;
    }

    public Map<String, AttributeWrapper> getRegisteredAttributes() {
        return registeredAttributes;
    }

    /**
     * Writes all the given default attributes to the item's metadata, its current stats are also updated
     * @param i the item to define its default attributes on
     * @param attributes the attributes to define the item's default attributes with
     */
    public void setDefaultStats(ItemStack i, Map<String, AttributeWrapper> attributes){
        if (i == null) return;
        assert i.getItemMeta() != null;
        ItemMeta meta = i.getItemMeta();
        if (attributes.isEmpty()) {
            meta.getPersistentDataContainer().remove(defaultAttributeKey);
            meta.setAttributeModifiers(null);
        } else {
            Collection<String> stringAttributes = new HashSet<>();
            for (AttributeWrapper wrapper : attributes.values()){
                stringAttributes.add(wrapper.getAttribute() + ":" + wrapper.getAmount() + ":" + wrapper.getOperation());
            }
            String defaultAttributes = String.join(";", stringAttributes);
            meta.getPersistentDataContainer().set(defaultAttributeKey, PersistentDataType.STRING, defaultAttributes);
        }
        i.setItemMeta(meta);
    }

    /**
     * Returns all the attribute stats the item has by default, this does not imply vanilla stats as they can be removed.
     * This will be a map where its key is the attribute name and its value is the AttributeWrapper associated with it.
     * @param i the item to get its default attribute stats from
     * @return the default attributes of the item
     */
    public Map<String, AttributeWrapper> getDefaultStats(ItemStack i){
        if (i == null) return null;
        Map<String, AttributeWrapper> attributes = new HashMap<>();
        assert i.getItemMeta() != null;
        ItemMeta meta = i.getItemMeta();
        if (meta.getPersistentDataContainer().has(defaultAttributeKey, PersistentDataType.STRING)){
            String attributeString = meta.getPersistentDataContainer().get(defaultAttributeKey, PersistentDataType.STRING);
            if (attributeString != null){
                String[] defaultAttributes = attributeString.split(";");
                for (String a : defaultAttributes){
                    String[] attributeProperties = a.split(":");
                    if (attributeProperties.length >= 2){
                        String attribute = attributeProperties[0];
                        String value = attributeProperties[1];
                        String operation = "ADD_NUMBER";
                        if (attributeProperties.length >= 3){
                            operation = attributeProperties[2];
                        }
                        try {
                            if (registeredAttributes.containsKey(attribute)){
                                AttributeWrapper wrapper = registeredAttributes.get(attribute).clone();
                                wrapper.setAmount(Double.parseDouble(value));
                                if (operation == null) {
                                    wrapper.setOperation(AttributeModifier.Operation.ADD_NUMBER);
                                } else {
                                    if (operation.equals("null")){
                                        wrapper.setOperation(AttributeModifier.Operation.ADD_NUMBER);
                                    } else {
                                        wrapper.setOperation(AttributeModifier.Operation.valueOf(operation));
                                    }
                                }
                                attributes.put(wrapper.getAttribute(), wrapper);
                            } else {
                                ValhallaMMO.getPlugin().getLogger().warning("Attempting to grab attribute " + attribute + " but it was not registered.");
                            }
                        } catch (IllegalArgumentException | CloneNotSupportedException e){
                            ValhallaMMO.getPlugin().getLogger().warning("Malformed metadata on item " + i.getType() + ", attempted to parse double value " + value + " and operation " + operation + ", but they could not be parsed.");
                        }
                    } else {
                        ValhallaMMO.getPlugin().getLogger().warning("Malformed metadata on item " + i.getType() + ", notify plugin author. Expected property length 2 or 3, but it was less.");
                    }
                }
            } else {
                attributes.putAll(getVanillaStats(i));
            }
        } else {
            attributes.putAll(getVanillaStats(i));
        }
        return attributes;
    }

    /**
     * Returns all the vanilla and custom attribute stats the item has. This will be a map where its key is the attribute
     * name and its value is the AttributeWrapper associated with it.
     * @param i the item to get its stats from
     * @return the attribute stats of the item
     */
    public Map<String, AttributeWrapper> getCurrentStats(ItemStack i){
        if (i == null) return null;
        Map<String, AttributeWrapper> attributes = new HashMap<>();
        ItemMeta meta = i.getItemMeta();
        if (meta == null) return attributes;
        if (meta.getPersistentDataContainer().has(customAttributeKey, PersistentDataType.STRING)){
            String customAttributeString = meta.getPersistentDataContainer().get(customAttributeKey, PersistentDataType.STRING);
            if (customAttributeString != null){
                String[] customAttributes = customAttributeString.split(";");
                for (String a : customAttributes){
                    String[] attributeProperties = a.split(":");
                    if (attributeProperties.length >= 2){
                        String attribute = attributeProperties[0];
                        String value = attributeProperties[1];
                        String operation = "ADD_NUMBER";
                        if (attributeProperties.length >= 3){
                            operation = attributeProperties[2];
                        }
                        try {
                            if (registeredAttributes.containsKey(attribute)){
                                AttributeWrapper wrapper = registeredAttributes.get(attribute).clone();
                                double finalValue = Double.parseDouble(value);
                                wrapper.setAmount(finalValue);
                                if (operation == null) {
                                    wrapper.setOperation(AttributeModifier.Operation.ADD_NUMBER);
                                } else {
                                    if (operation.equals("null")){
                                        wrapper.setOperation(AttributeModifier.Operation.ADD_NUMBER);
                                    } else {
                                        wrapper.setOperation(AttributeModifier.Operation.valueOf(operation));
                                    }
                                }
                                attributes.put(wrapper.getAttribute(), wrapper);
                            } else {
                                ValhallaMMO.getPlugin().getLogger().warning("Attempting to grab attribute " + attribute + " but it was not registered.");
                            }
                        } catch (IllegalArgumentException | CloneNotSupportedException e){
                            ValhallaMMO.getPlugin().getLogger().warning("Malformed metadata on item " + i.getType() + ", attempted to parse double value " + value + " and operation " + operation + ", but they could not be parsed.");
                        }
                    } else {
                        ValhallaMMO.getPlugin().getLogger().warning("Malformed metadata on item " + i.getType() + ", notify plugin author. Expected property length 2 or 3, but it was less.");
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
    public Map<String, AttributeWrapper> getVanillaStats(ItemStack i){
        Map<String, AttributeWrapper> attributes = new HashMap<>();
        if (i == null) return attributes;
        if (defaultVanillaAttributes.containsKey(i.getType())){
            return defaultVanillaAttributes.get(i.getType());
        }
        return attributes;
    }

    /**
     * Sets the given attributes to the item, but only if item has the attribute type in its default attributes.
     * @param i the item to set its attributes to
     * @param attributes the attributes to set to the item
     */
    public void setStats(ItemStack i, Map<String, AttributeWrapper> attributes){
        if (i == null) return;
        ItemMeta meta = i.getItemMeta();
        assert meta != null;
        if (attributes != null){
            if (!attributes.isEmpty()){
                meta.setAttributeModifiers(null);
                for (AttributeWrapper registeredAttributeWrapper : registeredAttributes.values()){
                    registeredAttributeWrapper.onRemove(meta);
                }

                Map<String, AttributeWrapper> customAttributes = new HashMap<>();
                for (AttributeWrapper attribute : new HashMap<>(attributes).values()){
                    if (!getDefaultStats(i).containsKey(attribute.getAttribute())) continue;
                    try {
                        Attribute vanillaAttribute = Attribute.valueOf(attribute.getAttribute());
                        double value = attribute.getAmount();
                        if (attribute.getAttribute().equals("GENERIC_ATTACK_SPEED") && attribute.getOperation() == AttributeModifier.Operation.ADD_NUMBER) {
                            value -= 4D;
                        }
                        if (attribute.getAttribute().equals("GENERIC_ATTACK_DAMAGE") && attribute.getOperation() == AttributeModifier.Operation.ADD_NUMBER) {
                            value -= 1D;
                        }
                        //vanilla attribute being applied
                        meta.addAttributeModifier(vanillaAttribute, new AttributeModifier(
                                UUID.randomUUID(),
                                attribute.getAttribute().replaceFirst("_", ".").toLowerCase(),
                                value,
                                attribute.getOperation(),
                                ItemUtils.getEquipmentSlot(i)
                        ));
                        customAttributes.put(attribute.getAttribute(), attribute);
                    } catch (IllegalArgumentException ignored){
                        //custom attribute being applied
                        customAttributes.put(attribute.getAttribute(), attribute);
                    }
                }
                if (!customAttributes.isEmpty()){
                    List<String> customAttributeStringComponents = new ArrayList<>();
                    for (AttributeWrapper wrapper : customAttributes.values()){
                        customAttributeStringComponents.add(wrapper.getAttribute() + ":" + wrapper.getAmount() + ":" + wrapper.getOperation());
                        wrapper.onApply(meta);
                    }
                    meta.getPersistentDataContainer().set(customAttributeKey, PersistentDataType.STRING, String.join(";", customAttributeStringComponents));
                } else {
                    meta.getPersistentDataContainer().remove(customAttributeKey);
                }
                i.setItemMeta(meta);
                return;
            }
        }
        meta.setAttributeModifiers(null);
        for (AttributeWrapper registeredAttributeWrapper : registeredAttributes.values()){
            registeredAttributeWrapper.onRemove(meta);
        }
        meta.getPersistentDataContainer().remove(customAttributeKey);
        i.setItemMeta(meta);
        //remove attributes
    }

    /**
     * Stores all the item's vanilla stats to the item's PersistentDataContainer. If the item has no vanilla attributes,
     * nothing happens. Example: Items like a diamond chestplate will be assigned 8 armor and 2 toughness
     * The attribute modifiers will also be applied to the item
     * @param i the item to set vanilla attributes to.
     */
    public void applyVanillaStats(ItemStack i){
        if (i == null) return;
        Map<String, AttributeWrapper> vanillaStats = new HashMap<>(getVanillaStats(i));
        if (vanillaStats.size() > 0){
            ItemMeta itemMeta = i.getItemMeta();
            if (itemMeta == null) return;
            setDefaultStats(i, vanillaStats);
        }
    }

    public AttributeWrapper getAttributeWrapper(ItemStack i, String attribute){
        if (i == null) return null;
        return getCurrentStats(i).get(attribute);
    }

    /**
     * Attempts to grab an AttributeWrapper off an item. If the item does not directly have an attribute wrapper,
     * it'll attempt to grab a default attribute wrapper. If it doesn't have a default one, it'll attempt to grab
     * a vanilla attribute wrapper. If it still doesn't have one of those, it'll return null.
     * @param i the item to grab the attribute wrapper off of
     * @param attribute the attribute to try and grab off the item
     * @return the current wrapper, default wrapper, or vanilla wrapper.
     */
    public AttributeWrapper getAnyAttributeWrapper(ItemStack i, String attribute){
        AttributeWrapper attributeWrapper = ItemAttributesManager.getInstance().getAttributeWrapper(i, attribute);
        if (attributeWrapper == null) attributeWrapper = ItemAttributesManager.getInstance().getDefaultStat(i, attribute);
        if (attributeWrapper == null) attributeWrapper = ItemAttributesManager.getInstance().getVanillaStats(i).get(attribute);
        return attributeWrapper;
    }

    public AttributeWrapper getDefaultStat(ItemStack i, String attribute){
        if (i == null) return null;
        return getDefaultStats(i).get(attribute);
    }

    /**
     * Adds a new attribute to the item's default stats
     * @param i the item to add the attribute to
     * @param attribute the attribute to add to the item
     */
    public void addDefaultStat(ItemStack i, AttributeWrapper attribute){
        if (i == null) return;
        Map<String, AttributeWrapper> defaultStats = getDefaultStats(i);
        defaultStats.put(attribute.getAttribute(), attribute);

        setDefaultStats(i, defaultStats);
        setAttributeStrength(i, attribute.getAttribute(), attribute.getAmount());
    }

    public void removeDefaultStat(ItemStack i, String attribute){
        if (i == null) return;
        Map<String, AttributeWrapper> defaultStats = getDefaultStats(i);
        defaultStats.remove(attribute);
        setDefaultStats(i, defaultStats);
    }

    /**
     * Sets an attribute's strength to an item only if the type of the item has this attribute by default.
     * This does not edit default attributes, it only changes the value of the applied attributes.
     * For certain attributes the value is corrected automatically, like attack speed is reduced by 4.0 and attack damage
     * reduced by 1.0 if their operation equals ADD_NUMBER
     * @param i the item to add the attribute to
     * @param attribute the attribute to add to the item
     * @param value the value to give to the item
     */
    public void setAttributeStrength(ItemStack i, String attribute, double value){
        if (i == null) return;
        Map<String, AttributeWrapper> currentStats = new HashMap<>(getCurrentStats(i));
        Map<String, AttributeWrapper> defaultStats = new HashMap<>(getDefaultStats(i));
        if (defaultStats.containsKey(attribute)){
            try {
                AttributeWrapper currentAttribute = defaultStats.get(attribute).clone();
                if (value < currentAttribute.getMinValue()) {
                    value = currentAttribute.getMinValue();
                }
                if (value > currentAttribute.getMaxValue()){
                    value = currentAttribute.getMaxValue();
                }
                value = Utils.round(value, 4);
                currentAttribute.setAmount(value);
                currentStats.put(currentAttribute.getAttribute(), currentAttribute);
                setStats(i, currentStats);
            } catch (CloneNotSupportedException ignored){
                ValhallaMMO.getPlugin().getLogger().warning("Attempted to clone attribute wrapper, but this failed");
            }
        }
    }

    /**
     * Sets a default attribute's strength to an item only if the type of the item has this attribute by default.
     * For certain attributes the value is corrected automatically, like attack speed is reduced by 4.0 and attack damage
     * reduced by 1.0 if their operation equals ADD_NUMBER
     * @param i the item to add the attribute to
     * @param attribute the attribute to add to the item
     * @param value the value to give to the item
     */
    public void setDefaultAttributeStrength(ItemStack i, String attribute, double value){
        if (i == null) return;
        Map<String, AttributeWrapper> defaultStats = new HashMap<>(getDefaultStats(i));
        if (defaultStats.containsKey(attribute)){
            try {
                AttributeWrapper newDefaultAttribute = defaultStats.get(attribute).clone();
                if (value < newDefaultAttribute.getMinValue()) {
                    value = newDefaultAttribute.getMinValue();
                }
                if (value > newDefaultAttribute.getMaxValue()){
                    value = newDefaultAttribute.getMaxValue();
                }
                value = Utils.round(value, 4);
                newDefaultAttribute.setAmount(value);
                defaultStats.put(newDefaultAttribute.getAttribute(), newDefaultAttribute);
                setDefaultStats(i, defaultStats);
            } catch (CloneNotSupportedException ignored){
                ValhallaMMO.getPlugin().getLogger().warning("Attempted to clone attribute wrapper, but this failed");
            }
        }
    }

    // The following 5 methods are only there to help define minecraft's vanilla attribute stats for tools and armor better
    private void weaponDamage(Material weapon, double value){
        Map<String, AttributeWrapper> modifiers = new HashMap<>();
        if (defaultVanillaAttributes.containsKey(weapon)){
            modifiers = defaultVanillaAttributes.get(weapon);
        }
        AttributeWrapper wrapperToAdd = new VanillaAttackDamageWrapper(value, AttributeModifier.Operation.ADD_NUMBER, ItemUtils.getEquipmentSlot(new ItemStack(weapon)));
        modifiers.put(wrapperToAdd.getAttribute(), wrapperToAdd);
        defaultVanillaAttributes.put(weapon, modifiers);
    }

    private void weaponSpeed(Material weapon, double value){
        Map<String, AttributeWrapper> modifiers = new HashMap<>();
        if (defaultVanillaAttributes.containsKey(weapon)){
            modifiers = defaultVanillaAttributes.get(weapon);
        }
        AttributeWrapper wrapperToAdd = new VanillaAttackSpeedWrapper(value, AttributeModifier.Operation.ADD_NUMBER, ItemUtils.getEquipmentSlot(new ItemStack(weapon)));
        modifiers.put(wrapperToAdd.getAttribute(), wrapperToAdd);
        defaultVanillaAttributes.put(weapon, modifiers);
    }

    private void armorRating(Material armor, double value){
        EquipmentSlot slot = ItemUtils.getEquipmentSlot(new ItemStack(armor));
        if (slot != null){
            Map<String, AttributeWrapper> modifiers = new HashMap<>();
            if (defaultVanillaAttributes.containsKey(armor)){
                modifiers = defaultVanillaAttributes.get(armor);
            }
            AttributeWrapper wrapperToAdd = new VanillaArmorWrapper(value, AttributeModifier.Operation.ADD_NUMBER, slot);
            modifiers.put(wrapperToAdd.getAttribute(), wrapperToAdd);
            defaultVanillaAttributes.put(armor, modifiers);
        }
    }

    private void armorToughnessRating(Material armor, double value){
        EquipmentSlot slot = ItemUtils.getEquipmentSlot(new ItemStack(armor));
        if (slot != null){
            Map<String, AttributeWrapper> modifiers = new HashMap<>();
            if (defaultVanillaAttributes.containsKey(armor)){
                modifiers = defaultVanillaAttributes.get(armor);
            }
            AttributeWrapper wrapperToAdd = new VanillaArmorToughnessWrapper(value, AttributeModifier.Operation.ADD_NUMBER, slot);
            modifiers.put(wrapperToAdd.getAttribute(), wrapperToAdd);
            defaultVanillaAttributes.put(armor, modifiers);
        }
    }

    private void armorKnockbackResistanceRating(Material armor, double value){
        EquipmentSlot slot = ItemUtils.getEquipmentSlot(new ItemStack(armor));
        if (slot != null){
            Map<String, AttributeWrapper> modifiers = new HashMap<>();
            if (defaultVanillaAttributes.containsKey(armor)){
                modifiers = defaultVanillaAttributes.get(armor);
            }
            AttributeWrapper wrapperToAdd = new VanillaKnockbackResistanceWrapper(value, AttributeModifier.Operation.ADD_NUMBER, slot);
            modifiers.put(wrapperToAdd.getAttribute(), wrapperToAdd);
            defaultVanillaAttributes.put(armor, modifiers);
        }
    }

    private void bowStrength(Material bow, double value){
        Map<String, AttributeWrapper> modifiers = new HashMap<>();
        if (defaultVanillaAttributes.containsKey(bow)){
            modifiers = defaultVanillaAttributes.get(bow);
        }
        AttributeWrapper wrapperToAdd = new CustomDrawStrengthWrapper(value, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        modifiers.put(wrapperToAdd.getAttribute(), wrapperToAdd);
        defaultVanillaAttributes.put(bow, modifiers);
    }

    public static ItemAttributesManager getInstance(){
        if (manager == null) manager = new ItemAttributesManager();
        return manager;
    }
}
