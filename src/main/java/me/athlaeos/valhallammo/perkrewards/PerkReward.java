package me.athlaeos.valhallammo.perkrewards;

import me.athlaeos.valhallammo.dom.ObjectType;
import org.bukkit.entity.Player;

public abstract class PerkReward implements Cloneable{
    protected String name;
    protected Object argument;

    public PerkReward(String name, Object argument){
        this.name = name;
        this.argument = argument;
    }

    public abstract void execute(Player player);

    public String getName() {
        return name;
    }

    public void setArgument(Object argument) {
        this.argument = argument;
    }

    public Object getArgument() {
        return argument;
    }

    public abstract ObjectType getType();

    @Override
    public PerkReward clone() throws CloneNotSupportedException {
        return (PerkReward) super.clone();
    }
}
