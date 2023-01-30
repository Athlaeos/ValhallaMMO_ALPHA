package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.TripleArgDynamicItemModifier;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class CustomModelDataAddModifier extends TripleArgDynamicItemModifier {
    public CustomModelDataAddModifier(String name) {
        super(name, 0D, 0D, 0D, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_MISC;

        this.bigStepDecrease = 25D; // first 3 digits
        this.bigStepIncrease = 25D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 100;
        this.minStrength = 0;
        this.maxStrength = 9999999D;

        this.bigStepDecrease2 = 10D; // digit 4-5
        this.bigStepIncrease2 = 10D;
        this.smallStepDecrease2 = 1D;
        this.smallStepIncrease2 = 1D;
        this.defaultStrength2 = 0;
        this.minStrength2 = 0;
        this.maxStrength2 = 99D;

        this.bigStepDecrease3 = 10D; // digit 6-7
        this.bigStepIncrease3 = 10D;
        this.smallStepDecrease3 = 1D;
        this.smallStepIncrease3 = 1D;
        this.defaultStrength3 = 0;
        this.minStrength3 = 0;
        this.maxStrength3 = 99D;

        this.description = Utils.chat("&7Sets the item's custom model data to a specific value. The first button encodes the first 3" +
                " digits, the 2nd button the 4th and 5th digits, and the 3rd button the 6th and 7th. If the first option is greater than 999, " +
                "it instead will represent the entire Custom Model Data regardless of the other two button options. This is to make it more intuitive to use " +
                "when used in a command.");
        this.displayName = Utils.chat("&7&lAdd Custom Model Data");
        this.icon = Material.GRASS_BLOCK;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<model_data/digit1-2-3>");
    }
    @Override
    public List<String> tabAutoCompleteSecondArg() {
        return strength > 999 ? Collections.singletonList("0") : Collections.singletonList("<digit4-5>");
    }

    @Override
    public List<String> tabAutoCompleteThirdArg() {
        return strength > 999 ? Collections.singletonList("0") : Collections.singletonList("<digit6-7>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        if (outputItem == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        meta.setCustomModelData(getCustomModelData());
        outputItem.setItemMeta(meta);
        return outputItem;
    }

    private int getCustomModelData(){
        return strength > 999 ? (int) strength : (int) ((strength * 10000) + (strength2 * 100) + strength3);
    }

    @Override
    public String toString() {
        return Utils.chat("&7Setting the item's custom model data to &e" + getCustomModelData() + "&7.");
    }
}
