package me.athlaeos.valhallammo.skills.mining;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.Offset;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.events.BlockDropItemStackEvent;
import me.athlaeos.valhallammo.events.PlayerSkillExperienceGainEvent;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.loottables.ChancedBlockLootTable;
import me.athlaeos.valhallammo.loottables.LootManager;
import me.athlaeos.valhallammo.loottables.chance_based_block_loot.ChancedBlastMiningLootTable;
import me.athlaeos.valhallammo.loottables.chance_based_block_loot.ChancedMiningLootTable;
import me.athlaeos.valhallammo.managers.*;
import me.athlaeos.valhallammo.skills.*;
import me.athlaeos.valhallammo.utility.EntityUtils;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.ShapeUtils;
import me.athlaeos.valhallammo.utility.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.util.List;
import java.util.*;

public class MiningSkill extends Skill implements GatheringSkill, ExplosionSkill, OffensiveSkill, InteractSkill {
    private final Map<UUID, PlayerModeData> quickModeData;

    private final Map<Material, Double> blockDropEXPReward;
    private final Map<Material, Integer> quickModeBlockWorth;

    private ChancedMiningLootTable miningLootTable = null;
    private ChancedBlastMiningLootTable blastMiningLootTable = null;

    private final String quickmode_toggle_on;
    private final String quickmode_toggle_off;

    private final double expMultiplierMine;
    private final double expMultiplierBlast;

    private final boolean vein_mining_instant;
    private final int veinMineLimit;
    private final boolean veinMineInstantPickup;

    private final boolean forgivingMultipliers;
    private final boolean remove_tnt_chaining;

    private final boolean handleDropsSelf = ValhallaMMO.isSpigot();
    private final boolean cosmetic_outline;
    private final String outline_color;

    public Map<Material, Double> getBlockDropEXPReward() {
        return blockDropEXPReward;
    }

    public MiningSkill(String type) {
        super(type);
        skillTreeMenuOrderPriority = 4;
        ChancedBlockLootTable miningLootTable = LootManager.getInstance().getChancedBlockLootTables().get("mining_mining");
        if (miningLootTable != null){
            if (miningLootTable instanceof ChancedMiningLootTable){
                this.miningLootTable = (ChancedMiningLootTable) miningLootTable;
            }
        }
        ChancedBlockLootTable blastMiningLootTable = LootManager.getInstance().getChancedBlockLootTables().get("mining_blast");
        if (blastMiningLootTable != null){
            if (blastMiningLootTable instanceof ChancedBlastMiningLootTable){
                this.blastMiningLootTable = (ChancedBlastMiningLootTable) blastMiningLootTable;
            }
        }

        this.quickModeData = new HashMap<>();
        this.blockDropEXPReward = new HashMap<>();
        this.quickModeBlockWorth = new HashMap<>();
        YamlConfiguration miningConfig = ConfigManager.getInstance().getConfig("skill_mining.yml").get();
        YamlConfiguration progressionConfig = ConfigManager.getInstance().getConfig("progression_mining.yml").get();

        this.loadCommonConfig(miningConfig, progressionConfig);

        expMultiplierMine = progressionConfig.getDouble("experience.exp_multiplier_mine");
        expMultiplierBlast = progressionConfig.getDouble("experience.exp_multiplier_blast");
        veinMineLimit = miningConfig.getInt("break_limit_vein_mining");
        veinMineInstantPickup = miningConfig.getBoolean("instant_pickup_vein_mining");
        forgivingMultipliers = miningConfig.getBoolean("forgiving_multipliers");
        remove_tnt_chaining = miningConfig.getBoolean("remove_tnt_chaining");
        quickmode_toggle_on = miningConfig.getString("quickmode_toggle_on");
        quickmode_toggle_off = miningConfig.getString("quickmode_toggle_off");
        cosmetic_outline = miningConfig.getBoolean("cosmetic_outline");
        outline_color = miningConfig.getString("outline_color");
        vein_mining_instant = miningConfig.getBoolean("vein_mining_instant");

        ConfigurationSection blockBreakSection = progressionConfig.getConfigurationSection("experience.mining_break");
        if (blockBreakSection != null){
            for (String key : blockBreakSection.getKeys(false)){
                try {
                    Material block = Material.valueOf(key);
                    double reward = progressionConfig.getDouble("experience.mining_break." + key);
                    blockDropEXPReward.put(block, reward);
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("invalid block type given:" + key + " for the block break rewards in progression_mining.yml, no reward set for this type until corrected.");
                }
            }
        }

        ConfigurationSection quickBlockValueSection = miningConfig.getConfigurationSection("quickmode_block_values");
        if (quickBlockValueSection != null){
            for (String key : quickBlockValueSection.getKeys(false)){
                try {
                    Material block = Material.valueOf(key);
                    if (!block.isBlock()) throw new IllegalArgumentException();
                    int value = miningConfig.getInt("quickmode_block_values." + key);
                    quickModeBlockWorth.put(block, value);
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("invalid block type given:" + key + " for the block interact rewards in skill_mining.yml, no reward set for this type until corrected.");
                }
            }
        }
    }

    @Override
    public NamespacedKey getKey() {
        return new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_profile_mining");
    }

    @Override
    public Profile getCleanProfile() {
        return new MiningProfile(null);
    }

    @Override
    public void addEXP(Player p, double amount, boolean silent, PlayerSkillExperienceGainEvent.ExperienceGainReason reason) {
        double multiplier = ((AccumulativeStatManager.getInstance().getStats("MINING_EXP_GAIN_GENERAL", p, true) / 100D));
        double finalAmount = amount * multiplier;
        super.addEXP(p, finalAmount, silent, reason);
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        Block b = event.getBlock();
        Profile p = ProfileManager.getManager().getProfile(event.getPlayer(), "MINING");
        if (p != null){
            if (p instanceof MiningProfile){
                // If block is unbreakable, event gets cancelled and nothing else happens.
                if (((MiningProfile) p).getUnbreakableBlocks().contains(b.getType().toString())){
                    event.setCancelled(true);
                    return;
                }
            }
        }
        if (!blockDropEXPReward.containsKey(b.getType())) return;
        // Nothing is going to happen if the player isn't using a pickaxe
        ItemStack pickaxe = EntityUtils.getHoldingItem(event.getPlayer(), EquipmentClass.PICKAXE);
        if (pickaxe == null) {
            return;
        }
        BlockStore.setBreakReason(b, BlockStore.BreakReason.MINED);

        // punish player based on the blocks they mined with quickmine enabled
        PlayerModeData data = getData(event.getPlayer());
        if (data.isEnabled()){
            double damageMultiplier = AccumulativeStatManager.getInstance().getStats("MINING_QUICK_MINE_DURABILITY_MULTIPLIER", event.getPlayer(), true);
            int damage = Math.max(0, Utils.excessChance(damageMultiplier) - 1);
            if (damage > 0){
                if (ItemUtils.damageItem(event.getPlayer(), pickaxe, damage, EntityEffect.BREAK_EQUIPMENT_MAIN_HAND)){
                    event.getPlayer().getInventory().setItemInMainHand(null);
                }
            }
            int quickMineRate = (int) Utils.round(AccumulativeStatManager.getInstance().getStats("MINING_QUICK_MINE_DRAIN_RATE", event.getPlayer(), true), 3);
            if (quickMineRate > 0) {
                Material block = event.getBlock().getType();
                int blockValue = quickModeBlockWorth.getOrDefault(block, 1);
                if (blockValue / quickMineRate > maxPunishments(event.getPlayer())) {
                    event.setCancelled(true);
                    data.toggle(event.getPlayer());
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                            Utils.chat((data.isEnabled()) ? quickmode_toggle_on : quickmode_toggle_off)
                    ));
                    return;
                }
                data.increment(blockValue);
                int i = data.getScore();
                for (;i >= quickMineRate; i -= quickMineRate){
                    if (!punish(event.getPlayer())){
                        event.setCancelled(true);
                        data.toggle(event.getPlayer());
                        return;
                    }
                }
                data.setScore(i);
            }
        }

        if (!BlockStore.isPlaced(b)){
            // multiply/add experience for the block broken
            double expMultiplier = AccumulativeStatManager.getInstance().getStats("MINING_ORE_EXPERIENCE_MULTIPLIER", event.getPlayer(), true);
            if (event.getExpToDrop() > 0){
                event.setExpToDrop(Utils.excessChance(event.getExpToDrop() * expMultiplier));
            }
            event.setExpToDrop(event.getExpToDrop() + Utils.excessChance(AccumulativeStatManager.getInstance().getStats("MINING_VANILLA_EXP_REWARD", event.getPlayer(), true)));
        }

        if (!Utils.getBlockAlteringPlayers().getOrDefault("valhalla_vein_miner", new HashSet<>()).contains(event.getPlayer().getUniqueId())){
            boolean unlockedVeinMining = false;
            int veinMiningCooldown = 0;
            Collection<Material> allowedVeinMineBlocks;
            if (p != null){
                if (p instanceof MiningProfile){
                    veinMiningCooldown = ((MiningProfile) p).getVeinMiningCooldown();
                    allowedVeinMineBlocks = ItemUtils.getMaterialList(((MiningProfile) p).getValidVeinMinerBlocks());
                    if (!allowedVeinMineBlocks.contains(event.getBlock().getType())) {
                        return;
                    }
                    unlockedVeinMining = veinMiningCooldown > 0;
                }
            }

            if (unlockedVeinMining){
                if (event.getPlayer().isSneaking()){
                    boolean dataWasEnabled = false;
                    if (data.isEnabled()) {
                        dataWasEnabled = true;
                        data.toggle();
                    }
                    if (CooldownManager.getInstance().isCooldownPassed(event.getPlayer().getUniqueId(), "cooldown_vein_mining")){
                        // trigger ultra harvest special ability
                        List<Block> affectedBlocks = Utils.getBlockVein(
                                b.getLocation(),
                                new HashSet<>(Collections.singletonList(event.getBlock().getType())),
                                veinMineLimit,
                                block -> blockDropEXPReward.containsKey(block.getType()),
                                new Offset(-1, 1, -1), new Offset(-1, 1, 0), new Offset(-1, 1, 1),
                                new Offset(0, 1, -1), new Offset(0, 1, 0), new Offset(0, 1, 1),
                                new Offset(1, 1, -1), new Offset(1, 1, 0), new Offset(1, 1, 1),
                                new Offset(-1, 0, -1), new Offset(-1, 0, 0), new Offset(-1, 0, 1),
                                new Offset(0, 0, -1),                                       new Offset(0, 0, 1),
                                new Offset(1, 0, -1), new Offset(1, 0, 0), new Offset(1, 0, 1),
                                new Offset(-1, -1, -1), new Offset(-1, -1, 0), new Offset(-1, -1, 1),
                                new Offset(0, -1, -1), new Offset(0, -1, 0), new Offset(0, -1, 1),
                                new Offset(1, -1, -1), new Offset(1, -1, 0), new Offset(1, -1, 1));

                        final Material blockBrokenType = event.getBlock().getType();
                        final boolean dataWasEnabled1 = dataWasEnabled;

                        if (vein_mining_instant){
                            Utils.alterBlocksInstant(
                                    "valhalla_vein_miner",
                                    event.getPlayer(),
                                    affectedBlocks,
                                    block -> blockBrokenType == block.getType() && blockDropEXPReward.containsKey(block.getType()),
                                    EquipmentClass.PICKAXE,
                                    block -> {
                                        Utils.breakBlock(event.getPlayer(), block, veinMineInstantPickup);
                                        if (cosmetic_outline) {
                                            Color color = Utils.hexToRgb(outline_color);
                                            ShapeUtils.outlineBlock(block, 4, 0.5f, color.getRed(), color.getGreen(), color.getBlue());
                                        }
                                    },
                                    block -> {
                                        if (dataWasEnabled1){
                                            if (data.isEnabled()){
                                                // to make sure quickmode is disabled while the vein mine is happening, but re-enabled after
                                                data.toggle();
                                            }
                                        }
                                    });
                        } else {
                            Utils.alterBlocks(
                                    "valhalla_vein_miner",
                                    event.getPlayer(),
                                    affectedBlocks,
                                    block -> blockBrokenType == block.getType() && blockDropEXPReward.containsKey(block.getType()),
                                    EquipmentClass.PICKAXE,
                                    block -> {
                                        Utils.breakBlock(event.getPlayer(), block, veinMineInstantPickup);
                                        if (cosmetic_outline) {
                                            Color color = Utils.hexToRgb(outline_color);
                                            ShapeUtils.outlineBlock(block, 4, 0.5f, color.getRed(), color.getGreen(), color.getBlue());
                                        }
                                    },
                                    block -> {
                                        if (dataWasEnabled1){
                                            if (data.isEnabled()){
                                                // to make sure quickmode is disabled while the vein mine is happening, but re-enabled after
                                                data.toggle();
                                            }
                                        }
                                    });
                        }
                        CooldownManager.getInstance().setCooldownIgnoreIfPermission(event.getPlayer(), veinMiningCooldown, "cooldown_vein_miner");
                    } else {
                        int cooldown = (int) CooldownManager.getInstance().getCooldown(event.getPlayer().getUniqueId(), "cooldown_vein_miner");
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

    private int maxPunishments(Player p){
        return Math.max(0, (int) Math.floor(p.getSaturation() + p.getFoodLevel() + p.getHealth()) - 1);
    }

    private boolean punish(Player p){
        if (p.getSaturation() > 0){
            p.setSaturation(Math.max(0, p.getSaturation() - 1));
            return true;
        } else if (p.getFoodLevel() > 0){
            p.setFoodLevel(p.getFoodLevel() - 1);
            return true;
        } else if (p.getHealth() > 1) {
            p.setHealth(p.getHealth() - 1);
            p.playEffect(EntityEffect.HURT);
            return true;
        }
        return false;
    }

    @Override
    public void onBlockDamage(BlockDamageEvent event) {
        if (!blockDropEXPReward.containsKey(event.getBlock().getType())) return;
        if (!event.isCancelled()){
            if (event.getBlock().getType().getHardness() > 100000 || event.getBlock().getType().getHardness() < 0) return;
            ItemStack pickaxe = EntityUtils.getHoldingItem(event.getPlayer(), EquipmentClass.PICKAXE);
            boolean canBreakBlock = pickaxe == null ? !event.getBlock().getDrops().isEmpty() : !event.getBlock().getDrops(pickaxe, event.getPlayer()).isEmpty();
            if (!canBreakBlock) return;
            PlayerModeData data = getData(event.getPlayer());
            if (data.isEnabled()){
                if (pickaxe == null) {
                    data.toggle(event.getPlayer());
                    return;
                }
                event.setInstaBreak(true);
                // possible ghost block fix
//                ValhallaMMO.getPlugin().getServer().getScheduler().runTaskLater(ValhallaMMO.getPlugin(), () -> {
//                    event.getPlayer().sendBlockChange(event.getBlock().getLocation(), event.getBlock().getBlockData());
//                }, 1L);
            }
        }
    }

    @Override
    public void onBlockPlaced(BlockPlaceEvent event) {

    }

    @Override
    public void onBlockHarvest(PlayerHarvestBlockEvent event) {

    }

    @Override
    public void onItemsDropped(BlockDropItemEvent event) {
        if (!BlockStore.isPlaced(event.getBlock())) {
            if (blockDropEXPReward.containsKey(event.getBlockState().getType())) {
                List<Item> newItems = new ArrayList<>();
                double dropMultiplier = AccumulativeStatManager.getInstance().getStats("MINING_MINING_DROP_MULTIPLIER", event.getPlayer(), true);

                ItemUtils.multiplyItems(event.getItems(), newItems, dropMultiplier, forgivingMultipliers, blockDropEXPReward.keySet());

                if (!event.getItems().isEmpty()){
                    double rareDropMultiplier = AccumulativeStatManager.getInstance().getStats("MINING_MINING_RARE_DROP_CHANCE_MULTIPLIER", event.getPlayer(), true);
                    miningLootTable.onItemDrop(event.getBlockState(), newItems, event.getPlayer(), rareDropMultiplier);
                }
                event.getItems().clear();
                if (!handleDropsSelf){
                    event.getItems().addAll(newItems);
                }
                if (!handleDropsSelf){ // not spigot
                    event.getItems().addAll(newItems);
                } else {
                    for (Item i : newItems){
                        event.getBlockState().getWorld().dropItemNaturally(event.getBlock().getLocation(), i.getItemStack());
                    }
                }

                // reward player experience for mining a block
                double amount = 0;
                for (Item i : newItems){
                    if (i == null) continue;
                    if (Utils.isItemEmptyOrNull(i.getItemStack())) continue;
                    amount += blockDropEXPReward.getOrDefault(i.getItemStack().getType(), 0D) * i.getItemStack().getAmount();
                }
                double multiplier = ((AccumulativeStatManager.getInstance().getStats("MINING_EXP_GAIN_MINING", event.getPlayer(), true) / 100D));
                addEXP(event.getPlayer(), expMultiplierMine * amount * multiplier, false, PlayerSkillExperienceGainEvent.ExperienceGainReason.SKILL_ACTION);

                BlockStore.setPlaced(event.getBlock(), false);
            }
        }
    }

    @Override
    public void onItemStacksDropped(BlockDropItemStackEvent event) {
        // this method is exclusively triggered by exploded blocks dropping their items, so
        // if a block is broken because of an explosion it should multiply the rewards, but otherwise not because
        // this has been done in the onItemDropped() method
        if (!BlockStore.isPlaced(event.getBlock())) {
            if (blockDropEXPReward.containsKey(event.getBlockState().getType())) {
                List<ItemStack> newItems = new ArrayList<>();
                BlockStore.BreakReason reason = BlockStore.getBreakReason(event.getBlock());
                if (reason == BlockStore.BreakReason.EXPLOSION){
                    double dropMultiplier = AccumulativeStatManager.getInstance().getStats("MINING_BLAST_DROP_MULTIPLIER", event.getPlayer(), true);

                    ItemUtils.multiplyItemStacks(event.getItems(), newItems, dropMultiplier, forgivingMultipliers, blockDropEXPReward.keySet());

                    event.getItems().clear();
                    event.getItems().addAll(newItems);
                }

                // reward player experience for mining a block
                double amount = 0;
                for (ItemStack i : newItems){
                    if (Utils.isItemEmptyOrNull(i)) continue;
                    amount += blockDropEXPReward.getOrDefault(i.getType(), 0D) * i.getAmount();
                }
                double multiplier = ((AccumulativeStatManager.getInstance().getStats("MINING_EXP_GAIN_MINING", event.getPlayer(), true) / 100D));
                addEXP(event.getPlayer(), expMultiplierMine * amount * multiplier, false, PlayerSkillExperienceGainEvent.ExperienceGainReason.SKILL_ACTION);

                BlockStore.setPlaced(event.getBlock(), false);
            }
        }
    }

    private PlayerModeData getData(Player p){
        if (!quickModeData.containsKey(p.getUniqueId())){
            quickModeData.put(p.getUniqueId(), new PlayerModeData());
        }
        return quickModeData.get(p.getUniqueId());
    }

    @Override
    public void onBlockExplode(BlockExplodeEvent event){
        // do nothing
    }

    @Override
    public void onEntityExplode(EntityExplodeEvent event) {
        // increase blast radius of tnt
        if (event.getEntity() instanceof TNTPrimed){
            TNTPrimed tnt = (TNTPrimed) event.getEntity();

            if (tnt.getSource() == null) return;

            Player player = null;
            if (tnt.getSource() instanceof Player){
                player = (Player) tnt.getSource();
            } else if (tnt.getSource() instanceof AbstractArrow){
                AbstractArrow arrow = (AbstractArrow) tnt.getSource();
                if (arrow.getShooter() instanceof Player){
                    player = (Player) arrow.getShooter();
                }
            }

            if (player != null){
                double exp = 0;
                double multiplier = ((AccumulativeStatManager.getInstance().getStats("MINING_EXP_GAIN_BLAST", player, true) / 100D));
                for (Block b : event.blockList()){
                    if (blockDropEXPReward.containsKey(b.getType())){
                        exp += expMultiplierBlast * blockDropEXPReward.get(b.getType()) * multiplier;
                    }
                }
                addEXP(player, exp, false, PlayerSkillExperienceGainEvent.ExperienceGainReason.SKILL_ACTION);
                int fortuneLevel = 0;
                Profile p = ProfileManager.getManager().getProfile(player, "MINING");
                if (p != null){
                    if (p instanceof MiningProfile){
                        fortuneLevel = ((MiningProfile) p).getExplosionFortuneLevel();
                    }
                }

                for (Block b : new HashSet<>(event.blockList())){
                    if (b.getType().isAir()) continue;
                    if (!remove_tnt_chaining){
                        if (b.getType() == Material.TNT) continue;
                    }
                    BlockStore.setBreakReason(b, BlockStore.BreakReason.EXPLOSION);
                    // if the block was placed, it is treated as a regular explosion
                    // if not, it's custom exploded
                    if (!BlockStore.isPlaced(b)){
                        event.blockList().remove(b);

                        // if the custom loot table does not end up dropping anything custom, explode the block anyway
                        if (!blastMiningLootTable.onBlockExplode(b, player, fortuneLevel)){
                            Utils.explodeBlock(player, b, false, fortuneLevel);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        if (event.getEntity() instanceof TNTPrimed) {
            TNTPrimed tnt = (TNTPrimed) event.getEntity();
            if (tnt.getSource() == null) return;
            Player player = null;
            if (tnt.getSource() instanceof Player) {
                player = (Player) tnt.getSource();
            }

            if (player != null){
                double tntStrength = AccumulativeStatManager.getInstance().getStats("MINING_BLAST_RADIUS_MULTIPLIER", player, true);
                event.setRadius(event.getRadius() * (float) tntStrength);
            }
        }
    }

    @Override
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (event.getDamager() instanceof TNTPrimed){
            double multiplier = AccumulativeStatManager.getInstance().getStats("MINING_BLAST_EXPLOSION_DAMAGE_MULTIPLIER", event.getEntity(), true);
            if (multiplier <= 0) {
                event.setCancelled(true);
            } else {
                event.setDamage(event.getDamage() * multiplier);
            }
        }
    }

    @Override
    public void onEntityKilled(EntityDeathEvent event) {
        // do nothing
    }

    @Override
    public void onEntityInteract(PlayerInteractEntityEvent event) {

    }

    @Override
    public void onAtEntityInteract(PlayerInteractAtEntityEvent event) {

    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        if (event.useItemInHand() != Event.Result.ALLOW){
            if (event.getPlayer().getHealth() <= 1.0) return;
            if ((int) Utils.round(AccumulativeStatManager.getInstance().getStats("MINING_QUICK_MINE_DRAIN_RATE", event.getPlayer(), true), 3) <= 0) return;
            if (EntityUtils.getHoldingItem(event.getPlayer(), EquipmentClass.PICKAXE) != null && event.getPlayer().isSneaking()){
                PlayerModeData data = getData(event.getPlayer());
                if (!data.isEnabled()) {
                    if (!CooldownManager.getInstance().isCooldownPassed(event.getPlayer().getUniqueId(), "cooldown_mining_quickmine")) {
                        int cooldown = (int) CooldownManager.getInstance().getCooldown(event.getPlayer().getUniqueId(), "cooldown_mining_quickmine");
                        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                new TextComponent(
                                        Utils.chat(TranslationManager.getInstance().getTranslation("status_cooldown"))
                                                .replace("%timestamp%", Utils.toTimeStamp(cooldown, 1000))
                                                .replace("%time_seconds%", String.format("%d", (int) Math.ceil(cooldown / 1000D)))
                                                .replace("%time_minutes%", String.format("%.1f", cooldown / 60000D))
                                ));
                        return;
                    }
                }
                data.toggle(event.getPlayer());
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                        Utils.chat((data.isEnabled()) ? quickmode_toggle_on : quickmode_toggle_off)
                ));
            }
        }
    }

    private static class PlayerModeData {
        private boolean enabled = false;
        private int score = 0;

        public void toggle(Player player){
            enabled = !enabled;
            if (!enabled){
                if (!CooldownManager.getInstance().isCooldownPassed(player.getUniqueId(), "cooldown_mining_quickmine")) return;
                Profile p = ProfileManager.getManager().getProfile(player, "MINING");
                if (p != null){
                    if (!(p instanceof MiningProfile)) return;
                    MiningProfile profile = (MiningProfile) p;
                    if (profile.getQuickMineCooldown() > 0){
                        CooldownManager.getInstance().setCooldownIgnoreIfPermission(player, profile.getQuickMineCooldown(), "cooldown_mining_quickmine");
                    }
                }
            }
        }

        public void toggle(){
            this.enabled = !this.enabled;
        }

        public void increment(int score){
            this.score += score;
            if (this.score < 0) this.score = 0;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public int getScore() {
            return score;
        }
    }
}

