package me.athlaeos.valhallammo.placeholders;

import org.bukkit.entity.Player;

public class GeneralPlayerNickName extends Placeholder{
    public GeneralPlayerNickName(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        return s.replace(placeholder, p.getDisplayName());
    }
}
