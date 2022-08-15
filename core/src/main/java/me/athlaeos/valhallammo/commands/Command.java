package me.athlaeos.valhallammo.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface Command {
	boolean execute(CommandSender sender, String[] args);
	String getFailureMessage();
	String[] getRequiredPermission();
	String getDescription();
	String getCommand();
	List<String> getSubcommandArgs(CommandSender sender, String[] args);
}
