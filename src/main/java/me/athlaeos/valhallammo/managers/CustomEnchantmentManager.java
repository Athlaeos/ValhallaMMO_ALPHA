package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.items.enchantmentwrappers.DrawStrengthEnchantment;
import me.athlaeos.valhallammo.items.enchantmentwrappers.EnchantmentWrapper;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemEnchantmentsManager {
    private static ItemEnchantmentsManager manager = null;

    private final Map<String, EnchantmentWrapper> registeredEnchantments;
    // key used to save all current custom attributes to the item
    private final NamespacedKey customEnchantmentKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_custom_enchantments");
    private final NamespacedKey customEnchantmentCountKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_enchantments_count");

    public void setEnchantmentsCount(ItemStack i, int count){
        if (i == null) return;
        ItemMeta meta = i.getItemMeta();
        if (meta == null) return;
        meta.getPersistentDataContainer().set(customEnchantmentCountKey, PersistentDataType.INTEGER, count);
        i.setItemMeta(meta);
    }

    public int getEnchantmentsCount(ItemStack i){
        if (i == null) return 0;
        ItemMeta meta = i.getItemMeta();
        if (meta == null) return 0;
        if (meta.getPersistentDataContainer().has(customEnchantmentCountKey, PersistentDataType.INTEGER)){
            return meta.getPersistentDataContainer().get(customEnchantmentCountKey, PersistentDataType.INTEGER);
        }
        return 0;
    }

    public ItemEnchantmentsManager(){
        registeredEnchantments = new HashMap<>();

        registerAttribute(new DrawStrengthEnchantment(0D));

    }

    public void registerAttribute(EnchantmentWrapper attribute){
        registeredEnchantments.put(attribute.getAttribute(), attribute);
    }

    /**
     * Returns all the vanilla and custom attribute stats the item has. This will be a map where its key is the attribute
     * name and its value is the AttributeWrapper associated with it.
     * @param i the item to get its stats from
     * @return the attribute stats of the item
     */
    public Map<String, EnchantmentWrapper> getCurrentEnchantments(ItemStack i){
        if (i == null) return null;
        Map<String, EnchantmentWrapper> enchantments = new HashMap<>();
        assert i.getItemMeta() != null;
        ItemMeta meta = i.getItemMeta();
        if (meta.getPersistentDataContainer().has(customEnchantmentKey, PersistentDataType.STRING)){
            String customAttributeString = meta.getPersistentDataContainer().get(customEnchantmentKey, PersistentDataType.STRING);
            if (customAttributeString != null){
                String[] customAttributes = customAttributeString.split(";");
                for (String a : customAttributes){
                    String[] attributeProperties = a.split(":");
                    if (attributeProperties.length >= 2){
                        String enchantment = attributeProperties[0];
                        String value = attributeProperties[1];
                        try {
                            if (registeredEnchantments.containsKey(enchantment)){
                                EnchantmentWrapper wrapper = registeredEnchantments.get(enchantment).clone();
                                double finalValue = Double.parseDouble(value);
                                wrapper.setAmount(finalValue);

                                enchantments.put(wrapper.getAttribute(), wrapper);
                            } else {
                                System.out.println("[ValhallaMMO] Attempting to grab enchantment " + enchantment + " but it was not registered.");
                            }
                        } catch (IllegalArgumentException | CloneNotSupportedException e){
                            System.out.println("[ValhallaMMO] Malformed metadata on item " + i.getType() + ", attempted to parse double value " + value + ", but it could not be parsed.");
                        }
                    } else {
                        System.out.println("[ValhallaMMO] Malformed metadata on item " + i.getType() + ", notify plugin author. Expected property length 2, but it was less.");
                    }
                }
            }
        }
        return enchantments;
    }

    /**
     * Sets the given attributes to the item, but only if item has the attribute type in its default attributes.
     * @param i the item to set its attributes to
     * @param attributes the attributes to set to the item
     */
    public void setEnchantments(ItemStack i, Map<String, EnchantmentWrapper> attributes){
        if (i == null) return;
        ItemMeta meta = i.getItemMeta();
        assert meta != null;
        if (attributes != null){
            if (!attributes.isEmpty()){
                meta.setAttributeModifiers(null);
                for (EnchantmentWrapper registeredEnchantmentWrapper : registeredEnchantments.values()){
                    registeredEnchantmentWrapper.onRemove(meta);
                }

                if (!attributes.isEmpty()){
                    List<String> customAttributeStringComponents = new ArrayList<>();
                    for (EnchantmentWrapper wrapper : attributes.values()){
                        customAttributeStringComponents.add(wrapper.getAttribute() + ":" + wrapper.getAmount());
                        wrapper.onApply(meta);
                    }
                    meta.getPersistentDataContainer().set(customEnchantmentKey, PersistentDataType.STRING, String.join(";", customAttributeStringComponents));
                } else {
                    meta.getPersistentDataContainer().remove(customEnchantmentKey);
                }
                i.setItemMeta(meta);
                return;
            }
        }

        for (EnchantmentWrapper registeredAttributeWrapper : registeredEnchantments.values()){
            registeredAttributeWrapper.onRemove(meta);
        }
        meta.getPersistentDataContainer().remove(customEnchantmentKey);
        i.setItemMeta(meta);
        //remove attributes
    }

    public EnchantmentWrapper getCustomEnchant(ItemStack i, String enchant){
        if (i == null) return null;
        return getCurrentEnchantments(i).get(enchant);
    }


    /**
     * Sets an attribute's strength to an item only if the type of the item has this attribute by default.
     * This does not edit default attributes, it only changes the value of the applied attributes.
     * For certain attributes the value is corrected automatically, like attack speed is reduced by 4.0 and attack damage
     * reduced by 1.0 if their operation equals ADD_NUMBER
     * @param i the item to add the attribute to
     * @param enchantment the attribute to add to the item
     * @param value the value to give to the item
     */
    public void setEnchantmentStrength(ItemStack i, String enchantment, double value){
        if (i == null) return;
        Map<String, EnchantmentWrapper> currentStats = new HashMap<>(getCurrentEnchantments(i));
        try {
            EnchantmentWrapper currentAttribute = registeredEnchantments.get(enchantment).clone();
            if (currentAttribute == null) return;
            if (value < currentAttribute.getMinValue()) {
                value = currentAttribute.getMinValue();
            }
            if (value > currentAttribute.getMaxValue()){
                value = currentAttribute.getMaxValue();
            }
            value = Utils.round(value, 4);
            currentAttribute.setAmount(value);
            currentStats.put(currentAttribute.getAttribute(), currentAttribute);
            setEnchantments(i, currentStats);
        } catch (CloneNotSupportedException ignored){
            System.out.println("[ValhallaMMO] Attempted to clone attribute wrapper, but this failed");
        }
    }

    public static ItemEnchantmentsManager getInstance(){
        if (manager == null) manager = new ItemEnchantmentsManager();
        return manager;
    }

    public Map<String, EnchantmentWrapper> getRegisteredEnchantments() {
        return registeredEnchantments;
    }
}
