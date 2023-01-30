package me.athlaeos.valhallammo.commands.valhalla_commands;

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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
						DynamicCraftingTableRecipe recipe = new DynamicCraftingTableRecipe(args[3], new ItemStack(Material.WOODEN_SWORD), new HashMap<>(), true, true, true, false, false, false, new ArrayList<>());
						new ManageShapedRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), recipe).open();
					} else if (args[2].equalsIgnoreCase("cauldron")){
						DynamicCauldronRecipe recipe = new DynamicCauldronRecipe(args[3], new HashSet<>(), new ItemStack(Material.DIRT), new ItemStack(Material.CLAY), true, true, true, false, false, true, new ArrayList<>());
						new ManageCauldronRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), recipe).open();
					} else if (args[2].equalsIgnoreCase("smithing")){
						DynamicSmithingTableRecipe recipe = new DynamicSmithingTableRecipe(args[3], new ItemStack(Material.NETHERITE_PICKAXE), new ItemStack(Material.DIAMOND_PICKAXE), new ItemStack(Material.NETHERITE_INGOT), false, false, true, false, true, false, false, new ArrayList<>(), new ArrayList<>());
						new ManageSmithingRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), recipe).open();
					} else if (args[2].equalsIgnoreCase("campfire")){
						DynamicCampfireRecipe recipe = new DynamicCampfireRecipe(args[3], new ItemStack(Material.IRON_ORE), new ItemStack(Material.IRON_INGOT), 0, 600, 0, false, false, false, new ArrayList<>());
						new ManageCookingRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), ManageCookingRecipeMenu.RecipeType.CAMPFIRE, recipe).open();
					} else if (args[2].equalsIgnoreCase("furnace")){
						DynamicFurnaceRecipe recipe = new DynamicFurnaceRecipe(args[3], new ItemStack(Material.IRON_ORE), new ItemStack(Material.IRON_INGOT), 400, 0, false, false, false, new ArrayList<>());
						new ManageCookingRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), ManageCookingRecipeMenu.RecipeType.FURNACE, recipe).open();
					} else if (args[2].equalsIgnoreCase("blast_furnace")){
						DynamicBlastingRecipe recipe = new DynamicBlastingRecipe(args[3], new ItemStack(Material.IRON_ORE), new ItemStack(Material.IRON_INGOT), 200, 0, false, false, false, new ArrayList<>());
						new ManageCookingRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), ManageCookingRecipeMenu.RecipeType.BLAST_FURNACE, recipe).open();
					} else if (args[2].equalsIgnoreCase("smoker")){
						DynamicSmokingRecipe recipe = new DynamicSmokingRecipe(args[3], new ItemStack(Material.PORKCHOP), new ItemStack(Material.COOKED_PORKCHOP), 200, 0, false, false, false, new ArrayList<>());
						new ManageCookingRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), ManageCookingRecipeMenu.RecipeType.SMOKER, recipe).open();
					} else if (args[2].equalsIgnoreCase("brewing")){
						DynamicBrewingRecipe recipe = new DynamicBrewingRecipe(args[3], null, null, false, new ArrayList<>());
						List<DynamicItemModifier> modifiers = new ArrayList<>();
						DynamicItemModifier modifier = DynamicItemModifierManager.getInstance().createModifier("exp_bonus_alchemy", 100, ModifierPriority.LAST);
						if (modifier != null){
							modifiers.add(modifier);
						}
						DynamicItemModifier.sortModifiers(modifiers);
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
					DynamicCraftingTableRecipe recipe = CustomRecipeManager.getInstance().getDynamicShapedRecipe(args[2]);
					new ManageShapedRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), recipe).open();
				} else if (CustomRecipeManager.getInstance().getCauldronRecipes().containsKey(args[2])){
					DynamicCauldronRecipe recipe = CustomRecipeManager.getInstance().getCauldronRecipes().get(args[2]);
					new ManageCauldronRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), recipe).open();
				} else if (CustomRecipeManager.getInstance().getSmithingRecipes().containsKey(args[2])){
					DynamicSmithingTableRecipe recipe = CustomRecipeManager.getInstance().getDynamicSmithingRecipe(args[2]);
					new ManageSmithingRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), recipe).open();
				} else if (CustomRecipeManager.getInstance().getCookingRecipes().containsKey(args[2])){
					DynamicCookingRecipe<?> recipe = CustomRecipeManager.getInstance().getCookingRecipes().get(args[2]);
					if (recipe instanceof DynamicCampfireRecipe){
						new ManageCookingRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), ManageCookingRecipeMenu.RecipeType.CAMPFIRE, recipe).open();
					} else if (recipe instanceof DynamicFurnaceRecipe){
						new ManageCookingRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), ManageCookingRecipeMenu.RecipeType.FURNACE, recipe).open();
					} else if (recipe instanceof DynamicBlastingRecipe){
						new ManageCookingRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), ManageCookingRecipeMenu.RecipeType.BLAST_FURNACE, recipe).open();
					} else if (recipe instanceof DynamicSmokingRecipe){
						new ManageCookingRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), ManageCookingRecipeMenu.RecipeType.SMOKER, recipe).open();
					}
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
							List<DynamicItemModifier> modifiers = new ArrayList<>();
							for (DynamicItemModifier m : recipe.getItemModifiers()){
								try {
									modifiers.add(m.clone());
								} catch (CloneNotSupportedException ignored){
								}
							}
							DynamicItemModifier.sortModifiers(modifiers);

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
						if (args[3].equals(args[2]) || CustomRecipeManager.getInstance().recipeExists(args[3])) {
							Utils.sendMessage(sender, Utils.chat(error_recipe_exists));
							return true;
						}
						DynamicCraftingTableRecipe recipe = CustomRecipeManager.getInstance().getDynamicShapedRecipe(args[2]);
						List<DynamicItemModifier> modifiers = new ArrayList<>();
						for (DynamicItemModifier m : recipe.getItemModifiers()){
							try {
								modifiers.add(m.clone());
							} catch (CloneNotSupportedException ignored){
							}
						}
						DynamicItemModifier.sortModifiers(modifiers);
						DynamicCraftingTableRecipe newRecipe = new DynamicCraftingTableRecipe(args[3], recipe.getResult(), recipe.getExactItems(), recipe.isUnlockedForEveryone(), recipe.isUseMetadata(), recipe.isAllowIngredientVariations(), recipe.isRequireCustomTools(), recipe.isTinkerFirstItem(), recipe.isShapeless(), modifiers);
						new ManageShapedRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), newRecipe).open();
					} else if (CustomRecipeManager.getInstance().getSmithingRecipes().containsKey(args[2])){
						if (args[3].equals(args[2]) || CustomRecipeManager.getInstance().recipeExists(args[3])) {
							Utils.sendMessage(sender, Utils.chat(error_recipe_exists));
							return true;
						}
						DynamicSmithingTableRecipe recipe = CustomRecipeManager.getInstance().getDynamicSmithingRecipe(args[2]);
						List<DynamicItemModifier> resultModifiers = new ArrayList<>();
						for (DynamicItemModifier m : recipe.getModifiersResult()){
							try {
								resultModifiers.add(m.clone());
							} catch (CloneNotSupportedException ignored){
							}
						}
						DynamicItemModifier.sortModifiers(resultModifiers);
						List<DynamicItemModifier> additionModifiers = new ArrayList<>();
						for (DynamicItemModifier m : recipe.getModifiersAddition()){
							try {
								additionModifiers.add(m.clone());
							} catch (CloneNotSupportedException ignored){
							}
						}
						DynamicItemModifier.sortModifiers(additionModifiers);
						DynamicSmithingTableRecipe newRecipe = new DynamicSmithingTableRecipe(args[3], recipe.getResult(), recipe.getBase(), recipe.getAddition(), recipe.isUseMetaBase(), recipe.isUseMetaAddition(), recipe.requireCustomTools(), recipe.isTinkerBase(), recipe.isConsumeAddition(), recipe.isAllowBaseVariations(), recipe.isAllowAdditionVariations(), recipe.getModifiersResult(), recipe.getModifiersAddition());
						newRecipe.setUnlockedForEveryone(recipe.isUnlockedForEveryone());
						new ManageSmithingRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), newRecipe).open();
					} else if (CustomRecipeManager.getInstance().getCauldronRecipes().containsKey(args[2])){
						if (args[3].equals(args[2]) || CustomRecipeManager.getInstance().recipeExists(args[3])) {
							Utils.sendMessage(sender, Utils.chat(error_recipe_exists));
							return true;
						}
						DynamicCauldronRecipe recipe = CustomRecipeManager.getInstance().getCauldronRecipes().get(args[2]);
						List<DynamicItemModifier> resultModifiers = new ArrayList<>();
						for (DynamicItemModifier m : recipe.getItemModifiers()){
							try {
								resultModifiers.add(m.clone());
							} catch (CloneNotSupportedException ignored){
							}
						}
						DynamicItemModifier.sortModifiers(resultModifiers);
						DynamicCauldronRecipe newRecipe = new DynamicCauldronRecipe(args[3], recipe.getIngredients(), recipe.getCatalyst(), recipe.getResult(), recipe.isIngredientsExactMeta(), recipe.isCatalystExactMeta(), recipe.isRequireCustomCatalyst(), recipe.isTinkerCatalyst(), recipe.isRequiresBoilingWater(), recipe.isConsumesWaterLevel(), recipe.getItemModifiers());
						newRecipe.setUnlockedForEveryone(recipe.isUnlockedForEveryone());
						new ManageCauldronRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), newRecipe).open();
					} else if (CustomRecipeManager.getInstance().getCookingRecipes().containsKey(args[2])){
						if (args[3].equals(args[2]) || CustomRecipeManager.getInstance().recipeExists(args[3])) {
							Utils.sendMessage(sender, Utils.chat(error_recipe_exists));
							return true;
						}
						DynamicCookingRecipe<?> recipe = CustomRecipeManager.getInstance().getCookingRecipes().get(args[2]);
						List<DynamicItemModifier> modifiers = new ArrayList<>();
						for (DynamicItemModifier m : recipe.getModifiers()){
							try {
								modifiers.add(m.clone());
							} catch (CloneNotSupportedException ignored){
							}
						}
						DynamicItemModifier.sortModifiers(modifiers);
						DynamicCookingRecipe<?> newRecipe = null;
						if (recipe instanceof DynamicCampfireRecipe){
							DynamicCampfireRecipe r = (DynamicCampfireRecipe) recipe;
							newRecipe = new DynamicCampfireRecipe(args[3], r.getInput(), r.getResult(), r.getCampfireMode(), r.getCookTime(), r.getExperience(), r.isTinkerInput(), r.isUseMetadata(), r.requiresCustomTool(), modifiers);
							new ManageCookingRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), ManageCookingRecipeMenu.RecipeType.CAMPFIRE, newRecipe).open();
						} else if (recipe instanceof DynamicFurnaceRecipe){
							newRecipe = new DynamicFurnaceRecipe(args[3], recipe.getInput(), recipe.getResult(), recipe.getCookTime(), recipe.getExperience(), recipe.isTinkerInput(), recipe.isUseMetadata(), recipe.requiresCustomTool(), modifiers);
							new ManageCookingRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), ManageCookingRecipeMenu.RecipeType.FURNACE, newRecipe).open();
						} else if (recipe instanceof DynamicBlastingRecipe){
							newRecipe = new DynamicBlastingRecipe(args[3], recipe.getInput(), recipe.getResult(), recipe.getCookTime(), recipe.getExperience(), recipe.isTinkerInput(), recipe.isUseMetadata(), recipe.requiresCustomTool(), modifiers);
							new ManageCookingRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), ManageCookingRecipeMenu.RecipeType.BLAST_FURNACE, newRecipe).open();
						} else if (recipe instanceof DynamicSmokingRecipe){
							newRecipe = new DynamicSmokingRecipe(args[3], recipe.getInput(), recipe.getResult(), recipe.getCookTime(), recipe.getExperience(), recipe.isTinkerInput(), recipe.isUseMetadata(), recipe.requiresCustomTool(), modifiers);
							new ManageCookingRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), ManageCookingRecipeMenu.RecipeType.SMOKER, newRecipe).open();
						}
					} else if (CustomRecipeManager.getInstance().getBrewingRecipes().containsKey(args[2])){
						if (args[3].equals(args[2]) || CustomRecipeManager.getInstance().recipeExists(args[3])) {
							Utils.sendMessage(sender, Utils.chat(error_recipe_exists));
							return true;
						}
						DynamicBrewingRecipe recipe = CustomRecipeManager.getInstance().getBrewingRecipe(args[2]);
						List<DynamicItemModifier> modifiers = new ArrayList<>();
						for (DynamicItemModifier m : recipe.getItemModifiers()){
							try {
								modifiers.add(m.clone());
							} catch (CloneNotSupportedException ignored){
							}
						}
						DynamicItemModifier.sortModifiers(modifiers);

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
				return Arrays.asList("improvement", "craft", "shaped", "class_improvement", "brewing", "campfire", "furnace", "blast_furnace", "smoker", "smithing", "cauldron");
			} else if (args[1].equalsIgnoreCase("edit") || args[1].equalsIgnoreCase("copy")){
				return new ArrayList<>(CustomRecipeManager.getInstance().getAllRecipes());
			}
		}
		return null;
	}
}
