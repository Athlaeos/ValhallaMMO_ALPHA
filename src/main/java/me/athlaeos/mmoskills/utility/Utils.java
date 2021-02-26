package me.athlaeos.mmoskills.utility;

import me.athlaeos.mmoskills.managers.MinecraftVersionManager;
import me.athlaeos.mmoskills.domain.MinecraftVersion;
import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static String chat (String s) {
        if (MinecraftVersionManager.getInstance().currentVersionNewerThan(MinecraftVersion.MINECRAFT_1_16)){
            return newChat(s);
        } else {
            return oldChat(s);
        }
    }

    public static String oldChat(String message) {
        return ChatColor.translateAlternateColorCodes('&', message + "");
    }

    private static final Pattern HEX_PATTERN = Pattern.compile("&(#\\w{6})");

    public static String newChat(String message) {
        {
            Matcher matcher = HEX_PATTERN.matcher(message);
            StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
            while (matcher.find()) {
                String group = matcher.group(1);
                matcher.appendReplacement(buffer, ChatColor.COLOR_CHAR + "x"
                        + ChatColor.COLOR_CHAR + group.charAt(0) + ChatColor.COLOR_CHAR + group.charAt(1)
                        + ChatColor.COLOR_CHAR + group.charAt(2) + ChatColor.COLOR_CHAR + group.charAt(3)
                        + ChatColor.COLOR_CHAR + group.charAt(4) + ChatColor.COLOR_CHAR + group.charAt(5)
                );
            }
            return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
        }
    }
}
