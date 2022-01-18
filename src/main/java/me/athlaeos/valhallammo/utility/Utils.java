package me.athlaeos.mmoskills.utility;

import me.athlaeos.mmoskills.managers.MinecraftVersionManager;
import me.athlaeos.mmoskills.domain.MinecraftVersion;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }

    public static List<Location> getCubeWithLines(Location center, int lineDensity, double radius){
        List<Location> square = new ArrayList<>();

        Location p1 = new Location(center.getWorld(), center.getX()-radius, center.getY()-radius, center.getZ()-radius);
        Location p2 = new Location(center.getWorld(), center.getX()-radius, center.getY()-radius, center.getZ()+radius);
        Location p3 = new Location(center.getWorld(), center.getX()-radius, center.getY()+radius, center.getZ()-radius);
        Location p4 = new Location(center.getWorld(), center.getX()-radius, center.getY()+radius, center.getZ()+radius);
        Location p5 = new Location(center.getWorld(), center.getX()+radius, center.getY()-radius, center.getZ()-radius);
        Location p6 = new Location(center.getWorld(), center.getX()+radius, center.getY()-radius, center.getZ()+radius);
        Location p7 = new Location(center.getWorld(), center.getX()+radius, center.getY()+radius, center.getZ()-radius);
        Location p8 = new Location(center.getWorld(), center.getX()+radius, center.getY()+radius, center.getZ()+radius);

        square.addAll(Utils.getPointsInLine(p1, p2, lineDensity));
        square.addAll(Utils.getPointsInLine(p1, p3, lineDensity));
        square.addAll(Utils.getPointsInLine(p2, p4, lineDensity));
        square.addAll(Utils.getPointsInLine(p3, p4, lineDensity));
        square.addAll(Utils.getPointsInLine(p5, p6, lineDensity));
        square.addAll(Utils.getPointsInLine(p5, p7, lineDensity));
        square.addAll(Utils.getPointsInLine(p6, p8, lineDensity));
        square.addAll(Utils.getPointsInLine(p7, p8, lineDensity));
        square.addAll(Utils.getPointsInLine(p1, p5, lineDensity));
        square.addAll(Utils.getPointsInLine(p2, p6, lineDensity));
        square.addAll(Utils.getPointsInLine(p3, p7, lineDensity));
        square.addAll(Utils.getPointsInLine(p4, p8, lineDensity));

        return square;
    }

    public static List<Location> getSquareWithLines(Location center, int lineDensity, double radius){
        List<Location> square = new ArrayList<>();

        Location p1 = new Location(center.getWorld(), center.getX()-radius, center.getY(), center.getZ()-radius);
        Location p2 = new Location(center.getWorld(), center.getX()-radius, center.getY(), center.getZ()+radius);
        Location p3 = new Location(center.getWorld(), center.getX()+radius, center.getY(), center.getZ()-radius);
        Location p4 = new Location(center.getWorld(), center.getX()+radius, center.getY(), center.getZ()+radius);

        square.addAll(Utils.getPointsInLine(p1, p2, lineDensity));
        square.addAll(Utils.getPointsInLine(p1, p3, lineDensity));
        square.addAll(Utils.getPointsInLine(p2, p4, lineDensity));
        square.addAll(Utils.getPointsInLine(p3, p4, lineDensity));

        return square;
    }

    public static List<Location> getPointsInLine(Location point1, Location point2, int amount){
        double xStep = (point1.getX() - point2.getX()) / amount;
        double yStep = (point1.getY() - point2.getY()) / amount;
        double zStep = (point1.getZ() - point2.getZ()) / amount;
        List<Location> points = new ArrayList<>();
        for (int i = 0; i < amount + 1; i++){
            points.add(new Location(
                    point1.getWorld(),
                    point1.getX() - xStep * i,
                    point1.getY() - yStep * i,
                    point1.getZ() - zStep * i));
        }
        return points;
    }

    public static List<Location> transformPoints(Location center, List<Location> points, double yaw, double pitch, double roll, double scale) {
        // Convert to radians
        yaw = Math.toRadians(yaw);
        pitch = Math.toRadians(pitch);
        roll = Math.toRadians(roll);
        List<Location> list = new ArrayList<>();

        // Store the values so we don't have to calculate them again for every single point.
        double cp = Math.cos(pitch);
        double sp = Math.sin(pitch);
        double cy = Math.cos(yaw);
        double sy = Math.sin(yaw);
        double cr = Math.cos(roll);
        double sr = Math.sin(roll);
        double x, bx, y, by, z, bz;
        for (Location point : points) {
            x = point.getX() - center.getX();
            bx = x;
            y = point.getY() - center.getY();
            by = y;
            z = point.getZ() - center.getZ();
            bz = z;
            x = ((x*cy-bz*sy)*cr+by*sr)*scale;
            y = ((y*cp+bz*sp)*cr-bx*sr)*scale;
            z = ((z*cp-by*sp)*cy+bx*sy)*scale;
            list.add(new Location(point.getWorld(), (center.getX()+x), (center.getY()+y), (center.getZ()+z)));
        }
        return list;
    }

    public static List<String> seperateStringIntoLines(String string, int maxLength, String linePrefix){
        List<String> lines = new ArrayList<>();
        String[] words = string.split(" ");
        if (words.length == 0) return lines;
        StringBuilder word = new StringBuilder();
        for (String s : words){
            if (word.length() + s.length() > maxLength || s.contains("\n")){
                lines.add(word.toString());
                word = new StringBuilder();
                word.append(Utils.chat(linePrefix)).append(s);
            } else if (words[0].equals(s)){
                word.append(s);
            } else {
                word.append(" ").append(s);
            }
            if (words[words.length - 1].equals(s)){
                lines.add(word.toString());
            }
        }
        return lines;
    }

    public static Map<Integer, ArrayList<ItemStack>> paginateItemStackList(int pageSize, List<ItemStack> allEntries) {
        Map<Integer, ArrayList<ItemStack>> pages = new HashMap<>();
        int stepper = 0;

        for (int pageNumber = 0; pageNumber < Math.ceil((double)allEntries.size()/(double)pageSize); pageNumber++) {
            ArrayList<ItemStack> pageEntries = new ArrayList<>();
            for (int pageEntry = 0; pageEntry < pageSize && stepper < allEntries.size(); pageEntry++, stepper++) {
                pageEntries.add(allEntries.get(stepper));
            }
            pages.put(pageNumber, pageEntries);
        }
        return pages;
    }

    public static ItemStack createItemStack(Material material, String displayName, List<String> lore){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(displayName);
        if (lore != null){
            List<String> coloredLore = new ArrayList<>();
            for (String l : lore){
                coloredLore.add(Utils.chat(l));
            }
            meta.setLore(coloredLore);
        }
        item.setItemMeta(meta);
        return item;
    }
}
