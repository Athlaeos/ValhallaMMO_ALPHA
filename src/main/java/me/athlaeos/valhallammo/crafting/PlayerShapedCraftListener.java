package me.athlaeos.valhallammo.skills.smithing.recipes.listeners;

import me.athlaeos.valhallammo.skills.smithing.recipes.dom.DynamicShapedRecipe;
import me.athlaeos.valhallammo.skills.smithing.recipes.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.skills.smithing.recipes.managers.CustomRecipeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class PlayerShapedCraftListener implements Listener {

    @EventHandler
    public void onPlayerCraft(CraftItemEvent e){
        CustomRecipeManager manager = CustomRecipeManager.getInstance();
        if (e.getWhoClicked() instanceof Player){
            if (e.getRecipe() instanceof ShapedRecipe){
                DynamicShapedRecipe recipe = manager.getDynamicShapedRecipe(((ShapedRecipe) e.getRecipe()).getKey());
                if (recipe != null){
                    ItemStack output = e.getRecipe().getResult();
                    for (DynamicItemModifier modifier : recipe.getModifiers()){
                        modifier.processItem((Player) e.getWhoClicked(), output);
                    }
                    e.setCurrentItem(output);
                }
            }
        }
    }
}
