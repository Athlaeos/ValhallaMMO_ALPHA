package me.athlaeos.valhallammo.utility;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.Action;
import me.athlaeos.valhallammo.dom.MinecraftVersion;
import me.athlaeos.valhallammo.dom.Offset;
import me.athlaeos.valhallammo.events.BlockDropItemStackEvent;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.managers.MinecraftVersionManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {

    private static Random random = null;

    public static Random getRandom(){
        if (random == null){
            random = new Random();
        }
        return random;
    }

    private final static TreeMap<Integer, String> map = new TreeMap<>();

    private static void populateRomanMap(){
        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");
    }

    public static boolean withinManhattanRange(Location l1, Location l2, double range){
        double distX = l1.getX() - l2.getX();
        double distY = l1.getY() - l2.getY();
        double distZ = l1.getZ() - l2.getZ();
        distX = (distX < 0) ? -distX : distX;
        distY = (distY < 0) ? -distY : distY;
        distZ = (distZ < 0) ? -distZ : distZ;
        return distX <= range && distY <= range && distZ <= range;
    }

    public static Map<String, OfflinePlayer> getPlayersFromUUIDCollection(Collection<UUID> uuids){
        Map<String, OfflinePlayer> players = new HashMap<>();
        for (UUID uuid : uuids){
            OfflinePlayer player = ValhallaMMO.getPlugin().getServer().getOfflinePlayer(uuid);
            players.put(player.getName(), player);
        }
        return players;
    }

//    public static double fastSqrt(double d){
//        double result1 = Double.longBitsToDouble( ( ( Double.doubleToLongBits( d )-(1L<<52) )>>1 ) + ( 1L<<61 ) );
//        double newton1 = (result1 + d/result1)*0.5;
//        double result2 = Double.longBitsToDouble( ( ( Double.doubleToLongBits( newton1 )-(1L<<52) )>>1 ) + ( 1L<<61 ) );
//        double newton2 = (result2 + d/result2)*0.5;
//        double r3 = Double.longBitsToDouble( ( ( Double.doubleToLongBits( newton2 )-(1L<<52) )>>1 ) + ( 1L<<61 ) );
//        return r3;
//    }

    public static String toRoman(int number) {
        if (number == 0) return "0";
        if (number == 1) return "I";
        if (map.isEmpty()) {
            populateRomanMap();
        }
        int l =  map.floorKey(number);
        if ( number == l ) {
            return map.get(number);
        }
        return map.get(l) + toRoman(number-l);
    }

    /**
     * Calls the appropriate events. These items will not be called in the drop event, so this is one way of rewarding the
     * player for items that weren't dropped as such.
     * @param player the player to break the block
     * @param block the block to break
     */
    public static void breakBlock(Player player, Block block){
        if (MinecraftVersionManager.getInstance().currentVersionOlderThan(MinecraftVersion.MINECRAFT_1_16)){
            // try to break a block and call the appropriate events
            ItemStack tool;
            if (!isItemEmptyOrNull(player.getInventory().getItemInMainHand())) {
                tool = player.getInventory().getItemInMainHand();
            } else {
                tool = player.getInventory().getItemInOffHand();
                if (isItemEmptyOrNull(tool)) tool = null;
            }
            BlockBreakEvent breakEvent = new BlockBreakEvent(block, player);
            ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(breakEvent);
            if (breakEvent.isCancelled()) return;

            List<ItemStack> drops = new ArrayList<>((tool == null) ? block.getDrops() : block.getDrops(tool, player));
            List<Item> items = drops.stream().map(i -> block.getWorld().dropItemNaturally(block.getLocation(), i)).collect(Collectors.toList());
            BlockDropItemEvent event = new BlockDropItemEvent(block, block.getState(), player, new ArrayList<>(items));
            ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()){
                items.forEach(Item::remove);
            } else {
                items.forEach(i -> {
                    if (!event.getItems().contains(i)){
                        // if the event drops do not contain the original item, remove it
                        i.remove();
                    }
                });
            }

            block.getWorld().spawnParticle(Particle.BLOCK_DUST, block.getLocation().add(0.5, 0.5, 0.5), 16, 0.5, 0.5, 0.5, block.getBlockData());
            block.setType(Material.AIR);

            ItemUtils.damageItem(player, player.getInventory().getItemInMainHand(), 1, EntityEffect.BREAK_EQUIPMENT_MAIN_HAND);
        } else {
            player.breakBlock(block);
        }
//        // try to break a block and call the appropriate events
//        ItemStack tool;
//        if (!Utils.isItemEmptyOrNull(player.getInventory().getItemInMainHand())) {
//            tool = player.getInventory().getItemInMainHand();
//        } else {
//            tool = player.getInventory().getItemInOffHand();
//            if (Utils.isItemEmptyOrNull(tool)) tool = null;
//        }
//        BlockBreakEvent breakEvent = new BlockBreakEvent(block, player);
//        ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(breakEvent);
//        if (breakEvent.isCancelled()) return new ArrayList<>();
//
//        List<ItemStack> drops = new ArrayList<>((tool == null) ? block.getDrops() : block.getDrops(tool, player));
//        List<ItemStack> receivedDrops = new ArrayList<>();
//        if (!drops.isEmpty()){
//            BlockDropItemStackEvent dropEvent = new BlockDropItemStackEvent(block, block.getState(), player, drops);
//            if (instantPickup){
//                Map<Integer, ItemStack> excessDrops = player.getInventory().addItem(dropEvent.getItems().toArray(new ItemStack[0]));
//                receivedDrops = new ArrayList<>(drops);
//                receivedDrops.removeAll(excessDrops.values());
//                dropEvent.getItems().clear();
//                dropEvent.getItems().addAll(excessDrops.values());
//            }
//            ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(dropEvent);
//        }
//
//        if (!breakEvent.isCancelled()) {
//            block.getWorld().spawnParticle(Particle.BLOCK_DUST, block.getLocation().add(0.5, 0.5, 0.5), 16, 0.5, 0.5, 0.5, block.getBlockData());
//            block.setType(Material.AIR);
//        }
//        return receivedDrops;
    }

    private static final Map<String, Collection<UUID>> blockAlteringPlayers = new HashMap<>();

    public static Map<String, Collection<UUID>> getBlockAlteringPlayers() {
        return blockAlteringPlayers;
    }

    /**
     * does something to the given collection of blocks, where each action is delayed by 1 tick per block
     * @param type the unique ID under which the chain altering is being done, this is to prevent this ability from spamming too much
     * @param who the player responsible for altering these blocks
     * @param blocks the blocks to alter
     * @param blockValidator additional checkup to see if the blocks in the collection are still valid for alteration
     * @param toolType optional tool in main hand used to alter these blocks, if null no tool is used and no durability is taken
     * @param alteration what happens to the block
     * @param onFinish what happens at the end of the iteration, where the block represents the final block
     */
    public static void alterBlocks(String type, Player who, List<Block> blocks, Predicate<Block> blockValidator, EquipmentClass toolType, Action<Block> alteration, Action<Block> onFinish){
        if (alteration == null) return;
        blockAlteringPlayers.putIfAbsent(type, new HashSet<>());
        Collection<UUID> players = blockAlteringPlayers.get(type);
        players.add(who.getUniqueId());
        blockAlteringPlayers.put(type, players);
        boolean requireTool = toolType != null;

        new BukkitRunnable(){
            final Iterator<Block> iterator = blocks.iterator();
            @Override
            public void run() {
                if (iterator.hasNext()){
                    Block b = iterator.next();
                    boolean valid = blockValidator == null || blockValidator.test(b);
                    if (valid){
                        if (requireTool){
                            ItemStack holdingItem = EntityUtils.getHoldingItem(who, toolType);
                            if (holdingItem == null) {
                                stop();
                            }// else {
//                                if (who.getGameMode() != GameMode.CREATIVE){
//                                    if (ItemUtils.damageItem(who, holdingItem, 1, EntityEffect.BREAK_EQUIPMENT_MAIN_HAND)){
//                                        who.getInventory().setItemInMainHand(null);
//                                        stop();
//                                    }
//                                }
//                            }
                        }
                        alteration.act(b);
                    }
                } else {
                    stop();
                }
            }

            private void stop(){
                Collection<UUID> players = blockAlteringPlayers.get(type);
                players.remove(who.getUniqueId());
                blockAlteringPlayers.put(type, players);
                if (onFinish != null) onFinish.act(blocks.get(blocks.size() - 1));
                cancel();
            }
        }.runTaskTimer(ValhallaMMO.getPlugin(), 0L, 1L);
    }

    /**
     * does something to the given collection of blocks
     * @param type the unique ID under which the chain altering is being done, this is to prevent this ability from spamming too much
     * @param who the player responsible for altering these blocks
     * @param blocks the blocks to alter
     * @param blockValidator additional checkup to see if the blocks in the collection are still valid for alteration
     * @param toolType optional tool in main hand used to alter these blocks, if null no tool is used and no durability is taken
     * @param alteration what happens to the block
     * @param onFinish what happens at the end of the iteration, where the block represents the final block
     */
    public static void alterBlocksInstant(String type, Player who, List<Block> blocks, Predicate<Block> blockValidator, EquipmentClass toolType, Action<Block> alteration, Action<Block> onFinish){
        if (alteration == null) return;
        blockAlteringPlayers.putIfAbsent(type, new HashSet<>());
        Collection<UUID> players = blockAlteringPlayers.get(type);
        players.add(who.getUniqueId());
        blockAlteringPlayers.put(type, players);
        boolean requireTool = toolType != null;

        for (Block b : blocks){
            boolean valid = blockValidator == null || blockValidator.test(b);
            if (valid){
                if (requireTool){
                    ItemStack holdingItem = EntityUtils.getHoldingItem(who, toolType);
                    if (holdingItem == null) {
                        break;
                    } else {
                        if (who.getGameMode() != GameMode.CREATIVE){
                            if (ItemUtils.damageItem(who, holdingItem, 1, EntityEffect.BREAK_EQUIPMENT_MAIN_HAND)){
                                who.getInventory().setItemInMainHand(null);
                                break;
                            }
                        }
                    }
                }
                alteration.act(b);
            }
        }
        if (onFinish != null) onFinish.act(blocks.get(blocks.size() - 1));
        Collection<UUID> ps = blockAlteringPlayers.get(type);
        ps.remove(who.getUniqueId());
        blockAlteringPlayers.put(type, ps);
    }

    /**
     * Decays a block as if it were a leaf block
     * should probably only be used on leaf blocks as to not confuse other plugins
     * @param block the block to decay
     */
    public static void decayBlock(Block block){
        LeavesDecayEvent decayEvent = new LeavesDecayEvent(block);
        ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(decayEvent);
        if (decayEvent.isCancelled()) return;

        block.breakNaturally();
    }

    public static void explodeBlock(Player player, Block block, boolean instantPickup, int fortuneLevel){
        // try to break a block and call the appropriate events
        ItemStack tool = new ItemStack(Material.IRON_PICKAXE);
        if (fortuneLevel > 0){
            tool.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, fortuneLevel);
        } else if (fortuneLevel < 0){
            tool.addEnchantment(Enchantment.SILK_TOUCH, 1);
        }

        BlockDropItemStackEvent dropEvent = new BlockDropItemStackEvent(block, block.getState(), player, new ArrayList<>(block.getDrops(tool, player)));
        ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(dropEvent);
        if (instantPickup){
            Map<Integer, ItemStack> excessDrops = player.getInventory().addItem(dropEvent.getItems().toArray(new ItemStack[0]));
            dropEvent.getItems().clear();
            dropEvent.getItems().addAll(excessDrops.values());
        }

        block.getWorld().spawnParticle(Particle.BLOCK_DUST, block.getLocation().add(0.5, 0.5, 0.5), 16, 0.5, 0.5, 0.5, block.getBlockData());
        block.setType(Material.AIR);
    }

    /**
     * Returns a collection of players from the given selector and sends the appropriate warnings to the sender if
     * anything goes wrong. Returns a collection with a single player if no selector was used. Returns an empty
     * collection if anything went wrong.
     * @param source the command sender that attempts the selector
     * @param selector the selector string
     * @return a collection of matching players, or single player if player name was used for selector
     */
    public static Collection<Player> selectPlayers(CommandSender source, String selector){
        Collection<Player> targets = new HashSet<>();
        if (selector.startsWith("@")){
            try {
                for (Entity part : Bukkit.selectEntities(source, selector)){
                    if (part instanceof Player){
                        targets.add((Player) part);
                    }
                }
            } catch (IllegalArgumentException e){
                String invalidSelector = TranslationManager.getInstance().getTranslation("error_command_invalid_selector");
                Utils.sendMessage(source, Utils.chat(invalidSelector.replace("%error%", e.getMessage())));
                return targets;
            }
        } else {
            Player target = ValhallaMMO.getPlugin().getServer().getPlayer(selector);
            if (target == null){
                String playerNotFound = TranslationManager.getInstance().getTranslation("error_command_player_offline");
                Utils.sendMessage(source, Utils.chat(playerNotFound));
                return targets;
            }
            targets.add(target);
        }
        return targets;
    }

    public static boolean doesPathExist(YamlConfiguration config, String root, String key){
        ConfigurationSection section = config.getConfigurationSection(root);
        return section != null && section.getKeys(false).contains(key);
    }

    /**
     * returns a timestamp based on the base amount of x in a second
     * @param ticks ticks
     * @param base base to represent a second (example, if 20 is 1 second give a base of 20, if 1 is 1 second give 1.)
     * @return a timestamp in a hh:mm:ss format (or mm:ss if not enough ticks for an hour are given), or ∞ if ticks is < 0
     */
    public static String toTimeStamp(long ticks, long base){
        if (ticks == 0) return "0:00";
        if (ticks < 0) return "∞";
        int hours = (int) Math.floor(ticks / (3600D * base));
        ticks %= (base * 3600);
        int minutes = (int) Math.floor(ticks / (60D * base));
        ticks %= (base * 60);
        int seconds = (int) Math.floor(ticks / (double) base);
        if (hours > 0){
            if (seconds < 10){
                if (minutes < 10){
                    return String.format("%d:0%d:0%d", hours, minutes, seconds);
                } else {
                    return String.format("%d:%d:0%d", hours, minutes, seconds);
                }
            } else {
                if (minutes < 10){
                    return String.format("%d:0%d:%d", hours, minutes, seconds);
                } else {
                    return String.format("%d:%d:%d", hours, minutes, seconds);
                }
            }
        } else {
            if (seconds < 10){
                return String.format("%d:0%d", minutes, seconds);
            } else {
                return String.format("%d:%d", minutes, seconds);
            }
        }
    }

    private static String daysFormat = null;
    private static String hoursFormat = null;
    private static String minutesFormat = null;
    private static String secondsFormat = null;
    public static String toTimeStamp2(long ticks, long base){
        if (daysFormat == null) daysFormat = TranslationManager.getInstance().getTranslation("timeformat_days");
        if (hoursFormat == null) hoursFormat = TranslationManager.getInstance().getTranslation("timeformat_hours");
        if (minutesFormat == null) minutesFormat = TranslationManager.getInstance().getTranslation("timeformat_minutes");
        if (secondsFormat == null) secondsFormat = TranslationManager.getInstance().getTranslation("timeformat_seconds");
        if (ticks < 0) return "∞";
        int days = (int) Math.floor(ticks / (3600D * 24 * base));
        ticks %= (base * (3600 * 24));
        int hours = (int) Math.floor(ticks / (3600D * base));
        ticks %= (base * 3600);
        int minutes = (int) Math.floor(ticks / (60D * base));
        ticks %= (base * 60);
        int seconds = (int) Math.floor(ticks / (double) base);
        if (days > 0){
            return daysFormat
                    .replace("%days%", "" + days)
                    .replace("%hours%", "" + hours)
                    .replace("%minutes%", "" + minutes)
                    .replace("%seconds%", "" + seconds);
        } else if (hours > 0){
            return hoursFormat
                    .replace("%days%", "" + days)
                    .replace("%hours%", "" + hours)
                    .replace("%minutes%", "" + minutes)
                    .replace("%seconds%", "" + seconds);
        } else if (minutes > 0){
            return minutesFormat
                    .replace("%days%", "" + days)
                    .replace("%hours%", "" + hours)
                    .replace("%minutes%", "" + minutes)
                    .replace("%seconds%", "" + seconds);
        } else {
            return secondsFormat
                    .replace("%days%", "" + days)
                    .replace("%hours%", "" + hours)
                    .replace("%minutes%", "" + minutes)
                    .replace("%seconds%", "" + seconds);
        }
    }


    public static String toPascalCase(String s){
        if (s == null) return null;
        if (s.length() == 0) return s;
        String allLowercase = s.toLowerCase();
        char c = allLowercase.charAt(0);
        return allLowercase.replaceFirst("" + c, "" + Character.toUpperCase(c));
    }

    /**
     * Returns a HashSet of blocks containing all the blocks matching the filter surrounding the origin location.
     * The blocks it can expand to are defined in scanArea, expressed in offsets.
     * The method stops once the amount of blocks hits the limit.
     * @param origin the block of origin
     * @param filter the block filter. If a scanned block type is not in this filter, it is ignored.
     * @param limit the max amount of blocks this method is allowed to scan
     * @param predicate additional conditions the block has to meet to be included
     * @param scanArea the offset blocks relative to the scanned block
     * @return the blocks found
     */
    public static List<Block> getBlockVein(Location origin, HashSet<Material> filter, int limit, Predicate<Block> predicate, Offset... scanArea){
        HashSet<Block> vein = new HashSet<>();
        List<Block> orderedVein = new ArrayList<>();
        if (limit == 0 || filter.isEmpty() || scanArea.length == 0) return orderedVein;
        vein.add(origin.getBlock());
        orderedVein.add(origin.getBlock());
        HashSet<Block> scanBlocks = new HashSet<>();
        scanBlocks.add(origin.getBlock());

        getSurroundingBlocks(orderedVein, scanBlocks, vein, limit, filter, predicate, scanArea);

        return orderedVein;
    }

    /**
     * Returns a HashSet of blocks containing all the blocks matching the filter surrounding the origin location.
     * The blocks it can expand to are defined in scanArea, expressed in offsets.
     * The method stops once the amount of blocks hits the limit.
     * @param origin the block of origin
     * @param filter the block filter. If a scanned block type is not in this filter, it is ignored.
     * @param limit the max amount of blocks this method is allowed to scan
     * @param scanArea the offset blocks relative to the scanned block
     * @return the blocks found
     */
    public static List<Block> getBlockVein(Location origin, HashSet<Material> filter, int limit, Offset... scanArea){
        Predicate<Block> p = o -> true;
        return getBlockVein(origin, filter, limit, p, scanArea);
    }

    private static void getSurroundingBlocks(List<Block> orderedVein, HashSet<Block> scanBlocks, HashSet<Block> currentVein, int limit, HashSet<Material> filter, Predicate<Block> predicate, Offset... scanArea){
        HashSet<Block> newScanBlocks = new HashSet<>();
        if (currentVein.size() >= limit) return;

        for (Block b : scanBlocks){
            for (Offset o : scanArea){
                Location offset = b.getLocation().clone().add(o.getOffX(), o.getOffY(), o.getOffZ());
                if (!predicate.test(offset.getBlock())) continue;
                if (filter.contains(offset.getBlock().getType())){
                    if (currentVein.contains(offset.getBlock())) continue;
                    currentVein.add(offset.getBlock());
                    orderedVein.add(offset.getBlock());
                    if (currentVein.size() >= limit) return;
                    newScanBlocks.add(offset.getBlock());
                }
            }
        }

        if (newScanBlocks.isEmpty()) return;
        getSurroundingBlocks(orderedVein, newScanBlocks, currentVein, limit, filter, predicate, scanArea);
    }

    /**
     * Checks if the item contains lore 'find' anywhere, if it does, it is replaced by 'replace'. If not found, it is
     * appended on the end of the lore.
     * @param meta the item meta to check the lore from
     * @param find the string to find in the lore
     * @param replace the string to replace that line with
     */
    public static void findAndReplaceLore(ItemMeta meta, String find, String replace){
        if (meta == null) return;

        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();
        int index = -1;
        for (String l : lore){
            if (l.contains(find)){
                index = lore.indexOf(l);
                break;
            }
        }

        if (index != -1) {
            lore.remove(index);
        }
        if (replace != null && !replace.equals("")){
            lore.add(Utils.chat(replace));
        }
        meta.setLore(lore);
    }

    public static void removeIfLoreContains(ItemMeta meta, String find){
        if (meta == null) return;

        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();
        int strengthLoreIndex = -1;
        for (String l : lore){
            if (l.contains(find)){
                strengthLoreIndex = lore.indexOf(l);
                break;
            }
        }

        if (strengthLoreIndex != -1) {
            lore.remove(strengthLoreIndex);
        }
        meta.setLore(lore);
    }

    public static boolean isItemEmptyOrNull(ItemStack i){
        return i == null || i.getType().isAir();
    }

    public static void sendMessage(CommandSender whomst, String message){
        if (message != null){
            if (!message.equals("")){
                whomst.sendMessage(chat(message));
            }
        }
    }

    public static String getItemName(ItemStack i){
        String name;
        assert i.getItemMeta() != null;
        if (i.getItemMeta().hasDisplayName()){
            name = Utils.chat(i.getItemMeta().getDisplayName());
        } else if (TranslationManager.getInstance().getLocalizedMaterialNames().containsKey(i.getType())){
            name = Utils.chat(TranslationManager.getInstance().getLocalizedMaterialNames().get(i.getType()));
        } else if (i.getItemMeta().hasLocalizedName()){
            name = Utils.chat(i.getItemMeta().getLocalizedName());
        } else {
            name = i.getType().toString().toLowerCase().replace("_", " ");
        }
        return name;
    }

    public static String chat (String s) {
        if (s == null) return "";
        if (MinecraftVersionManager.getInstance().currentVersionNewerThan(MinecraftVersion.MINECRAFT_1_16)){
            return newChat(s);
        } else {
            return oldChat(s);
        }
    }

    public static TextComponent chatComponent(String message) {
        return new TextComponent(chat(message));
    }

    public static TextComponent chatHover(String message, String hoverMessage) {
        TextComponent textComponent = chatComponent(message);
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverMessage)));
        return textComponent;
    }

    public static TextComponent chatCommand(String message, String hoverMessage, String commandString) {
        TextComponent textComponent = chatHover(message, chat(hoverMessage));
        if (!commandString.isEmpty())
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, commandString));
        return textComponent;
    }

    public static TextComponent chatLink(String message, String hoverMessage, String link) {
        TextComponent textComponent = chatHover(message, hoverMessage);
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
        return textComponent;
    }

    public static Map<Integer, ArrayList<String>> paginateTextList(int pageSize, List<String> allEntries) {
        Map<Integer, ArrayList<String>> pages = new HashMap<>();
        int stepper = 0;

        for (int pageNumber = 0; pageNumber < Math.ceil((double)allEntries.size()/(double)pageSize); pageNumber++) {
            ArrayList<String> pageEntries = new ArrayList<>();
            for (int pageEntry = 0; pageEntry < pageSize && stepper < allEntries.size(); pageEntry++, stepper++) {
                pageEntries.add(allEntries.get(stepper));
            }
            pages.put(pageNumber, pageEntries);
        }
        return pages;
    }

    public static String oldChat(String message) {
        return ChatColor.translateAlternateColorCodes('&', message + "");
    }
    static final Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static String newChat(String message) {
        char COLOR_CHAR = ChatColor.COLOR_CHAR;
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find())
        {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return Utils.oldChat(matcher.appendTail(buffer).toString());
    }

    public static double round(double value, int places) {
        if (places < 0) places = 2;

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static ItemStack setCustomModelData(ItemStack i, int data){
        if (i == null) return null;
        if (data < 0) return i;
        ItemMeta meta = i.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(data);
        i.setItemMeta(meta);
        return i;
    }

    private static final Map<String, Double> evalCache = new HashMap<>();
    public static double eval(String expression) {
        if (evalCache.containsKey(expression)) return evalCache.get(expression);
        String str = expression
                .replaceAll(",", ".")
                .replace("$pi", String.format("%.15f", Math.PI))
                .replace("$e", String.format("%.15f", Math.E))
                .replaceAll("[^A-Za-z0-9.^*/+()-]+", "");
        if (str.length() <= 0) return 0;
        double result = new Object() {
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
                if (pos < str.length()) {
                    throw new RuntimeException("Unexpected: " + (char)ch + " while trying to parse formula " + expression);
                }
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
                    switch (func) {
                        case "sqrt":
                            x = Math.sqrt(x);
                            break;
                        case "sin":
                            x = Math.sin(Math.toRadians(x));
                            break;
                        case "cos":
                            x = Math.cos(Math.toRadians(x));
                            break;
                        case "tan":
                            x = Math.tan(Math.toRadians(x));
                            break;
                        default:
                            throw new RuntimeException("Unknown function: " + func + " while trying to parse formula " + expression);
                    }
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch + " while trying to parse formula " + expression);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
        evalCache.put(expression, result);
        return result;
    }

    public static double evalBigDecimal(String expression) {
        if (evalCache.containsKey(expression)) return evalCache.get(expression);
        String str = expression
                .replaceAll(",", ".")
                .replace("$pi", String.format("%.15f", Math.PI))
                .replace("$e", String.format("%.15f", Math.E))
                .replaceAll("[^A-Za-z0-9.^*/+()-]+", "");
        if (str.length() <= 0) return 0;
        double result = new Object() {
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

            BigDecimal parse() {
                nextChar();
                BigDecimal x = parseExpression();
                if (pos < str.length()) {
                    throw new RuntimeException("Unexpected: " + (char)ch + " while trying to parse formula " + expression);
                }
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            BigDecimal parseExpression() {
                BigDecimal x = parseTerm();
                for (;;) {
                    if      (eat('+')) x = x.add(parseTerm()); // addition
                    else if (eat('-')) x = x.subtract(parseTerm()); // subtraction
                    else return x;
                }
            }

            BigDecimal parseTerm() {
                BigDecimal x = parseFactor();
                for (;;) {
                    if      (eat('*')) x = x.multiply(parseFactor()); // multiplication
                    else if (eat('/')) x = x.divide(parseFactor(), 10, RoundingMode.HALF_DOWN); // division
                    else return x;
                }
            }

            BigDecimal parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return parseFactor().negate(); // unary minus

                BigDecimal x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = BigDecimal.valueOf(Double.parseDouble(str.substring(startPos, this.pos)));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    switch (func) {
                        case "sqrt":
                            x = BigDecimal.valueOf(Math.sqrt(x.doubleValue()));
                            break;
                        case "sin":
                            x = BigDecimal.valueOf(Math.sin(Math.toRadians(x.doubleValue())));
                            break;
                        case "cos":
                            x = BigDecimal.valueOf(Math.cos(Math.toRadians(x.doubleValue())));
                            break;
                        case "tan":
                            x = BigDecimal.valueOf(Math.tan(Math.toRadians(x.doubleValue())));
                            break;
                        default:
                            throw new RuntimeException("Unknown function: " + func + " while trying to parse formula " + expression);
                    }
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch + " while trying to parse formula " + expression);
                }

                if (eat('^')) x = pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse().doubleValue();
        evalCache.put(expression, result);
        return result;
    }

    public static BigDecimal pow(BigDecimal base, BigDecimal exponent) {
        BigDecimal result;
        int signOf2 = exponent.signum();

        // Perform X^(A+B)=X^A*X^B (B = remainder)
        double dn1 = base.doubleValue();
        // Compare the same row of digits according to context
        BigDecimal n2 = exponent.multiply(new BigDecimal(signOf2)); // n2 is now positive
        BigDecimal remainderOf2 = n2.remainder(BigDecimal.ONE);
        BigDecimal n2IntPart = n2.subtract(remainderOf2);
        // Calculate big part of the power using context -
        // bigger range and performance but lower accuracy
        BigDecimal intPow = base.pow(n2IntPart.intValueExact());
        BigDecimal doublePow = BigDecimal.valueOf(Math.pow(dn1, remainderOf2.doubleValue()));
        result = intPow.multiply(doublePow);

        // Fix negative power
        if (signOf2 == -1)
            result = BigDecimal.ONE.divide(result, RoundingMode.HALF_UP);
        return result;
    }

    public static List<String> separateStringIntoLines(String string, int maxLength){
        List<String> lines = new ArrayList<>();
        String[] words = string.split(" ");
        if (words.length == 0) return lines;
        StringBuilder sentence = new StringBuilder();
        for (String s : words){
            if (sentence.length() + s.length() > maxLength || s.contains("-n")){
                s = s.replace("-n", "");
                lines.add(sentence.toString());
                String previousSentence = sentence.toString();
                sentence = new StringBuilder();
                sentence.append(Utils.chat(org.bukkit.ChatColor.getLastColors(Utils.chat(previousSentence)))).append(s);
            } else if (words[0].equals(s)){
                sentence.append(s);
            } else {
                sentence.append(" ").append(s);
            }
        }
        lines.add(sentence.toString());
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

    public static ItemStack createItemStack(Material material, String displayName, List<String> lore, int customModelData){
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
        meta.setCustomModelData(customModelData);
        item.setItemMeta(meta);
        return item;
    }

    public static Color hexToRgb(String colorStr) {
        return new Color(
                Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
                Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
                Integer.valueOf( colorStr.substring( 5, 7 ), 16 ));
    }

    public static String rgbToHex(int r, int g, int b){
        return String.format("#%02x%02x%02x", r, g, b);
    }

    /**
     * Returns an integer based on the chance given, but always between this chance rounded down and the chance rounded
     * up. Example:
     * a chance of 3.4 will always return at least 3, with a 40% chance to return 4 instead.
     * a chance of 0.9 will have a 90% chance to return 1
     * a chance of 7.5 will always return at least 7, with a 50% chance to return 8 instead.
     * @param chance the chance to calculate from
     * @return an integer returning at least the chance rounded down, with the remaining chance to return 1 extra
     */
    public static int excessChance(double chance){
        boolean negative = chance < 0;
        int atLeast = (negative) ? (int) Math.ceil(chance) : (int) Math.floor(chance);
        double remainingChance = chance - atLeast;
        if (getRandom().nextDouble() <= Math.abs(remainingChance)) {
            if(negative) {
                atLeast--;
            } else{
                atLeast++;
            }
        }
        return atLeast;
    }

    private static final int[][] offsets = new int[][]{
            {1, 0, 0},
            {-1, 0, 0},
            {0, 1, 0},
            {0, -1, 0},
            {0, 0, 1},
            {0, 0, -1}
    };

    public static Collection<Block> getBlocksTouching(Block start, int radiusX, int radiusY, int radiusZ, Material... touching){
        Collection<Block> blocks = new HashSet<>();
        for(double x = start.getLocation().getX() - radiusX; x <= start.getLocation().getX() + radiusX; x++){
            for(double y = start.getLocation().getY() - radiusY; y <= start.getLocation().getY() + radiusY; y++){
                for(double z = start.getLocation().getZ() - radiusZ; z <= start.getLocation().getZ() + radiusZ; z++){
                    Location loc = new Location(start.getWorld(), x, y, z);
                    blocks.add(loc.getBlock());
                }
            }
        }
        if (touching.length == 0) return blocks;
        return blocks.stream().filter(block -> {
            for (int[] offset : offsets){
                Location l = block.getLocation().add(offset[0], offset[1], offset[2]);
                if (Arrays.asList(touching).contains(l.getBlock().getType())){
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    public static Collection<Block> getBlocksTouchingAnything(Block start, int radiusX, int radiusY, int radiusZ){
        return getBlocksTouching(start, radiusX, radiusY, radiusZ).stream().filter(block -> {
            for (int[] offset : offsets){
                Location l = block.getLocation().add(offset[0], offset[1], offset[2]);
                if (!l.getBlock().getType().isAir()){
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    public static int getTotalExperience(int level) {
        int xp = 0;

        if (level >= 0 && level <= 15) {
            xp = (int) Math.round(Math.pow(level, 2) + 6 * level);
        } else if (level > 15 && level <= 30) {
            xp = (int) Math.round((2.5 * Math.pow(level, 2) - 40.5 * level + 360));
        } else if (level > 30) {
            xp = (int) Math.round(((4.5 * Math.pow(level, 2) - 162.5 * level + 2220)));
        }
        return xp;
    }

    public static int getTotalExperience(Player player) {
        return Math.round(player.getExp() * player.getExpToLevel()) + getTotalExperience(player.getLevel());
    }

    public static void setTotalExperience(Player player, int amount) {
        int level;
        int xp;
        float a = 0;
        float b = 0;
        float c = -amount;

        if (amount > getTotalExperience(0) && amount <= getTotalExperience(15)) {
            a = 1;
            b = 6;
        } else if (amount > getTotalExperience(15) && amount <= getTotalExperience(30)) {
            a = 2.5f;
            b = -40.5f;
            c += 360;
        } else if (amount > getTotalExperience(30)) {
            a = 4.5f;
            b = -162.5f;
            c += 2220;
        }
        level = (int) Math.floor((-b + Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a));
        xp = amount - getTotalExperience(level);
        player.setLevel(level);
        player.setExp(0);
        player.giveExp(xp);
    }
}
