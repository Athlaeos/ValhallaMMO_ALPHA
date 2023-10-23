package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.crafting.CauldronCraftingListener;
import me.athlaeos.valhallammo.events.BlockDropItemStackEvent;
import me.athlaeos.valhallammo.loottables.ChancedBlockLootTable;
import me.athlaeos.valhallammo.loottables.LootManager;
import me.athlaeos.valhallammo.loottables.chance_based_block_loot.GlobalChancedBlockLootTable;
import me.athlaeos.valhallammo.managers.BlockStore;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.skills.ExplosionSkill;
import me.athlaeos.valhallammo.skills.GatheringSkill;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.utility.ItemUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BlockListener implements Listener {
    private GlobalChancedBlockLootTable lootTable = null;

    public BlockListener(){
        ChancedBlockLootTable table = LootManager.getInstance().getChancedBlockLootTables().get("global_blocks");
        if (table instanceof GlobalChancedBlockLootTable){
            lootTable = (GlobalChancedBlockLootTable) table;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockForm(BlockFormEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getBlock().getWorld().getName())) return;
        if (!e.isCancelled()){
            BlockStore.setPlaced(e.getBlock(), false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onStructureForm(StructureGrowEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getWorld().getName())) return;
        if (!e.isCancelled()){
            for (BlockState b : e.getBlocks()){
                BlockStore.setPlaced(b.getBlock(), false);
            }
        }
    }

    @EventHandler(priority =EventPriority.HIGHEST)
    public void onPistonExtend(BlockPistonExtendEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getBlock().getWorld().getName())) return;
        if (!e.isCancelled()){
            if (e.getBlocks().stream().anyMatch(CauldronCraftingListener::isCustomCauldron)) {
                e.setCancelled(true);
                return;
            }

            if (e.getBlock().getBlockData() instanceof Directional){
                Directional d = (Directional) e.getBlock().getBlockData();
                for (Block b : e.getBlocks().stream().map(b -> b.getRelative(d.getFacing())).collect(Collectors.toSet())){
                    BlockStore.setPlaced(b, true);
                }
            }
        }
    }

    @EventHandler(priority =EventPriority.HIGHEST)
    public void onPistonRetract(BlockPistonRetractEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getBlock().getWorld().getName())) return;
        if (!e.isCancelled() && e.getBlock().getBlockData() instanceof Directional){
            Directional d = (Directional) e.getBlock().getBlockData();
            for (Block b : e.getBlocks().stream().map(b -> b.getRelative(d.getFacing().getOppositeFace())).collect(Collectors.toSet())){
                BlockStore.setPlaced(b, true);
            }
        }
    }

    @EventHandler(priority =EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getBlock().getWorld().getName())) return;
        if (!e.isCancelled()){

            if (CauldronCraftingListener.isCustomCauldron(e.getBlock())){
                CauldronCraftingListener.dumpCauldronContents(e.getBlock());
            }

            for (Skill s : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (s != null){
                    if (s instanceof GatheringSkill){
                        ((GatheringSkill) s).onBlockBreak(e);
                    }
                }
            }
        }
    }

    @EventHandler(priority =EventPriority.HIGHEST)
    public void onBlockHarvest(PlayerHarvestBlockEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getHarvestedBlock().getWorld().getName())) return;
        if (!e.isCancelled()){
            for (Skill s : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (s != null){
                    if (s instanceof GatheringSkill){
                        ((GatheringSkill) s).onBlockHarvest(e);
                    }
                }
            }
        }
    }

    @EventHandler(priority =EventPriority.HIGHEST)
    public void onBlockDamage(BlockDamageEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getBlock().getWorld().getName())) return;
        if (!e.isCancelled()){
            EntityDamagedListener.getLastArmSwingReasons().put(e.getPlayer().getUniqueId(), EntityDamagedListener.ArmSwingReason.BLOCK_DAMAGE);
            for (Skill s : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (s != null){
                    if (s instanceof GatheringSkill){
                        ((GatheringSkill) s).onBlockDamage(e);
                    }
                }
            }
        }
    }

    private final Collection<Material> logs = ItemUtils.getMaterialList(Arrays.asList(
            "OAK_LOG", "SPRUCE_LOG", "JUNGLE_LOG", "BIRCH_LOG",
            "DARK_OAK_LOG", "ACACIA_LOG", "CRIMSON_STEM", "WARPED_STEM",
            "OAK_WOOD", "SPRUCE_WOOD", "JUNGLE_WOOD", "BIRCH_WOOD",
            "DARK_OAK_WOOD", "ACACIA_WOOD", "CRIMSON_HYPHAE", "WARPED_HYPHAE"
    ));

    @EventHandler(priority =EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getBlock().getWorld().getName())) return;
        if (!e.isCancelled()){
            for (Skill s : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (s != null){
                    if (s instanceof GatheringSkill){
                        ((GatheringSkill) s).onBlockPlaced(e);
                    }
                }
            }
            if (logs.contains(e.getBlockReplacedState().getType())) return;
            // stripped logs are an exception because spigot interprets stripping logs as a block place event, this
            // prevents stripping logs from being marked as "placed" blocks
            if (e.getPlayer().getGameMode() != GameMode.CREATIVE){
                BlockStore.setPlaced(e.getBlock(), true);
            }
        }
    }

    @EventHandler(priority =EventPriority.HIGHEST)
    public void onBlockDropItems(BlockDropItemEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getBlock().getWorld().getName())) return;
        if (!e.isCancelled()){
            for (Skill s : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (s != null){
                    if (s instanceof GatheringSkill){
                        ((GatheringSkill) s).onItemsDropped(e);
                    }
                }
            }
            if (lootTable != null && !BlockStore.isPlaced(e.getBlock())){
                List<Item> newItems = new ArrayList<>(e.getItems());
                lootTable.onItemDrop(e.getBlockState(), newItems, e.getPlayer(), 1);
//                if (!e.getItems().isEmpty()){
//                }
//                boolean handleDropsSelf = ValhallaMMO.isSpigot();
//                e.getItems().clear();
//                if (!handleDropsSelf){
//                    e.getItems().addAll(newItems);
//                }
//                if (!handleDropsSelf){ // not spigot
//                    e.getItems().addAll(newItems);
//                } else {
//                    for (Item i : newItems){
//                        e.getBlockState().getWorld().dropItemNaturally(e.getBlock().getLocation(), i.getItemStack());
//                    }
//                }
            }
        }
    }

    @EventHandler(priority =EventPriority.HIGHEST)
    public void onBlockDropItemStack(BlockDropItemStackEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getBlock().getWorld().getName())) return;
        if (!e.isCancelled()){
            for (Skill s : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (s != null){
                    if (s instanceof GatheringSkill){
                        ((GatheringSkill) s).onItemStacksDropped(e);
                    }
                }
            }

            Location dropLocation = e.getBlock().getLocation().add(0.5, 0.5, 0.5);
            if (dropLocation.getWorld() == null) return;
            for (ItemStack i : e.getItems()){
                dropLocation.getWorld().dropItemNaturally(dropLocation, i);
            }
        }
    }

    @EventHandler(priority =EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getEntity().getWorld().getName())) return;
        if (!e.isCancelled()){
            for (Skill s : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (s != null){
                    if (s instanceof ExplosionSkill){
                        ((ExplosionSkill) s).onEntityExplode(e);
                    }
                }
            }
        }
    }

    @EventHandler(priority =EventPriority.HIGHEST)
    public void onExplosionPrime(ExplosionPrimeEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getEntity().getWorld().getName())) return;
        if (!e.isCancelled()){
            for (Skill s : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (s != null){
                    if (s instanceof ExplosionSkill){
                        ((ExplosionSkill) s).onExplosionPrime(e);
                    }
                }
            }
        }
    }

    @EventHandler(priority =EventPriority.HIGHEST)
    public void onBlockExplode(BlockExplodeEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getBlock().getWorld().getName())) return;
        if (!e.isCancelled()){
            for (Skill s : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (s != null){
                    if (s instanceof ExplosionSkill){
                        ((ExplosionSkill) s).onBlockExplode(e);
                    }
                }
            }
        }
    }
}
