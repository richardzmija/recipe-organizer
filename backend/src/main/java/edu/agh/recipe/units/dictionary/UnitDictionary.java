package edu.agh.recipe.units.dictionary;

import edu.agh.recipe.units.domain.MeasurementUnit;

import java.util.Optional;

public interface UnitDictionary {
    /**
     * Tries to resolve a string representation of a unit into a MeasurementUnit.
     *
     * @param rawUnit unit string, e.g. "teaspoon", "grams", "cup"
     * @return Optional with corresponding MeasurementUnit or empty if not recognized
     */
    Optional<MeasurementUnit> resolve(String rawUnit);
}
