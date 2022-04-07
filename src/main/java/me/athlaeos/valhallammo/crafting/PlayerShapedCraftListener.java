package me.athlaeos.valhallammo.crafting;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.recipetypes.DynamicShapedRecipe;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.managers.CustomRecipeManager;
import me.athlaeos.valhallammo.managers.SmithingItemTreatmentManager;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.skills.account.AccountProfile;
import me.athlaeos.valhallammo.skills.smithing.SmithingSkill;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PlayerShapedCraftListener implements Listener {

    @EventHandler
    public void onPlayerCraft(CraftItemEvent e){
        CustomRecipeManager manager = CustomRecipeManager.getInstance();
        if (e.getWhoClicked() instanceof Player){
            if (e.getRecipe() instanceof ShapedRecipe){
                if (CustomRecipeManager.getInstance().getDisabledRecipes().contains(((ShapedRecipe) e.getRecipe()).getKey())){
                    e.getInventory().setResult(null);
                }
                DynamicShapedRecipe recipe = manager.getDynamicShapedRecipe(((ShapedRecipe) e.getRecipe()).getKey());
                if (recipe != null){
                    Profile profile = ProfileManager.getProfile((Player) e.getWhoClicked(), "ACCOUNT");
                    if (profile != null){
                        if (profile instanceof AccountProfile){
                            if (((AccountProfile) profile).getUnlockedRecipes().contains(recipe.getName())){
                                e.getInventory().setResult(null);
                                return;
                            }
                        }
                    }
                    if (!Utils.isItemEmptyOrNull(e.getInventory().getResult())){
                        ItemStack result = resultPostConditions(recipe, e.getInventory().getMatrix(), e.getInventory().getResult());
                        if (result != null){
                            List<DynamicItemModifier> modifiers = new ArrayList<>(recipe.getItemModifiers());
                            modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));
                            for (DynamicItemModifier modifier : modifiers){
                                if (result == null) break;
                                result = modifier.processItem((Player) e.getWhoClicked(), result);
                            }

                            Skill skill = SkillProgressionManager.getInstance().getSkill("SMITHING");
                            if (skill != null){
                                if (skill instanceof SmithingSkill){
                                    double expReward = ((SmithingSkill) skill).expForCraftedItem((Player) e.getWhoClicked(), result);
                                    skill.addEXP((Player) e.getWhoClicked(), expReward, false);
                                }
                            }
                        }
                        e.getInventory().setResult(result);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerCraft(PrepareItemCraftEvent e){
        CustomRecipeManager manager = CustomRecipeManager.getInstance();
        if (e.getRecipe() instanceof ShapedRecipe){
            if (e.getRecipe() == null) return;
            if (CustomRecipeManager.getInstance().getDisabledRecipes().contains(((ShapedRecipe) e.getRecipe()).getKey())){
                e.getInventory().setResult(null);
            }
            DynamicShapedRecipe recipe = manager.getDynamicShapedRecipe(((ShapedRecipe) e.getRecipe()).getKey());

            if (recipe != null){
                if (!Utils.isItemEmptyOrNull(e.getInventory().getResult())){
                    ItemStack result = resultPostConditions(recipe, e.getInventory().getMatrix(), e.getInventory().getResult());
                    if (result != null){
                        if (e.getViewers().size() > 0){
                            Profile profile = ProfileManager.getProfile((Player) e.getViewers().get(0), "ACCOUNT");
                            if (profile != null){
                                if (profile instanceof AccountProfile){
                                    if (((AccountProfile) profile).getUnlockedRecipes().contains(recipe.getName())){
                                        e.getInventory().setResult(null);
                                        return;
                                    }
                                }
                            }
                            List<DynamicItemModifier> modifiers = new ArrayList<>(recipe.getItemModifiers());
                            modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));
                            for (DynamicItemModifier modifier : modifiers){
                                modifier.processItem((Player) e.getViewers().get(0), result);
                            }
                        }
                    }
                    e.getInventory().setResult(result);
                }
            }
        }
    }

    private ItemStack resultPostConditions(DynamicShapedRecipe recipe, ItemStack[] matrix, ItemStack result){
        for (Integer slot : recipe.getExactItems().keySet()){
            ItemStack compareItem = matrix[slot];
            if (compareItem == null) continue;
            boolean isCustom = SmithingItemTreatmentManager.getInstance().isItemCustom(compareItem);
            boolean isTool = EquipmentClass.getClass(compareItem.getType()) != null;

            if (recipe.isUseMetadata()){
                boolean overrideMetaRequirement = false;
                if (recipe.isRequireCustomTools()){
                    if (isCustom && isTool) {
                        overrideMetaRequirement = true;
                    }
                }
                if (!overrideMetaRequirement){
                    if (!compareItem.isSimilar(recipe.getExactItems().get(slot))) {
                        return null;
                    }
                }
            }
            if (recipe.isRequireCustomTools()){
                if (isTool){
                    if (!isCustom) return null;
                }
            }
            if (recipe.isTinkerFirstItem()){
                if (matrix[0] == null) {
                    return null;
                }
                if (recipe.getRecipe().getResult().getType() != matrix[0].getType()){
                    return null;
                } else {
                    result = matrix[0].clone();
                }
            }
        }
        return result;
    }

    @EventHandler
    public void onItemSmith(SmithItemEvent e){
        if (SmithingItemTreatmentManager.getInstance().isItemCustom(e.getInventory().getItem(0))){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPrepareItemSmith(PrepareSmithingEvent e){
        if (SmithingItemTreatmentManager.getInstance().isItemCustom(e.getInventory().getItem(0))){
            e.setResult(null);
        }
    }
}
