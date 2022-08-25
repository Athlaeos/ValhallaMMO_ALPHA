package me.athlaeos.valhallammo.managers;

import com.jeff_media.customblockdata.CustomBlockData;
import me.athlaeos.valhallammo.ValhallaMMO;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlockStore {
    private static final NamespacedKey blockPlacedKey = new NamespacedKey(ValhallaMMO.getPlugin(), "block_placement_status");
    private static Map<Location, UUID> placedBlocks = new HashMap<>();

    private static final Map<Location, BreakReason> brokenBlocks = new HashMap<>();

    /**
     * Returns true if the block has been placed, or false if it hasn't
     * @param b the block
     * @return true if placed, false if not
     */
    public static boolean isPlaced(Block b){
        PersistentDataContainer customBlockData = new CustomBlockData(b, ValhallaMMO.getPlugin());
        if (customBlockData.has(blockPlacedKey, PersistentDataType.INTEGER)) {
            return true;
        }
        return placedBlocks.containsKey(b.getLocation());
    }



    /**
     * Sets the placement status of the block. If placed is true, the block will be considered placed by a particular
     * person. If placed is false, it is no longer considered placed.
     * Skills involving the breaking of blocks should not reward the player if the block was placed
     * @param b the block to change its status of
     * @param placed the placement status
     */
    public static void setPlaced(Block b, OfflinePlayer placer, boolean placed){
        if (placed){
            placedBlocks.put(b.getLocation(), placer.getUniqueId());
        } else {
            placedBlocks.remove(b.getLocation());
        }
    }

    /**
     * Sets the placement status of the block. If placed is true, the block will be considered placed by no particular
     * person. If placed is false, it is no longer considered placed.
     * Skills involving the breaking of blocks should not reward the player if the block was placed
     * @param b the block to change its status of
     * @param placed the placement status
     */
    public static void setPlaced(Block b, boolean placed){
        PersistentDataContainer customBlockData = new CustomBlockData(b, ValhallaMMO.getPlugin());
        if (placed){
            customBlockData.set(blockPlacedKey, PersistentDataType.INTEGER, 1);
            placedBlocks.put(b.getLocation(), null);
        } else {
            customBlockData.remove(blockPlacedKey);
            placedBlocks.remove(b.getLocation());
        }
    }

    /**
     * Returns the UUID of who placed the block if there is one, can return null.
     * If the block wasn't placed by anyone at all it will also return null.
     * @param b the block to check if it's been placed
     * @return the player who placed the block, will be null if block isn't placed (by player)
     */
    public static UUID getPlacedBy(Block b){
        return placedBlocks.get(b.getLocation());
    }

    /**
     * Returns the break reason of a block, if there is one.
     * @param b the block to check its break reason
     * @return returns NOT_BROKEN if the block is not broken, or if the reason was not specified.
     * returns EXPLOSION if the block was registered to be broken by an explosion
     * returns MINED if the block was registered to be mined by a player
     */
    public static BreakReason getBreakReason(Block b){
        if (brokenBlocks.containsKey(b.getLocation())){
            return brokenBlocks.get(b.getLocation());
        }
        return BreakReason.NOT_BROKEN;
    }

    public static void setBreakReason(Block b, BreakReason reason){
        if (reason == BreakReason.NOT_BROKEN){
            brokenBlocks.remove(b.getLocation());
        } else {
            brokenBlocks.put(b.getLocation(), reason);
        }
    }

    public static Map<Location, UUID> getPlacedBlocks() {
        return placedBlocks;
    }

    public static void setPlacedBlocks(Map<Location, UUID> blocks) {
        placedBlocks = blocks;
    }

    public static Map<Location, BreakReason> getBrokenBlocks() {
        return brokenBlocks;
    }

    public enum BreakReason{
        EXPLOSION,
        MINED,
        NOT_BROKEN
    }
}
