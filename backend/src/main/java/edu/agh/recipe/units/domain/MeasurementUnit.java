package edu.agh.recipe.units.domain;

public enum MeasurementUnit {
    // Metric units
    MILLILITERS("ml", "Milliliter", MeasurementSystem.METRIC),
    LITERS("l", "Liter", MeasurementSystem.METRIC),
    GRAMS("g", "Gram", MeasurementSystem.METRIC),

    // Imperial units
    CUPS("cup", "Cup", MeasurementSystem.IMPERIAL),
    TEASPOONS("tea spoon", "Tea Spoon", MeasurementSystem.IMPERIAL),
    TABLESPOONS("table spoon", "Table Spoon", MeasurementSystem.IMPERIAL),
    OUNCES("oz", "Ounce", MeasurementSystem.IMPERIAL),
    POUNDS("lb", "Pound", MeasurementSystem.IMPERIAL);

    private final String symbol;
    private final String name;
    private final MeasurementSystem system;

    MeasurementUnit(String symbol, String name, MeasurementSystem system) {
        this.symbol = symbol;
        this.name = name;
        this.system = system;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public MeasurementSystem getSystem() {
        return system;
    }
}
