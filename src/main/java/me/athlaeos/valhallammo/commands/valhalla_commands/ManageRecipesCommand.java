package me.athlaeos.valhallammo.commands.valhalla_commands;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.commands.Command;
import me.athlaeos.valhallammo.crafting.DynamicItemModifierManager;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.crafting.recipetypes.*;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.managers.CustomRecipeManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.menus.*;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.*;

public class ManageRecipesCommand implements Command {
	private final String recipes_description;
	private final String error_recipe_exists;
	private final String error_recipe_missing;

	public ManageRecipesCommand(){
		recipes_description = TranslationManager.getInstance().getTranslation("description_command_recipes");
		error_recipe_exists = TranslationManager.getInstance().getTranslation("error_command_recipe_exists");
		error_recipe_missing = TranslationManager.getInstance().getTranslation("error_command_recipe_missing");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 1){
			new RecipeCategoryMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender)).open();
			return true;
		} else if (args.length >= 3){
			if (args[1].equalsIgnoreCase("new")){
				if (args.length >= 4){
					if (CustomRecipeManager.getInstance().getAllCustomRecipes().containsKey(args[3]) || CustomRecipeManager.getInstance().getShapedRecipes().containsKey(args[3])){
						Utils.sendMessage(sender, Utils.chat(error_recipe_exists));
						return true;
					}
					String displayName = String.join(" ", Arrays.copyOfRange(args, 4, args.length));
					if (args[2].equalsIgnoreCase("improvement")){
						if (args.length >= 5){
							ItemImprovementRecipe recipe = new ItemImprovementRecipe(args[3], displayName, Material.WOODEN_SWORD, Material.CRAFTING_TABLE, new HashSet<>());
							new ManageTinkerRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), recipe).open();
						} else {
							return false;
						}
					} else if (args[2].equalsIgnoreCase("class_improvement")){
						if (args.length >= 5){
							ItemClassImprovementRecipe recipe = new ItemClassImprovementRecipe(args[3], displayName, EquipmentClass.CHESTPLATE, Material.CRAFTING_TABLE, new HashSet<>());
							new ManageClassTinkerRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), recipe).open();
						} else {
							return false;
						}
					} else if (args[2].equalsIgnoreCase("craft")){
						if (args.length >= 5){
							ItemCraftingRecipe recipe = new ItemCraftingRecipe(args[3], displayName, new ItemStack(Material.WOODEN_SWORD), Material.CRAFTING_TABLE, new HashSet<>());
							new ManageCraftRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), recipe).open();
						} else {
							return false;
						}
					} else if (args[2].equalsIgnoreCase("shaped")){
						DynamicShapedRecipe recipe = new DynamicShapedRecipe(args[3], new ShapedRecipe(new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_" + args[3]), new ItemStack(Material.WOODEN_SWORD)), new HashMap<>(), true, true, false, new HashSet<>());
						new ManageShapedRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), recipe).open();
					} else if (args[2].equalsIgnoreCase("brewing")){
						DynamicBrewingRecipe recipe = new DynamicBrewingRecipe(args[3], null, null, false, new HashSet<>());
						Collection<DynamicItemModifier> modifiers = new HashSet<>();
						DynamicItemModifier modifier = DynamicItemModifierManager.getInstance().createModifier("exp_bonus_alchemy", 100, ModifierPriority.LAST);
						if (modifier != null){
							modifiers.add(modifier);
						}
						recipe.setModifiers(modifiers);
						new ManageBrewingRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), recipe).open();
					}
				}
			} else if (args[1].equalsIgnoreCase("edit")){
				if (CustomRecipeManager.getInstance().getAllCustomRecipes().containsKey(args[2])){
					AbstractCustomCraftingRecipe recipe = CustomRecipeManager.getInstance().getRecipeByName(args[2]);
					if (recipe instanceof ItemImprovementRecipe){
						new ManageTinkerRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), (ItemImprovementRecipe) recipe).open();
					} else if (recipe instanceof ItemCraftingRecipe){
						new ManageCraftRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), (ItemCraftingRecipe) recipe).open();
					}
				} else if (CustomRecipeManager.getInstance().getShapedRecipes().containsKey(args[2])){
					DynamicShapedRecipe recipe = CustomRecipeManager.getInstance().getDynamicShapedRecipe(args[2]);
					new ManageShapedRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), recipe).open();
				} else if (CustomRecipeManager.getInstance().getBrewingRecipes().containsKey(args[2])){
					DynamicBrewingRecipe recipe = CustomRecipeManager.getInstance().getBrewingRecipe(args[2]);
					new ManageBrewingRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), recipe).open();
				} else {
					Utils.sendMessage(sender, Utils.chat(error_recipe_missing));
				}
			} else if (args[1].equalsIgnoreCase("copy")){
				if (args.length >= 4){
					if (CustomRecipeManager.getInstance().getAllCustomRecipes().containsKey(args[2])){
						if (args.length >= 5){
							if (args[3].equals(args[2]) || CustomRecipeManager.getInstance().getRecipeByName(args[3]) != null) {
								Utils.sendMessage(sender, Utils.chat(error_recipe_exists));
								return true;
							}
							AbstractCustomCraftingRecipe recipe = CustomRecipeManager.getInstance().getRecipeByName(args[2]);
							AbstractCustomCraftingRecipe newRecipe = null;
							String displayName = String.join(" ", Arrays.copyOfRange(args, 4, args.length));
							Collection<DynamicItemModifier> modifiers = new HashSet<>();
							for (DynamicItemModifier m : recipe.getItemModifers()){
								try {
									modifiers.add(m.clone());
								} catch (CloneNotSupportedException ignored){
								}
							}

							if (recipe instanceof ItemCraftingRecipe){
								newRecipe = new ItemCraftingRecipe(args[3], displayName, ((ItemCraftingRecipe) recipe).getResult(), recipe.getCraftingBlock(), new HashSet<>(recipe.getIngredients()), recipe.getCraftingTime(), recipe.breakStation(), modifiers);
								newRecipe.setValidation(recipe.getValidation());
							} else if (recipe instanceof ItemImprovementRecipe){
								newRecipe = new ItemImprovementRecipe(args[3], displayName, ((ItemImprovementRecipe) recipe).getRequiredItemType(), recipe.getCraftingBlock(), new HashSet<>(recipe.getIngredients()), recipe.getCraftingTime(), recipe.breakStation(), modifiers);
								newRecipe.setValidation(recipe.getValidation());
							} else if (recipe instanceof ItemClassImprovementRecipe){
								newRecipe = new ItemClassImprovementRecipe(args[3], displayName, ((ItemClassImprovementRecipe) recipe).getRequiredEquipmentClass(), recipe.getCraftingBlock(), new HashSet<>(recipe.getIngredients()), recipe.getCraftingTime(), recipe.breakStation(), modifiers);
								newRecipe.setValidation(recipe.getValidation());
							}
							if (newRecipe != null){
								if (newRecipe instanceof ItemImprovementRecipe){
									new ManageTinkerRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), (ItemImprovementRecipe) newRecipe).open();
								} else if (newRecipe instanceof ItemClassImprovementRecipe){
									new ManageClassTinkerRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), (ItemClassImprovementRecipe) newRecipe).open();
								} else {
									new ManageCraftRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), (ItemCraftingRecipe) newRecipe).open();
								}
							}
							return true;
						}
					}
					if (CustomRecipeManager.getInstance().getShapedRecipes().containsKey(args[2])){
						if (args[3].equals(args[2]) || CustomRecipeManager.getInstance().getShapedRecipes().get(args[3]) != null) {
							Utils.sendMessage(sender, Utils.chat(error_recipe_exists));
							return true;
						}
						DynamicShapedRecipe recipe = CustomRecipeManager.getInstance().getDynamicShapedRecipe(args[2]);
						ShapedRecipe oldShapedRecipe = recipe.getRecipe();
						ShapedRecipe newShapedRecipe = new ShapedRecipe(new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_" + args[3]), oldShapedRecipe.getResult());

						Collection<DynamicItemModifier> modifiers = new HashSet<>();
						for (DynamicItemModifier m : recipe.getItemModifiers()){
							try {
								modifiers.add(m.clone());
							} catch (CloneNotSupportedException ignored){
							}
						}

						newShapedRecipe.shape(oldShapedRecipe.getShape());
						for (Character c : oldShapedRecipe.getIngredientMap().keySet()){
							if (c == ' ') continue;
							if (oldShapedRecipe.getIngredientMap().get(c) == null) continue;
							newShapedRecipe.setIngredient(c, oldShapedRecipe.getIngredientMap().get(c).getType());
						}

						DynamicShapedRecipe newRecipe = new DynamicShapedRecipe(args[3], newShapedRecipe, recipe.getExactItems(), recipe.isUseMetadata(), recipe.isRequireCustomTools(), recipe.isTinkerFirstItem(), modifiers);
						new ManageShapedRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), newRecipe).open();
					} else if (CustomRecipeManager.getInstance().getBrewingRecipes().containsKey(args[2])){
						if (args[3].equals(args[2]) || CustomRecipeManager.getInstance().getBrewingRecipes().get(args[3]) != null) {
							Utils.sendMessage(sender, Utils.chat(error_recipe_exists));
							return true;
						}
						DynamicBrewingRecipe recipe = CustomRecipeManager.getInstance().getBrewingRecipe(args[2]);
						Collection<DynamicItemModifier> modifiers = new HashSet<>();
						for (DynamicItemModifier m : recipe.getItemModifiers()){
							try {
								modifiers.add(m.clone());
							} catch (CloneNotSupportedException ignored){
							}
						}

						DynamicBrewingRecipe newRecipe = new DynamicBrewingRecipe(args[3], recipe.getIngredient(), recipe.getRequiredType(), recipe.isPerfectMeta(), modifiers);
						new ManageBrewingRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), newRecipe).open();
					} else {
						Utils.sendMessage(sender, Utils.chat(error_recipe_missing));
					}
				}
			}
			return true;
		}

		return false;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"valhalla.recipes"};
	}

	@Override
	public String getFailureMessage() {
		return "&c/valhalla recipes <new/edit/copy> [type | name | copyfrom] [name | | copyto] [display name | | display name]";
	}

	@Override
	public String getCommand() {
		return "/valhalla recipes <new/edit/copy> [type | name | copyfrom] [name | | copyto] [display name | | display name]";
	}

	@Override
	public String getDescription() {
		return recipes_description;
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		if (args.length == 2) return Arrays.asList("new", "edit", "copy");
		if (args.length == 3){
			if (args[1].equalsIgnoreCase("new")){
				return Arrays.asList("improvement", "craft", "shaped", "class_improvement", "brewing");
			} else if (args[1].equalsIgnoreCase("edit") || args[1].equalsIgnoreCase("copy")){
				List<String> returns = new ArrayList<>(CustomRecipeManager.getInstance().getAllCustomRecipes().keySet());
				returns.addAll(CustomRecipeManager.getInstance().getShapedRecipes().keySet());
				returns.addAll(CustomRecipeManager.getInstance().getBrewingRecipes().keySet());
				return returns;
			}
		}
		return null;
	}
}
