package me.athlaeos.valhallammo.placeholder.placeholders;

import me.athlaeos.valhallammo.placeholder.Placeholder;
import org.bukkit.entity.Player;

public class GeneralPlayerName extends Placeholder {
    public GeneralPlayerName(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        return s.replace(placeholder, p.getName());
    }
}
