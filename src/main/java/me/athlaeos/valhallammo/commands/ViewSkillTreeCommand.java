package me.athlaeos.valhallammo.commands;

import me.athlaeos.valhallammo.skills.smithing.menus.CreateCustomRecipeMenu;
import me.athlaeos.valhallammo.menus.PlayerMenuUtilManager;
import me.athlaeos.valhallammo.crafting.dom.ItemCraftingRecipe;
import me.athlaeos.valhallammo.crafting.dom.ItemImprovementRecipe;
import me.athlaeos.valhallammo.crafting.CustomRecipeManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ManageRecipesCommand implements Command {
	private String recipes_description;

	public ManageRecipesCommand(){
		recipes_description = Utils.chat("&7description");//ConfigManager.getInstance().getConfig("translations.yml").get().getString("menu_description");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 1){
			new CreateCustomRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender)).open();
			return true;
		} else if (args.length >= 3){
			if (CustomRecipeManager.getInstance().getAllRecipes().containsKey(args[2])){
				sender.sendMessage(Utils.chat("&cRecipe with this name already exists."));
				return true;
			}
			if (args[1].equalsIgnoreCase("improvement")){
				ItemImprovementRecipe recipe = new ItemImprovementRecipe(args[2], Material.WOODEN_SWORD, Material.CRAFTING_TABLE, new HashSet<>());
				new CreateCustomRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), recipe).open();
			} else if (args[1].equalsIgnoreCase("craft")){
				ItemCraftingRecipe recipe = new ItemCraftingRecipe(args[2], new ItemStack(Material.WOODEN_SWORD), Material.CRAFTING_TABLE, new HashSet<>());
				new CreateCustomRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender), recipe).open();
			} else if (args[1].equalsIgnoreCase("shaped")){
				sender.sendMessage(Utils.chat("&bWork in progress!"));
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
		return "&4/valhalla recipes";
	}

	@Override
	public String[] getHelpEntry() {
		return new String[]{
				Utils.chat("&8&m                                             "),
				Utils.chat("&d/valhalla recipes <type> <name>"),
				Utils.chat("&7" + recipes_description),
				Utils.chat("&7> &dvalhalla.recipes")
		};
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		if (args.length == 2) return Arrays.asList("improvement", "craft", "shaped");
		return null;
	}
}
