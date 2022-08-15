package me.athlaeos.valhallammo.placeholder.placeholders;

import me.athlaeos.valhallammo.placeholder.Placeholder;
import org.bukkit.entity.Player;

public class GeneralPlayerNickName extends Placeholder {
    public GeneralPlayerNickName(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        return s.replace(placeholder, p.getDisplayName());
    }
}
