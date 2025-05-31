package edu.agh.recipe.units.service;

import edu.agh.recipe.units.domain.MeasurementUnit;
import edu.agh.recipe.units.domain.Quantity;
import edu.agh.recipe.units.domain.QuantityFormat;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DefaultConversionService implements ConversionService {

    private final Map<UnitPair, Double> conversionFactors = new HashMap<>();

    public DefaultConversionService() {
        initializeConversionFactors();
    }

    /**
     * Uses US customary measures for imperial units.
     */
    private void initializeConversionFactors() {
        // Metric to metric conversions
        addConversion(MeasurementUnit.MILLILITERS, MeasurementUnit.LITERS, 0.001);
        addConversion(MeasurementUnit.LITERS, MeasurementUnit.MILLILITERS, 1000.0);

        // Imperial volume conversions
        addConversion(MeasurementUnit.TEASPOONS, MeasurementUnit.TABLESPOONS, 0.333);
        addConversion(MeasurementUnit.TABLESPOONS, MeasurementUnit.TEASPOONS, 3.0);
        addConversion(MeasurementUnit.TABLESPOONS, MeasurementUnit.CUPS, 0.0625);
        addConversion(MeasurementUnit.CUPS, MeasurementUnit.TABLESPOONS, 16.0);
        addConversion(MeasurementUnit.CUPS, MeasurementUnit.TEASPOONS, 48.0);

        // Imperial weight conversions
        addConversion(MeasurementUnit.OUNCES, MeasurementUnit.POUNDS, 0.0625);
        addConversion(MeasurementUnit.POUNDS, MeasurementUnit.OUNCES, 16.0);

        // Cross-system conversions
        // Volume
        addConversion(MeasurementUnit.MILLILITERS, MeasurementUnit.CUPS, 0.00423);
        addConversion(MeasurementUnit.CUPS, MeasurementUnit.MILLILITERS, 236.59);
        addConversion(MeasurementUnit.MILLILITERS, MeasurementUnit.TABLESPOONS, 0.0676);
        addConversion(MeasurementUnit.TABLESPOONS, MeasurementUnit.MILLILITERS, 14.79);
        addConversion(MeasurementUnit.MILLILITERS, MeasurementUnit.TEASPOONS, 0.202);
        addConversion(MeasurementUnit.TEASPOONS, MeasurementUnit.MILLILITERS, 4.93);

        // Weight
        addConversion(MeasurementUnit.GRAMS, MeasurementUnit.OUNCES, 0.03527);
        addConversion(MeasurementUnit.OUNCES, MeasurementUnit.GRAMS, 28.35);
        addConversion(MeasurementUnit.GRAMS, MeasurementUnit.POUNDS, 0.00220);
        addConversion(MeasurementUnit.POUNDS, MeasurementUnit.GRAMS, 453.59);
    }

    private void addConversion(MeasurementUnit from, MeasurementUnit to, double factor) {
        conversionFactors.put(new UnitPair(from, to), factor);
    }

    @Override
    public Quantity convert(Quantity sourceQuantity, MeasurementUnit sourceUnit, MeasurementUnit targetUnit) {
        if (sourceUnit == targetUnit) {
            return sourceQuantity;
        }

        Double factor = conversionFactors.get(new UnitPair(sourceUnit, targetUnit));
        if (factor == null) {
            throw new UnsupportedConversionException(sourceUnit, targetUnit);
        }

        double convertedValue = sourceQuantity.getValue() * factor;

        // Determine appropriate format based on target unit
        QuantityFormat format = targetUnit.isFractional() ?
            QuantityFormat.FRACTION : sourceQuantity.getFormat();

        return new Quantity(convertedValue, format);
    }

    @Override
    public boolean canConvert(MeasurementUnit sourceUnit, MeasurementUnit targetUnit) {
        if (sourceUnit == targetUnit) {
            return true;
        }
        return conversionFactors.containsKey(new UnitPair(sourceUnit, targetUnit));
    }

    private record UnitPair(MeasurementUnit from, MeasurementUnit to) {}

    public static class UnsupportedConversionException extends RuntimeException {
        public UnsupportedConversionException(MeasurementUnit from, MeasurementUnit to) {
            super("Conversion from " + from.getName() + " to " + to.getName() + " is not supported");
        }
    }
}