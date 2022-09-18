package me.athlaeos.valhallammo.commands;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.commands.valhalla_commands.*;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValhallaCommandManager implements TabExecutor {
	
	private final ValhallaMMO plugin;
	private final Map<String, Command> commands = new HashMap<>();
	private final String invalid_command;
	private final String error_no_permission;
	private static ValhallaCommandManager manager = null;

	public ValhallaCommandManager(ValhallaMMO plugin) {
		this.plugin = plugin;
		invalid_command = TranslationManager.getInstance().getTranslation("error_command_invalid_command");
		error_no_permission = TranslationManager.getInstance().getTranslation("error_command_no_permission");

		commands.put("help", new HelpCommand());
		commands.put("recipes", new ManageRecipesCommand());
		commands.put("profile", new ProfileStatsCommand());
		commands.put("reset", new ResetProfilesCommand());
		commands.put("exp", new EXPCommand());
		commands.put("skills", new ViewSkillTreeCommand());
		commands.put("modify", new ItemModifierCommand());
		commands.put("reward", new PerkRewardCommand());
		commands.put("loot", new ManageLootTablesCommand());
		commands.put("globalbuff", new GlobalEffectCommand());
		commands.put("givebook", new GiveBookCommand());
		commands.put("reload", new ReloadCommand());
		commands.put("import", new ImportCommand());
		commands.put("itemindex", new ItemDictionaryCommand());
		commands.put("resourcepack", new SetupResourcePackCommand());
		commands.put("toggleexp", new HideBossBarsCommand());

	    ((HelpCommand) commands.get("help")).giveCommandMap(commands);

		PluginCommand command = plugin.getCommand("valhalla");
		assert command != null;
		command.setExecutor(this);
	}

	public static ValhallaCommandManager getInstance(){
		if (manager == null){
			manager = new ValhallaCommandManager(ValhallaMMO.getPlugin());
		}
		return manager;
	}

	public void reload(){
		manager = new ValhallaCommandManager(ValhallaMMO.getPlugin());
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String name, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(Utils.chat(String.format("&eValhallaMMO v%s by Athlaeos", plugin.getDescription().getVersion())));
			sender.sendMessage(Utils.chat("&7/val help"));
			return true;
		}
		
		for (String subCommand : commands.keySet()) {
			if (args[0].equalsIgnoreCase(subCommand)) {
				boolean hasPermission = false;
				if (commands.get(subCommand).getRequiredPermission().length == 0) {
					hasPermission = true;
				} else {
					for (String permission : commands.get(subCommand).getRequiredPermission()){
						if (sender.hasPermission(permission)){
							hasPermission = true;
							break;
						}
					}
				}
				if (!hasPermission){
					Utils.sendMessage(sender, Utils.chat(error_no_permission));
					return true;
				}
				if (!commands.get(subCommand).execute(sender, args)) {
					Utils.sendMessage(sender, Utils.chat(commands.get(subCommand).getFailureMessage()));
				}
				return true;
			}
		}
		Utils.sendMessage(sender, Utils.chat(invalid_command));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String name, String[] args) {
		if (args.length == 1) {
			List<String> allowedCommands = new ArrayList<>();
			for (String arg : commands.keySet()) {
				Command command = commands.get(arg);
				if (command.getRequiredPermission().length == 0) {
					allowedCommands.add(arg);
				} else {
					for (String permission : command.getRequiredPermission()){
						if (sender.hasPermission(permission)) {
							allowedCommands.add(arg);
						}
					}
				}
			}
			return allowedCommands;
		} else if (args.length > 1) {
			commandLoop:
			for (String arg : commands.keySet()) {
				Command command = commands.get(arg);
				for (String permission : command.getRequiredPermission()){
					if (!sender.hasPermission(permission)) continue commandLoop;
				}
				if (args[0].equalsIgnoreCase(arg)) {
					return commands.get(arg).getSubcommandArgs(sender, args);
				}
			}
		}
		return null;
	}
}
