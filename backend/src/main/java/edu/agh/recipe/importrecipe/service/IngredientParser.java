package edu.agh.recipe.importrecipe.service;

import edu.agh.recipe.recipes.model.RecipeIngredient;
import edu.agh.recipe.units.domain.MeasurementUnit;
import edu.agh.recipe.units.domain.Quantity;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IngredientParser {

    private static final Map<String, MeasurementUnit> UNIT_SYMBOL_MAP = Map.ofEntries(
            Map.entry("ml", MeasurementUnit.MILLILITERS),
            Map.entry("mililitr", MeasurementUnit.MILLILITERS),
            Map.entry("mililitry", MeasurementUnit.MILLILITERS),
            Map.entry("mililitrów", MeasurementUnit.MILLILITERS),
            Map.entry("l", MeasurementUnit.LITERS),
            Map.entry("litr", MeasurementUnit.LITERS),
            Map.entry("litry", MeasurementUnit.LITERS),
            Map.entry("litrów", MeasurementUnit.LITERS),
            Map.entry("g", MeasurementUnit.GRAMS),
            Map.entry("gram", MeasurementUnit.GRAMS),
            Map.entry("gramy", MeasurementUnit.GRAMS),
            Map.entry("gramów", MeasurementUnit.GRAMS),
            Map.entry("cup", MeasurementUnit.CUPS),
            Map.entry("filiżanka", MeasurementUnit.CUPS),
            Map.entry("filiżanki", MeasurementUnit.CUPS),
            Map.entry("filiżanek", MeasurementUnit.CUPS),
            Map.entry("tea spoon", MeasurementUnit.TEASPOONS),
            Map.entry("łyżeczka", MeasurementUnit.TEASPOONS),
            Map.entry("łyżeczki", MeasurementUnit.TEASPOONS),
            Map.entry("łyżeczek", MeasurementUnit.TEASPOONS),
            Map.entry("table spoon", MeasurementUnit.TABLESPOONS),
            Map.entry("łyżka", MeasurementUnit.TABLESPOONS),
            Map.entry("łyżki", MeasurementUnit.TABLESPOONS),
            Map.entry("łyżek", MeasurementUnit.TABLESPOONS),
            Map.entry("łyżka stołowa", MeasurementUnit.TABLESPOONS),
            Map.entry("łyżki stołowe", MeasurementUnit.TABLESPOONS),
            Map.entry("łyżkek stołowych", MeasurementUnit.TABLESPOONS),
            Map.entry("oz", MeasurementUnit.OUNCES),
            Map.entry("lb", MeasurementUnit.POUNDS)
    );

    public static RecipeIngredient parse(String line) {
        Pattern pattern = Pattern.compile("(?<qty>[\\d,./]+)\\s*(?<unit>\\w+)?\\s+(?<name>.+)");
//        Pattern pattern = Pattern.compile("^(?:(?<qty>[\\d,./\\s]+)\\s*(?<unit>\\w+)?\\s+)?(?<name>.+)$");
        Matcher matcher = pattern.matcher(line.trim());

        if (matcher.matches()) {
            String quantityStr = Optional.ofNullable(matcher.group("qty"))
                    .map(q -> q.replace(',', '.').trim())
                    .orElse(null);
            double quantity = quantityStr != null ? parseQuantity(quantityStr) : 1.0;

            String unitStr = Optional.ofNullable(matcher.group("unit"))
                    .map(String::trim)
                    .orElse(null);
            MeasurementUnit unit = unitStr != null ? UNIT_SYMBOL_MAP.get(unitStr.toLowerCase()) : null;

            String ingredientName = matcher.group("name");

            if (unit != null) {
                return RecipeIngredient.of(ingredientName, unit, quantity);
            } else {
                return new RecipeIngredient(ingredientName, null, Quantity.of(quantity));
            }
        }

        return new RecipeIngredient(line, null, null);
    }

    private static double parseQuantity(String input) {

        if (input.contains("/")) {
            String[] parts = input.split(" ");
            if (parts.length == 2) {
                // mixed number
                double whole = Double.parseDouble(parts[0]);
                String[] fraction = parts[1].split("/");
                return whole + (Double.parseDouble(fraction[0]) / Double.parseDouble(fraction[1]));
            } else if (parts.length == 1) {
                // common fraction
                String[] fraction = parts[0].split("/");
                return Double.parseDouble(fraction[0]) / Double.parseDouble(fraction[1]);

            }
        }

        return Double.parseDouble(input);
    }


}
