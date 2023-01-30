package me.athlaeos.valhallammo.menus;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;

import java.util.List;

public interface CraftingManagerMenu {
    void setResultModifiers(List<DynamicItemModifier> modifiers);
}
