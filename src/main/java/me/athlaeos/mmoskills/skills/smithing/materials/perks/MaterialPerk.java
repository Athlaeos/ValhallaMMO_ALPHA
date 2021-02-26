package me.athlaeos.mmoskills.skills.smithing.materials.perks;

import org.bukkit.event.Event;

public abstract class MaterialPerk {
    protected boolean isEnabled;
    public abstract void execute(Event event);

    public boolean isEnabled() {
        return isEnabled;
    }
}
