package me.athlaeos.valhallammo.placeholder.placeholders;

import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import org.bukkit.entity.Player;

public class TotalStatPlaceholder extends Placeholder {
    private final String statSource;
    private final String precision;
    private final int points;

    public TotalStatPlaceholder(String placeholder, String statSource, int precision) {
        super(placeholder);
        this.statSource = statSource.toUpperCase();
        this.points = precision;
        if (precision == 0){
            this.precision = "%,d";
        } else {
            this.precision = "%,." + precision + "f";
        }
    }

    @Override
    public String parse(String s, Player p) {
        double stat = AccumulativeStatManager.getInstance().getStats(statSource, p, false);
        if (this.points == 0){
            return s.replace(this.placeholder, String.format(precision, (int) stat));
        } else {
            return s.replace(this.placeholder, String.format(precision, stat));
        }
    }
}
