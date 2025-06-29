package edu.agh.recipe.units.domain;

public enum MeasurementSystem {
    METRIC,
    IMPERIAL;

    public static MeasurementSystem fromString(String value) {
        try {
            return value != null ? valueOf(value.toUpperCase()) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}