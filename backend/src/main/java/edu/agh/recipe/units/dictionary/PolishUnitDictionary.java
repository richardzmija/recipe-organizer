package edu.agh.recipe.units.dictionary;

import edu.agh.recipe.units.domain.MeasurementUnit;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class PolishUnitDictionary implements UnitDictionary {

    private final Map<String, MeasurementUnit> unitMap = new HashMap<>();

    public PolishUnitDictionary() {
        // Can be moved to a configuration file or a database.
        unitMap.put("ml", MeasurementUnit.MILLILITERS);
        unitMap.put("mililitr", MeasurementUnit.MILLILITERS);
        unitMap.put("mililitry", MeasurementUnit.MILLILITERS);
        unitMap.put("mililitrów", MeasurementUnit.MILLILITERS);

        unitMap.put("l", MeasurementUnit.LITERS);
        unitMap.put("litr", MeasurementUnit.LITERS);
        unitMap.put("litry", MeasurementUnit.LITERS);
        unitMap.put("litrów", MeasurementUnit.LITERS);

        unitMap.put("g", MeasurementUnit.GRAMS);
        unitMap.put("gram", MeasurementUnit.GRAMS);
        unitMap.put("gramy", MeasurementUnit.GRAMS);
        unitMap.put("gramów", MeasurementUnit.GRAMS);

        unitMap.put("filiżanka", MeasurementUnit.CUPS);
        unitMap.put("filiżanki", MeasurementUnit.CUPS);
        unitMap.put("filiżanek", MeasurementUnit.CUPS);
        unitMap.put("cup", MeasurementUnit.CUPS);

        unitMap.put("łyżeczka", MeasurementUnit.TEASPOONS);
        unitMap.put("łyżeczki", MeasurementUnit.TEASPOONS);
        unitMap.put("łyżeczek", MeasurementUnit.TEASPOONS);
        unitMap.put("tea spoon", MeasurementUnit.TEASPOONS);
        unitMap.put("teaspoon", MeasurementUnit.TEASPOONS);
        unitMap.put("tea spoons", MeasurementUnit.TEASPOONS);
        unitMap.put("teaspoons", MeasurementUnit.TEASPOONS);

        unitMap.put("łyżka", MeasurementUnit.TABLESPOONS);
        unitMap.put("łyżki", MeasurementUnit.TABLESPOONS);
        unitMap.put("łyżek", MeasurementUnit.TABLESPOONS);
        unitMap.put("łyżka stołowa", MeasurementUnit.TABLESPOONS);
        unitMap.put("łyżki stołowe", MeasurementUnit.TABLESPOONS);
        unitMap.put("łyżek stołowych", MeasurementUnit.TABLESPOONS);
        unitMap.put("table spoon", MeasurementUnit.TABLESPOONS);
        unitMap.put("tablespoon", MeasurementUnit.TABLESPOONS);
        unitMap.put("table spoons", MeasurementUnit.TABLESPOONS);
        unitMap.put("tablespoons", MeasurementUnit.TABLESPOONS);

        unitMap.put("oz", MeasurementUnit.OUNCES);
        unitMap.put("lb", MeasurementUnit.POUNDS);
    }

    @Override
    public Optional<MeasurementUnit> resolve(String rawUnit) {
        if (rawUnit == null) return Optional.empty();
        return Optional.ofNullable(unitMap.get(rawUnit.toLowerCase().trim()));
    }
}
