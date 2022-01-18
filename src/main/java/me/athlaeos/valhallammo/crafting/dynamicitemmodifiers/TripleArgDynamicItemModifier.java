package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers;

public abstract class DuoArgDynamicItemModifier extends DynamicItemModifier implements Cloneable{
    protected double strength2;
    protected double minStrength2;
    protected double defaultStrength2;
    protected double maxStrength2;
    protected double smallStepIncrease2;
    protected double bigStepIncrease2;
    protected double smallStepDecrease2;
    protected double bigStepDecrease2;

    public DuoArgDynamicItemModifier(String name, double strength, double strength2, ModifierPriority priority){
        super(name, strength, priority);
        this.strength2 = strength2;
    }

    public double getMinStrength2() {
        return minStrength2;
    }

    public double getDefaultStrength2() {
        return defaultStrength2;
    }

    public double getMaxStrength2() {
        return maxStrength2;
    }

    public double getSmallStepIncrease2() {
        return smallStepIncrease2;
    }

    public double getBigStepIncrease2() {
        return bigStepIncrease2;
    }

    public double getSmallStepDecrease2() {
        return smallStepDecrease2;
    }

    public double getBigStepDecrease2() {
        return bigStepDecrease2;
    }

    public double getStrength2() {
        return strength2;
    }

    public void setStrength2(double strength2) {
        this.strength2 = strength2;
    }

    @Override
    public DuoArgDynamicItemModifier clone() throws CloneNotSupportedException {
        return (DuoArgDynamicItemModifier) super.clone();
    }
}
