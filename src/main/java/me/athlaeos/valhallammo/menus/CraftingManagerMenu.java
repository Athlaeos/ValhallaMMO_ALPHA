package me.athlaeos.valhallammo.menus;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;

import java.util.Collection;

public interface CraftingManagerMenu {
    void setCurrentModifiers(Collection<DynamicItemModifier> modifiers);
}
