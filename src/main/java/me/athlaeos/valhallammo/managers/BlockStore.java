package me.athlaeos.valhallammo.managers;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

import java.util.*;

public class ExtraBlockData {
    private static final Map<Block, UUID> placedBlocks = new HashMap<>();

    private static final Map<Block, BreakReason> brokenBlocks = new HashMap<>();

    /**
     * Returns true if the block has been placed, or false if it hasn't
     * @param b the block
     * @return true if placed, false if not
     */
    public static boolean isPlaced(Block b){
        return placedBlocks.containsKey(b);
    }



    /**
     * Sets the placement status of the block. If placed is true, the block will be considered placed by a particular
     * person. If placed is false, it is no longer considered placed.
     * @param b the block to change its status of
     * @param placed the placement status
     */
    public static void setPlaced(Block b, OfflinePlayer placer, boolean placed){
        if (placed){
            placedBlocks.put(b, placer.getUniqueId());
        } else {
            placedBlocks.remove(b);
        }
    }

    /**
     * Sets the placement status of the block. If placed is true, the block will be considered placed by no particular
     * person. If placed is false, it is no longer considered placed.
     * @param b the block to change its status of
     * @param placed the placement status
     */
    public static void setPlaced(Block b, boolean placed){
        if (placed){
            placedBlocks.put(b, null);
        } else {
            placedBlocks.remove(b);
        }
    }

    /**
     * Returns the UUID of who placed the block if there is one, can return null.
     * If the block wasn't placed by anyone at all it will also return null.
     * @param b the block to check if it's been placed
     * @return the player who placed the block, will be null if block isn't placed (by player)
     */
    public static UUID getPlacedBy(Block b){
        return placedBlocks.get(b);
    }

    public static BreakReason getBreakReason(Block b){
        if (brokenBlocks.containsKey(b)){
            return brokenBlocks.get(b);
        }
        return BreakReason.OTHER;
    }

    public static void setBreakReason(Block b, BreakReason reason){
        if (reason == BreakReason.OTHER){
            brokenBlocks.remove(b);
        } else {
            brokenBlocks.put(b, reason);
        }
    }

    public static Map<Block, UUID> getPlacedBlocks() {
        return placedBlocks;
    }

    public static Map<Block, BreakReason> getBrokenBlocks() {
        return brokenBlocks;
    }

    public enum BreakReason{
        EXPLOSION,
        MINED,
        OTHER
    }
}
