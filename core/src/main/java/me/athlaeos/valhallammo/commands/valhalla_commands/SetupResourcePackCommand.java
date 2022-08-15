
package me.athlaeos.valhallammo.commands.valhalla_commands;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.commands.Command;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.apache.commons.io.FileUtils;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class SetupResourcePackCommand implements Command {
	/*
	 * I copied most of this from EliteMobs source code, so code credit for this class goes to them
	 */
	private final String error_command_resourcepack;
	private final String status_command_resourcepack_enabled;
	private final String status_command_resourcepack_disabled;
	private final String description_command_resourcepack;
	private final String status_command_resourcepack_downloaded;
	private final String status_command_resourcepack_setup;

	public SetupResourcePackCommand(){
		error_command_resourcepack = TranslationManager.getInstance().getTranslation("error_command_resourcepack");
		status_command_resourcepack_enabled = TranslationManager.getInstance().getTranslation("status_command_resourcepack_enabled");
		status_command_resourcepack_disabled = TranslationManager.getInstance().getTranslation("status_command_resourcepack_disabled");
		description_command_resourcepack = TranslationManager.getInstance().getTranslation("description_command_resourcepack");
		status_command_resourcepack_downloaded = TranslationManager.getInstance().getTranslation("status_command_resourcepack_downloaded");
		status_command_resourcepack_setup = TranslationManager.getInstance().getTranslation("status_command_resourcepack_setup");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length < 2){
			return false;
		} else {
			if (args[1].equalsIgnoreCase("download")){
				URL fetchWebsite;
				try {
					fetchWebsite = new URL("https://download.mc-packs.net/pack/afab4e00e0021852242248b49e1c89e133481c31.zip");
				} catch (MalformedURLException e) {
					ValhallaMMO.getPlugin().getLogger().severe("Could not fetch resource pack from https://download.mc-packs.net/pack/afab4e00e0021852242248b49e1c89e133481c31.zip");
					sender.sendMessage(Utils.chat(error_command_resourcepack));
					return true;
				}
				File file = new File("plugins/ValhallaMMO/ValhallaMMO.zip");
				try {
					FileUtils.copyURLToFile(fetchWebsite, file);
				} catch (IOException e) {
					ValhallaMMO.getPlugin().getLogger().severe("Could not fetch resource pack from https://download.mc-packs.net/pack/afab4e00e0021852242248b49e1c89e133481c31.zip");
					sender.sendMessage(Utils.chat(error_command_resourcepack));
					return true;
				}
				sender.sendMessage(Utils.chat(status_command_resourcepack_downloaded));
			} else if (args[1].equalsIgnoreCase("setup")){
				URL fetchWebsite;
				try {
					fetchWebsite = new URL("https://download.mc-packs.net/pack/afab4e00e0021852242248b49e1c89e133481c31.zip");
				} catch (MalformedURLException e) {
					ValhallaMMO.getPlugin().getLogger().severe("Could not fetch resource pack from https://download.mc-packs.net/pack/afab4e00e0021852242248b49e1c89e133481c31.zip");
					sender.sendMessage(Utils.chat(error_command_resourcepack));
					return true;
				}
				File file = new File("plugins/ValhallaMMO/ValhallaMMO.zip");
				try {
					FileUtils.copyURLToFile(fetchWebsite, file);
				} catch (IOException e) {
					ValhallaMMO.getPlugin().getLogger().severe("Could not fetch resource pack from https://download.mc-packs.net/pack/afab4e00e0021852242248b49e1c89e133481c31.zip");
					sender.sendMessage(Utils.chat(error_command_resourcepack));
					return true;
				}
				String sha1;
				try {
					sha1 = sha1Code(file);
				} catch (Exception e) {
					ValhallaMMO.getPlugin().getLogger().severe("Could not fetch resource pack from https://download.mc-packs.net/pack/afab4e00e0021852242248b49e1c89e133481c31.zip");
					sender.sendMessage(Utils.chat(error_command_resourcepack));
					return true;
				}

				if (!modify("resource-pack-sha1", sha1)) {
					ValhallaMMO.getPlugin().getLogger().severe("Could not fetch resource pack from https://download.mc-packs.net/pack/afab4e00e0021852242248b49e1c89e133481c31.zip");
					sender.sendMessage(Utils.chat(error_command_resourcepack));
					return true;
				}

				if (!modify("resource-pack", "https://download.mc-packs.net/pack/afab4e00e0021852242248b49e1c89e133481c31.zip")) {
					ValhallaMMO.getPlugin().getLogger().severe("Could not fetch resource pack from https://download.mc-packs.net/pack/afab4e00e0021852242248b49e1c89e133481c31.zip");
					sender.sendMessage(Utils.chat(error_command_resourcepack));
					return true;
				}
				sender.sendMessage(Utils.chat(status_command_resourcepack_setup));
			} else {
				boolean enabled;
				if (args[1].equalsIgnoreCase("enable")) {
					enabled = true;
				} else if (args[1].equalsIgnoreCase("disable")) {
					enabled = false;
				} else return false;
				ValhallaMMO.setPackEnabled(enabled);
				sender.sendMessage(Utils.chat(enabled ? status_command_resourcepack_enabled : status_command_resourcepack_disabled));
			}
		}
		return true;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"valhalla.resourcepack"};
	}

	@Override
	public String getFailureMessage() {
		return "&c/valhalla resourcepack";
	}

	@Override
	public String getDescription() {
		return description_command_resourcepack;
	}

	@Override
	public String getCommand() {
		return "/valhalla resourcepack <download/enable/disable/setup>";
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		if (args.length == 2){
			return Arrays.asList("enable", "disable", "download", "setup");
		}
		return null;
	}

	private String sha1Code(File file) throws IOException, NoSuchAlgorithmException {
		FileInputStream fileInputStream = new FileInputStream(file);
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		DigestInputStream digestInputStream = new DigestInputStream(fileInputStream, digest);
		byte[] bytes = new byte[1024];
		// read all file content
		while (digestInputStream.read(bytes) > 0)
			digest = digestInputStream.getMessageDigest();
		byte[] resultByteArry = digest.digest();
		return bytesToHexString(resultByteArry);
	}

	private String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			int value = b & 0xFF;
			if (value < 16) {
				// if value less than 16, then it's hex String will be only
				// one character, so we need to append a character of '0'
				sb.append("0");
			}
			sb.append(Integer.toHexString(value).toUpperCase());
		}
		return sb.toString();
	}

	private boolean modify(String configKey, String configSetting) {
		File serverProperties = null;
		try {
			serverProperties = new File(Paths.get(ValhallaMMO.getPlugin().getDataFolder().getParentFile().getCanonicalFile().getParentFile().toString() + File.separatorChar + "server.properties").toString());
			if (!serverProperties.exists()) {
				ValhallaMMO.getPlugin().getLogger().severe("Could not find server.properties");
				return false;
			}
		} catch (Exception exception) {
			ValhallaMMO.getPlugin().getLogger().severe("Could not access server.properties");
			exception.printStackTrace();
			return false;
		}
		try {
			FileInputStream in = new FileInputStream(serverProperties);
			Properties props = new Properties();
			props.load(in);
			in.close();

			java.io.FileOutputStream out = new java.io.FileOutputStream(serverProperties);
			props.setProperty(configKey, configSetting);
			props.store(out, null);
			out.close();
		} catch (Exception ex) {
			ValhallaMMO.getPlugin().getLogger().severe("Could not write to server.properties");
			return false;
		}
		return true;
	}
}
