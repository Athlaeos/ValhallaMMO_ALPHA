package me.athlaeos.valhallammo.crafting;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.commands.valhalla_commands.RecipeRevealToggleCommand;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.AdvancedDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.recipetypes.DynamicSmithingTableRecipe;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.managers.CustomRecipeManager;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.managers.SmithingItemTreatmentManager;
import me.athlaeos.valhallammo.skills.account.AccountProfile;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class PlayerSmithingListener implements Listener {

    private final Map<UUID, AdvancedDynamicItemModifier.Pair<DynamicSmithingTableRecipe, AdvancedDynamicItemModifier.Pair<AdvancedDynamicItemModifier.Pair<ItemStack, ItemStack>, ItemStack>>> additionItemCache = new HashMap<>();

    @EventHandler
    public void onSmithingTableInteract(InventoryClickEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getWhoClicked().getWorld().getName())) return;
        if (e.getView().getTopInventory() instanceof SmithingInventory){
            SmithingInventory inventory = (SmithingInventory) e.getView().getTopInventory();
            if (e.getClickedInventory() instanceof SmithingInventory){
                if (e.getRawSlot() == 0 || e.getRawSlot() == 1){
                    ItemUtils.calculateClickedSlotOnlyAllow1Placed(e);
                    // place only 1 item in either smithing slots
                } else if (Utils.isItemEmptyOrNull(e.getCurrentItem())){
                    // cancel event if empty result slot is clicked
                    e.setCancelled(true);
                } else {
                    ItemStack base = e.getInventory().getItem(0);
                    ItemStack addition = e.getInventory().getItem(1);
                    SmithingRecipe recipe = getSmithingRecipeAssociatedToItem(e.getInventory().getItem(0), e.getInventory().getItem(1));
                    if (recipe != null){
                        DynamicSmithingTableRecipe r = CustomRecipeManager.getInstance().getDynamicSmithingRecipe(recipe.getKey());
                        if (r != null){
                            if (!r.isConsumeAddition()){
                                AdvancedDynamicItemModifier.Pair<DynamicSmithingTableRecipe, AdvancedDynamicItemModifier.Pair<AdvancedDynamicItemModifier.Pair<ItemStack, ItemStack>, ItemStack>>
                                        additionPrediction = additionItemCache.get(e.getWhoClicked().getUniqueId());
                                if (additionPrediction != null){
                                    if (!r.getName().equals(additionPrediction.getValue1().getName())
                                            || !(additionPrediction.getValue2().getValue1().getValue1().isSimilar(base)
                                            && additionPrediction.getValue2().getValue1().getValue2().isSimilar(addition))) {
                                        // if the recipe used when caching the addition item doesn't match the current recipe
                                        // or if the used items don't match what was used during the making of the cached item
                                        // then cancel
                                        e.getInventory().setItem(2, null);
                                        e.setCancelled(true);
                                        additionItemCache.remove(e.getWhoClicked().getUniqueId());
                                        return;
                                    }
                                    addition = additionPrediction.getValue2().getValue2();
                                }
                                ItemStack finalAddition = addition;
                                additionItemCache.remove(e.getWhoClicked().getUniqueId());
                                if (finalAddition != null) ValhallaMMO.getPlugin().getServer().getScheduler().runTaskLater(ValhallaMMO.getPlugin(), () -> e.getInventory().setItem(1, finalAddition), 1L);
                            }
                            //e.setCurrentItem(results.getValue1());
                        }
                    }
                }
            } else if (!Utils.isItemEmptyOrNull(e.getCurrentItem()) && e.getClickedInventory() instanceof PlayerInventory && e.isShiftClick()){
                int firstEmpty = inventory.firstEmpty();
                if (firstEmpty == 0 || firstEmpty == 1){
                    ItemStack clickedItem = e.getCurrentItem().clone();
                    if (clickedItem.getAmount() > 1){
                        ItemStack newItem = clickedItem.clone();
                        newItem.setAmount(clickedItem.getAmount() - 1);
                        e.setCurrentItem(newItem);
                        clickedItem.setAmount(1);
                    } else {
                        e.setCurrentItem(null);
                    }
                    inventory.setItem(firstEmpty, clickedItem);
                    e.setCancelled(true);
                } else {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPrepareSmithing(PrepareSmithingEvent e){
        if (e.getInventory().getLocation() != null && e.getInventory().getLocation().getWorld() != null && ValhallaMMO.isWorldBlacklisted(e.getInventory().getLocation().getWorld().getName())) return;
        if (Utils.isItemEmptyOrNull(e.getInventory().getItem(0)) || Utils.isItemEmptyOrNull(e.getInventory().getItem(1))) return;
        ItemStack base = e.getInventory().getItem(0);
        ItemStack addition = e.getInventory().getItem(1);
        SmithingRecipe recipe = getSmithingRecipeAssociatedToItem(base, addition);
        if (recipe != null && e.getView().getPlayer() instanceof Player && !Utils.isItemEmptyOrNull(e.getResult())){
            assert base != null && addition != null;
            ItemStack originalAddition = addition.clone();
            DynamicSmithingTableRecipe r = CustomRecipeManager.getInstance().getDynamicSmithingRecipe(recipe.getKey());
            Player p = (Player) e.getView().getPlayer();
            if (r != null){
                // custom recipe
                if (!r.isUnlockedForEveryone()){
                    if (!p.hasPermission("valhalla.allrecipes")){
                        Profile profile = ProfileManager.getManager().getProfile(p, "ACCOUNT");
                        if (profile instanceof AccountProfile){
                            if (!((AccountProfile) profile).getUnlockedRecipes().contains(r.getName())){
                                // player does not have this recipe unlocked
                                e.setResult(null);
                                return;
                            }
                        }
                    }
                }

                if (r.requireCustomTools()){
                    if ((EquipmentClass.getClass(base) != null && !SmithingItemTreatmentManager.getInstance().isItemCustom(base))
                    || (EquipmentClass.getClass(addition) != null && !SmithingItemTreatmentManager.getInstance().isItemCustom(addition))){
                        // items needs to be custom, but aren't
                        e.setResult(null);
                        return;
                    }
                }

                ItemStack result = r.getResult().clone();
                if (r.isTinkerBase()) result = base.clone(); // it's a tinkering recipe, so the recipe result is a copy of the base item before modifiers

                AdvancedDynamicItemModifier.Pair<ItemStack, ItemStack> results = DynamicItemModifier.modify(result, addition, p, r.getModifiersResult(), false, false, true);
                if (results == null || results.getValue1() == null || results.getValue2() == null) {
                    e.setResult(null);
                    return;
                }
                result = results.getValue1();
                addition = results.getValue2();
                if (!r.isConsumeAddition()){
                    AdvancedDynamicItemModifier.Pair<ItemStack, ItemStack> additionResults = DynamicItemModifier.modify(addition, result, p, r.getModifiersAddition(), false, true, true);
                    if (additionResults == null || additionResults.getValue1() == null || additionResults.getValue2() == null) {
                        e.setResult(null);
                        return;
                    }
                    result = additionResults.getValue2();
                    addition = additionResults.getValue1();
                }
                if (ItemUtils.shouldItemBreak(result)) {
                    result = null;
                }
                if (ItemUtils.shouldItemBreak(addition)) addition = null;
                additionItemCache.put(p.getUniqueId(), new AdvancedDynamicItemModifier.Pair<>(r, new AdvancedDynamicItemModifier.Pair<>(new AdvancedDynamicItemModifier.Pair<>(base, originalAddition), addition)));
                e.setResult(result);
            } else {
                if (RecipeRevealToggleCommand.getRevealRecipesForCollection().contains(p.getUniqueId())){
                    p.sendMessage("SMITHING: " + recipe.getKey().getKey());
                }
                // it's a vanilla recipe, so it should be cancelled if the upgraded tool is not custom
                if (SmithingItemTreatmentManager.getInstance().isItemCustom(e.getInventory().getItem(0))){
                    e.setResult(null);
//                    return;
                }
//                if (CustomRecipeManager.getInstance().getDisabledRecipes().contains(recipe.getKey())){
//                    e.setResult(null);
//                }
            }
        } else {
            ItemStack b = e.getInventory().getItem(0);
            if (!Utils.isItemEmptyOrNull(b)){
                if (EquipmentClass.getClass(b) != null && SmithingItemTreatmentManager.getInstance().isItemCustom(b)){
                    e.setResult(null);
                }
            }
        }
    }

    private SmithingRecipe getSmithingRecipeAssociatedToItem(ItemStack i1, ItemStack i2){
        if (Utils.isItemEmptyOrNull(i1) || Utils.isItemEmptyOrNull(i2)) return null;
        Iterator<Recipe> iterator = ValhallaMMO.getPlugin().getServer().recipeIterator();
        SmithingRecipe found = null;
        while (iterator.hasNext()){
            Recipe r = iterator.next();
            if (r instanceof SmithingRecipe){
                SmithingRecipe recipe = (SmithingRecipe) r;
                if (recipe.getBase().test(i1) && recipe.getAddition().test(i2)){
                    if (recipe.getBase() instanceof RecipeChoice.ExactChoice && recipe.getAddition() instanceof RecipeChoice.ExactChoice) return recipe;
                    if (recipe.getBase() instanceof RecipeChoice.ExactChoice || recipe.getAddition() instanceof RecipeChoice.ExactChoice) return recipe;
                    found = recipe;
                }
            }
        }
        return found;
    }
}
