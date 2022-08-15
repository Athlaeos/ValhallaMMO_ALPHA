package me.athlaeos.valhallammo.commands.valhalla_commands;

import me.athlaeos.valhallammo.commands.Command;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HelpCommand implements Command {
	private final String invalid_number;
	private final String help_description;
	private final List<String> help_format;

	public HelpCommand(){
		invalid_number = TranslationManager.getInstance().getTranslation("error_command_invalid_number");
		help_description = TranslationManager.getInstance().getTranslation("description_command_help");
		help_format = TranslationManager.getInstance().getList("command_help_format");
	}

	private List<Command> commands = new ArrayList<>();

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Map<Integer, ArrayList<String>> helpCommandList;
		List<String> helpLines = new ArrayList<>();
		
		for (Command c : commands) {
			for (String permission : c.getRequiredPermission()) {
				if (sender.hasPermission(permission)) {
					for (String line : help_format){
						helpLines.add(line
								.replace("%description%", c.getDescription())
								.replace("%permissions%", String.join("|", c.getRequiredPermission()))
								.replace("%command%", c.getCommand()));
					}
					break;
				}
			}
		}

		helpCommandList = Utils.paginateTextList(help_format.size() * 3, helpLines);
		
		if (helpCommandList.size() == 0) {
			return true;
		}
		
		// args[0] is "help" and args.length > 0
		if (args.length == 1) {
			for (String line : helpCommandList.get(0)) {
				sender.sendMessage(Utils.chat(line));
			}
			Utils.chat("&8&m                                             ");
			sender.sendMessage(Utils.chat(String.format("&8[&e1&8/&e%s&8]", helpCommandList.size())));
			return true;
		}

		if (args.length == 2) {
			try {
				Integer.parseInt(args[1]);
			} catch (NumberFormatException nfe) {
				Utils.sendMessage(sender, Utils.chat(invalid_number));
				return true;
			}

			int pageNumber = Integer.parseInt(args[1]);
			if (pageNumber < 1) {
				pageNumber = 1;
			}
			if (pageNumber > helpCommandList.size()) {
				pageNumber = helpCommandList.size();
			}
			
			for (String entry : helpCommandList.get(pageNumber - 1)) {
				sender.sendMessage(Utils.chat(entry));
			}
			Utils.chat("&8&m                                             ");
			sender.sendMessage(Utils.chat(String.format("&8[&e%s&8/&e%s&8]", pageNumber, helpCommandList.size())));
			return true;
		}

		return false;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"valhalla.help"};
	}

	@Override
	public String getFailureMessage() {
		return "&c/valhalla help <page>";
	}

	@Override
	public String getCommand() {
		return "/valhalla help <page>";
	}

	@Override
	public String getDescription() {
		return help_description;
	}

	public void giveCommandMap(Map<String, Command> commandMap) {
		commands = new ArrayList<>(commandMap.values());
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		if (args.length == 2) {
			List<String> subargs = new ArrayList<>();
			subargs.add("1");
			subargs.add("2");
			subargs.add("3");
			subargs.add("...");
			return subargs;
		}
		List<String> subargs = new ArrayList<>();
		subargs.add(" ");
		return subargs;
	}
}
