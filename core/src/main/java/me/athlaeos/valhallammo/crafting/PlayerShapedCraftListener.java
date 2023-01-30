package me.athlaeos.valhallammo.crafting;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.commands.valhalla_commands.RecipeRevealToggleCommand;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.recipetypes.DynamicCraftingTableRecipe;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.dom.RequirementType;
import me.athlaeos.valhallammo.events.PlayerSkillExperienceGainEvent;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.items.potioneffectwrappers.PotionEffectWrapper;
import me.athlaeos.valhallammo.managers.*;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.skills.account.AccountProfile;
import me.athlaeos.valhallammo.skills.smithing.SmithingSkill;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.EntityEffect;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PlayerShapedCraftListener implements Listener {
    private final String error_crafting_no_sufficient_tool_owned;

    public PlayerShapedCraftListener(){
        error_crafting_no_sufficient_tool_owned = TranslationManager.getInstance().getTranslation("error_crafting_no_sufficient_tool_owned");
    }

    @EventHandler
    public void onPlayerCraft(CraftItemEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getWhoClicked().getWorld().getName())) {
            return;
        }
        CustomRecipeManager manager = CustomRecipeManager.getInstance();
        if (e.getWhoClicked() instanceof Player){
            if (e.getRecipe() instanceof ShapedRecipe || e.getRecipe() instanceof ShapelessRecipe){
//                if (CustomRecipeManager.getInstance().getDisabledRecipes().contains(((Keyed) e.getRecipe()).getKey())){
//                    e.getInventory().setResult(null);
//                }
                DynamicCraftingTableRecipe recipe = manager.getDynamicShapedRecipe(((Keyed) e.getRecipe()).getKey());
                if (recipe != null){
                    if (!e.getWhoClicked().hasPermission("valhalla.allrecipes")){
                        if (!recipe.isUnlockedForEveryone()){
                            Profile profile = ProfileManager.getManager().getProfile((Player) e.getWhoClicked(), "ACCOUNT");
                            if (profile != null){
                                if (profile instanceof AccountProfile){
                                    if (!((AccountProfile) profile).getUnlockedRecipes().contains(recipe.getName())){
                                        e.getInventory().setResult(null);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    if (!Utils.isItemEmptyOrNull(e.getInventory().getResult())){
                        ItemStack result = resultPostConditions(recipe, e.getInventory().getMatrix(), e.getInventory().getResult(), e.getRecipe() instanceof ShapelessRecipe);
                        if (result != null){
                            PlayerInventory inventory = e.getWhoClicked().getInventory(); //Get crafting inventory
                            ClickType clickType = e.getClick();
                            int realAmount = 1;
                            if(clickType.isShiftClick())
                            {
                                int available = 0; // max items available to fit in inventory
                                int maxCraftable = 1000; // the max amount of items the player could craft if they have enough inventory space,
                                // this is just the same as the smallest itemstack amount
                                for (ItemStack i : e.getInventory().getMatrix()){
                                    if (Utils.isItemEmptyOrNull(i)) continue;
                                    if (i.getAmount() < maxCraftable) maxCraftable = i.getAmount();
                                }
                                for (ItemStack i : inventory.getStorageContents()){
                                    if (available > maxCraftable) break; // inventory has enough space, no longer need to check
                                    if (Utils.isItemEmptyOrNull(i)){
                                        available += result.getType().getMaxStackSize();
                                    } else if (result.getType().getMaxStackSize() > 1 && i.isSimilar(result)){
                                        available += i.getType().getMaxStackSize() - i.getAmount();
                                    }
                                }

                                realAmount = Math.min(available, maxCraftable); //lowerAmount * result.getAmount();
                            } else {
                                if (e.getRawSlot() == 0 && !Utils.isItemEmptyOrNull(e.getCursor()) && !Utils.isItemEmptyOrNull(e.getCurrentItem())) {
                                    e.setCancelled(true);
                                    return;
                                }
                            }
                            if (recipe.getToolRequirementType() > 0){
                                int slotMatchFound = getRequiredTool(e.getWhoClicked().getInventory(), recipe.getRequiredToolId(), recipe.getToolRequirementType());
                                if (slotMatchFound < 0){
                                    e.setCancelled(true);
                                    e.getWhoClicked().sendMessage(Utils.chat(error_crafting_no_sufficient_tool_owned));
                                    return;
                                } else {
                                    ItemStack foundItem = e.getWhoClicked().getInventory().getItem(slotMatchFound);
                                    if (!Utils.isItemEmptyOrNull(foundItem) && ItemUtils.damageItem((Player) e.getWhoClicked(), foundItem, realAmount, EntityEffect.BREAK_EQUIPMENT_MAIN_HAND, true)){
                                        e.getWhoClicked().getInventory().setItem(slotMatchFound, null);
                                    }
                                }
                            }
                            result = DynamicItemModifier.modify(result, (Player) e.getWhoClicked(), recipe.getItemModifiers(), false, true, true, realAmount);

                            //List<DynamicItemModifier> modifiers = new ArrayList<>(recipe.getItemModifiers());
                            //modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));
                            //for (DynamicItemModifier modifier : modifiers){
                            //    if (result == null) break;
                            //    result = modifier.processItem((Player) e.getWhoClicked(), result, realAmount);
                            //}
                            if (result != null && !recipe.isTinkerFirstItem()){
                                Skill skill = SkillProgressionManager.getInstance().getSkill("SMITHING");
                                if (skill != null){
                                    if (skill instanceof SmithingSkill){
                                        double expReward = ((SmithingSkill) skill).expForCraftedItem((Player) e.getWhoClicked(), result);
                                        skill.addEXP((Player) e.getWhoClicked(), expReward * realAmount, false, PlayerSkillExperienceGainEvent.ExperienceGainReason.SKILL_ACTION);
                                    }
                                }
                            }
                        }
                        e.getInventory().setResult(result);
                    }
                } else {
                    if (RecipeRevealToggleCommand.getRevealRecipesForCollection().contains(e.getWhoClicked().getUniqueId())){
                        e.getWhoClicked().sendMessage("CRAFT: " + ((Keyed) e.getRecipe()).getKey().getKey());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerCraft(PrepareItemCraftEvent e){
        if (e.getInventory().getLocation() != null){
            if (e.getInventory().getLocation().getWorld() != null){
                if (ValhallaMMO.isWorldBlacklisted(e.getInventory().getLocation().getWorld().getName())) return;
            }
        }
        if (e.isRepair()){
            for (ItemStack i : e.getInventory().getMatrix()){
                if (Utils.isItemEmptyOrNull(i)) continue;
                if (SmithingItemTreatmentManager.getInstance().isItemCustom(i)) {
                    // cancel tool repair if one of the items is custom
                    e.getInventory().setResult(null);
                    return;
                }
            }
        }
        CustomRecipeManager manager = CustomRecipeManager.getInstance();
        if (e.getRecipe() instanceof ShapedRecipe || e.getRecipe() instanceof ShapelessRecipe){
//            if (CustomRecipeManager.getInstance().getDisabledRecipes().contains(((Keyed) e.getRecipe()).getKey())){
//                e.getInventory().setResult(null);
//            }
            DynamicCraftingTableRecipe recipe = manager.getDynamicShapedRecipe(((Keyed) e.getRecipe()).getKey());

            if (recipe != null){
                if (!Utils.isItemEmptyOrNull(e.getInventory().getResult())){
                    ItemStack result = resultPostConditions(recipe, e.getInventory().getMatrix(), e.getInventory().getResult(), e.getRecipe() instanceof ShapelessRecipe);
                    if (result != null){
                        if (e.getViewers().size() > 0){
                            if (!e.getViewers().get(0).hasPermission("valhalla.allrecipes")){
                                if (!recipe.isUnlockedForEveryone()){
                                    Profile profile = ProfileManager.getManager().getProfile((Player) e.getViewers().get(0), "ACCOUNT");
                                    if (profile != null){
                                        if (profile instanceof AccountProfile){
                                            if (!((AccountProfile) profile).getUnlockedRecipes().contains(recipe.getName())){
                                                e.getInventory().setResult(null);
                                                return;
                                            }
                                        }
                                    }
                                }
                            }
                            result = DynamicItemModifier.modify(result, (Player) e.getViewers().get(0), recipe.getItemModifiers(), false, false, true);

                            //List<DynamicItemModifier> modifiers = new ArrayList<>(recipe.getItemModifiers());
                            //modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));
                            //for (DynamicItemModifier modifier : modifiers){
                            //    if (result == null) break;
                            //    try {
                            //        DynamicItemModifier tempMod = modifier.clone();
                            //        tempMod.setUse(false);
                            //        tempMod.setValidate(true);
                            //        result = tempMod.processItem((Player) e.getViewers().get(0), result);
                            //    } catch (CloneNotSupportedException ignored){
                            //    }
                            //}
                        }
                    }
                    e.getInventory().setResult(result);
                }
            }
        } else if (e.getRecipe() instanceof ComplexRecipe){
            if (e.getRecipe() != null && ((ComplexRecipe) e.getRecipe()).getKey().getKey().equals("tipped_arrow") && !Utils.isItemEmptyOrNull(e.getInventory().getResult()) && Arrays.stream(e.getInventory().getMatrix()).noneMatch(Utils::isItemEmptyOrNull)){
                if (e.getInventory().getMatrix()[0].isSimilar(e.getInventory().getMatrix()[1])
                 && e.getInventory().getMatrix()[0].isSimilar(e.getInventory().getMatrix()[2])
                 && e.getInventory().getMatrix()[0].isSimilar(e.getInventory().getMatrix()[3])
                 && e.getInventory().getMatrix()[0].isSimilar(e.getInventory().getMatrix()[5])
                 && e.getInventory().getMatrix()[0].isSimilar(e.getInventory().getMatrix()[6])
                 && e.getInventory().getMatrix()[0].isSimilar(e.getInventory().getMatrix()[7])
                 && e.getInventory().getMatrix()[0].isSimilar(e.getInventory().getMatrix()[8])){
                    // arrows are similar to eachother, proceed
                    ItemStack arrow = e.getInventory().getMatrix()[0].clone();
                    if (arrow.isSimilar(new ItemStack(Material.ARROW))) {
                        PotionEffectManager.renamePotion(e.getInventory().getResult(), true);
                        return; // recipe is vanilla
                    }
                    arrow.setAmount(8);
                    arrow.setType(Material.TIPPED_ARROW);
                    Collection<PotionEffectWrapper> defaultStats = PotionAttributesManager.getInstance().getDefaultPotionEffects(e.getInventory().getMatrix()[4]);
                    Collection<PotionEffectWrapper> currentStats = PotionAttributesManager.getInstance().getCurrentStats(e.getInventory().getMatrix()[4]);
                    if (defaultStats.isEmpty()) return; // potion is vanilla
                    PotionAttributesManager.getInstance().setDefaultPotionEffects(arrow, defaultStats);
                    if (!currentStats.isEmpty()){
                        for (PotionEffectWrapper potionEffect : currentStats){
                            potionEffect.setDuration((int) Math.floor(potionEffect.getDuration() / 2D));
                        }

                        PotionAttributesManager.getInstance().setStats(arrow, currentStats);
                    }


                    PotionEffectManager.renamePotion(arrow, true);

                    PotionMeta arrowMeta = (PotionMeta) arrow.getItemMeta();
                    PotionMeta lingeringMeta = (PotionMeta) e.getInventory().getMatrix()[4].getItemMeta();
                    if (arrowMeta != null && lingeringMeta != null){
                        arrowMeta.setColor(lingeringMeta.getColor());
                        arrowMeta.addItemFlags(lingeringMeta.getItemFlags().toArray(new ItemFlag[0]));
                        arrow.setItemMeta(arrowMeta);
                    }

                    e.getInventory().setResult(arrow);
                } else {
                    // arrows are not similar to eachother, nullify result
                    e.getInventory().setResult(null);
                }
            }
        }
    }

    private ItemStack resultPostConditions(DynamicCraftingTableRecipe recipe, ItemStack[] matrix, ItemStack result, boolean isShapeless){
        boolean resultOverwritten = false;
        if (isShapeless){
            // shapeless recipe
            // exact recipe choices are not possible for shapeless recipes, so manual meta confirmation is required
            List<ItemStack> exactIngredients = new ArrayList<>(recipe.getExactItems().values());
            // if the recipe does not need exact metadata, then all items need to be simplified into their base material item
            // if the recipe DOES need exact metadata, but also needs the first equipment to be tinkered, then only the equipment should be simplified
            // if the recipe needs exact metadata, including tools, then no items should be simplified
            if (!recipe.isUseMetadata() || (recipe.isUseMetadata() && recipe.isTinkerFirstItem())){
                boolean simplifyEquipmentOnly = recipe.isUseMetadata();
                List<ItemStack> simplifiedIngredients = new ArrayList<>();
                for (ItemStack i : exactIngredients){
                    ItemStack single = i.clone();
                    single.setAmount(1);
                    simplifiedIngredients.add(simplifyEquipmentOnly ? (EquipmentClass.getClass(i) != null ? new ItemStack(i.getType()) : single) : new ItemStack(i.getType()));
                }
                exactIngredients = simplifiedIngredients;
            }

            for (ItemStack matrixItem : matrix){
                if (Utils.isItemEmptyOrNull(matrixItem)) continue;

                ItemStack slotClone = matrixItem.clone();
                boolean isTool = EquipmentClass.getClass(matrixItem) != null;

                // if the recipe does not use exact meta, simplify slot item
                if (!recipe.isUseMetadata()) slotClone = new ItemStack(slotClone.getType());
                // if the recipes uses exact meta, and the first equipment needs to be tinkered, simplify only if the item is equipment
                else if (recipe.isUseMetadata() && recipe.isTinkerFirstItem()) slotClone = isTool ? new ItemStack(slotClone.getType()) : slotClone;
                slotClone.setAmount(1);

                // if the matrix slot item is not contained in the ingredients, null is returned
                // this is to check if all required ingredients are actually present
                if (exactIngredients.contains(slotClone)){
                    exactIngredients.remove(slotClone);
                } else {
                    return null;
                }

                boolean isCustom = SmithingItemTreatmentManager.getInstance().isItemCustom(matrixItem);

                // if the item is a tool, the recipe requires custom tools, but the item is not a custom tool then a null result is returned
                if (isTool && recipe.isRequireCustomTools() && !isCustom) return null;
                if (!resultOverwritten && recipe.isTinkerFirstItem() && isTool) {// && recipe.isUseMetadata()
                    result = matrixItem.clone();
                    resultOverwritten = true;
                }
            }

            // at this point all ingredients should have been verified and consumed, leaving an empty collection. If not, the matrix could contain several items of the same
            // type that do not match the required ingredient data
            if (!exactIngredients.isEmpty()) return null;
        } else {
            // shaped recipe
            // only for equipment is their base material type required IF exact meta required AND tinker first item are enabled
            // everything else should be acceptable from the vanilla recipe implementation itself
            for (ItemStack matrixItem : matrix){
                if (Utils.isItemEmptyOrNull(matrixItem)) continue;
                boolean isCustom = SmithingItemTreatmentManager.getInstance().isItemCustom(matrixItem);
                boolean isTool = EquipmentClass.getClass(matrixItem) != null;

                // if the item is a tool, the recipe requires custom tools, but the item is not a custom tool then a null result is returned
                if (isTool && recipe.isRequireCustomTools() && !isCustom) return null;
                // if the result has not yet been overwritten AND if the recipe wants to tinker the first tool AND if the recipe requires exact metas AND the item is a tool
                // THEN the result is overwritten as the first tool found, modifiers are then executed accordingly
                // ((the requirement of exact meta along with tinkering the first tool means the ingredient is registered as a material requirement rather than an exact item requirement))
                if (!resultOverwritten && recipe.isTinkerFirstItem() && isTool) {// && recipe.isUseMetadata()
                    result = matrixItem.clone();
                    resultOverwritten = true;
                }
            }
        }
        return result;
    }

    private int getRequiredTool(Inventory playerInventory, int toolRequired, int toolRequirementType){
        for (int i = 0; i < playerInventory.getContents().length; i++){
            ItemStack item = playerInventory.getContents()[i];
            if (Utils.isItemEmptyOrNull(item)) continue;
            if (RequirementType.doItemFitsCraftingConditions(item, toolRequirementType, toolRequired)) return i;
        }
        return -1;
    }
}
