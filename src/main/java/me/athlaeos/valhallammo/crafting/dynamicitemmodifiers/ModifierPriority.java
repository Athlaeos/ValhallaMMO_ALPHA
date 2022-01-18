package me.athlaeos.valhallammo.skills.smithing.recipes.dynamicitemmodifiers;

import me.athlaeos.valhallammo.utility.Utils;

public enum ModifierPriority {
    SOONEST(1, "&eHighest priority&7:&e %priority%&7. -nExecutes before anything else."),
    SOON(2, "&eHigh priority&7:&e %priority%&7. -nExecutes after the highest priority modifiers."),
    NEUTRAL(3, "&eDefault priority&7:&e %priority%&7. -nExecutes after high or highest priority modifiers."),
    LATER(4, "&eLow priority&7:&e %priority%&7. -nExecutes after default or higher priorities."),
    LAST(5, "&eLowest priority&7:&e %priority%&7. -nExecutes last.");

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
