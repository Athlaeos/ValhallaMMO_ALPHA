package me.athlaeos.valhallammo.books;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.utility.Utils;
import net.md_5.bungee.api.chat.BaseComponent;

public class LinkBasePlaceholder extends BookBasePlaceholder {
    private final String message;
    private final String hover;
    private final String link;
    public LinkBasePlaceholder(String placeholder, String... args){
        super(placeholder);
        this.message = args[0];
        this.hover = args[1];
        this.link = args[2];
    }

    @Override
    public BaseComponent getReplacement() {
        ValhallaMMO.getPlugin().getServer().spigot().broadcast(Utils.chatLink(message, hover, link));
        return Utils.chatCommand(message, hover, link);
    }

    @Override
    public int getRequiredArgs() {
        return 3;
    }

    @Override
    public String[] getArgQuestions() {
        return new String[]{"What message should the link be hidden under?",
                "What message should the player see while hovering over the link?",
                "What link should the player be lead to when clicking the message?"};
    }
}
