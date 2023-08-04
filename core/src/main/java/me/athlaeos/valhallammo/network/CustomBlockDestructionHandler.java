package me.athlaeos.valhallammo.network;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.DigInfo;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CustomBlockDestructionHandler implements IPacketHandler {
    public boolean readBefore(Player player, Object packet) {
        if(player.getGameMode() == GameMode.CREATIVE) return true;
        DigInfo info = ValhallaMMO.getNMS().packetInfoAdapter(player, packet);
        if (info == null) return true;

        runBlockBreak(info);
        return true;
    }

    public void readAfter(Player player, Object packet) {
//        if(player.getGameMode() == GameMode.CREATIVE) return;
//        DigInfo info = ValhallaMMO.getNMS().packetInfoAdapter(player, packet);
//        if (info == null) return;
//
//        Breaker.get().getBreakingSystem().conclude(info);
    }

    private final Map<Block, DigInfo> digInfoMap = new HashMap<>();

    public Map<Block, DigInfo> getDigInfoMap() {
        return digInfoMap;
    }

    public void runBlockBreak(DigInfo blockBreakData) {
        digInfoMap.put(blockBreakData.getBlock(), blockBreakData);

        if (blockBreakData.getPlayer().getGameMode() == GameMode.CREATIVE || blockBreakData.getHardness() == 0) {
            ValhallaMMO.getNMS().breakBlock(blockBreakData.getPlayer(), blockBreakData.getBlock());
        } else if (blockBreakData.getHardness() > 0) {
            final Collection<Player> nearbyPlayers = getNearbyPlayers(blockBreakData.getBlock().getLocation(), 20);
            ItemStack tool = blockBreakData.getMinedWith();

            float toolStrength = ValhallaMMO.getNMS().toolPower(tool, blockBreakData.getBlock());
            float timer = (blockBreakData.getHardness() / (toolStrength * 6)) * 20;
            int crackAmount = 10;

            if (toolStrength > 1) {
                // Correct tool
                if (tool.getItemMeta() != null) {
                    // Enchantment buff
                    if (tool.getItemMeta().hasEnchant(Enchantment.DIG_SPEED)) {
                        crackAmount -= tool.getItemMeta()
                                .getEnchantLevel(Enchantment.DIG_SPEED);
                    }

                    // Haste buff
                    if (blockBreakData.getPlayer().hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
                        PotionEffect potionEffect = blockBreakData.getPlayer()
                                .getPotionEffect(PotionEffectType.FAST_DIGGING);

                        if (potionEffect != null) {
                            crackAmount -= potionEffect.getAmplifier();
                        }
                    }
                }
            } else {
                // Wrong tool debuff
                timer = timer * (blockBreakData.getHardness() * 2);
            }

            // Mining fatigue debuff
            if (blockBreakData.getPlayer().hasPotionEffect(PotionEffectType.SLOW_DIGGING)) {
                PotionEffect potionEffect = blockBreakData.getPlayer().getPotionEffect(PotionEffectType.SLOW_DIGGING);

                if (potionEffect != null) {
                    timer *= potionEffect.getAmplifier() * 15;
                }
            }

            // Swimming debuff
            if (blockBreakData.getPlayer().getLocation().add(0, 1, 0).getBlock().getType() == Material.WATER) {
                timer *= 5;
            }

            // Vehicle debuff
            if (blockBreakData.getPlayer().getVehicle() != null) {
                timer *= 5;
            }

            double miningSpeedBonus = AccumulativeStatManager.getInstance().getCachedStat(blockBreakData.getPlayer(), "MINING_GENERAL_SPEED_BONUS");
            // block and tool-specific bonusses
            timer /= (1 + miningSpeedBonus);

            int finalCrackAmount = crackAmount;

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (blockBreakData.getCrackAmount() < finalCrackAmount) {
                        if ((System.currentTimeMillis() - blockBreakData.getLastMineTime()) < 230) {
                            // Continue because player is mining
                            for (Player nearbyPlayer : nearbyPlayers) {
                                blockCrackAnimation(blockBreakData, nearbyPlayer);
                            }

                            blockBreakData.addCrack();
                        } else {
                            // Cancel because player stopped mining
                            for (Player nearbyPlayer : nearbyPlayers) {
                                blockCrackAnimation(blockBreakData, nearbyPlayer, -1);
                            }

                            digInfoMap.remove(blockBreakData.getBlock());

                            // Cancel runnable
                            cancel();
                        }
                    } else {
                        // Break block mine finished
                        for (Player nearbyPlayer : nearbyPlayers) {
                            blockCrackAnimation(blockBreakData, nearbyPlayer, -1);
                        }

                        ValhallaMMO.getNMS().blockBreakAnimation(blockBreakData.getPlayer(), blockBreakData.getBlock(), blockBreakData.getAnimationId(), blockBreakData.getCrackAmount());

                        // Break block on the main thread
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                ValhallaMMO.getNMS().breakBlock(blockBreakData.getPlayer(), blockBreakData.getBlock());
                                digInfoMap.remove(blockBreakData.getBlock());
                            }
                        }.runTask(ValhallaMMO.getPlugin());

                        // Cancel runnable
                        cancel();
                    }
                }
            }.runTaskTimerAsynchronously(ValhallaMMO.getPlugin(), 0L, (long) timer);
        }
    }


    public void blockCrackAnimation(DigInfo blockBreakData, Player player) {
        blockCrackAnimation(blockBreakData, player, blockBreakData.getCrackAmount());
    }

    public void blockCrackAnimation(DigInfo blockBreakData, Player player, int stage) {
        ValhallaMMO.getNMS().blockBreakAnimation(player, blockBreakData.getBlock(), blockBreakData.getAnimationId(), stage);
    }


    public Collection<Player> getNearbyPlayers(Location location, int range) {
        final Collection<Player> nearbyPlayers = new HashSet<>();

        if (location.getWorld() != null) {
            for (Entity entity : location.getWorld().getNearbyEntities(location, range, range, range)) {
                if (entity instanceof Player) {
                    nearbyPlayers.add((Player) entity);
                }
            }
        }

        return nearbyPlayers;
    }

}
