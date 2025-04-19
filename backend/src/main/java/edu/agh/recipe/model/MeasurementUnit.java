package edu.agh.recipe.model;

public enum MeasurementUnit {
    // Metric units
    MILLILITERS(MeasurementSystem.METRIC),
    LITERS(MeasurementSystem.METRIC),
    GRAMS(MeasurementSystem.METRIC),

    // Imperial units
    CUPS(MeasurementSystem.IMPERIAL),
    TEASPOONS(MeasurementSystem.IMPERIAL),
    TABLESPOONS(MeasurementSystem.IMPERIAL),
    OUNCES(MeasurementSystem.IMPERIAL),
    POUNDS(MeasurementSystem.IMPERIAL);

    private final MeasurementSystem system;

    MeasurementUnit(MeasurementSystem system) {
        this.system = system;
    }

    public MeasurementSystem getSystem() {
        return system;
    }

    public enum MeasurementSystem {
        METRIC, IMPERIAL
    }
}
