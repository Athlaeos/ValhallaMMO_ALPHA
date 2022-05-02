package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.experience;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class ExpLevelCostModifier extends DynamicItemModifier {
    public ExpLevelCostModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;

        this.category = ModifierCategory.EXPERIENCE;
        this.bigStepDecrease = 10;
        this.bigStepIncrease = 10;
        this.smallStepDecrease = 1;
        this.smallStepIncrease = 1;
        this.defaultStrength = 0;
        this.minStrength = -1000;
        this.maxStrength = 1000;
        this.description = Utils.chat("&7Recipe requires and takes(or gives, if negative) some EXP levels to craft");
        this.displayName = Utils.chat("&7&lEXP Cost: Levels");
        this.icon = Material.EXPERIENCE_BOTTLE;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<exp_level_requirement>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (crafter == null) return null;
        if (crafter.getGameMode() == GameMode.CREATIVE) return outputItem;
        if (this.validate){
            if (crafter.getLevel() < (int) strength) return null;
        }
        if (this.use){
            if (outputItem == null) return null;
            if (crafter.getLevel() < (int) strength) return null;
            crafter.setLevel(crafter.getLevel() - (int) strength);
        }
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat((strength > 0) ?
                String.format("&7Recipe requires and consumes &e%s&7 EXP Levels to craft", "" + (int) strength) :
                String.format("&7Recipe grants the user &e%s&7 EXP Levels after crafting", "" + (int) -strength));
    }
}
