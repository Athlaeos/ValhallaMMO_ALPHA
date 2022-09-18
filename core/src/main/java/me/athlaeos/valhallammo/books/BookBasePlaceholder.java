package me.athlaeos.valhallammo.books;

import net.md_5.bungee.api.chat.BaseComponent;

public abstract class BookBasePlaceholder {
    protected String placeholder;
    public BookBasePlaceholder(String placeholder){
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    /**
     * @return the string outcome with which the placeholder should be replaced
     */
    public abstract BaseComponent getReplacement();

    /**
     * the amount of arguments required to properly fill in the placeholder. If the amount of args does not match this number,
     * the placeholder will not be filled.
     * @return the amount of arguments required
     */
    public abstract int getRequiredArgs();

    /**
     * the questions asked to the person filling in a placeholder to clarify the purpose of each argument
     * @return the questions asked to the person in order
     */
    public abstract String[] getArgQuestions();
}
