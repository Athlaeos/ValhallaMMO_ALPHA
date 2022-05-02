package me.athlaeos.valhallammo.dom;

import me.athlaeos.valhallammo.utility.Utils;

public class Scaling {

    private final double lowerBound;
    private final boolean ignoreLower;
    private final double upperBound;
    private final boolean ignoreUpper;
    private final ScalingMode scalingType;
    private final String scaling;

    public Scaling(String scaling, ScalingMode scalingType, double lowerBound, double upperBound, boolean ignoreLower, boolean ignoreUpper) {
        this.scaling = scaling;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.scalingType = scalingType;
        this.ignoreLower = ignoreLower;
        this.ignoreUpper = ignoreUpper;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public ScalingMode getScalingType() {
        return scalingType;
    }

    public boolean doIgnoreLower() {
        return ignoreLower;
    }

    public boolean doIgnoreUpper() {
        return ignoreUpper;
    }

    public String getScaling() {
        return scaling;
    }
}
