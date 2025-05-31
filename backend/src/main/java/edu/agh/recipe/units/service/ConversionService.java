package edu.agh.recipe.units.service;

import edu.agh.recipe.units.domain.MeasurementUnit;
import edu.agh.recipe.units.domain.Quantity;

public interface ConversionService {
    Quantity convert(Quantity sourceQuantity, MeasurementUnit sourceUnit, MeasurementUnit targetUnit);
    boolean canConvert(MeasurementUnit sourceUnit, MeasurementUnit targetUnit);
}
