package me.athlaeos.valhallammo.skills.enchanting;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.events.PlayerSkillExperienceGainEvent;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.items.ItemTreatment;
import me.athlaeos.valhallammo.items.MaterialClass;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.managers.CooldownManager;
import me.athlaeos.valhallammo.managers.EnchantingItemEnchantmentsManager;
import me.athlaeos.valhallammo.managers.SmithingItemTreatmentManager;
import me.athlaeos.valhallammo.skills.EnchantmentApplicationSkill;
import me.athlaeos.valhallammo.skills.OffensiveSkill;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.*;

public class EnchantingSkill extends Skill implements OffensiveSkill, EnchantmentApplicationSkill, Listener {
    private final Map<Enchantment, Double> enchantmentBaseValues;
    private final Map<Integer, Double> enchantmentLevelMultipliers;
    private final Map<MaterialClass, Double> enchantmentMaterialClassMultipliers;
    private final Map<EquipmentClass, Double> enchantmentEquipmentClassMultipliers;

    private final double diminishingReturnsMultiplier;
    private final int diminishingReturnsCount;
    private final boolean anvil_downgrading;
    private final List<EntityType> diminishingReturnsEntities = new ArrayList<>();
    private final Map<EntityType, Double> entityEXPMultipliers = new HashMap<>();
    private final Map<UUID, Integer> diminishingReturnTallyCounter = new HashMap<>();

    public EnchantingSkill(String type) {
        super(type);
        skillTreeMenuOrderPriority = 2;
        enchantmentBaseValues = new HashMap<>();
        enchantmentLevelMultipliers = new HashMap<>();
        enchantmentMaterialClassMultipliers = new HashMap<>();
        enchantmentEquipmentClassMultipliers = new HashMap<>();
        YamlConfiguration enchantmentConfig = ConfigManager.getInstance().getConfig("skill_enchanting.yml").get();
        YamlConfiguration progressionConfig = ConfigManager.getInstance().getConfig("progression_enchanting.yml").get();

        this.loadCommonConfig(enchantmentConfig, progressionConfig);

        this.anvil_downgrading = enchantmentConfig.getBoolean("anvil_downgrading");
        this.diminishingReturnsMultiplier = progressionConfig.getDouble("experience.diminishing_returns.multiplier");
        this.diminishingReturnsCount = progressionConfig.getInt("experience.diminishing_returns.amount");
        for (String s : progressionConfig.getStringList("experience.diminishing_returns.on")){
            try {
                this.diminishingReturnsEntities.add(EntityType.valueOf(s));
            } catch (IllegalArgumentException ignored){
            }
        }

        ConfigurationSection expReducedEntitySection = progressionConfig.getConfigurationSection("experience.diminishing_returns.mob_experience");
        if (expReducedEntitySection != null){
            for (String mob : expReducedEntitySection.getKeys(false)){
                try {
                    EntityType entity = EntityType.valueOf(mob);
                    double value = progressionConfig.getDouble("experience.diminishing_returns.mob_experience." + mob);
                    entityEXPMultipliers.put(entity, value);
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("invalid entity type given at progression_enchanting.yml experience.diminishing_returns.mob_experience." + mob);
                }
            }
        }

        ConfigurationSection baseEnchantmentValueSection = progressionConfig.getConfigurationSection("experience.exp_gain.enchantment_base");
        if (baseEnchantmentValueSection != null){
            for (String s : baseEnchantmentValueSection.getKeys(false)){
                Enchantment e = Enchantment.getByKey(NamespacedKey.minecraft(s.toLowerCase()));
                if (e != null){
                    double base = progressionConfig.getDouble("experience.exp_gain.enchantment_base." + s);
                    enchantmentBaseValues.put(e, base);
                } else {
                    ValhallaMMO.getPlugin().getLogger().warning("invalid enchantment type given at progression_enchanting.yml experience.exp_gain.enchantment_base." + s);
                }
            }
        }

        ConfigurationSection levelMultiplierSection = progressionConfig.getConfigurationSection("experience.exp_gain.enchantment_level_multiplier");
        if (levelMultiplierSection != null){
            for (String s : levelMultiplierSection.getKeys(false)){
                try {
                    int level = Integer.parseInt(s);
                    double multiplier = progressionConfig.getDouble("experience.exp_gain.enchantment_level_multiplier." + s);
                    enchantmentLevelMultipliers.put(level, multiplier);
                } catch (NumberFormatException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("invalid enchantment level given at skill_enchanting.yml experience.exp_gain.enchantment_level_multiplier." + s);
                }
            }
        }

        ConfigurationSection materialClassMultiplierSection = progressionConfig.getConfigurationSection("experience.exp_gain.enchantment_type_multiplier");
        if (materialClassMultiplierSection != null){
            for (String s : materialClassMultiplierSection.getKeys(false)){
                try {
                    MaterialClass materialClass = MaterialClass.valueOf(s);
                    double multiplier = progressionConfig.getDouble("experience.exp_gain.enchantment_type_multiplier." + s);
                    enchantmentMaterialClassMultipliers.put(materialClass, multiplier);
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("invalid Material Class given at skill_enchanting.yml experience.exp_gain.enchantment_level_multiplier." + s);
                }
            }
        }

        ConfigurationSection itemTypeMultiplierSection = progressionConfig.getConfigurationSection("experience.exp_gain.enchantment_item_multiplier");
        if (itemTypeMultiplierSection != null){
            for (String s : itemTypeMultiplierSection.getKeys(false)){
                try {
                    EquipmentClass equipmentClass = EquipmentClass.valueOf(s);
                    double multiplier = progressionConfig.getDouble("experience.exp_gain.enchantment_item_multiplier." + s);
                    enchantmentEquipmentClassMultipliers.put(equipmentClass, multiplier);
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("invalid Equipment Class given at skill_enchanting.yml experience.exp_gain.enchantment_level_multiplier." + s);
                }
            }
        }
    }

    @EventHandler
    public void onGrindstoneUsage(InventoryClickEvent e){
        if (e.getClickedInventory() instanceof GrindstoneInventory && !e.isCancelled()){
            CooldownManager.getInstance().setCooldown(e.getWhoClicked().getUniqueId(), 5000, "valhalla_enchanting_vanilla_exp_multiplier_prevention");
        }
    }

    public Map<EntityType, Double> getEntityEXPMultipliers() {
        return entityEXPMultipliers;
    }

    public void mobKillHandler(EntityType type, Player p){
        if (p.hasPermission("valhalla.ignorediminishingreturns")) return;
        if (diminishingReturnsEntities.contains(type)){
            int count = 0;
            if (diminishingReturnTallyCounter.containsKey(p.getUniqueId())){
                count = diminishingReturnTallyCounter.get(p.getUniqueId());
            }
            count++;
            diminishingReturnTallyCounter.put(p.getUniqueId(), count);
        }
    }

    private void reduceTallyCounter(Player p){
        if (!diminishingReturnTallyCounter.containsKey(p.getUniqueId())){
            return;
        }
        int count = diminishingReturnTallyCounter.get(p.getUniqueId());
        if (count < diminishingReturnsCount) return;
        count -= diminishingReturnsCount;
        diminishingReturnTallyCounter.put(p.getUniqueId(), count);
    }

    public boolean doDiminishingReturnsApply(Player p){
        if (p.hasPermission("valhalla.ignorediminishingreturns")) return false;
        if (!diminishingReturnTallyCounter.containsKey(p.getUniqueId())){
            return false;
        }
        int count = diminishingReturnTallyCounter.get(p.getUniqueId());
        return count >= diminishingReturnsCount;
    }

    @Override
    public NamespacedKey getKey() {
        return new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_profile_enchanting");
    }

    @Override
    public Profile getCleanProfile() {
        return new EnchantingProfile(null);
    }

    @Override
    public void addEXP(Player p, double amount, boolean silent, PlayerSkillExperienceGainEvent.ExperienceGainReason reason) {
        double finalAmount = amount * ((AccumulativeStatManager.getInstance().getStats("ENCHANTING_EXP_GAIN_GENERAL", p, true) / 100D));
        super.addEXP(p, finalAmount, silent, reason);
    }

    public void rewardEXPForEnchantments(Player p, ItemStack item, Map<Enchantment, Integer> enchantments){
        if (item == null) return;
        if (p == null) return;
        if (enchantments.isEmpty()) return;
        double amount = 0D;
        double equipmentMultiplier;
        double materialMultiplier;
        EquipmentClass equipmentClass = EquipmentClass.getClass(item);
        MaterialClass materialClass = MaterialClass.getMatchingClass(item);
        if (equipmentClass == null) {
            equipmentMultiplier = 1D;
        } else {
            if (enchantmentEquipmentClassMultipliers.get(equipmentClass) != null){
                equipmentMultiplier = enchantmentEquipmentClassMultipliers.get(equipmentClass);
            } else {
                equipmentMultiplier = 1D;
            }
        }
        if (materialClass == null) {
            materialMultiplier = 1D;
        } else {
            if (enchantmentMaterialClassMultipliers.get(materialClass) != null){
                materialMultiplier = enchantmentMaterialClassMultipliers.get(materialClass);
            } else {
                materialMultiplier = 1D;
            }
        }

        for (Enchantment e : enchantments.keySet()){
            double levelMultiplier = 0D;
            if (enchantmentLevelMultipliers.containsKey(enchantments.get(e))){
                levelMultiplier = enchantmentLevelMultipliers.get(enchantments.get(e));
            }
            double baseAmount = 0D;
            if (enchantmentBaseValues.containsKey(e)){
                baseAmount = enchantmentBaseValues.get(e);
            }

            amount += baseAmount * equipmentMultiplier * levelMultiplier * materialMultiplier;
        }
        double generalEXPMultiplier = ((AccumulativeStatManager.getInstance().getStats("ENCHANTING_EXP_GAIN_VANILLA", p, true) / 100D));
        amount *= generalEXPMultiplier;

        if (doDiminishingReturnsApply(p)){
            amount *= diminishingReturnsMultiplier;
            reduceTallyCounter(p);
        }
        addEXP(p, amount, false, PlayerSkillExperienceGainEvent.ExperienceGainReason.SKILL_ACTION);
    }

    @Override
    public void onEntityDamage(EntityDamageByEntityEvent event) {

    }

    @Override
    public void onEntityKilled(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        mobKillHandler(event.getEntityType(), event.getEntity().getKiller());
        if (entityEXPMultipliers.containsKey(event.getEntityType())){
            event.setDroppedExp(Utils.excessChance(event.getDroppedExp() * entityEXPMultipliers.get(event.getEntityType())));
        }
    }

    @Override
    public void onEnchantItem(EnchantItemEvent event) {
        Player enchanter = event.getEnchanter();
        ItemStack item = event.getItem();
        if (SmithingItemTreatmentManager.getInstance().hasTreatment(item, ItemTreatment.UNENCHANTABLE)) {
            event.setCancelled(true);
            return;
        }

        int generalSkill = (int) AccumulativeStatManager.getInstance().getStats("ENCHANTING_QUALITY_GENERAL", enchanter, true);
        int vanillaSkill = (int) AccumulativeStatManager.getInstance().getStats("ENCHANTING_QUALITY_VANILLA", enchanter, true);
        double chance = AccumulativeStatManager.getInstance().getStats("ENCHANTING_AMPLIFY_CHANCE", enchanter, true);

        for (Enchantment en : event.getEnchantsToAdd().keySet()){
            if (Utils.getRandom().nextDouble() <= chance){
                Map<Enchantment, Integer> newEnchantments = EnchantingItemEnchantmentsManager.getInstance().applyEnchantmentScaling(generalSkill + vanillaSkill, en, event.getEnchantsToAdd().get(en));
                event.getEnchantsToAdd().putAll(newEnchantments);
            }
        }


        Map<Integer, EnchantmentOffer[]> cachedOffers = storedEnchantmentOffers.getOrDefault(enchanter.getUniqueId(), new HashMap<>()).getOrDefault(event.getItem().getType(), new HashMap<>());
        if (enchantmentOfferSkillLevels.getOrDefault(enchanter.getUniqueId(), 0) == (vanillaSkill + generalSkill)) {
            offersLoop: for (EnchantmentOffer[] offers : cachedOffers.values()){
                for (EnchantmentOffer offer : offers){
                    if (offer.getCost() == event.getExpLevelCost() && event.getEnchantsToAdd().containsKey(offer.getEnchantment())) {
                        event.getEnchantsToAdd().put(offer.getEnchantment(), offer.getEnchantmentLevel());
                        break offersLoop;
                    }
                }
            }
        }

//            if (item.getItemMeta() instanceof EnchantmentStorageMeta){
//                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item;
//                for (Enchantment en : meta.getStoredEnchants().keySet()){
//                    e.getEnchantsToAdd().put(en, meta.getStoredEnchants().get(en));
//                }
//            } else {
//                for (Enchantment en : item.getEnchantments().keySet()){
//                    e.getEnchantsToAdd().put(en, item.getEnchantments().get(en));
//                }
//            }

        int consumed = event.whichButton() + 1;

        if (event.getEnchanter().getGameMode() != GameMode.CREATIVE){
            if (Utils.getRandom().nextDouble() <= AccumulativeStatManager.getInstance().getStats("ENCHANTING_LAPIS_SAVE_CHANCE", enchanter, true)){
                ItemStack lapisSlot = event.getInventory().getItem(1);
                ItemStack newLapis = new ItemStack(Material.LAPIS_LAZULI, consumed);
                if (!Utils.isItemEmptyOrNull(lapisSlot)){
                    if (lapisSlot.isSimilar(newLapis)){
                        lapisSlot.setAmount(lapisSlot.getAmount() + consumed);
                        event.getInventory().setItem(1, lapisSlot);
                    } else {
                        Map<Integer, ItemStack> remainingItems = event.getEnchanter().getInventory().addItem(newLapis);
                        if (!remainingItems.isEmpty()){
                            for (ItemStack i : remainingItems.values()){
                                Item drop = event.getEnchanter().getWorld().dropItemNaturally(event.getEnchanter().getEyeLocation(), i);
                                drop.setOwner(event.getEnchanter().getUniqueId());
                            }
                        }
                    }
                } else {
                    event.getInventory().setItem(1, newLapis);
                }
            }
        }

        double refundChance = AccumulativeStatManager.getInstance().getStats("ENCHANTING_REFUND_CHANCE", event.getEnchanter(), true);
        if (Utils.getRandom().nextDouble() <= refundChance){
            double refundAmount = Math.max(0, Math.min(AccumulativeStatManager.getInstance().getStats("ENCHANTING_REFUND_AMOUNT", event.getEnchanter(), true), 1D));
            // refundAmount is now a value between 0 and 1
            int expSpent = Utils.getTotalExperience(enchanter.getLevel()) - Utils.getTotalExperience(enchanter.getLevel() - (event.whichButton() + 1));

            int refunded = Utils.excessChance(expSpent * refundAmount);
            event.getEnchanter().giveExp(Math.max(0, refunded));
        }

        rewardEXPForEnchantments(enchanter, item, event.getEnchantsToAdd());
        resetPlayerEnchantmentCache(enchanter);
    }

    private static final Map<UUID, Map<Material, Map<Integer, EnchantmentOffer[]>>> storedEnchantmentOffers = new HashMap<>();
    private static final Map<UUID, Integer> enchantmentOfferSkillLevels = new HashMap<>();

    public static void resetPlayerEnchantmentCache(Player p){
        storedEnchantmentOffers.remove(p.getUniqueId());
        enchantmentOfferSkillLevels.remove(p.getUniqueId());
    }

    @Override
    public void onPrepareEnchant(PrepareItemEnchantEvent event) {
        if (SmithingItemTreatmentManager.getInstance().hasTreatment(event.getItem(), ItemTreatment.UNENCHANTABLE)) {
            event.setCancelled(true);
            return;
        }
        Map<Material, Map<Integer, EnchantmentOffer[]>> existingMaterialOffers = storedEnchantmentOffers.getOrDefault(event.getEnchanter().getUniqueId(), new HashMap<>());
        Map<Integer, EnchantmentOffer[]> existingLevelOffers = existingMaterialOffers.getOrDefault(event.getItem().getType(), new HashMap<>());
        if (!existingLevelOffers.containsKey(event.getEnchantmentBonus())){
            int generalSkill = (int) AccumulativeStatManager.getInstance().getStats("ENCHANTING_QUALITY_GENERAL", event.getEnchanter(), true);
            int vanillaSkill = (int) AccumulativeStatManager.getInstance().getStats("ENCHANTING_QUALITY_VANILLA", event.getEnchanter(), true);
            double chance = AccumulativeStatManager.getInstance().getStats("ENCHANTING_AMPLIFY_CHANCE", event.getEnchanter(), true);

            EnchantingItemEnchantmentsManager.getInstance().applyEnchantmentOffersScaling(generalSkill + vanillaSkill, event.getOffers(), chance);

            existingLevelOffers.put(event.getEnchantmentBonus(), event.getOffers());
            existingMaterialOffers.put(event.getItem().getType(), existingLevelOffers);
            enchantmentOfferSkillLevels.put(event.getEnchanter().getUniqueId(), generalSkill + vanillaSkill);
            storedEnchantmentOffers.put(event.getEnchanter().getUniqueId(), existingMaterialOffers);
        } else {
            EnchantmentOffer[] storedOffers = existingLevelOffers.get(event.getEnchantmentBonus());
            for (int i = 0; i < storedOffers.length && i < event.getOffers().length; i++){
                event.getOffers()[i] = storedOffers[i];
            }
        }
    }


    private final Map<UUID, Map<Enchantment, Integer>> anvilMaxLevelCache = new HashMap<>();

    @Override
    public void onAnvilUsage(PrepareAnvilEvent event) {
        Player combiner = (Player) event.getView().getPlayer();
        ItemStack item1 = event.getInventory().getItem(0);
        ItemStack item2 = event.getInventory().getItem(1);
        ItemStack result = event.getResult();
        if (!Utils.isItemEmptyOrNull(item1) && !Utils.isItemEmptyOrNull(item2) && !Utils.isItemEmptyOrNull(result)) {
            // If item 1 or item 2 have the UNENCHANTABLE tag, and the result has different enchantments from item1 it implies
            // the player attempted to combine items that are unenchantable and should therefore not be combined
            // if the result ends up having the same enchantments as item 1 it can be assumed no enchantments are added to
            // the item, and so this event should not be interfered with. if any of the items in the anvil are empty,
            // nothing is being combined(successfully) and so nothing is wrong
            if (SmithingItemTreatmentManager.getInstance().hasTreatment(item1, ItemTreatment.UNENCHANTABLE)
                    || SmithingItemTreatmentManager.getInstance().hasTreatment(item2, ItemTreatment.UNENCHANTABLE)){
                boolean matches = result.getEnchantments().size() == item1.getEnchantments().size();
                if (matches){
                    for (Enchantment e : item1.getEnchantments().keySet()){
                        if (!result.getEnchantments().containsKey(e)){
                            matches = false;
                            break;
                        }
                        if (result.getEnchantments().get(e).intValue() != item1.getEnchantments().get(e).intValue()){
                            matches = false;
                            break;
                        }
                    }
                }

                if (!matches){
                    event.setResult(null);
                    return;
                }
            }

            if (item2.getItemMeta() instanceof EnchantmentStorageMeta){
                if (((EnchantmentStorageMeta) item2.getItemMeta()).getStoredEnchants().isEmpty()) return;
            } else if (item2.getEnchantments().isEmpty()) return;

            Map<Enchantment, Integer> maxLevels;
            if (!anvilMaxLevelCache.containsKey(combiner.getUniqueId())){
                if (combiner.getGameMode() == GameMode.CREATIVE){
                    maxLevels = new HashMap<>();
                    for (Enchantment e : Enchantment.values()){
                        maxLevels.put(e, e.getMaxLevel() == 1 ? 1 : Integer.MAX_VALUE);
                    }
                } else {
                    int playerAnvilSkill = (int) AccumulativeStatManager.getInstance().getStats("ENCHANTING_QUALITY_ANVIL", combiner, true);
                    maxLevels = EnchantingItemEnchantmentsManager.getInstance().getAnvilMaxLevels(playerAnvilSkill);
                    anvilMaxLevelCache.put(combiner.getUniqueId(), maxLevels);
                }
            } else {
                maxLevels = anvilMaxLevelCache.get(combiner.getUniqueId());
                ValhallaMMO.getPlugin().getServer().getScheduler().runTaskLater(
                        ValhallaMMO.getPlugin(),
                        () -> anvilMaxLevelCache.remove(combiner.getUniqueId()),
                        5L);
            }
            event.getInventory().setMaximumRepairCost(Integer.MAX_VALUE);
            combiner.updateInventory();

            Map<Enchantment, Integer> item1Enchantments = new HashMap<>();
            Map<Enchantment, Integer> item2Enchantments = new HashMap<>();
            Map<Enchantment, Integer> resultEnchantments = new HashMap<>();
            if (item1.getItemMeta() instanceof EnchantmentStorageMeta){
                item1Enchantments.putAll(((EnchantmentStorageMeta) item1.getItemMeta()).getStoredEnchants());
            } else {
                item1Enchantments.putAll(item1.getEnchantments());
            }

            if (item2.getItemMeta() instanceof EnchantmentStorageMeta){
                item2Enchantments.putAll(((EnchantmentStorageMeta) item2.getItemMeta()).getStoredEnchants());
            } else {
                item2Enchantments.putAll(item2.getEnchantments());
            }

            if (result.getItemMeta() instanceof EnchantmentStorageMeta){
                resultEnchantments.putAll(((EnchantmentStorageMeta) result.getItemMeta()).getStoredEnchants());
            } else {
                resultEnchantments.putAll(result.getEnchantments());
            }
            Map<Enchantment, Integer> newEnchantments = combineEnchantments(item1Enchantments, item2Enchantments, maxLevels);
            for (Enchantment e : resultEnchantments.keySet()){
                if (!newEnchantments.containsKey(e)) continue;
                resultEnchantments.put(e, newEnchantments.get(e));
            }

            if (result.getItemMeta() instanceof EnchantmentStorageMeta){
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) result.getItemMeta();
                for (Enchantment e : meta.getStoredEnchants().keySet()){
                    meta.removeStoredEnchant(e);
                }
                for (Enchantment e : resultEnchantments.keySet()){
                    meta.addStoredEnchant(e, resultEnchantments.get(e), true);
                }
                result.setItemMeta(meta);
            } else {
                for (Enchantment e : result.getEnchantments().keySet()){
                    result.removeEnchantment(e);
                }
                result.addUnsafeEnchantments(resultEnchantments);
            }
            event.setResult(result);

            if (event.getInventory().getRepairCost() >= 40){
                event.getInventory().setRepairCost(39);
            }

            combiner.updateInventory();
        }
    }

    private Map<Enchantment, Integer> combineEnchantments(Map<Enchantment, Integer> item1Enchantments, Map<Enchantment, Integer> item2Enchantments, Map<Enchantment, Integer> maxAllowed){
        Map<Enchantment, Integer> newEnchantments = new HashMap<>();

        for (Enchantment e : item1Enchantments.keySet()){
            int level = item1Enchantments.get(e);
            int maxLevel = maxAllowed.getOrDefault(e, e.getMaxLevel());
            if (item2Enchantments.containsKey(e)){
                int compareLevel = item2Enchantments.get(e);
                if (level == compareLevel){
                    newEnchantments.put(e, (!anvil_downgrading && maxLevel <= level ? level : Math.min(maxLevel, level + 1)));
                } else {
                    newEnchantments.put(e, (!anvil_downgrading && maxLevel <= level ? level : Math.min(maxLevel, Math.max(level, compareLevel))));
                }
            } else {
                newEnchantments.put(e, (!anvil_downgrading && maxLevel <= level ? level : Math.min(maxLevel, level)));
            }
        }

        for (Enchantment e : item2Enchantments.keySet()){
            int level = item2Enchantments.get(e);
            int maxLevel = maxAllowed.getOrDefault(e, e.getMaxLevel());
            if (!item1Enchantments.containsKey(e)){
                newEnchantments.put(e, (!anvil_downgrading && maxLevel <= level ? level : Math.min(maxLevel, level)));
            }
        }

        return newEnchantments;
    }
}
