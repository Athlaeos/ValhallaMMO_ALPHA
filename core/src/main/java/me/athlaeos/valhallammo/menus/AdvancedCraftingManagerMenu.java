package me.athlaeos.valhallammo.menus;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;

import java.util.List;

public interface AdvancedCraftingManagerMenu extends CraftingManagerMenu{
    void setSecondaryModifiers(List<DynamicItemModifier> modifiers);
}
