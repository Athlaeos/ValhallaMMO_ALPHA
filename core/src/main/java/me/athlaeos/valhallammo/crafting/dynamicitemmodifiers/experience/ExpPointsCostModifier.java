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

public class ExpPointsCostModifier extends DynamicItemModifier {
    public ExpPointsCostModifier(String name) {
        super(name, 0D, ModifierPriority.NEUTRAL);

        this.name = name;

        this.category = ModifierCategory.EXPERIENCE;
        this.bigStepDecrease = 10;
        this.bigStepIncrease = 10;
        this.smallStepDecrease = 1;
        this.smallStepIncrease = 1;
        this.defaultStrength = 0;
        this.minStrength = -1000;
        this.maxStrength = 1000;
        this.description = Utils.chat("&7Recipe requires and takes(or gives, if negative) some EXP points to craft");
        this.displayName = Utils.chat("&7&lEXP Cost: Points");
        this.icon = Material.EXPERIENCE_BOTTLE;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<exp_points_requirement>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        if (crafter == null) return null;
        if (crafter.getGameMode() == GameMode.CREATIVE) return outputItem;
        int playerExperience = Utils.getTotalExperience(crafter);
        if (this.validate){
            if (playerExperience < ((int) strength * timesExecuted)) return null;
        }
        if (this.use){
            if (playerExperience < ((int) strength * timesExecuted)) return null;
            Utils.setTotalExperience(crafter, playerExperience - ((int) strength * timesExecuted));
        }
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat((strength > 0) ?
                String.format("&7Recipe requires and consumes &e%s&7 EXP Points to craft", "" + (int) strength) :
                String.format("&7Recipe grants the user &e%s&7 EXP Points after crafting", "" + (int) -strength));
    }
}
