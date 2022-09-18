package me.athlaeos.valhallammo.books;

import java.util.HashMap;
import java.util.Map;

public class BookUtils {
    private static BookUtils utils = null;
    public static BookUtils getInstance(){
        if (utils == null) utils = new BookUtils();
        return utils;
    }

    private final Map<String, BookBasePlaceholder> placeholders = new HashMap<>();

    public BookUtils(){
        register(new LinkBasePlaceholder("<l>", "", "", ""));
    }

    private void register(BookBasePlaceholder placeholder){
        placeholders.put(placeholder.getPlaceholder(), placeholder);
    }

    public Map<String, BookBasePlaceholder> getPlaceholders() {
        return placeholders;
    }
}
