package me.athlaeos.valhallammo.dom;

import org.bukkit.Material;

public class Transmutation {
    private final Material from;
    private final Material to;
    private final String name;

    public Transmutation(String name, Material from, Material to){
        this.name = name;
        this.from = from;
        this.to = to;
    }

    public String getName() {
        return name;
    }

    public Material getFrom() {
        return from;
    }

    public Material getTo() {
        return to;
    }
}
