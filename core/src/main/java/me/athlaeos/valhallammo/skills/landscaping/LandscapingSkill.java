package me.athlaeos.valhallammo.skills.landscaping;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.Offset;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.events.BlockDropItemStackEvent;
import me.athlaeos.valhallammo.events.PlayerSkillExperienceGainEvent;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.loottables.ChancedBlockLootTable;
import me.athlaeos.valhallammo.loottables.LootManager;
import me.athlaeos.valhallammo.loottables.chance_based_block_loot.ChancedDiggingLootTable;
import me.athlaeos.valhallammo.loottables.chance_based_block_loot.ChancedWoodcuttingLootTable;
import me.athlaeos.valhallammo.loottables.chance_based_block_loot.ChancedWoodstrippingLootTable;
import me.athlaeos.valhallammo.managers.*;
import me.athlaeos.valhallammo.skills.GatheringSkill;
import me.athlaeos.valhallammo.skills.InteractSkill;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.ShapeUtils;
import me.athlaeos.valhallammo.utility.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.List;
import java.util.*;

public class LandscapingSkill extends Skill implements GatheringSkill, InteractSkill {
    private final Map<Material, Double> woodcuttingDropEXPReward;
    private final Map<Material, Double> woodcuttingStripEXPReward;
    private final Map<Material, Double> diggingDropEXPReward;
    private ChancedWoodcuttingLootTable woodcuttingLootTable = null;
    private ChancedWoodstrippingLootTable woodstrippingLootTable = null;
    private ChancedDiggingLootTable diggingLootTable = null;

    private final int treeCapitatorBreakLimit;
    private final boolean treeCapitatorInstantPickup;
    private final boolean forgivingMultipliers;
    private final int leaf_decay_limit_tree_capitator;
    private final boolean cosmetic_outline;
    private final String outline_color;
    private final boolean tree_capitator_instant;

    private final boolean handleDropsSelf = ValhallaMMO.isSpigot();

    public Map<Material, Double> getDiggingDropEXPReward() {
        return diggingDropEXPReward;
    }

    public Map<Material, Double> getWoodcuttingDropEXPReward() {
        return woodcuttingDropEXPReward;
    }

    public Map<Material, Double> getWoodcuttingStripEXPReward() {
        return woodcuttingStripEXPReward;
    }

    public LandscapingSkill(String type) {
        super(type);
        skillTreeMenuOrderPriority = 5;
        ChancedBlockLootTable woodcutting = LootManager.getInstance().getChancedBlockLootTables().get("landscaping_woodcutting");
        if (woodcutting != null){
            if (woodcutting instanceof ChancedWoodcuttingLootTable){
                woodcuttingLootTable = (ChancedWoodcuttingLootTable) woodcutting;
            }
        }
        ChancedBlockLootTable woodstripping = LootManager.getInstance().getChancedBlockLootTables().get("landscaping_woodstripping");
        if (woodstripping != null){
            if (woodstripping instanceof ChancedWoodstrippingLootTable){
                woodstrippingLootTable = (ChancedWoodstrippingLootTable) woodstripping;
            }
        }
        ChancedBlockLootTable digging = LootManager.getInstance().getChancedBlockLootTables().get("landscaping_digging");
        if (digging != null){
            if (digging instanceof ChancedDiggingLootTable){
                this.diggingLootTable = (ChancedDiggingLootTable) digging;
            }
        }

        this.woodcuttingDropEXPReward = new HashMap<>();
        this.woodcuttingStripEXPReward = new HashMap<>();
        this.diggingDropEXPReward = new HashMap<>();

        YamlConfiguration landscapingConfig = ConfigManager.getInstance().getConfig("skill_landscaping.yml").get();
        YamlConfiguration progressionConfig = ConfigManager.getInstance().getConfig("progression_landscaping.yml").get();

        this.loadCommonConfig(landscapingConfig, progressionConfig);

        treeCapitatorBreakLimit = landscapingConfig.getInt("break_limit_tree_capitator");
        treeCapitatorInstantPickup = landscapingConfig.getBoolean("instant_pickup_tree_capitator");
        forgivingMultipliers = landscapingConfig.getBoolean("forgiving_multipliers");
        leaf_decay_limit_tree_capitator = landscapingConfig.getInt("leaf_decay_limit_tree_capitator");
        cosmetic_outline = landscapingConfig.getBoolean("cosmetic_outline");
        outline_color = landscapingConfig.getString("outline_color");
        tree_capitator_instant = landscapingConfig.getBoolean("tree_capitator_instant");

        ConfigurationSection woodcuttingBreakSection = progressionConfig.getConfigurationSection("experience.woodcutting_break");
        if (woodcuttingBreakSection != null){
            for (String key : woodcuttingBreakSection.getKeys(false)){
                try {
                    Material block = Material.valueOf(key);
                    double reward = progressionConfig.getDouble("experience.woodcutting_break." + key);
                    woodcuttingDropEXPReward.put(block, reward);
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("invalid block type given:" + key + " for the woodcutting block break rewards in progression_landscaping.yml, no reward set for this type until corrected.");
                }
            }
        }

        ConfigurationSection woodcuttingStripSection = progressionConfig.getConfigurationSection("experience.woodcutting_strip");
        if (woodcuttingStripSection != null){
            for (String key : woodcuttingStripSection.getKeys(false)){
                try {
                    Material block = Material.valueOf(key);
                    if (!block.isBlock()) throw new IllegalArgumentException();
                    double reward = progressionConfig.getDouble("experience.woodcutting_strip." + key);
                    woodcuttingStripEXPReward.put(block, reward);
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("invalid block type given:" + key + " for the woodcutting block strip rewards in progression_landscaping.yml, no reward set for this type until corrected.");
                }
            }
        }

        ConfigurationSection diggingBreakSection = progressionConfig.getConfigurationSection("experience.digging_break");
        if (diggingBreakSection != null){
            for (String key : diggingBreakSection.getKeys(false)){
                try {
                    Material block = Material.valueOf(key);
                    double reward = progressionConfig.getDouble("experience.digging_break." + key);
                    diggingDropEXPReward.put(block, reward);
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("invalid block type given:" + key + " for the digging block break rewards in progression_landscaping.yml, no reward set for this type until corrected.");
                }
            }
        }
    }

    @Override
    public NamespacedKey getKey() {
        return new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_profile_landscaping");
    }

    @Override
    public Profile getCleanProfile() {
        return new LandscapingProfile(null);
    }

    @Override
    public void addEXP(Player p, double amount, boolean silent, PlayerSkillExperienceGainEvent.ExperienceGainReason reason) {
        double finalAmount = amount * ((AccumulativeStatManager.getInstance().getStats("LANDSCAPING_EXP_GAIN_GENERAL", p, true) / 100D));
        super.addEXP(p, finalAmount, silent, reason);
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        Block b = event.getBlock();
        if (b.getType().isAir()) return;
        if (!(woodcuttingDropEXPReward.containsKey(b.getType()) || diggingDropEXPReward.containsKey(b.getType()))) {
            return;
        }
        if (!BlockStore.isPlaced(b)){
            if (woodcuttingDropEXPReward.containsKey(b.getType())){
                event.setExpToDrop(event.getExpToDrop() + Utils.excessChance(AccumulativeStatManager.getInstance().getStats("LANDSCAPING_WOODCUTTING_VANILLA_EXP_REWARD", event.getPlayer(), true)));
            } else if (diggingDropEXPReward.containsKey(b.getType())){
                event.setExpToDrop(event.getExpToDrop() + Utils.excessChance(AccumulativeStatManager.getInstance().getStats("LANDSCAPING_DIGGING_VANILLA_EXP_REWARD", event.getPlayer(), true)));
            }
        }

        boolean unlockedTreeCapitator = false;
        int treeCapitatorCooldown = 0;
        Profile p = ProfileManager.getManager().getProfile(event.getPlayer(), "LANDSCAPING");
        Collection<Material> allowedTreeCapitatorBlocks = new HashSet<>();
        boolean replaceSaplings = false;
        if (p != null){
            if (p instanceof LandscapingProfile){
                treeCapitatorCooldown = ((LandscapingProfile) p).getTreeCapitatorCooldown();
                allowedTreeCapitatorBlocks = ItemUtils.getMaterialList(((LandscapingProfile) p).getValidTreeCapitatorBlocks());
                unlockedTreeCapitator = treeCapitatorCooldown > 0;
                replaceSaplings = ((LandscapingProfile) p).isReplaceSaplings();
            }
        }
        if (replaceSaplings){
            if (isTree(b)){
                Block blockUnder = b.getLocation().add(0, -1, 0).getBlock();
                if (validFungusPlantingBlocks.contains(blockUnder.getType())) {
                    final Collection<Material> validPlaceBlocks = getValidSaplingPlantingBlocks(b.getType());
                    if (validPlaceBlocks.contains(blockUnder.getType())){
                        Material sapMat = getSaplingFromBlock(b.getType());
                        if (sapMat != null){
                            new BukkitRunnable(){
                                @Override
                                public void run() {
                                    ItemStack sapling = new ItemStack(sapMat);
                                    BlockPlaceEvent placeEvent = new BlockPlaceEvent(event.getBlock(), b.getState(), blockUnder, sapling, event.getPlayer(), true, EquipmentSlot.HAND);
                                    ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(placeEvent);
                                    if (!placeEvent.isCancelled()){
                                        Block block = b.getLocation().getBlock();
                                        Block blockUnder = b.getLocation().add(0, -1, 0).getBlock();
                                        if (validPlaceBlocks.contains(blockUnder.getType())){
                                            if (block.getType().isAir()){
                                                block.setType(sapMat);
                                            }
                                        }
                                    }
                                }
                            }.runTaskLater(ValhallaMMO.getPlugin(), 4L);
                        }
                    }
                }
            }
        }
        if (!Utils.getBlockAlteringPlayers().getOrDefault("valhalla_tree_capitator", new HashSet<>()).contains(event.getPlayer().getUniqueId())){
            if (unlockedTreeCapitator){
                if (event.getPlayer().isSneaking()){
                    if (isTree(event.getBlock())){
                        if (allowedTreeCapitatorBlocks.contains(event.getBlock().getType())){
                            if (CooldownManager.getInstance().isCooldownPassed(event.getPlayer().getUniqueId(), "cooldown_tree_capitator")){
                                // trigger tree capitator special ability
                                List<Block> affectedBlocks = Utils.getBlockVein(
                                        b.getLocation(),
                                        new HashSet<>(logs),
                                        treeCapitatorBreakLimit,
                                        new Offset(-1, 1, -1), new Offset(-1, 1, 0), new Offset(-1, 1, 1),
                                        new Offset(0, 1, -1), new Offset(0, 1, 0), new Offset(0, 1, 1),
                                        new Offset(1, 1, -1), new Offset(1, 1, 0), new Offset(1, 1, 1),
                                        new Offset(-1, 0, -1), new Offset(-1, 0, 0), new Offset(-1, 0, 1),
                                        new Offset(0, 0, -1), new Offset(0, 0, 1),
                                        new Offset(1, 0, -1), new Offset(1, 0, 0), new Offset(1, 0, 1));
                                List<Block> leaves = getTreeLeaves(b);

                                Collections.shuffle(leaves);
                                me.athlaeos.valhallammo.dom.Action<Block> afterAction = leaf_decay_limit_tree_capitator > 0 ?
                                        block -> Utils.alterBlocks(
                                                "valhalla_tree_capitator_leaf_decay",
                                                event.getPlayer(),
                                                leaves,
                                                bl -> {
                                                    if (bl.getBlockData() instanceof Leaves) {
                                                        Leaves leaf = (Leaves) bl.getBlockData();
                                                        return leaf.getDistance() > 2 && !leaf.isPersistent();
                                                    }
                                                    return false;
                                                },
                                                null,
                                                Utils::decayBlock,
                                                null
                                        )
                                        : null;

                                if (tree_capitator_instant){
                                    Utils.alterBlocksInstant(
                                            "valhalla_tree_capitator",
                                            event.getPlayer(),
                                            affectedBlocks,
                                            block -> logs.contains(block.getType()),
                                            EquipmentClass.AXE,
                                            block -> {
                                                Utils.breakBlock(event.getPlayer(), block, treeCapitatorInstantPickup);
                                                if (cosmetic_outline) {
                                                    Color color = Utils.hexToRgb(outline_color);
                                                    ShapeUtils.outlineBlock(block, 4, 0.5f, color.getRed(), color.getGreen(), color.getBlue());
                                                }
                                            },
                                            afterAction
                                    );
                                } else {
                                    Utils.alterBlocks(
                                            "valhalla_tree_capitator",
                                            event.getPlayer(),
                                            affectedBlocks,
                                            block -> logs.contains(block.getType()),
                                            EquipmentClass.AXE,
                                            block -> {
                                                Utils.breakBlock(event.getPlayer(), block, treeCapitatorInstantPickup);
                                                if (cosmetic_outline) {
                                                    Color color = Utils.hexToRgb(outline_color);
                                                    ShapeUtils.outlineBlock(block, 4, 0.5f, color.getRed(), color.getGreen(), color.getBlue());
                                                }
                                            },
                                            afterAction
                                    );
                                }
                                CooldownManager.getInstance().setCooldownIgnoreIfPermission(event.getPlayer(), treeCapitatorCooldown, "cooldown_tree_capitator");
                            } else {
                                int cooldown = (int) CooldownManager.getInstance().getCooldown(event.getPlayer().getUniqueId(), "cooldown_tree_capitator");
                                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                        new TextComponent(
                                                Utils.chat(TranslationManager.getInstance().getTranslation("status_cooldown"))
                                                        .replace("%timestamp%", Utils.toTimeStamp(cooldown, 1000))
                                                        .replace("%time_seconds%", String.format("%d", (int) Math.ceil(cooldown / 1000D)))
                                                        .replace("%time_minutes%", String.format("%.1f", cooldown / 60000D))
                                        ));
                            }
                        }
                    }
                }
            }
        }
    }

    private final Collection<Material> validFungusPlantingBlocks = ItemUtils.getMaterialList(Arrays.asList(
            "GRASS_BLOCK", "DIRT", "COARSE_DIRT", "PODZOL", "FARMLAND", "ROOTED_DIRT", "MOSS_BLOCK",
            "CRIMSON_NYLIUM", "WARPED_NYLIUM", "SOUL_SOIL", "MYCELIUM"
    ));

    private final Collection<Material> validSaplingPlantingBlocks = ItemUtils.getMaterialList(Arrays.asList(
            "GRASS_BLOCK", "DIRT", "COARSE_DIRT", "PODZOL", "FARMLAND", "ROOTED_DIRT", "MOSS_BLOCK"
    ));

    private Collection<Material> getValidSaplingPlantingBlocks(Material m){
        if (logs.contains(m)){
            if (m.toString().startsWith("CRIMSON_") || m.toString().startsWith("WARPED_")){
                return validFungusPlantingBlocks;
            } else {
                return validSaplingPlantingBlocks;
            }
        }
        return new HashSet<>();
    }

    private boolean isTree(Block b){
        Block currentBlock;
        // check up to 48 blocks above the log mined
        for (int i = 1; i < 48; i++){
            if (b.getLocation().getY() + i >= b.getWorld().getMaxHeight()) break;
            currentBlock = b.getLocation().add(0, i, 0).getBlock();
            if (i < 4 && !logs.contains(currentBlock.getType())) return false; // at least 4 log blocks must be found before a leaf or air block
            if (leaves.contains(currentBlock.getType())) return true; // if leaf blocks are found, it is a tree
            if (!currentBlock.getType().isAir()){
                if (!logs.contains(currentBlock.getType())) break;
            }
            // from bottom to up, L = log, V = leaves, A = air
            // LLLAAVV is not considered a tree
            // LLLVVAA is considered a tree (AA not scanned)
        }
        return false;
    }

    private List<Block> getTreeLeaves(Block b){
        Block currentBlock;
        Set<Block> foundLeaves = new HashSet<>();
        // check up to 48 blocks above the log mined
        for (int i = 1; i < 48; i++){
            if (b.getLocation().getY() + i >= b.getWorld().getMaxHeight()) break;
            currentBlock = b.getLocation().add(0, i, 0).getBlock();

            if (leaves.contains(currentBlock.getType())) {
                return Utils.getBlockVein(
                        currentBlock.getLocation(),
                        new HashSet<>(this.leaves),
                        leaf_decay_limit_tree_capitator,
                        block -> true,
                        new Offset(0, 1, 0),
                        new Offset(-1, -1, 0), new Offset(-1, 0, 0), new Offset(-1, 1, 0),
                        new Offset(0, -1, -1), new Offset(0, 0, -1), new Offset(0, 1, -1),
                        new Offset(0, -1, 1), new Offset(0, 0, 1), new Offset(0, 1, 1),
                        new Offset(1, -1, 0), new Offset(1, 0, 0), new Offset(1, 1, 0),
                        new Offset(0, -1, 0),
                        new Offset(0, -2, 0),
                        new Offset(0, -3, 0)); // if leaf blocks are found, it is a tree
            }
            if (!logs.contains(currentBlock.getType())) break;
        }
        return new ArrayList<>();
    }

    @Override
    public void onBlockDamage(BlockDamageEvent event){
        // do nothing
    }

    private final Collection<Material> logs = ItemUtils.getMaterialList(Arrays.asList(
            "OAK_LOG", "SPRUCE_LOG", "JUNGLE_LOG", "BIRCH_LOG",
            "DARK_OAK_LOG", "ACACIA_LOG", "CRIMSON_STEM", "WARPED_STEM",
            "OAK_WOOD", "SPRUCE_WOOD", "JUNGLE_WOOD", "BIRCH_WOOD",
            "DARK_OAK_WOOD", "ACACIA_WOOD", "CRIMSON_HYPHAE", "WARPED_HYPHAE"
    ));

    private final Collection<Material> leaves = ItemUtils.getMaterialList(Arrays.asList(
            "OAK_LEAVES", "SPRUCE_LEAVES", "JUNGLE_LEAVES", "BIRCH_LEAVES",
            "DARK_OAK_LEAVES", "ACACIA_LEAVES", "NETHER_WART_BLOCK", "WARPED_WART_BLOCK"
    ));

    private final Collection<Material> strippedLogs = ItemUtils.getMaterialList(Arrays.asList(
            "STRIPPED_OAK_LOG", "STRIPPED_SPRUCE_LOG", "STRIPPED_JUNGLE_LOG", "STRIPPED_BIRCH_LOG",
            "STRIPPED_DARK_OAK_LOG", "STRIPPED_ACACIA_LOG", "STRIPPED_CRIMSON_STEM", "STRIPPED_WARPED_STEM",
            "STRIPPED_OAK_WOOD", "STRIPPED_SPRUCE_WOOD", "STRIPPED_JUNGLE_WOOD", "STRIPPED_BIRCH_WOOD",
            "STRIPPED_DARK_OAK_WOOD", "STRIPPED_ACACIA_WOOD", "STRIPPED_CRIMSON_HYPHAE", "STRIPPED_WARPED_HYPHAE"
    ));

    private final Map<Material, Collection<Material>> saplings = correlateSaplings();

    private Map<Material, Collection<Material>> correlateSaplings(){
        Map<Material, Collection<Material>> saplings = new HashMap<>();
        Map.Entry<Material, Collection<Material>> oak = getFromStrings("OAK_SAPLING", "OAK_LOG", "OAK_WOOD");
        if (oak != null) saplings.put(oak.getKey(), oak.getValue());
        Map.Entry<Material, Collection<Material>> spruce = getFromStrings("SPRUCE_SAPLING", "SPRUCE_LOG", "SPRUCE_WOOD");
        if (spruce != null) saplings.put(spruce.getKey(), spruce.getValue());
        Map.Entry<Material, Collection<Material>> jungle = getFromStrings("JUNGLE_SAPLING", "JUNGLE_LOG", "JUNGLE_WOOD");
        if (jungle != null) saplings.put(jungle.getKey(), jungle.getValue());
        Map.Entry<Material, Collection<Material>> birch = getFromStrings("BIRCH_SAPLING", "BIRCH_LOG", "BIRCH_WOOD");
        if (birch != null) saplings.put(birch.getKey(), birch.getValue());
        Map.Entry<Material, Collection<Material>> acacia = getFromStrings("ACACIA_SAPLING", "ACACIA_LOG", "ACACIA_WOOD");
        if (acacia != null) saplings.put(acacia.getKey(), acacia.getValue());
        Map.Entry<Material, Collection<Material>> dark_oak = getFromStrings("DARK_OAK_SAPLING", "DARK_OAK_LOG", "DARK_OAK_WOOD");
        if (dark_oak != null) saplings.put(dark_oak.getKey(), dark_oak.getValue());

        Map.Entry<Material, Collection<Material>> warped = getFromStrings("WARPED_FUNGUS", "WARPED_STEM", "WARPED_HYPHAE");
        if (warped != null) saplings.put(warped.getKey(), warped.getValue());
        Map.Entry<Material, Collection<Material>> crimson = getFromStrings("CRIMSON_FUNGUS", "CRIMSON_STEM", "CRIMSON_HYPHAE");
        if (crimson != null) saplings.put(crimson.getKey(), crimson.getValue());

        return saplings;
    }

    private Material getSaplingFromBlock(Material block){
        for (Material sapling : saplings.keySet()){
            Collection<Material> logs = saplings.get(sapling);
            if (logs.isEmpty()) continue;
            if (logs.contains(block)){
                return sapling;
            }
        }
        return null;
    }

    private Map.Entry<Material, Collection<Material>> getFromStrings(String key, String... value){
        try {
            return new Map.Entry<Material, Collection<Material>>() {
                @Override
                public Material getKey() {
                    return Material.valueOf(key);
                }

                @Override
                public Collection<Material> getValue() {
                    Collection<Material> values = new HashSet<>();
                    for (String v : value){
                        try {
                            values.add(Material.valueOf(v));
                        } catch (IllegalArgumentException ignored){
                        }
                    }
                    return values;
                }

                @Override
                public Collection<Material> setValue(Collection<Material> value) {
                    return value;
                }
            };
        } catch (IllegalArgumentException ignored){
            return null;
        }
    }

    @Override
    public void onEntityInteract(PlayerInteractEntityEvent event) {

    }

    @Override
    public void onAtEntityInteract(PlayerInteractAtEntityEvent event) {

    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        Block b = event.getClickedBlock();
        if (b == null) return;
        if (event.getPlayer().isSneaking()) return;
        Profile p = ProfileManager.getManager().getProfile(event.getPlayer(), "LANDSCAPING");
        if (p == null) return;
        if (!(p instanceof LandscapingProfile)) return;
        Collection<String> unlockedConversions = ((LandscapingProfile) p).getUnlockedConversions();
        if (!CooldownManager.getInstance().isCooldownPassed(event.getPlayer().getUniqueId(), "delay_block_interact_conversion")) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
            if (BlockConversionManager.getInstance().triggerInteractConversion(event.getPlayer(), b, unlockedConversions)){
                event.setCancelled(true);
            }
        } else if (event.getAction() == Action.LEFT_CLICK_BLOCK){
            if (BlockConversionManager.getInstance().triggerDamageConversion(event.getPlayer(), b)){
                event.setCancelled(true);
            }
        }
        CooldownManager.getInstance().setCooldown(event.getPlayer().getUniqueId(), 200, "delay_block_interact_conversion");
    }

    @Override
    public void onBlockPlaced(BlockPlaceEvent event) {
        Block b = event.getBlock();
        if (strippedLogs.contains(b.getType())) {
            if (!(woodcuttingStripEXPReward.containsKey(b.getType()))) return;
            double amount = woodcuttingStripEXPReward.get(b.getType());
            if (amount > 0){
                addEXP(event.getPlayer(), amount * ((AccumulativeStatManager.getInstance().getStats("LANDSCAPING_EXP_GAIN_WOODSTRIPPING", event.getPlayer(), true) / 100D)), false, PlayerSkillExperienceGainEvent.ExperienceGainReason.SKILL_ACTION);
            }
            woodstrippingLootTable.onLogStripped(event);
        } else if (saplings.containsKey(b.getType())){
            if (b.getBlockData() instanceof Sapling){
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        double growthRate = AccumulativeStatManager.getInstance().getStats("LANDSCAPING_INSTANT_GROWTH_RATE", event.getPlayer(), true);
                        int boneMeals = Math.max(0, Utils.excessChance(growthRate));
                        for (int i = 0; i < boneMeals; i++){
                            b.applyBoneMeal(BlockFace.UP);
                        }
                    }
                }.runTaskLater(ValhallaMMO.getPlugin(), 1L);
            }
        }
    }

    @Override
    public void onBlockHarvest(PlayerHarvestBlockEvent event) {

    }

    @Override
    public void onItemsDropped(BlockDropItemEvent event) {
        if (!BlockStore.isPlaced(event.getBlock())) {
            if (woodcuttingDropEXPReward.containsKey(event.getBlockState().getType()) || diggingDropEXPReward.containsKey(event.getBlockState().getType())) {
                List<Item> newItems = new ArrayList<>();
                double rareDropMultiplier;
                double dropMultiplier;
                ChancedBlockLootTable table;
//                if (woodcuttingBreakEXPReward.containsKey(event.getBlockState().getType())) {
//                    dropMultiplier = AccumulativeStatManager.getInstance().getStats("LANDSCAPING_WOODCUTTING_DROP_MULTIPLIER", event.getPlayer(), true);
//                } else if (diggingBreakEXPReward.containsKey(event.getBlockState().getType())) {
//                    dropMultiplier = AccumulativeStatManager.getInstance().getStats("LANDSCAPING_DIGGING_DROP_MULTIPLIER", event.getPlayer(), true);
//                } else {
//                    return;
//                }

                //ItemUtils.multiplyItems(event.getItems(), newItems, dropMultiplier, forgivingMultipliers);

                if (woodcuttingDropEXPReward.containsKey(event.getBlockState().getType())) {
                    dropMultiplier = AccumulativeStatManager.getInstance().getStats("LANDSCAPING_WOODCUTTING_DROP_MULTIPLIER", event.getPlayer(), true);
                    rareDropMultiplier = AccumulativeStatManager.getInstance().getStats("LANDSCAPING_WOODCUTTING_RARE_DROP_MULTIPLIER", event.getPlayer(), true);
                    table = woodcuttingLootTable;
                } else if (diggingDropEXPReward.containsKey(event.getBlockState().getType())) {
                    dropMultiplier = AccumulativeStatManager.getInstance().getStats("LANDSCAPING_DIGGING_DROP_MULTIPLIER", event.getPlayer(), true);
                    rareDropMultiplier = AccumulativeStatManager.getInstance().getStats("LANDSCAPING_DIGGING_RARE_DROP_MULTIPLIER", event.getPlayer(), true);
                    table = diggingLootTable;
                } else {
                    return;
                }

                Set<Material> filter = new HashSet<>(woodcuttingDropEXPReward.keySet());
                filter.addAll(diggingDropEXPReward.keySet());
                ItemUtils.multiplyItems(event.getItems(), newItems, dropMultiplier, forgivingMultipliers, filter);
                if (!event.getItems().isEmpty()){
                    table.onItemDrop(event.getBlockState(), newItems, event.getPlayer(), rareDropMultiplier);
                }
//                if (!event.getItems().isEmpty()){
//                    rareDropMultiplier = AccumulativeStatManager.getInstance().getStats("LANDSCAPING_WOODCUTTING_RARE_DROP_MULTIPLIER", event.getPlayer(), true);
//                    woodcuttingLootTable.onItemDrop(event.getBlockState(), newItems, event.getPlayer(), rareDropMultiplier);
//                    // if the event's dropped items isn't empty it implies the block dropped something
//                    // (stone harvested by hand will not trigger the loot table)
//                }
                // spigot is incompatible with adding new items to drops directly, its forks aren't.
                // if on spigot items should be dropped by the plugin here, if not, items can be added.
                // drops are cleared and re-added if not on spigot, or cleared and dropped manually if on spigot
                // new drops should always at least contain the initial drops unless their amounts were edited to 0
                //
                event.getItems().clear();
                if (!handleDropsSelf){ // not spigot
                    event.getItems().addAll(newItems);
                } else {
                    for (Item i : newItems){
                        event.getBlockState().getWorld().dropItemNaturally(event.getBlock().getLocation(), i.getItemStack());
                    }
                }

                double amount = 0;
                String expMultiplierStat = "";
                if (woodcuttingDropEXPReward.containsKey(event.getBlockState().getType())){
                    for (Item i : newItems){
                        if (i == null) continue;
                        if (Utils.isItemEmptyOrNull(i.getItemStack())) continue;
                        amount += woodcuttingDropEXPReward.getOrDefault(i.getItemStack().getType(), 0D) * i.getItemStack().getAmount();
                    }
                    expMultiplierStat = "LANDSCAPING_EXP_GAIN_WOODCUTTING";
                } else if (diggingDropEXPReward.containsKey(event.getBlockState().getType())){
                    for (Item i : newItems){
                        if (i == null) continue;
                        if (Utils.isItemEmptyOrNull(i.getItemStack())) continue;
                        amount += diggingDropEXPReward.getOrDefault(i.getItemStack().getType(), 0D) * i.getItemStack().getAmount();
                    }
                    expMultiplierStat = "LANDSCAPING_EXP_GAIN_DIGGING";
                }
                if (amount > 0){
                    addEXP(event.getPlayer(), amount * ((AccumulativeStatManager.getInstance().getStats(expMultiplierStat, event.getPlayer(), true) / 100D)), false, PlayerSkillExperienceGainEvent.ExperienceGainReason.SKILL_ACTION);
                }

                BlockStore.setPlaced(event.getBlock(), false);
            }
        }
    }

    @Override
    public void onItemStacksDropped(BlockDropItemStackEvent event) {
        if (!BlockStore.isPlaced(event.getBlock())) {
            if (woodcuttingDropEXPReward.containsKey(event.getBlockState().getType()) || diggingDropEXPReward.containsKey(event.getBlockState().getType()) || woodcuttingStripEXPReward.containsKey(event.getBlockState().getType())) {
                List<ItemStack> newItems = new ArrayList<>();
                double dropMultiplier;
                double rareDropMultiplier;
                ChancedBlockLootTable table;
                if (woodcuttingDropEXPReward.containsKey(event.getBlockState().getType())) {
                    dropMultiplier = AccumulativeStatManager.getInstance().getStats("LANDSCAPING_WOODCUTTING_DROP_MULTIPLIER", event.getPlayer(), true);
                    rareDropMultiplier = AccumulativeStatManager.getInstance().getStats("LANDSCAPING_WOODCUTTING_RARE_DROP_MULTIPLIER", event.getPlayer(), true);
                    table = woodcuttingLootTable;
                } else if (diggingDropEXPReward.containsKey(event.getBlockState().getType())) {
                    dropMultiplier = AccumulativeStatManager.getInstance().getStats("LANDSCAPING_DIGGING_DROP_MULTIPLIER", event.getPlayer(), true);
                    rareDropMultiplier = AccumulativeStatManager.getInstance().getStats("LANDSCAPING_DIGGING_RARE_DROP_MULTIPLIER", event.getPlayer(), true);
                    table = diggingLootTable;
                } else {
                    return;
                }

                ItemUtils.multiplyItemStacks(event.getItems(), newItems, dropMultiplier, forgivingMultipliers);
                if (!event.getItems().isEmpty()){
                    table.onItemStackDrop(event.getBlockState(), newItems, event.getPlayer(), rareDropMultiplier);
                }

                event.getItems().clear();
                event.getItems().addAll(newItems);
                BlockStore.setPlaced(event.getBlock(), false);
            }
        }
    }
}
