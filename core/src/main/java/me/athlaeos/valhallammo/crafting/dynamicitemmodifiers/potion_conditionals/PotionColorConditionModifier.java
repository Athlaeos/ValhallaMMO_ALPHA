package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.potion_conditionals;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicEditable;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.TripleArgDynamicItemModifier;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.Collections;
import java.util.List;

public class PotionColorConditionModifier extends TripleArgDynamicItemModifier implements DynamicEditable {
    public PotionColorConditionModifier(String name, double strength, double strength2, double strength3, ModifierPriority priority) {
        super(name, strength, strength2, strength3, priority);

        this.name = name;
        this.category = ModifierCategory.POTION_CONDITIONALS;

        this.bigStepDecrease = 25D; // R
        this.bigStepIncrease = 25D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 255D;

        this.bigStepDecrease2 = 25D; // G
        this.bigStepIncrease2 = 25D;
        this.smallStepDecrease2 = 1D;
        this.smallStepIncrease2 = 1D;
        this.defaultStrength2 = 0;
        this.minStrength2 = 0;
        this.maxStrength2 = 255D;

        this.bigStepDecrease3 = 25D; // B
        this.bigStepIncrease3 = 25D;
        this.smallStepDecrease3 = 1D;
        this.smallStepIncrease3 = 1D;
        this.defaultStrength3 = 0;
        this.minStrength3 = 0;
        this.maxStrength3 = 255D;

        this.description = Utils.chat("&7If the potion doesn't have the exact color given here, the recipe is cancelled.");
        this.displayName = Utils.chat("&e&lCONDITION: Potion Color");
        this.icon = Material.BLUE_DYE;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<red>");
    }

    @Override
    public List<String> tabAutoCompleteSecondArg() {
        return Collections.singletonList("<green>");
    }

    @Override
    public List<String> tabAutoCompleteThirdArg() {
        return Collections.singletonList("<blue>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        if (!(outputItem.getItemMeta() instanceof PotionMeta)) return null;
        PotionMeta meta = (PotionMeta) outputItem.getItemMeta();
        if (meta == null) return null;
        if (meta.getColor() == null) return null;
        if (meta.getColor().getRed() == (int) strength &&
            meta.getColor().getGreen() == (int) strength2 &&
            meta.getColor().getBlue() == (int) strength3) return outputItem;

        return null;
    }

    @Override
    public String toString() {
        int r = (int) strength;
        int g = (int) strength2;
        int b = (int) strength3;
        String hex = Utils.rgbToHex(r, g, b);
        return Utils.chat(String.format("&7RGB: &e%d&7, &e%d&7, &e%d&7. -nRequiring the color %s", r, g, b,
                Utils.chat("&" + hex + hex)));
    }

    @Override
    public void editIcon(ItemStack icon, int buttonNumber) {
        icon.setType(Material.POTION);
        if (icon.getItemMeta() instanceof PotionMeta){
            PotionMeta meta = (PotionMeta) icon.getItemMeta();
            meta.setColor(Color.fromRGB((int) strength, (int) strength2, (int) strength3));

            switch (buttonNumber){
                case 1:
                    meta.setDisplayName(Utils.chat("&4&lRed"));
                    break;
                case 2:
                    meta.setDisplayName(Utils.chat("&2&lGreen"));
                    break;
                case 3:
                    meta.setDisplayName(Utils.chat("&1&lBlue"));
                    break;
            }

            icon.setItemMeta(meta);
        }
    }
}
