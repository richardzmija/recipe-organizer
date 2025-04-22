package edu.agh.recipe.units.domain;

import java.text.DecimalFormat;
import java.util.function.Function;

public enum QuantityFormat {
    INTEGER(value -> String.valueOf(value.intValue())),
    DECIMAL(value -> {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(value);
    }),
    FRACTION(value -> {
        double fractional = value - Math.floor(value);
        int whole = (int) Math.floor(value);

        if (Math.abs(fractional) < 0.001) return String.valueOf(whole);

        // Common cooking fractions conversion
        if (Math.abs(fractional - 0.25) < 0.01) return whole > 0 ? whole + " 1/4" : "1/4";
        if (Math.abs(fractional - 0.33) < 0.01) return whole > 0 ? whole + " 1/3" : "1/3";
        if (Math.abs(fractional - 0.5) < 0.01) return whole > 0 ? whole + " 1/2" : "1/2";
        if (Math.abs(fractional - 0.66) < 0.01) return whole > 0 ? whole + " 2/3" : "2/3";
        if (Math.abs(fractional - 0.75) < 0.01) return whole > 0 ? whole + " 3/4" : "3/4";

        return DECIMAL.formatter.apply(value);
    });

    private final Function<Double, String> formatter;

    QuantityFormat(Function<Double, String> formatter) {
        this.formatter = formatter;
    }

    public String format(double value) {
        return formatter.apply(value);
    }
}
