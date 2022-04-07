package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers;

public abstract class TripleArgDynamicItemModifier extends DuoArgDynamicItemModifier implements Cloneable{
    protected double strength3;
    protected double minStrength3;
    protected double defaultStrength3;
    protected double maxStrength3;
    protected double smallStepIncrease3;
    protected double bigStepIncrease3;
    protected double smallStepDecrease3;
    protected double bigStepDecrease3;

    public TripleArgDynamicItemModifier(String name, double strength, double strength2, double strength3, ModifierPriority priority){
        super(name, strength, strength2, priority);
        this.strength3 = strength3;
    }

    public double getMinStrength3() {
        return minStrength3;
    }

    public double getDefaultStrength3() {
        return defaultStrength3;
    }

    public double getMaxStrength3() {
        return maxStrength3;
    }

    public double getSmallStepIncrease3() {
        return smallStepIncrease3;
    }

    public double getBigStepIncrease3() {
        return bigStepIncrease3;
    }

    public double getSmallStepDecrease3() {
        return smallStepDecrease3;
    }

    public double getBigStepDecrease3() {
        return bigStepDecrease3;
    }

    public double getStrength3() {
        return strength3;
    }

    public void setStrength3(double strength3) {
        this.strength3 = strength3;
    }

    @Override
    public TripleArgDynamicItemModifier clone() throws CloneNotSupportedException {
        return (TripleArgDynamicItemModifier) super.clone();
    }
}
