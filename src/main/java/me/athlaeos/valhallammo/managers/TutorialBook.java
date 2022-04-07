package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;

public class TutorialBook {
    private static TutorialBook tutorialBook = null;
    private ItemStack book = null;

    public static TutorialBook getTutorialBookInstance(){
        if (tutorialBook == null) tutorialBook = new TutorialBook();
        return tutorialBook;
    }

    public void reload(){
        tutorialBook = null;
        getTutorialBookInstance();
    }

    public void loadBookContents(){
        YamlConfiguration bookConfig = ConfigManager.getInstance().getConfig("tutorial_book.yml").get();

        String title = bookConfig.getString("title");
        String author = bookConfig.getString("author");
        int model_data = bookConfig.getInt("model_data");
        List<String> contents = bookConfig.getStringList("pages");
        if (contents.isEmpty() || title == null || author == null) return;
        List<String> coloredContents = new ArrayList<>();
        for (String s : contents){
            coloredContents.add(Utils.chat(s.replaceAll("<br>", "\n")));
        }
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        assert meta != null;
        meta.setTitle(Utils.chat(title));
        meta.setAuthor(Utils.chat(author));
        meta.setGeneration(BookMeta.Generation.ORIGINAL);
        if (model_data > 0) meta.setCustomModelData(model_data);
        contents.forEach(Utils::chat);
        meta.setPages(coloredContents);
        book.setItemMeta(meta);
        this.book = book;
    }

    public ItemStack getBook() {
        return book;
    }
}
