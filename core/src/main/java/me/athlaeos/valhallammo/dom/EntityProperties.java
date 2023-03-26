package me.athlaeos.valhallammo.dom;


import me.athlaeos.valhallammo.items.attributewrappers.AttributeWrapper;
import me.athlaeos.valhallammo.items.enchantmentwrappers.EnchantmentWrapper;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityProperties {
    private ItemStack helmet = null;
    private Map<String, EnchantmentWrapper> helmetEnchantments = new HashMap<>();
    private Map<String, AttributeWrapper> helmetAttributes = new HashMap<>();
    private ItemStack chestplate = null;
    private Map<String, EnchantmentWrapper> chestplateEnchantments = new HashMap<>();
    private Map<String, AttributeWrapper> chestplateAttributes = new HashMap<>();
    private ItemStack leggings = null;
    private Map<String, EnchantmentWrapper> leggingsEnchantments = new HashMap<>();
    private Map<String, AttributeWrapper> leggingsAttributes = new HashMap<>();
    private ItemStack boots = null;
    private Map<String, EnchantmentWrapper> bootsEnchantments = new HashMap<>();
    private Map<String, AttributeWrapper> bootsAttributes = new HashMap<>();
    private ItemStack mainHand = null;
    private Map<String, EnchantmentWrapper> mainHandEnchantments = new HashMap<>();
    private Map<String, AttributeWrapper> mainHandAttributes = new HashMap<>();
    private ItemStack offHand = null;
    private Map<String, EnchantmentWrapper> offHandEnchantments = new HashMap<>();
    private Map<String, AttributeWrapper> offHandAttributes = new HashMap<>();
    private final List<ItemStack> miscEquipment = new ArrayList<>();
    private final Map<ItemStack, Map<String, EnchantmentWrapper>> miscEquipmentEnchantments = new HashMap<>();
    private final Map<ItemStack, Map<String, AttributeWrapper>> miscEquipmentAttributes = new HashMap<>();
    private int heavyArmorCount = 0;
    private int lightArmorCount = 0;
    private int weightlessArmorCount = 0;
    private final Map<String, PotionEffect> activePotionEffects = new HashMap<>();

    public EntityProperties(){}

    public EntityProperties(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack leftHand, ItemStack rightHand){
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.mainHand = leftHand;
        this.offHand = rightHand;
    }

    public Map<String, PotionEffect> getActivePotionEffects() {
        return activePotionEffects;
    }

    public int getHeavyArmorCount() {
        return heavyArmorCount;
    }

    public void setHeavyArmorCount(int heavyArmorCount) {
        this.heavyArmorCount = heavyArmorCount;
    }

    public int getLightArmorCount() {
        return lightArmorCount;
    }

    public void setLightArmorCount(int lightArmorCount) {
        this.lightArmorCount = lightArmorCount;
    }

    public int getWeightlessArmorCount() {
        return weightlessArmorCount;
    }

    public void setWeightlessArmorCount(int weightlessArmorCount) {
        this.weightlessArmorCount = weightlessArmorCount;
    }

    public List<ItemStack> getMiscEquipment() {
        return miscEquipment;
    }

    public ItemStack getHelmet() {
        return helmet;
    }

    public void setHelmet(ItemStack helmet) {
        this.helmet = helmet;
    }

    public ItemStack getChestplate() {
        return chestplate;
    }

    public void setChestplate(ItemStack chestplate) {
        this.chestplate = chestplate;
    }

    public ItemStack getBoots() {
        return boots;
    }

    public void setBoots(ItemStack boots) {
        this.boots = boots;
    }

    public ItemStack getLeggings() {
        return leggings;
    }

    public void setLeggings(ItemStack leggings) {
        this.leggings = leggings;
    }

    public ItemStack getMainHand() {
        return mainHand;
    }

    public void setMainHand(ItemStack mainHand) {
        this.mainHand = mainHand;
    }

    public ItemStack getOffHand() {
        return offHand;
    }

    public void setOffHand(ItemStack offHand) {
        this.offHand = offHand;
    }

    public List<ItemStack> getIterable(boolean includeHands){
        List<ItemStack> iterable = new ArrayList<>();
        if (!Utils.isItemEmptyOrNull(helmet)) iterable.add(helmet);
        if (!Utils.isItemEmptyOrNull(chestplate)) iterable.add(chestplate);
        if (!Utils.isItemEmptyOrNull(leggings)) iterable.add(leggings);
        if (!Utils.isItemEmptyOrNull(boots)) iterable.add(boots);
        if (!miscEquipment.isEmpty()) iterable.addAll(miscEquipment);
        if (includeHands){
            if (!Utils.isItemEmptyOrNull(mainHand)) iterable.add(mainHand);
            if (!Utils.isItemEmptyOrNull(offHand)) iterable.add(offHand);
        }
        return iterable;
    }

    public List<ItemStack> getHands(){
        List<ItemStack> iterable = new ArrayList<>();
        if (!Utils.isItemEmptyOrNull(mainHand)) iterable.add(mainHand);
        if (!Utils.isItemEmptyOrNull(offHand)) iterable.add(offHand);
        return iterable;
    }

    public Map<ItemStack, Map<String, EnchantmentWrapper>> getMiscEquipmentEnchantments() {
        return miscEquipmentEnchantments;
    }

    public Map<String, EnchantmentWrapper> getBootsEnchantments() {
        return bootsEnchantments;
    }

    public Map<String, EnchantmentWrapper> getChestplateEnchantments() {
        return chestplateEnchantments;
    }

    public Map<String, EnchantmentWrapper> getHelmetEnchantments() {
        return helmetEnchantments;
    }

    public Map<String, EnchantmentWrapper> getLeggingsEnchantments() {
        return leggingsEnchantments;
    }

    public Map<String, EnchantmentWrapper> getMainHandEnchantments() {
        return mainHandEnchantments;
    }

    public Map<String, EnchantmentWrapper> getOffHandEnchantments() {
        return offHandEnchantments;
    }

    public void setLeggingsEnchantments(Map<String, EnchantmentWrapper> leggingsEnchantments) {
        this.leggingsEnchantments = leggingsEnchantments;
    }

    public void setBootsEnchantments(Map<String, EnchantmentWrapper> bootsEnchantments) {
        this.bootsEnchantments = bootsEnchantments;
    }

    public void setChestplateEnchantments(Map<String, EnchantmentWrapper> chestplateEnchantments) {
        this.chestplateEnchantments = chestplateEnchantments;
    }

    public void setHelmetEnchantments(Map<String, EnchantmentWrapper> helmetEnchantments) {
        this.helmetEnchantments = helmetEnchantments;
    }

    public void setOffHandEnchantments(Map<String, EnchantmentWrapper> offHandEnchantments) {
        this.offHandEnchantments = offHandEnchantments;
    }

    public void setMainHandEnchantments(Map<String, EnchantmentWrapper> mainHandEnchantments) {
        this.mainHandEnchantments = mainHandEnchantments;
    }

    public Map<String, AttributeWrapper> getBootsAttributes() {
        return bootsAttributes;
    }

    public Map<String, AttributeWrapper> getChestplateAttributes() {
        return chestplateAttributes;
    }

    public Map<String, AttributeWrapper> getHelmetAttributes() {
        return helmetAttributes;
    }

    public Map<String, AttributeWrapper> getLeggingsAttributes() {
        return leggingsAttributes;
    }

    public Map<ItemStack, Map<String, AttributeWrapper>> getMiscEquipmentAttributes() {
        return miscEquipmentAttributes;
    }

    public Map<String, AttributeWrapper> getMainHandAttributes() {
        return mainHandAttributes;
    }

    public Map<String, AttributeWrapper> getOffHandAttributes() {
        return offHandAttributes;
    }

    public void setBootsAttributes(Map<String, AttributeWrapper> bootsAttributes) {
        this.bootsAttributes = bootsAttributes;
    }

    public void setChestplateAttributes(Map<String, AttributeWrapper> chestplateAttributes) {
        this.chestplateAttributes = chestplateAttributes;
    }

    public void setHelmetAttributes(Map<String, AttributeWrapper> helmetAttributes) {
        this.helmetAttributes = helmetAttributes;
    }

    public void setLeggingsAttributes(Map<String, AttributeWrapper> leggingsAttributes) {
        this.leggingsAttributes = leggingsAttributes;
    }

    public void setMainHandAttributes(Map<String, AttributeWrapper> mainHandAttributes) {
        this.mainHandAttributes = mainHandAttributes;
    }

    public void setOffHandAttributes(Map<String, AttributeWrapper> offHandAttributes) {
        this.offHandAttributes = offHandAttributes;
    }
}
