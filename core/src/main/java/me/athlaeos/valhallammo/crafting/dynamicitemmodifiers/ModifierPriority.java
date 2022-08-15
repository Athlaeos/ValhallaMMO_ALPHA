package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers;

import me.athlaeos.valhallammo.utility.Utils;

public enum ModifierPriority {
    SOONEST(1, "&eHighest priority&7:&e %priority%&7. -nExecutes before anything else."),
    SOON(2, "&eHigh priority&7:&e %priority%&7. -nExecutes after the highest priority modifiers."),
    SOONISH(3, "&eHigher priority&7: &e%priority%&7. -nExecutes after high or highest priority modifiers."),
    NEUTRAL(4, "&eDefault priority&7:&e %priority%&7. -nExecutes after higher or higher priority modifiers."),
    LATERISH(5, "&eLower priority&7:&e %priority%&7. -nExecutes after default or higher priorities."),
    LATER(6, "&eLow priority&7:&e %priority%&7. -nExecutes after later or higher priorities."),
    LAST(7, "&eLowest priority&7:&e %priority%&7. -nExecutes last.");

    private final int priorityRating;
    private final String description;
    ModifierPriority(int priorityRating, String description){
        this.priorityRating = priorityRating;
        this.description = Utils.chat(description);
    }

    public int getPriorityRating() {
        return priorityRating;
    }

    public String getDescription() {
        return description;
    }
}
