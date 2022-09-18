package me.athlaeos.valhallammo.books;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.utility.Utils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.regex.Pattern;

public class BookSessionListener implements Listener {

    private final Map<Player, Session> sessions = new HashMap<>();

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e){
        if (sessions.containsKey(e.getPlayer())){
            Session session = sessions.get(e.getPlayer());
            if (session != null){
                if (session.answerQuestion(Utils.chat(e.getMessage()))){
                    session.updateBook();
                    sessions.remove(e.getPlayer());
                    e.setCancelled(true);
                    return;
                }
                e.setCancelled(true);
                e.getPlayer().sendMessage(Utils.chat(session.nextQuestion()));
            }
        }
    }

    @EventHandler
    public void onBookEdit(PlayerEditBookEvent e){
        if (e.isCancelled()) return;
        if (!e.getPlayer().hasPermission("booky.edit")) return;
        ItemStack book = e.getPlayer().getInventory().getItemInMainHand();
        if (book.getType() != Material.WRITABLE_BOOK){
            book = e.getPlayer().getInventory().getItemInOffHand();
        }
        if (book.getType() != Material.WRITABLE_BOOK) return;
        BookMeta newBookMeta = e.getNewBookMeta();
        if (e.isSigning()) {
            List<BaseComponent[]> newPages = new ArrayList<>();
            List<String[]> args = getPlaceholdersArgs(newBookMeta);
            Map<String, String[]> mappedArgs = new HashMap<>();
            for (String[] arg : args){
                mappedArgs.put(arg[0], arg);
            }

            for (String s : newBookMeta.getPages()){
                String page = Utils.chat(s);
                List<BaseComponent> newPage = new ArrayList<>();

                String[] placeholders = StringUtils.substringsBetween(page, "[", "]");
                if (placeholders == null) continue;
                for (String placeholder : placeholders){
                    String[] split = page.split(Pattern.quote("[" + placeholder + "]"));
                    newPage.add(new TextComponent(split[0]));
                    if (mappedArgs.containsKey(placeholder)) {
                        String[] arg = mappedArgs.get(placeholder);
                        if (arg[1].equals("l")) {
                            newPage.add(new LinkBasePlaceholder("l", Arrays.copyOfRange(arg, 2, arg.length)).getReplacement());
                            System.out.println("added link placeholder");
                        }
                    } else {
                        newPage.add(new TextComponent("[" + placeholder + "]"));
                        System.out.println("added text placeholder " + "[" + placeholder + "]");
                    }
                }
                newPage.add(new TextComponent(page));
                System.out.println("added text placeholder " + page);
                newPages.add(newPage.toArray(new BaseComponent[0]));
            }
            e.getNewBookMeta().spigot().getPages().clear();
            e.getNewBookMeta().spigot().getPages().addAll(newPages);

            for (String[] s : getPlaceholdersArgs(newBookMeta)){
                e.getPlayer().sendMessage(Utils.chat(
                        String.format("&fPlaceholder [%s] found, type %s, has the args %s",
                                s[0],
                                s[1],
                                String.join(", ", Arrays.copyOfRange(s, 2, s.length)))
                ));


            }
        } else {
            List<BookBasePlaceholder> foundPlaceholders = new ArrayList<>();
            for (int i = 0; i < newBookMeta.spigot().getPages().size(); i++){
                String page = newBookMeta.getPages().get(i);
                String[] placeholders = StringUtils.substringsBetween(page, "<", ">");
                if (placeholders == null) continue;
                for (String placeholder : placeholders){
                    BookBasePlaceholder p = BookUtils.getInstance().getPlaceholders().get("<" + placeholder + ">");
                    if (p == null) continue;
                    foundPlaceholders.add(p);
                }
            }
            if (!foundPlaceholders.isEmpty()){
                Session session = new Session(book, e.getNewBookMeta(), foundPlaceholders);
                sessions.put(e.getPlayer(), session);
                e.getPlayer().sendMessage(Utils.chat(session.nextQuestion()));
            }
        }
    }

    private static class Session{
        private final List<BookBasePlaceholder> placeholdersFound;
        private final ItemStack book;
        private final BookMeta bookMeta;
        private BookBasePlaceholder currentPlaceholder = null;
        private int currentQuestion = 0;
        private String[] currentAnswers;
        private List<String> bookContents;

        public List<BookBasePlaceholder> getPlaceholdersFound() {
            return placeholdersFound;
        }

        public Session(ItemStack book, BookMeta bookMeta, List<BookBasePlaceholder> placeholdersFound){
            this.book = book;
            this.bookMeta = bookMeta;
            this.placeholdersFound = new ArrayList<>(placeholdersFound);
            bookContents = bookMeta.getPages();
        }

        public BookMeta getBookMeta() {
            return bookMeta;
        }

        public ItemStack getBook() {
            return book;
        }

        public String[] getCurrentAnswers() {
            return currentAnswers;
        }

        public void updateBook(){
            bookMeta.setPages(bookContents);
            book.setItemMeta(bookMeta);
        }

        public boolean answerQuestion(String answer){
            currentAnswers[currentQuestion] = answer;
            currentQuestion++;
            if (currentQuestion == currentPlaceholder.getArgQuestions().length) {
                // this was the last question of the placeholder
                short id = -1;
                String placeholder = null;
                if (currentPlaceholder instanceof LinkBasePlaceholder){
                    placeholder = "l";
                    id = storePlaceholderArgs(bookMeta, "l", currentAnswers);
//                    BookUtils.getInstance().applyPlaceholder(bookContents, currentPlaceholder.getPlaceholder(), new LinkBasePlaceholder("<l>", currentAnswers));
                }

                if (id >= 0){
                    List<String> newContents = new ArrayList<>(bookContents);
                    for (int i = 0; i < bookContents.size(); i++){
                        String originalPage = bookContents.get(i);
                        originalPage = originalPage.replaceFirst("<" + placeholder + ">", "[" + id + "]");
                        if (!originalPage.equals(bookContents.get(i))){
                            newContents.set(i, originalPage);
                            break;
                        }
                    }
                    bookContents = newContents;
                }

                // last question of last placeholder
                return placeholdersFound.isEmpty();
            }
            return false;
        }

        public String nextQuestion(){
            if (currentPlaceholder == null){
                if (placeholdersFound.isEmpty()) return null;
                currentPlaceholder = placeholdersFound.get(0);
                placeholdersFound.remove(0);
                currentAnswers = new String[currentPlaceholder.getArgQuestions().length];
            } else {
                if (currentQuestion >= currentPlaceholder.getArgQuestions().length){
                    if (placeholdersFound.isEmpty()) return null;
                    currentQuestion = 0;
                    currentPlaceholder = placeholdersFound.get(0);
                    placeholdersFound.remove(0);
                    currentAnswers = new String[currentPlaceholder.getArgQuestions().length];
                }
            }
            return currentPlaceholder.getArgQuestions()[currentQuestion];
        }
    }

    /**
     * Stores the arguments of a placeholder into the book's PersistentDataContainer and returns the identifier of the
     * placeholder details
     * @param meta the book's meta
     * @param placeholder the placeholder, such as "l" for link or "b" for book
     * @param args the arguments of
     * @return the short ID of the identifier of this placeholder
     */
    public static short storePlaceholderArgs(BookMeta meta, String placeholder, String[] args){
        List<String[]> existingPlaceholders = getPlaceholdersArgs(meta);
        String[] newArg = new String[args.length + 2];
        short currentNextID = nextAvailableID(meta);
        newArg[0] = "" + currentNextID;
        newArg[1] = placeholder;
        System.arraycopy(args, 0, newArg, 2, args.length);
        // for (int i = 0; i < args.length; i++){
        //     newArg[2 + i] = args[i];
        // }
        existingPlaceholders.add(newArg);
        setPlaceholderArgs(meta, existingPlaceholders);
        setNextAvailableID(meta, (short) (currentNextID + 1));
        return currentNextID;
    }

    private static final NamespacedKey placeholderStorageKey = new NamespacedKey(ValhallaMMO.getPlugin(), "stored_placeholders");
    private static final NamespacedKey highestIDKey = new NamespacedKey(ValhallaMMO.getPlugin(), "highest_occupied_id");

    private static void setPlaceholderArgs(BookMeta meta, List<String[]> storedPlaceholders){
        List<String> stringList = new ArrayList<>();
        for (String[] placeholderSet : storedPlaceholders){
            stringList.add(String.join("{arg}", placeholderSet));
        }
        meta.getPersistentDataContainer().set(placeholderStorageKey, PersistentDataType.STRING, String.join("{sep}", stringList));
    }

    private static short nextAvailableID(BookMeta meta){
        if (meta.getPersistentDataContainer().has(highestIDKey, PersistentDataType.SHORT)){
            return meta.getPersistentDataContainer().get(highestIDKey, PersistentDataType.SHORT);
        }
        return 0;
    }

    private static void setNextAvailableID(BookMeta meta, short id){
        meta.getPersistentDataContainer().set(highestIDKey, PersistentDataType.SHORT, id);
    }

    private static List<String[]> getPlaceholdersArgs(BookMeta meta){
        List<String[]> storedPlaceholderArgs = new ArrayList<>();
        if (meta.getPersistentDataContainer().has(placeholderStorageKey, PersistentDataType.STRING)){
            // Placeholder args are stored in this format: identifier{arg}placeholder{arg}arg1{arg}arg2{arg}arg3{sep}identifier{arg}placeholder{arg}arg1{arg}arg2{arg}arg
            String value = meta.getPersistentDataContainer().get(placeholderStorageKey, PersistentDataType.STRING);
            if (value != null){
                for (String pagePlaceholder : value.split(Pattern.quote("{sep}"))){
                    storedPlaceholderArgs.add(pagePlaceholder.split(Pattern.quote("{arg}")));
                }
            }
        }
        return storedPlaceholderArgs;
    }
}
