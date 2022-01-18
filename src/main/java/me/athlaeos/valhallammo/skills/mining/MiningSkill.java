package me.athlaeos.valhallammo.skills.farming;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.MinecraftVersion;
import me.athlaeos.valhallammo.dom.Offset;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.events.BlockDropItemStackEvent;
import me.athlaeos.valhallammo.loottables.ChancedBlockLootTable;
import me.athlaeos.valhallammo.loottables.LootManager;
import me.athlaeos.valhallammo.loottables.TieredLootTable;
import me.athlaeos.valhallammo.loottables.chance_based_loot.ChancedFarmingLootTable;
import me.athlaeos.valhallammo.loottables.tiered_loot_tables.TieredFishingLootTable;
import me.athlaeos.valhallammo.managers.*;
import me.athlaeos.valhallammo.skills.GatheringSkill;
import me.athlaeos.valhallammo.skills.OffensiveSkill;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class FarmingSkill extends Skill implements GatheringSkill, OffensiveSkill {
    private final Map<Material, Double> blockBreakEXPReward;
    private final Map<Material, Double> blockInteractEXPReward;
    private final Map<EntityType, Double> entityBreedEXPReward;
    private final double fishingEXPReward;
    private ChancedFarmingLootTable farmingLootTable = null;
    private TieredFishingLootTable fishingLootTable = null;
    private final int ultraBreakLimit;
    private final boolean ultraBreakInstantPickup;

    public FarmingSkill() {
        ChancedBlockLootTable farming = LootManager.getInstance().getChancedLootTables().get("farming_farming");
        if (farming != null){
            if (farming instanceof ChancedFarmingLootTable){
                farmingLootTable = (ChancedFarmingLootTable) farming;
            }
        }
        TieredLootTable fishing = LootManager.getInstance().getTieredLootTables().get("farming_fishing");
        if (fishing != null){
            if (fishing instanceof TieredFishingLootTable){
                this.fishingLootTable = (TieredFishingLootTable) fishing;
            }
        }

        this.blockBreakEXPReward = new HashMap<>();
        this.blockInteractEXPReward = new HashMap<>();
        this.entityBreedEXPReward = new HashMap<>();
        YamlConfiguration farmingConfig = ConfigManager.getInstance().getConfig("skill_farming.yml").get();
        YamlConfiguration progressionConfig = ConfigManager.getInstance().getConfig("progression_farming.yml").get();

        this.type = SkillType.FARMING;
        this.loadCommonConfig(farmingConfig, progressionConfig);

        fishingEXPReward = progressionConfig.getDouble("experience.farming_fishing");
        ultraBreakLimit = farmingConfig.getInt("break_limit_ultra_harvesting");
        ultraBreakInstantPickup = farmingConfig.getBoolean("instant_pickup_ultra_harvesting");

        ConfigurationSection blockBreakSection = progressionConfig.getConfigurationSection("experience.farming_break");
        if (blockBreakSection != null){
            for (String key : blockBreakSection.getKeys(false)){
                try {
                    Material block = Material.valueOf(key);
                    if (!block.isBlock()) throw new IllegalArgumentException();
                    double reward = progressionConfig.getDouble("experience.farming_break." + key);
                    blockBreakEXPReward.put(block, reward);
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("[ValhallaMMO] invalid block type given:" + key + " for the block break rewards in " + progressionConfig.getName() + ".yml, no reward set for this type until corrected.");
                }
            }
        }

        ConfigurationSection blockInteractSection = progressionConfig.getConfigurationSection("experience.farming_interact");
        if (blockInteractSection != null){
            for (String key : blockInteractSection.getKeys(false)){
                try {
                    Material block = Material.valueOf(key);
                    if (!block.isBlock()) throw new IllegalArgumentException();
                    double reward = progressionConfig.getDouble("experience.farming_interact." + key);
                    blockInteractEXPReward.put(block, reward);
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("[ValhallaMMO] invalid block type given:" + key + " for the block interact rewards in " + progressionConfig.getName() + ".yml, no reward set for this type until corrected.");
                }
            }
        }

        ConfigurationSection entityBreedSection = progressionConfig.getConfigurationSection("experience.farming_breed");
        if (entityBreedSection != null){
            for (String key : entityBreedSection.getKeys(false)){
                try {
                    EntityType entity = EntityType.valueOf(key);
                    double reward = progressionConfig.getDouble("experience.farming_breed." + key);
                    entityBreedEXPReward.put(entity, reward);
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("[ValhallaMMO] invalid entity type given:" + key + " for the entity breed rewards in " + progressionConfig.getName() + ".yml, no reward set for this type until corrected.");
                }
            }
        }
    }

    public void onFishing(PlayerFishEvent event){
        if (event.getState() == PlayerFishEvent.State.FISHING){
            double fishingTimeMultiplier = AccumulativeStatManager.getInstance().getStats("FARMING_FISHING_TIME_MULTIPLIER", event.getPlayer(), true);
            event.getHook().setMinWaitTime(Utils.excessChance(fishingTimeMultiplier * event.getHook().getMinWaitTime()));
            event.getHook().setMaxWaitTime(Utils.excessChance(fishingTimeMultiplier * event.getHook().getMaxWaitTime()));
        } else if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH){
            double fishingEXPMultiplier = AccumulativeStatManager.getInstance().getStats("FARMING_FISHING_VANILLA_EXP_MULTIPLIER", event.getPlayer(), true);
            event.setExpToDrop(Utils.excessChance(event.getExpToDrop() * fishingEXPMultiplier));
            addEXP(event.getPlayer(), fishingEXPReward * ((AccumulativeStatManager.getInstance().getStats("FARMING_EXP_GAIN_FISHING", event.getPlayer(), true) / 100D)), false);

            if (fishingLootTable != null){
                fishingLootTable.onFishEvent(event);
            }
        }
    }

    public void onAnimalBreeding(EntityBreedEvent event){
        if (event.getBreeder() instanceof Player){
            Player p = (Player) event.getBreeder();
            if (entityBreedEXPReward.containsKey(event.getEntity().getType())){
                double exp = entityBreedEXPReward.get(event.getEntity().getType()) * ((AccumulativeStatManager.getInstance().getStats("FARMING_EXP_GAIN_BREEDING", p, true) / 100D));
                this.addEXP(p, exp, false);
            }
            int vanillaEXP = Utils.excessChance(event.getExperience() * (AccumulativeStatManager.getInstance().getStats("FARMING_BREEDING_VANILLA_EXP_MULTIPLIER", p, true)));
            event.setExperience(vanillaEXP);
            if (event.getEntity() instanceof org.bukkit.entity.Ageable){
                org.bukkit.entity.Ageable animal = (org.bukkit.entity.Ageable) event.getEntity();
                int newAge = Utils.excessChance(animal.getAge() * (AccumulativeStatManager.getInstance().getStats("FARMING_BREEDING_AGE_REDUCTION", p, true)));
                animal.setAge(newAge);
            }
        }
    }

    @Override
    public void addEXP(Player p, double amount, boolean silent) {
        double finalAmount = amount * ((AccumulativeStatManager.getInstance().getStats("FARMING_EXP_GAIN_GENERAL", p, true) / 100D));
        super.addEXP(p, finalAmount, silent);
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        Block b = event.getBlock();
        if (!blockBreakEXPReward.containsKey(b.getType())) return;
        boolean reward = false;
        if (MinecraftVersionManager.getInstance().currentVersionNewerThan(MinecraftVersion.MINECRAFT_1_17) && b.getBlockData() instanceof CaveVines){
            CaveVines vines = (CaveVines) b.getBlockData();
            if (vines.isBerries()){
                reward = true;
            }
        } else if (b.getBlockData() instanceof Ageable) {
            Ageable data = (Ageable) b.getBlockData();
            if (data.getAge() >= data.getMaximumAge()) {
                // reward player farming exp if crop has finished growing
                PlaceStore.setPlaced(b, false);
                reward = true;
            }
        } else {
            if (!PlaceStore.isPlaced(b)){
                // reward player farming exp if block is broken and wasn't placed first
                reward = true;
            }
        }
        if (reward){
            double amount = blockBreakEXPReward.get(b.getType());
            addEXP(event.getPlayer(), amount * ((AccumulativeStatManager.getInstance().getStats("FARMING_EXP_GAIN_FARMING", event.getPlayer(), true) / 100D)), false);

            double vanillaExpReward = AccumulativeStatManager.getInstance().getStats("FARMING_VANILLA_EXP_REWARD", event.getPlayer(), true);
            event.setExpToDrop(event.getExpToDrop() + Utils.excessChance(vanillaExpReward));
        }
    }

    private final List<Material> legalCrops = ItemUtils.getMaterialList(Arrays.asList(
            "WHEAT", "POTATOES", "CARROTS",
            "SWEET_BERRY_BUSH", "BEETROOTS", "MELON",
            "PUMPKIN", "COCOA", "BROWN_MUSHROOM",
            "RED_MUSHROOM", "CRIMSON_FUNGUS", "WARPED_FUNGUS",
            "NETHER_WART", "GLOW_BERRIES", "SUGAR_CANE",
            "CACTUS", "KELP", "SEA_PICKLE"
    ));
    @Override
    public void onBlockInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block b = event.getClickedBlock();
            assert b != null;
            if (b.getBlockData() instanceof Ageable){
                if (!legalCrops.contains(b.getType())) return;
                Ageable data = (Ageable) b.getBlockData();
                if (data.getAge() >= data.getMaximumAge()){
                    // crop fully grown
                    if (Arrays.asList("SWEET_BERRY_BUSH", "GLOW_BERRIES").contains(b.getType().toString())){
                        // clicked crop is of a type that isn't destroyed when harvested
                        if (blockInteractEXPReward.containsKey(b.getType())){
                            PlaceStore.setPlaced(b, false);
                            // reward player farming exp
                            double amount = blockInteractEXPReward.get(b.getType());
                            addEXP(event.getPlayer(), amount * ((AccumulativeStatManager.getInstance().getStats("FARMING_EXP_GAIN_FARMING", event.getPlayer(), true) / 100D)), false);

                            double vanillaExpReward = AccumulativeStatManager.getInstance().getStats("FARMING_VANILLA_EXP_REWARD", event.getPlayer(), true);
                            ExperienceOrb orb = (ExperienceOrb) b.getWorld().spawnEntity(b.getLocation().add(0.5, 0.5, 0.5), EntityType.EXPERIENCE_ORB);
                            orb.setExperience(Utils.excessChance(vanillaExpReward));
                        }
                    } else {
                        // clicked crop is of a type that needs to be destroyed to be harvested
                        boolean unlockedInstantHarvest = false;
                        boolean unlockedUltraHarvest = false;
                        int ultraHarvestCooldown = 0;
                        Profile p = ProfileUtil.getProfile(event.getPlayer(), SkillType.FARMING);
                        if (p != null){
                            if (p instanceof FarmingProfile){
                                unlockedInstantHarvest = ((FarmingProfile) p).isInstantHarvestingUnlocked();
                                ultraHarvestCooldown = ((FarmingProfile) p).getUltraHarvestingCooldown();
                                unlockedUltraHarvest = ultraHarvestCooldown > 0;
                            }
                        }
                        if (unlockedUltraHarvest){
                            if (event.getPlayer().isSneaking()){
                                if (CooldownManager.getInstance().isCooldownPassed(event.getPlayer().getUniqueId(), "cooldown_ultra_harvest")){
                                    // trigger ultra harvest special ability
                                    List<Block> affectedBlocks = Utils.getBlockVein(
                                            b.getLocation(),
                                            new HashSet<>(legalCrops),
                                            ultraBreakLimit,
                                            block -> {
                                                if (block.getBlockData() instanceof Ageable){
                                                    Ageable d = (Ageable) block.getBlockData();
                                                    return d.getAge() >= d.getMaximumAge();
                                                }
                                                return false;
                                            },
                                            new Offset(-1, 0, 0), new Offset(0, 0, 1),
                                            new Offset(1, 0, 0), new Offset(0, 0, -1));

                                    new BukkitRunnable(){
                                        final Iterator<Block> iterator = affectedBlocks.iterator();
                                        @Override
                                        public void run() {
                                            if (iterator.hasNext()){
                                                Block b = iterator.next();
                                                if (legalCrops.contains(b.getType())) {
                                                    instantHarvest(event.getPlayer(), b, ultraBreakInstantPickup);
                                                }
                                            } else {
                                                cancel();
                                            }
                                        }
                                    }.runTaskTimer(ValhallaMMO.getPlugin(), 0L, 1L);
                                    if (!event.getPlayer().hasPermission("valhalla.ignorecooldowns")){
                                        CooldownManager.getInstance().setCooldown(event.getPlayer().getUniqueId(), ultraHarvestCooldown, "cooldown_ultra_harvest");
                                    }
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
                        if (unlockedInstantHarvest){
                            instantHarvest(event.getPlayer(), b, false);
                        }
                    }
                }
            }
            if (b.getBlockData() instanceof Beehive){
                Beehive hive = (Beehive) b.getBlockData();
                if (hive.getHoneyLevel() >= hive.getMaximumHoneyLevel()){
                    // hive full of honey
                    double notConsumeChance = AccumulativeStatManager.getInstance().getStats("FARMING_HONEY_SAVE_CHANCE", event.getPlayer(), true);
                    if (Utils.getRandom().nextDouble() <= notConsumeChance){
                        // honey not consumed
                        new BukkitRunnable(){
                            @Override
                            public void run() {
                                hive.setHoneyLevel(hive.getMaximumHoneyLevel());
                                b.setBlockData(hive);
                            }
                        }.runTaskLater(ValhallaMMO.getPlugin(), 1L);
                    }
                }
            }
        }
    }

    private void instantHarvest(Player p, Block b, boolean toInventory){
        if (!(b.getBlockData() instanceof Ageable)){
            return;
        }
        Ageable data = (Ageable) b.getBlockData();
        if (data.getAge() < data.getMaximumAge()) return;
        if (blockBreakEXPReward.containsKey(b.getType())) {
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
            }
            PlaceStore.setPlaced(b, false);
            BlockBreakEvent breakEvent = new BlockBreakEvent(b, p);
            ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(breakEvent);

            BlockDropItemStackEvent dropEvent = new BlockDropItemStackEvent(b, b.getState(), p, new ArrayList<>(b.getDrops(tool, p)));
            ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(dropEvent);
            if (toInventory){
                Map<Integer, ItemStack> excessDrops = p.getInventory().addItem(dropEvent.getItems().toArray(new ItemStack[0]));
                dropEvent.getItems().clear();
                dropEvent.getItems().addAll(excessDrops.values());
            }

            data.setAge(0);
            b.getWorld().spawnParticle(Particle.BLOCK_DUST, b.getLocation().add(0.5, 0.5, 0.5), 16, 0.5, 0.5, 0.5, b.getBlockData());
            b.getWorld().playSound(b.getLocation().add(0.4, 0.4, 0.4), Sound.BLOCK_CROP_BREAK, 0.3F, 1F);
            b.setBlockData(data);
            BlockPlaceEvent placeEvent = new BlockPlaceEvent(b, b.getState(), blockUnderCrop, tool, p, true, usedSlot);
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
        if (b.getBlockData() instanceof Ageable){
            double growthRate = AccumulativeStatManager.getInstance().getStats("FARMING_INSTANT_GROWTH_RATE", event.getPlayer(), true);
            int stages = Utils.excessChance(growthRate);
            Ageable crop = (Ageable) b.getBlockData();
            crop.setAge(Math.min(crop.getAge() + stages, crop.getMaximumAge()));
            b.setBlockData(crop);
        }
    }

    @Override
    public void onItemsDropped(BlockDropItemEvent event) {
        if (!PlaceStore.isPlaced(event.getBlock())) {
            if (blockBreakEXPReward.containsKey(event.getBlockState().getType()) || blockInteractEXPReward.containsKey(event.getBlockState().getType())) {
                List<Item> newItems = new ArrayList<>();
                Location dropLocation = null;
                for (Item i : event.getItems()){
                    if (dropLocation == null) dropLocation = i.getLocation();
                    if (dropLocation.getWorld() == null) return;
                    ItemStack item = i.getItemStack();
                    double dropMultiplier = AccumulativeStatManager.getInstance().getStats("FARMING_DROP_MULTIPLIER", event.getPlayer(), true);
                    int newAmount = Utils.excessChance(item.getAmount() * dropMultiplier);
                    if (newAmount > item.getMaxStackSize()){
                        int limit = 4;
                        while(newAmount > item.getMaxStackSize()){
                            if (limit <= 0) break;
                            ItemStack drop = item.clone();
                            drop.setAmount(item.getMaxStackSize());
                            newItems.add(
                                    dropLocation.getWorld().dropItem(dropLocation, drop)
                            );
                            newAmount -= item.getMaxStackSize();
                            limit--;
                        }
                    }
                    item.setAmount(newAmount);
                    i.setItemStack(item);
                    newItems.add(i);
                }
                event.getItems().clear();
                event.getItems().addAll(newItems);
            }
            if (!event.getItems().isEmpty()){
                farmingLootTable.onItemDrop(event);
            }
        }
    }

    @Override
    public void onItemStacksDropped(BlockDropItemStackEvent event) {
        if (!PlaceStore.isPlaced(event.getBlock())) {
            if (blockBreakEXPReward.containsKey(event.getBlockState().getType()) || blockInteractEXPReward.containsKey(event.getBlockState().getType())) {
                List<ItemStack> newItems = new ArrayList<>();
                for (ItemStack i : event.getItems()){
                    double dropMultiplier = AccumulativeStatManager.getInstance().getStats("FARMING_DROP_MULTIPLIER", event.getPlayer(), true);
                    int newAmount = Utils.excessChance(i.getAmount() * dropMultiplier);
                    if (newAmount > i.getMaxStackSize()){
                        int limit = 4;
                        while(newAmount > i.getMaxStackSize()){
                            if (limit <= 0) break;
                            ItemStack drop = i.clone();
                            drop.setAmount(i.getMaxStackSize());
                            newItems.add(drop);
                            newAmount -= i.getMaxStackSize();
                            limit--;
                        }
                    }
                    i.setAmount(newAmount);
                    newItems.add(i);
                }
                event.getItems().clear();
                event.getItems().addAll(newItems);
            }
            if (!event.getItems().isEmpty()){
                farmingLootTable.onItemStackDrop(event);
            }
        }

//        if (!PlaceStore.isPlaced(event.getBlock())) {
//            List<ItemStack> newItems = new ArrayList<>();
//            for (ItemStack i : event.getItems()){
//                double dropMultiplier = AccumulativeStatManager.getInstance().getStats("FARMING_DROP_MULTIPLIER", event.getPlayer(), true);
//                int newAmount = Utils.excessChance(i.getAmount() * dropMultiplier);
//                if (newAmount > i.getMaxStackSize()){
//                    int limit = 4;
//                    while(newAmount > i.getMaxStackSize()){
//                        if (limit <= 0) break;
//                        ItemStack drop = i.clone();
//                        drop.setAmount(i.getMaxStackSize());
//                        newItems.add(drop);
//                        newAmount -= i.getMaxStackSize();
//                        limit--;
//                    }
//                }
//                i.setAmount(newAmount);
//                newItems.add(i);
//            }
//            event.getItems().clear();
//            event.getItems().addAll(newItems);
//
//            if (!event.getItems().isEmpty()){
//                System.out.println("on itemstack drop");
//                farmingLootTable.onItemStackDrop(event);
//            }
//        }
    }

    @Override
    public void onEntityDamage(EntityDamageByEntityEvent event) {

    }

    @Override
    public void onEntityKilled(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null){
            Player killer = event.getEntity().getKiller();
            if (entityBreedEXPReward.containsKey(event.getEntityType())){
                List<ItemStack> newItems = new ArrayList<>();
                for (ItemStack i : event.getDrops()){
                    double dropMultiplier = AccumulativeStatManager.getInstance().getStats("FARMING_ANIMAL_DROP_MULTIPLIER", killer, true);
                    int newAmount = Utils.excessChance(i.getAmount() * dropMultiplier);
                    if (newAmount > i.getMaxStackSize()){
                        int limit = 4;
                        while(newAmount > i.getMaxStackSize()){
                            if (limit <= 0) break;
                            ItemStack drop = i.clone();
                            drop.setAmount(i.getMaxStackSize());
                            newItems.add(drop);
                            newAmount -= i.getMaxStackSize();
                            limit--;
                        }
                    }
                    i.setAmount(newAmount);
                    newItems.add(i);
                }
                event.getDrops().clear();
                event.getDrops().addAll(newItems);
            }
        }
    }
}
