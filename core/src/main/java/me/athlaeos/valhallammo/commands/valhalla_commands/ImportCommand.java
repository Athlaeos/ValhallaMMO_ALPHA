package me.athlaeos.valhallammo.commands.valhalla_commands;

import me.athlaeos.valhallammo.commands.Command;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.crafting.recipetypes.*;
import me.athlaeos.valhallammo.managers.CooldownManager;
import me.athlaeos.valhallammo.managers.CustomRecipeManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class ImportCommand implements Command {
	private final String error_command_import_file_not_found;
	private final String warning_command_import;
	private final String status_command_import_success;
	private final String error_command_import_no_path_given;
	private final String description_command_import;
	private Long timeConsoleAttemptedReset = 0L;

	public ImportCommand(){
		error_command_import_file_not_found = TranslationManager.getInstance().getTranslation("error_command_import_file_not_found");
		warning_command_import = TranslationManager.getInstance().getTranslation("warning_command_import");
		status_command_import_success = TranslationManager.getInstance().getTranslation("status_command_import_success");
		error_command_import_no_path_given = TranslationManager.getInstance().getTranslation("error_command_import_no_path_given");
		description_command_import = TranslationManager.getInstance().getTranslation("description_command_import");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length <= 2){
			sender.sendMessage(Utils.chat(error_command_import_no_path_given));
			return true;
		} else {
			if (sender instanceof Player){
				if (CooldownManager.getInstance().isCooldownPassed(((Player) sender).getUniqueId(), "import_command_attempt")){
					sender.sendMessage(Utils.chat(warning_command_import));
					CooldownManager.getInstance().setCooldown(((Player) sender).getUniqueId(), 10000, "import_command_attempt");
					return true;
				}
			} else {
				if (timeConsoleAttemptedReset <= System.currentTimeMillis()){
					sender.sendMessage(Utils.chat(warning_command_import));
					timeConsoleAttemptedReset = System.currentTimeMillis() + 10000;
					return true;
				}
			}

			boolean overwrite;
			if (args[1].equalsIgnoreCase("true")){
				overwrite = true;
			} else if (args[1].equalsIgnoreCase("false")){
				overwrite = false;
			} else return false;

			YamlConfiguration config = ConfigManager.getInstance().getConfig(args[2]).get();
			ConfigurationSection section = config.getConfigurationSection("");
			if (section != null){
				Set<String> keys = section.getKeys(false);
				for (String type : keys){
					switch (type){
						case "brewing":{
							Collection<DynamicBrewingRecipe> recipes = CustomRecipeManager.getInstance().getBrewingRecipesFromConfig(config);
							for (DynamicBrewingRecipe recipe : recipes){
								if (overwrite){
									DynamicBrewingRecipe existingRecipe = CustomRecipeManager.getInstance().getBrewingRecipe(recipe.getName());
									if (existingRecipe != null){
										CustomRecipeManager.getInstance().unregister(existingRecipe);
									}
								}
								CustomRecipeManager.getInstance().register(recipe);
							}
							break;
						}
						case "cooking":{
							Collection<DynamicCookingRecipe<?>> recipes = CustomRecipeManager.getInstance().getCookingRecipesFromConfig(config);
							for (DynamicCookingRecipe<?> recipe : recipes){
								if (overwrite){
									DynamicCookingRecipe<?> existingRecipe = CustomRecipeManager.getInstance().getCookingRecipes().get(recipe.getName());
									if (existingRecipe != null){
										CustomRecipeManager.getInstance().unregister(existingRecipe);
									}
								}
								CustomRecipeManager.getInstance().register(recipe);
							}
							break;
						}
						case "shaped":{
							Collection<DynamicShapedRecipe> recipes = CustomRecipeManager.getInstance().getDynamicShapedRecipesFromConfig(config);
							for (DynamicShapedRecipe recipe : recipes){
								if (overwrite){
									DynamicShapedRecipe existingRecipe = CustomRecipeManager.getInstance().getDynamicShapedRecipe(recipe.getName());
									if (existingRecipe != null){
										CustomRecipeManager.getInstance().unregister(existingRecipe);
									}
								}
								CustomRecipeManager.getInstance().register(recipe);
							}
							break;
						}
						case "craft":
						case "class_improve":
						case "improve":{
							Collection<AbstractCustomCraftingRecipe> recipes = new HashSet<>(CustomRecipeManager.getInstance().getItemCraftingRecipesFromConfig(config));
							for (AbstractCustomCraftingRecipe recipe : recipes){
								if (overwrite){
									AbstractCustomCraftingRecipe existingRecipe = CustomRecipeManager.getInstance().getRecipeByName(recipe.getName());
									if (existingRecipe != null){
										CustomRecipeManager.getInstance().unregister(existingRecipe);
									}
								}
								CustomRecipeManager.getInstance().register(recipe);
							}
							break;
						}
						default:{
							sender.sendMessage(Utils.chat(error_command_import_file_not_found));
							return true;
						}
					}
				}
				if (!keys.isEmpty()){
					sender.sendMessage(Utils.chat(status_command_import_success));
					return true;
				}
			}
			sender.sendMessage(Utils.chat(error_command_import_file_not_found));
		}
		return true;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"valhalla.recipes"};
	}

	@Override
	public String getFailureMessage() {
		return "&c/valhalla import";
	}

	@Override
	public String getDescription() {
		return description_command_import;
	}

	@Override
	public String getCommand() {
		return "/valhalla import <overwrite> <file_name>.yml";
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		if (args.length == 2){
			return Arrays.asList("true", "false");
		} else if (args.length == 3){
			return Collections.singletonList(args[2] + ".yml");
		}
		return null;
	}
}
