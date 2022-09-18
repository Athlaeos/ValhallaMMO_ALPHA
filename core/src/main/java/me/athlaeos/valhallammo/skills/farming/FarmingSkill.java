package me.athlaeos.valhallammo.skills.farming;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.MinecraftVersion;
import me.athlaeos.valhallammo.dom.Offset;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.events.BlockDropItemStackEvent;
import me.athlaeos.valhallammo.events.EntityCustomPotionEffectEvent;
import me.athlaeos.valhallammo.events.PlayerSkillExperienceGainEvent;
import me.athlaeos.valhallammo.items.PotionType;
import me.athlaeos.valhallammo.loottables.ChancedBlockLootTable;
import me.athlaeos.valhallammo.loottables.ChancedEntityLootTable;
import me.athlaeos.valhallammo.loottables.LootManager;
import me.athlaeos.valhallammo.loottables.TieredLootTable;
import me.athlaeos.valhallammo.loottables.chance_based_block_loot.ChancedFarmingCropsLootTable;
import me.athlaeos.valhallammo.loottables.chance_based_entity_loot.ChancedFarmingAnimalLootTable;
import me.athlaeos.valhallammo.loottables.tiered_loot_tables.TieredFishingLootTable;
import me.athlaeos.valhallammo.managers.*;
import me.athlaeos.valhallammo.skills.*;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.ShapeUtils;
import me.athlaeos.valhallammo.utility.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.Beehive;
import org.bukkit.block.data.type.CaveVines;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.List;
import java.util.*;

public class FarmingSkill extends Skill implements GatheringSkill, OffensiveSkill, ItemConsumptionSkill, PotionEffectSkill, EntityTargetingSkill, FishingSkill, InteractSkill {
    private final Map<Material, Double> blockDropEXPReward;
    private final Map<Material, Double> blockInteractEXPReward;
    private final Map<EntityType, Double> entityBreedEXPReward;
    private final double fishingEXPReward;
    private ChancedFarmingCropsLootTable farmingLootTable = null;
    private TieredFishingLootTable fishingLootTable = null;
    private ChancedFarmingAnimalLootTable animalLootTable = null;
    private final int ultraBreakLimit;
    private final boolean ultraBreakInstantPickup;
    private final boolean forgivingMultipliers;
    private final boolean cosmetic_outline;
    private final String outline_color;
    private final boolean ultra_harvesting_instant;

    public Map<Material, Double> getBlockDropEXPReward() {
        return blockDropEXPReward;
    }

    public Map<EntityType, Double> getEntityBreedEXPReward() {
        return entityBreedEXPReward;
    }

    public FarmingSkill(String type) {
        super(type);
        skillTreeMenuOrderPriority = 6;
        ChancedBlockLootTable farming = LootManager.getInstance().getChancedBlockLootTables().get("farming_farming");
        if (farming != null) {
            if (farming instanceof ChancedFarmingCropsLootTable) {
                farmingLootTable = (ChancedFarmingCropsLootTable) farming;
            }
        }
        TieredLootTable fishing = LootManager.getInstance().getTieredLootTables().get("farming_fishing");
        if (fishing != null) {
            if (fishing instanceof TieredFishingLootTable) {
                this.fishingLootTable = (TieredFishingLootTable) fishing;
            }
        }
        ChancedEntityLootTable animals = LootManager.getInstance().getChancedEntityLootTables().get("farming_animals");
        if (animals != null) {
            if (animals instanceof ChancedFarmingAnimalLootTable) {
                this.animalLootTable = (ChancedFarmingAnimalLootTable) animals;
            }
        }

        this.blockDropEXPReward = new HashMap<>();
        this.blockInteractEXPReward = new HashMap<>();
        this.entityBreedEXPReward = new HashMap<>();
        YamlConfiguration farmingConfig = ConfigManager.getInstance().getConfig("skill_farming.yml").get();
        YamlConfiguration progressionConfig = ConfigManager.getInstance().getConfig("progression_farming.yml").get();

        this.loadCommonConfig(farmingConfig, progressionConfig);

        fishingEXPReward = progressionConfig.getDouble("experience.farming_fishing");
        ultraBreakLimit = farmingConfig.getInt("break_limit_ultra_harvesting");
        ultraBreakInstantPickup = farmingConfig.getBoolean("instant_pickup_ultra_harvesting");
        forgivingMultipliers = farmingConfig.getBoolean("forgiving_multipliers");
        cosmetic_outline = farmingConfig.getBoolean("cosmetic_outline");
        outline_color = farmingConfig.getString("outline_color");
        ultra_harvesting_instant = farmingConfig.getBoolean("ultra_harvesting_instant");

        ConfigurationSection blockBreakSection = progressionConfig.getConfigurationSection("experience.farming_break");
        if (blockBreakSection != null) {
            for (String key : blockBreakSection.getKeys(false)) {
                try {
                    Material block = Material.valueOf(key);
                    double reward = progressionConfig.getDouble("experience.farming_break." + key);
                    blockDropEXPReward.put(block, reward);
                } catch (IllegalArgumentException ignored) {
                    ValhallaMMO.getPlugin().getLogger().warning("invalid block type given:" + key + " for the block break rewards in " + progressionConfig.getName() + ".yml, no reward set for this type until corrected.");
                }
            }
        }

        ConfigurationSection blockInteractSection = progressionConfig.getConfigurationSection("experience.farming_interact");
        if (blockInteractSection != null) {
            for (String key : blockInteractSection.getKeys(false)) {
                try {
                    Material block = Material.valueOf(key);
                    if (!block.isBlock()) throw new IllegalArgumentException();
                    double reward = progressionConfig.getDouble("experience.farming_interact." + key);
                    blockInteractEXPReward.put(block, reward);
                } catch (IllegalArgumentException ignored) {
                    ValhallaMMO.getPlugin().getLogger().warning("invalid block type given:" + key + " for the block interact rewards in " + progressionConfig.getName() + ".yml, no reward set for this type until corrected.");
                }
            }
        }

        ConfigurationSection entityBreedSection = progressionConfig.getConfigurationSection("experience.farming_breed");
        if (entityBreedSection != null) {
            for (String key : entityBreedSection.getKeys(false)) {
                try {
                    EntityType entity = EntityType.valueOf(key);
                    double reward = progressionConfig.getDouble("experience.farming_breed." + key);
                    entityBreedEXPReward.put(entity, reward);
                } catch (IllegalArgumentException ignored) {
                    ValhallaMMO.getPlugin().getLogger().warning("invalid entity type given:" + key + " for the entity breed rewards in " + progressionConfig.getName() + ".yml, no reward set for this type until corrected.");
                }
            }
        }
    }

    @Override
    public NamespacedKey getKey() {
        return new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_profile_farming");
    }

    @Override
    public Profile getCleanProfile() {
        return new FarmingProfile(null);
    }

    @Override
    public void onFishing(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.FISHING) {
            double fishingTimeMultiplier = AccumulativeStatManager.getInstance().getStats("FARMING_FISHING_TIME_MULTIPLIER", event.getPlayer(), true);
            event.getHook().setMinWaitTime(Utils.excessChance(fishingTimeMultiplier * event.getHook().getMinWaitTime()));
            event.getHook().setMaxWaitTime(Utils.excessChance(fishingTimeMultiplier * event.getHook().getMaxWaitTime()));
        } else if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            double fishingEXPMultiplier = AccumulativeStatManager.getInstance().getStats("FARMING_FISHING_VANILLA_EXP_MULTIPLIER", event.getPlayer(), true);
            event.setExpToDrop(Utils.excessChance(event.getExpToDrop() * fishingEXPMultiplier));
            addEXP(event.getPlayer(), fishingEXPReward * ((AccumulativeStatManager.getInstance().getStats("FARMING_EXP_GAIN_FISHING", event.getPlayer(), true) / 100D)), false, PlayerSkillExperienceGainEvent.ExperienceGainReason.SKILL_ACTION);

            if (fishingLootTable != null) {
                fishingLootTable.onFishEvent(event);
            }
        }
    }

    public void onAnimalBreeding(EntityBreedEvent event) {
        if (event.getBreeder() instanceof Player) {
            Player p = (Player) event.getBreeder();
            if (entityBreedEXPReward.containsKey(event.getEntity().getType())) {
                double exp = entityBreedEXPReward.get(event.getEntity().getType()) * ((AccumulativeStatManager.getInstance().getStats("FARMING_EXP_GAIN_BREEDING", p, true) / 100D));
                this.addEXP(p, exp, false, PlayerSkillExperienceGainEvent.ExperienceGainReason.SKILL_ACTION);
            }
            int vanillaEXP = Utils.excessChance(event.getExperience() * (AccumulativeStatManager.getInstance().getStats("FARMING_BREEDING_VANILLA_EXP_MULTIPLIER", p, true)));
            event.setExperience(vanillaEXP);
            if (event.getEntity() instanceof org.bukkit.entity.Ageable) {
                org.bukkit.entity.Ageable animal = (org.bukkit.entity.Ageable) event.getEntity();
                int newAge = Utils.excessChance(animal.getAge() * (AccumulativeStatManager.getInstance().getStats("FARMING_BREEDING_AGE_REDUCTION", p, true)));
                animal.setAge(newAge);
            }
        }
    }

    @Override
    public void addEXP(Player p, double amount, boolean silent, PlayerSkillExperienceGainEvent.ExperienceGainReason reason) {
        double finalAmount = amount * ((AccumulativeStatManager.getInstance().getStats("FARMING_EXP_GAIN_GENERAL", p, true) / 100D));
        super.addEXP(p, finalAmount, silent, reason);
    }

    private final Collection<Material> ageableExceptions = ItemUtils.getMaterialList(Arrays.asList(
            "SUGAR_CANE", "BAMBOO", "CACTUS", "CHORUS_FLOWER", "KELP", "TWISTING_VINES", "WEEPING_VINES", "VINES"
    ));

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        Block b = event.getBlock();
        if (!blockDropEXPReward.containsKey(b.getType())) return;
        boolean reward = false;
        if (MinecraftVersionManager.getInstance().currentVersionNewerThan(MinecraftVersion.MINECRAFT_1_17) && b.getBlockData() instanceof CaveVines) {
            CaveVines vines = (CaveVines) b.getBlockData();
            if (vines.isBerries()) {
                reward = true;
            }
        } else if (b.getBlockData() instanceof Ageable && !ageableExceptions.contains(b.getType())) {
            Ageable data = (Ageable) b.getBlockData();
            if (data.getAge() >= data.getMaximumAge()) {
                // reward player farming exp if crop has finished growing
                BlockStore.setPlaced(b, false);
                reward = true;
            }
        } else {
            if (!BlockStore.isPlaced(b)) {
                // reward player farming exp if block is broken and wasn't placed first
                reward = true;
            }
        }
        if (reward) {
            double vanillaExpReward = AccumulativeStatManager.getInstance().getStats("FARMING_VANILLA_EXP_REWARD", event.getPlayer(), true);
            event.setExpToDrop(event.getExpToDrop() + Utils.excessChance(vanillaExpReward));
        }
    }

    @Override
    public void onBlockDamage(BlockDamageEvent event) {
        // do nothing
    }

    private final Collection<Material> legalCrops = new HashSet<>(
            ItemUtils.getMaterialList(Arrays.asList(
                    "WHEAT", "POTATOES", "CARROTS",
                    "SWEET_BERRY_BUSH", "BEETROOTS", "MELON",
                    "PUMPKIN", "COCOA", "BROWN_MUSHROOM",
                    "RED_MUSHROOM", "CRIMSON_FUNGUS", "WARPED_FUNGUS",
                    "NETHER_WART", "GLOW_BERRIES", "SUGAR_CANE",
                    "CACTUS", "KELP", "SEA_PICKLE"
            ))
    );

    private final Collection<Material> growableCrops = new HashSet<>(
            ItemUtils.getMaterialList(Arrays.asList(
                    "WHEAT", "POTATOES", "CARROTS", "BEETROOTS", "COCOA",
                    "NETHER_WART"
            ))
    );

    @Override
    public void onInteract(PlayerInteractEvent event) {
        if (event.useInteractedBlock() == Event.Result.DENY) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block b = event.getClickedBlock();
            assert b != null;
            if (b.getBlockData() instanceof Ageable) {
                if (!legalCrops.contains(b.getType())) return;
                Ageable data = (Ageable) b.getBlockData();
                if (data.getAge() >= data.getMaximumAge()) {

                    // clicked crop is of a type that needs to be destroyed to be harvested
                    boolean unlockedInstantHarvest = false;
                    boolean unlockedUltraHarvest = false;
                    int ultraHarvestCooldown = 0;
                    Profile p = ProfileManager.getManager().getProfile(event.getPlayer(), "FARMING");
                    if (p != null) {
                        if (p instanceof FarmingProfile) {
                            unlockedInstantHarvest = ((FarmingProfile) p).isInstantHarvestingUnlocked();
                            ultraHarvestCooldown = ((FarmingProfile) p).getUltraHarvestingCooldown();
                            unlockedUltraHarvest = ultraHarvestCooldown > 0;
                        }
                    }
                    if (unlockedUltraHarvest) {
                        if (event.getPlayer().isSneaking()) {
                            if (CooldownManager.getInstance().isCooldownPassed(event.getPlayer().getUniqueId(), "cooldown_ultra_harvest")) {
                                // trigger ultra harvest special ability
                                List<Block> affectedBlocks = Utils.getBlockVein(
                                        b.getLocation(),
                                        new HashSet<>(legalCrops),
                                        ultraBreakLimit,
                                        block -> {
                                            if (block.getBlockData() instanceof Ageable) {
                                                Ageable d = (Ageable) block.getBlockData();
                                                return d.getAge() >= d.getMaximumAge();
                                            }
                                            return false;
                                        },
                                        new Offset(-1, 0, 0), new Offset(0, 0, 1),
                                        new Offset(1, 0, 0), new Offset(0, 0, -1));

                                if (ultra_harvesting_instant) {
                                    Utils.alterBlocksInstant(
                                            "valhalla_ultra_harvest",
                                            event.getPlayer(),
                                            affectedBlocks,
                                            block -> legalCrops.contains(b.getType()),
                                            null,
                                            block -> {
                                                if (cosmetic_outline) {
                                                    Color color = Utils.hexToRgb(outline_color);
                                                    ShapeUtils.outlineBlock(block, 4, 0.5f, color.getRed(), color.getGreen(), color.getBlue());
                                                }
                                                instantHarvest(event.getPlayer(), block, ultraBreakInstantPickup);
                                            },
                                            null);
                                } else {
                                    Utils.alterBlocks(
                                            "valhalla_ultra_harvest",
                                            event.getPlayer(),
                                            affectedBlocks,
                                            block -> legalCrops.contains(b.getType()),
                                            null,
                                            block -> {
                                                if (cosmetic_outline) {
                                                    Color color = Utils.hexToRgb(outline_color);
                                                    ShapeUtils.outlineBlock(block, 4, 0.5f, color.getRed(), color.getGreen(), color.getBlue());
                                                }
                                                instantHarvest(event.getPlayer(), block, ultraBreakInstantPickup);
                                            },
                                            null);
                                }
                                CooldownManager.getInstance().setCooldownIgnoreIfPermission(event.getPlayer(), ultraHarvestCooldown, "cooldown_ultra_harvest");
                            } else {
                                int cooldown = (int) CooldownManager.getInstance().getCooldown(event.getPlayer().getUniqueId(), "cooldown_ultra_harvest");
                                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                        new TextComponent(
                                                Utils.chat(TranslationManager.getInstance().getTranslation("status_cooldown"))
                                                        .replace("%timestamp%", Utils.toTimeStamp(cooldown, 1000))
                                                        .replace("%time_seconds%", String.format("%d", (int) Math.ceil(cooldown / 1000D)))
                                                        .replace("%time_minutes%", String.format("%.1f", cooldown / 60000D))
                                        ));
                            }
                            return;
                        }
                    }
                    if (unlockedInstantHarvest) {
                        instantHarvest(event.getPlayer(), b, false);
                    }
                }
            }
            if (b.getBlockData() instanceof Beehive) {
                Beehive hive = (Beehive) b.getBlockData();
                if (hive.getHoneyLevel() >= hive.getMaximumHoneyLevel()) {
                    // hive full of honey
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Beehive hive = (Beehive) b.getBlockData();
                            if (hive.getHoneyLevel() < hive.getMaximumHoneyLevel()){
                                if (blockInteractEXPReward.containsKey(b.getType())) {
                                    BlockStore.setPlaced(b, false);
                                    // reward player farming exp
                                    double amount = blockInteractEXPReward.get(b.getType());
                                    addEXP(event.getPlayer(), amount * ((AccumulativeStatManager.getInstance().getStats("FARMING_EXP_GAIN_FARMING", event.getPlayer(), true) / 100D)), false, PlayerSkillExperienceGainEvent.ExperienceGainReason.SKILL_ACTION);

                                    double vanillaExpReward = AccumulativeStatManager.getInstance().getStats("FARMING_VANILLA_EXP_REWARD", event.getPlayer(), true);
                                    if (vanillaExpReward > 0) {
                                        ExperienceOrb orb = (ExperienceOrb) b.getWorld().spawnEntity(b.getLocation().add(0.5, 0.5, 0.5), EntityType.EXPERIENCE_ORB);
                                        orb.setExperience(Utils.excessChance(vanillaExpReward));
                                    }
                                }

                                double notConsumeChance = AccumulativeStatManager.getInstance().getStats("FARMING_HONEY_SAVE_CHANCE", event.getPlayer(), true);
                                if (Utils.getRandom().nextDouble() <= notConsumeChance) {
                                    // honey not consumed
                                    hive.setHoneyLevel(hive.getMaximumHoneyLevel());
                                    b.setBlockData(hive);
                                }
                            }
                        }
                    }.runTaskLater(ValhallaMMO.getPlugin(), 5L);
                }
            }
        }
    }

    private void instantHarvest(Player p, Block b, boolean toInventory) {
        if (!(b.getBlockData() instanceof Ageable)) {
            return;
        }
        Ageable data = (Ageable) b.getBlockData();
        if (data.getAge() < data.getMaximumAge()) return;
        if (blockDropEXPReward.containsKey(b.getType())) {
            // trigger instant harvest/replant mechanic
            EquipmentSlot usedSlot;
            ItemStack tool;
            Block blockUnderCrop = b.getWorld().getBlockAt(b.getLocation().add(0, -1, 0));
            if (!Utils.isItemEmptyOrNull(p.getInventory().getItemInMainHand())) {
                usedSlot = EquipmentSlot.HAND;
                tool = p.getInventory().getItemInMainHand();
            } else {
                usedSlot = EquipmentSlot.OFF_HAND;
                tool = p.getInventory().getItemInOffHand();
                if (Utils.isItemEmptyOrNull(tool)) tool = null;
            }
            BlockStore.setPlaced(b, false);
            BlockBreakEvent breakEvent = new BlockBreakEvent(b, p);
            ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(breakEvent);
            if (breakEvent.isCancelled()) return;
            ExperienceOrb orb = breakEvent.getBlock().getWorld().spawn(breakEvent.getBlock().getLocation().add(0.5, 0.5, 0.5), ExperienceOrb.class);
            orb.setExperience(breakEvent.getExpToDrop());

            BlockDropItemStackEvent dropEvent = new BlockDropItemStackEvent(b, b.getState(), p, new ArrayList<>((tool == null) ? b.getDrops() : b.getDrops(tool, p)));
            ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(dropEvent);
            if (toInventory) {
                Map<Integer, ItemStack> excessDrops = p.getInventory().addItem(dropEvent.getItems().toArray(new ItemStack[0]));
                dropEvent.getItems().clear();
                dropEvent.getItems().addAll(excessDrops.values());
            }

            data.setAge(0);
            b.getWorld().spawnParticle(Particle.BLOCK_DUST, b.getLocation().add(0.5, 0.5, 0.5), 16, 0.5, 0.5, 0.5, b.getBlockData());
            b.getWorld().playSound(b.getLocation().add(0.4, 0.4, 0.4), Sound.BLOCK_CROP_BREAK, 0.3F, 1F);
            b.setBlockData(data);
            BlockPlaceEvent placeEvent = new BlockPlaceEvent(b, b.getState(), blockUnderCrop, (tool == null) ? new ItemStack(Material.AIR) : tool, p, true, usedSlot);
            ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(placeEvent);
            if (!breakEvent.isCancelled()) {
                if (placeEvent.isCancelled()) {
                    b.setType(Material.AIR);
                }
            }
        }
    }

    @Override
    public void onBlockPlaced(BlockPlaceEvent event) {
        Block b = event.getBlock();
        if (b.getBlockData() instanceof Ageable) {
            double growthRate = AccumulativeStatManager.getInstance().getStats("FARMING_INSTANT_GROWTH_RATE", event.getPlayer(), true);
            int stages = Utils.excessChance(growthRate);
            Ageable crop = (Ageable) b.getBlockData();
            crop.setAge(Math.min(crop.getAge() + stages, crop.getMaximumAge()));
            b.setBlockData(crop);
        }
    }

    @Override
    public void onBlockHarvest(PlayerHarvestBlockEvent event) {
        if (blockInteractEXPReward.containsKey(event.getHarvestedBlock().getType())) {
            BlockStore.setPlaced(event.getHarvestedBlock(), false);
            // reward player farming exp
            double amount = blockInteractEXPReward.get(event.getHarvestedBlock().getType());
            addEXP(event.getPlayer(), amount * ((AccumulativeStatManager.getInstance().getStats("FARMING_EXP_GAIN_FARMING", event.getPlayer(), true) / 100D)), false, PlayerSkillExperienceGainEvent.ExperienceGainReason.SKILL_ACTION);

            double vanillaExpReward = AccumulativeStatManager.getInstance().getStats("FARMING_VANILLA_EXP_REWARD", event.getPlayer(), true);
            if (vanillaExpReward > 0) {
                ExperienceOrb orb = (ExperienceOrb) event.getHarvestedBlock().getWorld().spawnEntity(event.getHarvestedBlock().getLocation().add(0.5, 0.5, 0.5), EntityType.EXPERIENCE_ORB);
                orb.setExperience(Utils.excessChance(vanillaExpReward));
            }
        }
    }

    @Override
    public void onItemsDropped(BlockDropItemEvent event) {
        if (!BlockStore.isPlaced(event.getBlock())) {
            if (blockDropEXPReward.containsKey(event.getBlockState().getType()) || blockInteractEXPReward.containsKey(event.getBlockState().getType())) {
                List<Item> newItems = new ArrayList<>();
                boolean handleDropsSelf = ValhallaMMO.isSpigot();
                double dropMultiplier = AccumulativeStatManager.getInstance().getStats("FARMING_DROP_MULTIPLIER", event.getPlayer(), true);

                if (event.getBlock().getType() == Material.POTATOES || event.getBlock().getType() == Material.CARROTS){
                    for (Item i : event.getItems()){
                        if (i == null) continue;
                        if (Utils.isItemEmptyOrNull(i.getItemStack())) continue;
                        if (i.getItemStack().getAmount() == 1) {
                            event.getItems().remove(i);
                            break;
                        }
                    }
                }

                ItemUtils.multiplyItems(event.getItems(), newItems, dropMultiplier, forgivingMultipliers, blockDropEXPReward.keySet());

                if (!event.getItems().isEmpty()) {
                    double rareDropMultiplier = AccumulativeStatManager.getInstance().getStats("FARMING_RARE_DROP_CHANCE_MULTIPLIER", event.getPlayer(), true);
                    farmingLootTable.onItemDrop(event.getBlockState(), newItems, event.getPlayer(), rareDropMultiplier);
                }
                event.getItems().clear();
                if (!handleDropsSelf) {
                    event.getItems().addAll(newItems);
                }
                if (!handleDropsSelf) { // not spigot
                    event.getItems().addAll(newItems);
                } else {
                    for (Item i : newItems) {
                        event.getBlockState().getWorld().dropItemNaturally(event.getBlock().getLocation(), i.getItemStack());
                    }
                }

                double amount = 0;
                for (Item i : newItems){
                    if (i == null) continue;
                    if (Utils.isItemEmptyOrNull(i.getItemStack())) continue;
                    amount += blockDropEXPReward.getOrDefault(i.getItemStack().getType(), 0D) * i.getItemStack().getAmount();
                }
                addEXP(event.getPlayer(), amount * ((AccumulativeStatManager.getInstance().getStats("FARMING_EXP_GAIN_FARMING", event.getPlayer(), true) / 100D)), false, PlayerSkillExperienceGainEvent.ExperienceGainReason.SKILL_ACTION);

                BlockStore.setPlaced(event.getBlock(), false);
            }
        }
    }

    @Override
    public void onItemStacksDropped(BlockDropItemStackEvent event) {
        if (!BlockStore.isPlaced(event.getBlock())) {
            if (blockDropEXPReward.containsKey(event.getBlockState().getType()) || blockInteractEXPReward.containsKey(event.getBlockState().getType())) {
                List<ItemStack> newItems = new ArrayList<>();
                double dropMultiplier = AccumulativeStatManager.getInstance().getStats("FARMING_DROP_MULTIPLIER", event.getPlayer(), true);

                if (event.getBlock().getType() == Material.POTATOES || event.getBlock().getType() == Material.CARROTS){
                    for (ItemStack i : event.getItems()){
                        if (i.getAmount() == 1) {
                            event.getItems().remove(i);
                            break;
                        }
                    }
                }

                ItemUtils.multiplyItemStacks(event.getItems(), newItems, dropMultiplier, forgivingMultipliers, blockDropEXPReward.keySet());

                if (!event.getItems().isEmpty()) {
                    double rareDropMultiplier = AccumulativeStatManager.getInstance().getStats("FARMING_RARE_DROP_CHANCE_MULTIPLIER", event.getPlayer(), true);
                    farmingLootTable.onItemStackDrop(event.getBlockState(), newItems, event.getPlayer(), rareDropMultiplier);
                }
                event.getItems().clear();
                event.getItems().addAll(newItems);

                double amount = 0;
                for (ItemStack i : newItems){
                    if (Utils.isItemEmptyOrNull(i)) continue;
                    amount += blockDropEXPReward.getOrDefault(i.getType(), 0D) * i.getAmount();
                }
                addEXP(event.getPlayer(), amount * ((AccumulativeStatManager.getInstance().getStats("FARMING_EXP_GAIN_FARMING", event.getPlayer(), true) / 100D)), false, PlayerSkillExperienceGainEvent.ExperienceGainReason.SKILL_ACTION);

                BlockStore.setPlaced(event.getBlock(), false);
            }
        }
    }

    @Override
    public void onEntityDamage(EntityDamageByEntityEvent event) {

    }

    @Override
    public void onEntityKilled(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getKiller();
            if (entityBreedEXPReward.containsKey(event.getEntityType())) {
                List<ItemStack> newItems = new ArrayList<>(event.getDrops());
                //double dropMultiplier = AccumulativeStatManager.getInstance().getStats("FARMING_ANIMAL_DROP_MULTIPLIER", killer, true);

                //ItemUtils.multiplyItemStacks(event.getDrops(), newItems, dropMultiplier, forgivingMultipliers);

                if (!event.getDrops().isEmpty()) {
                    double rareDropMultiplier = AccumulativeStatManager.getInstance().getStats("FARMING_ANIMAL_RARE_DROP_CHANCE_MULTIPLIER", killer, true);
                    animalLootTable.onEntityKilled(event.getEntity(), newItems, rareDropMultiplier);
                    event.getDrops().clear();
                    event.getDrops().addAll(newItems);
                }
            }
        }
    }

    @Override
    public void onItemConsume(PlayerItemConsumeEvent event) {
        // do nothing
    }

    private final Collection<Material> fish = ItemUtils.getMaterialList(Arrays.asList(
            "COOKED_COD", "COD", "COOKED_SALMON", "SALMON", "PUFFERFISH", "TROPICAL_FISH"
    ));
    private final Collection<Material> vegetarian = ItemUtils.getMaterialList(Arrays.asList(
            "COOKIE", "DRIED_KELP", "GLOW_BERRIES", "HONEY_BOTTLE", "SWEET_BERRIES", "APPLE", "CHORUS_FRUIT",
            "MELON_SLICE", "POTATO", "CARROT", "PUMPKIN_PIE", "BAKED_POTATO", "BEETROOT", "BEETROOT_SOUP",
            "BREAD", "MUSHROOM_STEW", "SUSPICIOUS_STEW", "CAKE"
    ));
    private final Collection<Material> meats = ItemUtils.getMaterialList(Arrays.asList(
            "COOKED_MUTTON", "COOKED_PORKCHOP", "COOKED_BEEF", "COOKED_CHICKEN", "COOKED_RABBIT", "PORKCHOP",
            "BEEF", "CHICKEN", "RABBIT", "MUTTON", "RABBIT_STEW"
    ));
    private final Collection<Material> garbage = ItemUtils.getMaterialList(Arrays.asList(
            "POISONOUS_POTATO", "ROTTEN_FLESH", "SPIDER_EYE"
    ));
    private final Collection<Material> magical = ItemUtils.getMaterialList(Arrays.asList(
            "GOLDEN_CARROT", "ENCHANTED_GOLDEN_APPLE", "GOLDEN_APPLE"
    ));

    @Override
    public void onHungerChange(FoodLevelChangeEvent event) {
        if (!event.isCancelled()) {
            ItemStack food = event.getItem();
            if (food == null) return;
            float multiplier = 1F;
            if (fish.contains(food.getType())) {
                multiplier = (float) AccumulativeStatManager.getInstance().getStats("FARMING_HUNGER_MULTIPLIER_FISH", event.getEntity(), true);
            } else if (vegetarian.contains(food.getType())) {
                multiplier = (float) AccumulativeStatManager.getInstance().getStats("FARMING_HUNGER_MULTIPLIER_VEGETARIAN", event.getEntity(), true);
            } else if (meats.contains(food.getType())) {
                multiplier = (float) AccumulativeStatManager.getInstance().getStats("FARMING_HUNGER_MULTIPLIER_MEAT", event.getEntity(), true);
            } else if (garbage.contains(food.getType())) {
                multiplier = (float) AccumulativeStatManager.getInstance().getStats("FARMING_HUNGER_MULTIPLIER_GARBAGE", event.getEntity(), true);
            } else if (magical.contains(food.getType())) {
                multiplier = (float) AccumulativeStatManager.getInstance().getStats("FARMING_HUNGER_MULTIPLIER_MAGICAL", event.getEntity(), true);
            }

            int foodRegenerated = event.getFoodLevel() - event.getEntity().getFoodLevel();
            if (foodRegenerated < 0) {
                return;
            }
            foodRegenerated = Utils.excessChance(multiplier * foodRegenerated);
            event.setFoodLevel(event.getEntity().getFoodLevel() + foodRegenerated);
        }
    }

    @Override
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (event.getEntity() instanceof Player) {
            if (!event.isCancelled()) {
                if (event.getCause() == EntityPotionEffectEvent.Cause.FOOD) {
                    if (event.getNewEffect() != null) {
                        if (PotionType.getClass(event.getNewEffect().getType()) == PotionType.DEBUFF) {
                            Player target = (Player) event.getEntity();
                            Profile p = ProfileManager.getManager().getProfile(target, "FARMING");
                            if (p != null) {
                                if (p instanceof FarmingProfile) {
                                    if (((FarmingProfile) p).isBadFoodImmune()) {
                                        event.setCancelled(true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onCustomPotionEffect(EntityCustomPotionEffectEvent event) {
        if (event.getEntity() instanceof Player) {
            if (!event.isCancelled()) {
                if (event.getCause() == EntityPotionEffectEvent.Cause.FOOD) {
                    if (event.getNewEffect() != null) {
                        if (event.getNewEffect().getType() == PotionType.DEBUFF) {
                            Player target = (Player) event.getEntity();
                            Profile p = ProfileManager.getManager().getProfile(target, "FARMING");
                            if (p != null) {
                                if (p instanceof FarmingProfile) {
                                    if (((FarmingProfile) p).isBadFoodImmune()) {
                                        event.setCancelled(true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onPotionSplash(PotionSplashEvent event) {
        // do nothing
    }

    @Override
    public void onPotionLingering(LingeringPotionSplashEvent event) {
        // do nothing
    }

    @Override
    public void onEntityTargetEntity(EntityTargetLivingEntityEvent event) {
        if (event.getEntity().getType() == EntityType.BEE) {
            if (event.getTarget() instanceof Player) {
                if (event.getReason() == EntityTargetEvent.TargetReason.CLOSEST_PLAYER) {
                    Player target = (Player) event.getTarget();
                    Profile p = ProfileManager.getManager().getProfile(target, "FARMING");
                    if (p != null) {
                        if (p instanceof FarmingProfile) {
                            if (((FarmingProfile) p).isHiveBeeAggroImmune()) {
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onEntityTarget(EntityTargetEvent event) {
        // do nothing
    }

    @Override
    public void onLingerApply(AreaEffectCloudApplyEvent event) {
        // do nothing
    }

    @Override
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        // do nothing
    }

    @Override
    public void onAtEntityInteract(PlayerInteractAtEntityEvent event) {
        // do nothing
    }
}
