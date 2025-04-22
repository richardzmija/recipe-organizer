package edu.agh.recipe.units.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Quantity {
    private final double value;
    private final QuantityFormat format;

    @JsonCreator
    public Quantity(
            @JsonProperty("value") double value,
            @JsonProperty("format") QuantityFormat format) {
        this.value = value;
        this.format = format != null ? format : determineDefaultFormat(value);
    }

    public static Quantity of(double value) {
        return new Quantity(value, determineDefaultFormat(value));
    }

    private static QuantityFormat determineDefaultFormat(double value) {
        return isWholeNumber(value) ? QuantityFormat.INTEGER : QuantityFormat.DECIMAL;
    }

    private static boolean isWholeNumber(double value) {
        return Math.abs(value - Math.round(value)) < 0.000001;
    }

    public double getValue() {
        return value;
    }

    public QuantityFormat getFormat() {
        return format;
    }

    public String getFormattedValue() {
        return format.format(value);
    }

    @Override
    public String toString() {
        return getFormattedValue();
    }
}