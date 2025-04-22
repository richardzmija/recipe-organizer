package edu.agh.recipe.recipes.model;

import edu.agh.recipe.units.domain.MeasurementUnit;
import edu.agh.recipe.units.domain.Quantity;
import edu.agh.recipe.units.domain.QuantityFormat;

public record RecipeIngredient (
        String ingredientName,
        MeasurementUnit unit,
        Quantity quantity
) {
    public static RecipeIngredient of(String ingredientName, MeasurementUnit unit, double quantity) {
        Quantity q = unit.isFractional() ?
                new Quantity(quantity, QuantityFormat.FRACTION) :
                Quantity.of(quantity);
        return new RecipeIngredient(ingredientName.toLowerCase(), unit, q);
    }
}