package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface AdvancedDynamicItemModifier{

    /**
     * Allows edits to be made on two items given a player, who's own personal stats can be used to dynamically alter the item.
     * @param crafter the player who's stats are to be used for the item
     * @param i1 the first item, regular modifiers will be executed on this item alone
     * @param i2 the second item
     * @return a pair of the items after processing, either may be null
     */
    Pair<ItemStack, ItemStack> processItem(ItemStack i1, ItemStack i2, Player crafter, int timesExecuted);

    public class Pair<T, E>{
        private final T v1;
        private final E v2;
        public Pair(T v1, E v2){
            this.v1 = v1;
            this.v2 = v2;
        }

        public T getValue1() {
            return v1;
        }

        public E getValue2() {
            return v2;
        }
    }
}
